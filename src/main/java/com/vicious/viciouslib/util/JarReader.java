package com.vicious.viciouslib.util;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarReader {
    public static void cycleJarEntries(URL url, Consumer<JarEntry> consumer) {
        JarURLConnection connection = null;
        try {
            connection = (JarURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JarFile file = null;
        try {
            file = connection.getJarFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry e = entries.nextElement();
            consumer.accept(e);
        }
    }
}
