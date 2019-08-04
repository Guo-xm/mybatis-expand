package name.guoxm.mybatis.expand.tools;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 工具
 * 通过包名获取包下的所有类
 * Created on 2019/5/20.
 * @author guoxm
 */
public class ClassTool {

    public static Set<Class<?>> getClasses(String packageName, boolean recursive) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement(); // 获取下一个元素
                String protocol = url.getProtocol(); // 得到协议的名称
                if ("file".equals(protocol)) { // 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); // 获取包的物理路径
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes); // 以文件的方式扫描整个包下的文件 并添加到集合中
                } else if ("jar".equals(protocol)) {
                    packageName = handleJar(packageName, recursive, classes, packageDirName, url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static String handleJar(String packageName, boolean recursive, Set<Class<?>> classes, String packageDirName, URL url) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile(); // 获取jar
            Enumeration<JarEntry> entries = jar.entries(); // 从此jar包 得到一个枚举类
            while (entries.hasMoreElements()){
                JarEntry entry = entries.nextElement(); // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                String name = entry.getName();
                if (name.charAt(0) == '/') // 如果是以/开头的 获取后面的字符串
                    name = name.substring(1);
                if (name.startsWith(packageDirName)) { // 如果前半部分和定义的包名相同
                    int idx = name.lastIndexOf('/');
                    if (idx != -1) // 如果以"/"结尾 是一个包
                        packageName = name.substring(0, idx).replace('/', '.'); // 获取包名 把"/"替换成"."
                    if (idx != -1 || recursive) { // 如果可以迭代下去 并且是一个包
                        if (name.endsWith(".class") && !entry.isDirectory()) { // 如果是一个.class文件 而且不是目录
                            String className = name.substring(packageName.length() + 1, name.length() - 6); // 去掉后面的".class" 获取真正的类名
                            try {
                                classes.add(Class.forName(packageName + '.' + className)); // 添加到classes
                            } catch (ClassNotFoundException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return packageName;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes){
        File directory = new File(packagePath); // 获取此包的目录 建立一个File
        if (!directory.exists() || !directory.isDirectory())  // 如果不存在或者 也不是目录就直接返回
            return;  // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
        // 如果存在 就获取包下的所有文件 包括目录
        File[] directoryFiles = directory.listFiles(file -> { // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            return recursive && file.isDirectory() || file.getName().endsWith(".class");
        });
        if (directoryFiles == null)
            return;
        for (File file : directoryFiles){
            if (file.isDirectory()) { // 如果是目录 则递归扫描
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else { // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
