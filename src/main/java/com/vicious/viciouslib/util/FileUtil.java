package com.vicious.viciouslib.util;

import com.vicious.viciouslib.LoggerWrapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtil {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String resolve(String path){
        String resolved = "";
        String[] splits = path.split("/");
        for (String split : splits) {
            resolved+=split;
            if(!resolved.contains(".")){
                createDirectoryIfDNE(resolved);
            }
            else{
                try {
                    File f = new File(resolved);
                    if(!f.exists()) {
                        f.createNewFile();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            resolved += "/";
        }
        return path;
    }
    public static Path createDirectoryIfDNE(String path){
        Path p = toPath(path);
        if(!Files.isDirectory(p)){
            try {
                Files.createDirectories(p);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return p;
    }

    public static void createOrWipe(String path){
        Path p = toPath(path);
        try {
            Files.write(p, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e){
            try {
                Files.createFile(p);
            } catch(IOException ex){
                LoggerWrapper.logError("I'm not sure how we got here, but somehow the file you have created both exists and doesn't exist at the same time. Is this God?");
                ex.printStackTrace();
            }
        }
    }
    public static void createOrWipe(Path p){
        try {
            Files.write(p, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e){
            try {
                Files.createFile(p);
            } catch(IOException ex){
                LoggerWrapper.logError("I'm not sure how we got here, but somehow the file you have created both exists and doesn't exist at the same time. Is this God?");
                ex.printStackTrace();
            }
        }
    }
    public static JSONObject loadJSON(String path) throws JSONException,IOException{
        Path p = toPath(path);
        try{
            InputStream is = new FileInputStream(p.toFile());
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            is.close();
            return new JSONObject(jsonTxt);

        } catch (Exception ex){
            LoggerWrapper.logError("Could not load, probably doesn't actually exist. " + p.toString() + " caused by: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }
    public static JSONObject loadJSON(Path p) throws JSONException,IOException{
        try{
            InputStream is = new FileInputStream(p.toFile());
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            is.close();
            return new JSONObject(jsonTxt);

        } catch (Exception ex){
            LoggerWrapper.logError("Could not load, probably doesn't actually exist. " + p.toString() + " caused by: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    public static Path toPath(String path) {
        return Paths.get(FilenameUtils.separatorsToSystem(path));
    }
}
