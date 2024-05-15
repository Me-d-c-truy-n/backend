package com.crawldata.back_end.plugin;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

public class PluginLoader {
    private static final JarClassLoader jcl = new JarClassLoader();
    public static Object loadPluginClass(String path, String className) {
        jcl.add(path);
        JclObjectFactory factory = JclObjectFactory.getInstance();
        Object obj = factory.create(jcl, className);
        return obj;
    }

    public static void unloadPluginClass(String className) {
        jcl.unloadClass(className);
    }
}
