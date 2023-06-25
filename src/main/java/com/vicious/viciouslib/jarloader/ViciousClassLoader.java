package com.vicious.viciouslib.jarloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class ViciousClassLoader extends URLClassLoader {
    public ViciousClassLoader(ClassLoader parent) {
        super(new URL[0],parent);
    }

    public ViciousClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ViciousClassLoader(URL[] urls) {
        super(urls);
    }

    public ViciousClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public ViciousClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    public ViciousClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(name, urls, parent, factory);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
