package com.ucar;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ucar.anno.ApiAdapter;
import com.ucar.anno.ApiProperty;
import com.ucar.conf.DataBaseConf;
import com.ucar.conf.ModuleConf;
import com.ucar.constant.PropertyTypeEnum;
import com.ucar.dao.InterfaceDAO;
import com.ucar.dao.ModuleDAO;
import com.ucar.dao.PropertyDAO;
import com.ucar.dao.RepositoryDAO;
import com.ucar.entity.InterfaceDO;
import com.ucar.entity.ModuleDO;
import com.ucar.entity.PropertyDO;
import com.ucar.entity.RepositoryDO;
import com.ucar.module.NoParam;
import com.ucar.module.NoResult;
import com.ucar.util.ClassUtil;
import com.ucar.util.LoggerUtil;
import com.ucar.util.MySqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Mojo(name = "apiDocument",defaultPhase = LifecyclePhase.COMPILE)
public class MyMojo extends AbstractMojo {

    /**
     * 跳过文档生成,将不会执行插件内的任何逻辑 默认不跳过
     */
    @Parameter
    private boolean skipRap2 = false;

    /**
     * 需要生成文档的路径
     */
    @Parameter(required = true)
    private String documentPath;
    /**
     * 模块配置
     */
    @Parameter(required = true)
    private ModuleConf moduleConf;

    /**
     * 数据库配置
     */
    @Parameter(required = true)
    private DataBaseConf dataBaseConf;

    /**
     * maven项目数据
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject mavenProject;

    public void execute() {
        try {
            LoggerUtil.setLog(getLog());
            if (skipRap2) {
                LoggerUtil.getLog().debug("skipRap2 = true,跳过api文档生成");
                return;
            }
            MySqlUtil.setDataBaseConf(dataBaseConf);
            String outputDirectory = mavenProject.getBuild().getOutputDirectory();
            File f = new File(outputDirectory);
            //使用自定义类加载器,否则无法使用反射获取项目内的类
            ClassUtil.classLoader = new URLClassLoader(new URL[]{f.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            //指定的生成文档目录下的所有的adapter类列表
            List<Class<?>> allAdapterClass = ClassUtil.getAdapterClassesList(documentPath);
            //如果有相同的仓库名
            Long repositoryId = RepositoryDAO.findIdByName(moduleConf.getRepositoryName());
            if (repositoryId != null) {
                LoggerUtil.getLog().debug("查询到相同仓库名:" + moduleConf.getRepositoryName() + " ,将进行修改覆盖");
            }
            repositoryId = addOrUpdateRepository(repositoryId);
            addOrUpdateModule(allAdapterClass, repositoryId);
            //没有问题了再提交事务
            MySqlUtil.getConnection().commit();
        } catch (Throwable throwable) {
            try {
                MySqlUtil.getConnection().rollback();
                getLog().error("倒入数据失败 回滚数据");
                throwable.printStackTrace();
            } catch (Throwable throwables) {
                getLog().error(throwables);
            }
            getLog().debug(throwable);
        } finally {
            try {
                MySqlUtil.closeConnection();
            } catch (Throwable throwables) {
                getLog().error("请注意 : 数据库链接关闭异常!!");
                getLog().error(throwables);
            }
        }
    }

    private void addOrUpdateModule(List<Class<?>> allAdapterClassList, Long repositoryId) throws Exception {
        ModuleDO moduleDO = new ModuleDO();
        moduleDO.setName(moduleConf.getName());
        moduleDO.setDescription(moduleConf.getDescription());
        moduleDO.setRepositoryId(repositoryId);
        Long moduleId = ModuleDAO.findId(repositoryId, moduleDO.getName());
        if (moduleId != null) {
            LoggerUtil.getLog().debug("查询到" + moduleConf.getRepositoryName() + "仓库下相同的模块名:" + moduleConf.getName() + "将对该模块进行修改覆盖");
        }
        moduleDO.setId(moduleId);
        moduleId = ModuleDAO.insertOrUpdate(moduleDO);
        for (Class<?> adapterClass : allAdapterClassList) {
            ApiAdapter adapterAnno = adapterClass.getAnnotation(ApiAdapter.class);
            try {
                //接口下的入参字段列表
                List<PropertyDO> requestParam = null;
                //接口下的出参字段列表
                List<PropertyDO> responseResult = null;
                InterfaceDO interfaceDO = new InterfaceDO();
                interfaceDO.setUrl(adapterAnno.value());
                interfaceDO.setName(adapterAnno.name());
                Class<?> apiParamClass = adapterAnno.apiParam();
                Class<?> apiResultClass = adapterAnno.apiResult();
                if (!apiParamClass.equals(NoParam.class)) {//adapter上有入参用adapter上的
                    requestParam = resolveFields(apiParamClass, "request");
                }
                if (!apiResultClass.equals(NoResult.class)) {
                    responseResult = resolveFields(apiResultClass, "response");
                }
                interfaceDO.setMethod("GET");
                interfaceDO.setRepositoryId(repositoryId);
                interfaceDO.setModuleId(moduleId);
                interfaceDO.setRequestPropertyDOList(requestParam);
                interfaceDO.setResponsePropertyDOList(responseResult);
                //如果已经存在该接口 则更新接口数据 否则新增 由interfaceId判断
                Long interfaceId = InterfaceDAO.findId(interfaceDO.getRepositoryId(), interfaceDO.getModuleId(), interfaceDO.getUrl());
                interfaceDO.setId(interfaceId);
                addOrUpdateInterface(interfaceDO);
            } catch (Throwable throwable) {
                LoggerUtil.getLog().debug("该接口添加失败:" + adapterAnno.value());
                LoggerUtil.getLog().debug(throwable);
            }
        }
    }

    private void addOrUpdateInterface(InterfaceDO interfaceDO) throws Exception {
        Long newInterfaceId = InterfaceDAO.insertOrUpdate(interfaceDO);
        if (interfaceDO.getId() != null) {
            //如果该接口已存在,则先删除接口下的属性 然后再重新生成
            PropertyDAO.deleteInterfaceProperty(interfaceDO.getId());
        }
        PropertyDAO.insertList(interfaceDO.getRequestPropertyDOList(), -1L, newInterfaceId, interfaceDO.getModuleId(), interfaceDO.getRepositoryId());
        PropertyDAO.insertList(interfaceDO.getResponsePropertyDOList(), -1L, newInterfaceId, interfaceDO.getModuleId(), interfaceDO.getRepositoryId());
    }

    @Nullable
    private List<PropertyDO> resolveFields(Class<?> propertyParam, String scope) {
        if (isBaseType(propertyParam)) {
//            PropertyDO propertyDO = processBaseType(adapterParam, scope);
//            propertyList.add(propertyDO);
//            return propertyList;
            return null;
        } else {
            List<PropertyDO> propertyList = new LinkedList<>();
            return processObjectType(propertyParam, scope, propertyList);
        }

    }

    @Nullable
    @Contract("_, _, _ -> param3")
    private List<PropertyDO> processObjectType(@NotNull Class<?> adapterParam, String scope, List<PropertyDO> propertyList) {
        Field[] declaredFields = adapterParam.getDeclaredFields();//当前类声明的字段
        Class<?> superclass = adapterParam.getSuperclass();
        if (declaredFields.length == 0) {
            return propertyList;
        }
        if (adapterParam.getSuperclass() != null) {
            List<PropertyDO> propertyDOList = resolveFields(superclass, scope);
            if (propertyDOList != null) {
                propertyList.addAll(propertyDOList);
            }
        }
        for (Field declaredField : declaredFields) {
            try {
                ApiProperty propertyAnno;
                PropertyDO property;
                Class<?> type = declaredField.getType();
                property = new PropertyDO();
                property.setScope(scope);
                if (declaredField.isAnnotationPresent(ApiProperty.class)) {
                    propertyAnno = declaredField.getAnnotation(ApiProperty.class);
                    property.setName(propertyAnno.value());
                    property.setRequired(propertyAnno.required() ? 1 : 0);
                    property.setDescription(propertyAnno.desc());
                    property.setValue(propertyAnno.example());
                }
                String name = declaredField.getName();
                if (type.getName().contains("$") || "serialVersionUID".equals(name)) {
                    //1.内部类class会有一个外部类class的引用 eg:this$0 会造成死循环 栈溢出 这里处理一下
                    //2.有的返回类会继承Serializable serialVersionUID不认为是参数
                    return null;
                }
                if (StringUtils.isBlank(property.getName())) {//没有注释取字段名
                    property.setName(name);
                }
                if (type.equals(String.class) || type.equals(char.class) || type.equals(Character.class)) {
                    property.setType(PropertyTypeEnum.STRING.getName());
                } else if (Number.class.isAssignableFrom(type)) {
                    property.setType(PropertyTypeEnum.NUMBER.getName());
                } else if (type.equals(byte.class) || type.equals(int.class) || type.equals(long.class)
                        || type.equals(float.class) || type.equals(double.class) || type.equals(short.class)) {
                    property.setType(PropertyTypeEnum.NUMBER.getName());
                } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                    property.setType(PropertyTypeEnum.BOOLEAN.getName());
                } else if (List.class.isAssignableFrom(type)) {
                    property.setType(PropertyTypeEnum.ARRAY.getName());
                    Type genericType = declaredField.getGenericType();
                    if (genericType instanceof ParameterizedType) {//数组类型肯定要有一个范型,不然没法生成文档
                        Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                        if (actualTypeArguments != null && actualTypeArguments.length >= 1) {
                            Type actualTypeArgument = actualTypeArguments[0];
                            if (actualTypeArgument instanceof Class) {
                                Class<?> typeArgumentClass = (Class<?>) actualTypeArgument;
                                property.setPropertyDOList(resolveFields(typeArgumentClass, scope));
                            }
                        }
                    }
                } else if (type.isArray()) {
                    property.setType(PropertyTypeEnum.ARRAY.getName());
                    property.setPropertyDOList(resolveFields(type.getComponentType(), scope));
                } else if (Map.class.isAssignableFrom(type)) {
                    property.setType(PropertyTypeEnum.MAP.getName());
                } else {
                    property.setType(PropertyTypeEnum.OBJECT.getName());
                    property.setPropertyDOList(resolveFields(type, scope));
                }
                propertyList.add(property);

            } catch (Throwable throwable) {
                LoggerUtil.getLog().debug("该字段无法解析,没有生成该字段对应的文档:" + declaredField.getName());
                LoggerUtil.getLog().debug(throwable);
            }

        }
        return propertyList;
    }

    @NotNull
    private PropertyDO processBaseType(@NotNull Class<?> adapterParam, String scope) {
        PropertyDO p = new PropertyDO();
        p.setName("-");
        p.setScope(scope);
        if (adapterParam.equals(String.class)) {
            p.setType(PropertyTypeEnum.STRING.getName());
        } else if (adapterParam.equals(byte.class) || adapterParam.equals(int.class) || adapterParam.equals(long.class)
                || adapterParam.equals(float.class) || adapterParam.equals(double.class)) {
            p.setType(PropertyTypeEnum.NUMBER.getName());
        } else if (adapterParam.equals(boolean.class) || adapterParam.equals(Boolean.class)) {
            p.setType(PropertyTypeEnum.BOOLEAN.getName());
        } else if (List.class.isAssignableFrom(adapterParam)) {
            p.setType(PropertyTypeEnum.ARRAY.getName());
//            Type genericType = adapterParam.getGenericType();
//            if (genericType instanceof ParameterizedType) {//数组类型肯定要有一个范型,不然没法生成文档
//                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
//                if (actualTypeArguments != null && actualTypeArguments.length >= 1) {
//                    getLog().debug("==============" + JSONObject.toJSONString(actualTypeArguments));
//                    Type actualTypeArgument = actualTypeArguments[0];
//                    if (actualTypeArgument instanceof Class) {
//                        Class<?> typeArgumentClass = (Class<?>) actualTypeArgument;
//                        p.setPropertyDOList(resolveFields(typeArgumentClass, scope));
//                    }
//                }
//            }
        } else if (adapterParam.isArray()) {
            p.setType(PropertyTypeEnum.ARRAY.getName());
            p.setPropertyDOList(resolveFields(adapterParam, scope));
        } else if (Map.class.isAssignableFrom(adapterParam)) {
            p.setType(PropertyTypeEnum.MAP.getName());
        }
        return p;
    }

    private boolean isBaseType(@NotNull Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return true;
        } else if (Number.class.isAssignableFrom(clazz)) {
            return true;
        } else if (clazz.equals(byte.class) || clazz.equals(int.class) || clazz.equals(long.class)
                || clazz.equals(float.class) || clazz.equals(double.class)) {
            return true;
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            return true;
        } else if (List.class.isAssignableFrom(clazz)) {
            return true;
        } else if (clazz.isArray()) {
            return true;
        } else return Map.class.isAssignableFrom(clazz);
    }

    private Long addOrUpdateRepository(Long repositoryId) throws Exception {
        RepositoryDO repositoryDO = new RepositoryDO();
        repositoryDO.setName(moduleConf.getRepositoryName());
        repositoryDO.setDescription(moduleConf.getRepositoryDesc());
        repositoryDO.setId(repositoryId);
        //系统生成的仓库,认为拥有者和创建者为系统 1L用户和组织都是系统,仓库必须有创建者
        repositoryDO.setOwnerId(1L);
        repositoryDO.setCreatorId(1L);
        if (moduleConf.getOwnerId() != null) {
            repositoryDO.setOwnerId(moduleConf.getOwnerId());
            repositoryDO.setCreatorId(moduleConf.getOwnerId());
        }
//        repositoryDO.setOrganizationId(1L);
        repositoryId = RepositoryDAO.insertOrUpdate(repositoryDO);
        return repositoryId;
    }
}
