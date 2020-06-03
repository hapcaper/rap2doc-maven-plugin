package io.github.hapcaper.util;


import io.github.hapcaper.anno.ApiAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/21
 */
public class ClassUtil {

    public static ClassLoader classLoader;


    /*
     * 取得某一类所在包的所有类名 不含迭代
     */
    @Nullable
    public static String[] getPackageAllClassName(String classLocation, @NotNull String packageName) {
        //将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        StringBuilder realClassLocation = new StringBuilder(classLocation);
        for (String s : packagePathSplit) {
            realClassLocation.append(File.separator).append(s);
        }
        File packeageDir = new File(realClassLocation.toString());
        if (packeageDir.isDirectory()) {
            return packeageDir.list();
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     * @param packageName 包名
     * @return ${packageName} 对应类型的类列表
     */
    @NotNull
    public static List<Class<?>> getAdapterClassesList(@NotNull String packageName) {

        //第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(packageDirName);
            //循环迭代下去
            while (dirs.hasMoreElements()) {
                //获取下一个元素
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAllAdapter(packageName, url, true, classes);
                }
            }
        } catch (Throwable e) {
            LoggerUtil.getLog().debug(e);
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 报名
     * @param packagePath 包路径
     * @param recursive 是否递归迭代解析
     * @param classes 解析出来的类将会添加到该集合中
     */
    public static void findAll(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : Objects.requireNonNull(dirfiles)) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAll(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                try {
                    //添加到集合中去
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    classes.add(classLoader.loadClass(packageName + '.' + className));
                } catch (Throwable e) {
                    LoggerUtil.getLog().debug("类加载异常,无法生成与" + file.getName() + "对应的接口数据");
                    LoggerUtil.getLog().debug(e);
                }
            }
        }
    }

    /**
     * 以文件的形式来获取包下的Adapter的Class
     *
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive 是否递归迭代解析
     * @param classes 解析出来的类将会添加到该集合中
     */
    public static void findAllAdapter(String packageName, @NotNull URL packagePath, final boolean recursive, List<Class<?>> classes) throws URISyntaxException, MalformedURLException {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath.toURI());
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        if (dirfiles == null) {
            return;
        }
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAllAdapter(packageName + "." + file.getName(),
                        file.toURI().toURL(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                try {
                    //添加到集合中去
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = classLoader.loadClass(packageName+'.' + className);
                    if (clazz.isAnnotationPresent(ApiAdapter.class)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    LoggerUtil.getLog().debug("类加载异常,无法生成与" + file.getName() + "对应的接口数据");
                    LoggerUtil.getLog().debug(e);
                }
            }
        }
    }

}
