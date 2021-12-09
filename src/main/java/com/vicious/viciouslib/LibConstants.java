package com.vicious.viciouslib;

import com.vicious.viciouslib.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LibConstants {
    public static Path configPath = FileUtil.createDirectoryIfDNE(Paths.get("config"));
    public static Path viciousConfigDirectoryPath = FileUtil.createDirectoryIfDNE(Paths.get(configPath.toAbsolutePath() + "/vicious"));
    public static Path libConfigPath = Paths.get(viciousConfigDirectoryPath.toAbsolutePath() + "/lib.json");
}
