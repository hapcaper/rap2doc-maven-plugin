package com.ucar.dao;

import com.ucar.entity.PropertyDO;
import com.ucar.util.LoggerUtil;
import com.ucar.util.MySqlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/22
 */
public class PropertyDAO {


    @NotNull
    public static Long insert(@NotNull PropertyDO propertyDO) throws Exception {
        Statement statement = MySqlUtil.getConnection().createStatement();
        statement.executeUpdate(String.format("insert into Properties (scope, name, type, rule, value, description, parentId," +
                        "interfaceId, moduleId, repositoryId,required,createdAt, updatedAt) values ('%s','%s','%s','%s','%s','%s',%d,%d,%d,%d,%d,now(),now());",
                propertyDO.getScope(), propertyDO.getName(), propertyDO.getType(), propertyDO.getRule(), propertyDO.getValue(),
                propertyDO.getDescription(), propertyDO.getParentId(), propertyDO.getInterfaceId(), propertyDO.getModuleId(), propertyDO.getRepositoryId(), propertyDO.getRequired()),
                Statement.RETURN_GENERATED_KEYS);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new RuntimeException("添加新property失败");
        }
    }
    public static void insertList(@Nullable List<PropertyDO> list, Long parentId, Long interfaceId, Long moduleId, Long repositoryId) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (PropertyDO propertyDO : list) {
            propertyDO.setParentId(parentId);
            propertyDO.setInterfaceId(interfaceId);
            propertyDO.setModuleId(moduleId);
            propertyDO.setRepositoryId(repositoryId);
            LoggerUtil.getLog().debug("^^^^^^^^^^" + propertyDO.getName());
            Long newId = insert(propertyDO);
            List<PropertyDO> propertyDOList = propertyDO.getPropertyDOList();
            if (propertyDOList != null) {
                insertList(propertyDOList, newId, interfaceId, moduleId, repositoryId);
            }
        }

    }

    @NotNull
    private static Long findId(String name, Long parentId, Long interfaceId, Long moduleId, Long repositoryId) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("select id from Properties where name=? and parentId=? and interfaceId=? and moduleId=? and repositoryId=?;");
        statement.setString(1, name);
        statement.setLong(2, parentId);
        statement.setLong(3, interfaceId);
        statement.setLong(4, moduleId);
        statement.setLong(5, repositoryId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new RuntimeException();
        }
    }

    public static void deleteInterfaceProperty(Long interfaceId) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("delete from Properties where interfaceId = ?");
        statement.setLong(1, interfaceId);
        statement.executeUpdate();
    }
}
