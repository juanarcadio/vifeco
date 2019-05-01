package org.laeq.settings;

import java.io.File;

public class Settings {
    public static String defaultPath = String.format("%s%s%s",System.getProperty("user.home"), File.separator,"vifeco");
    public static String dbPath = String.format("%s%s%s", defaultPath, File.separator, "db");
    public static String exportPath = String.format("%s%s%s",defaultPath,File.separator, "export");
    public static String imporPath = String.format("%s%s%s",defaultPath,File.separator, "import");
    public static String svgPath = String.format("%s%s%s",defaultPath, File.separator, "svg");
    public static String statisticPath = String.format("%s%s%s",defaultPath, File.separator, "statistic");
}
