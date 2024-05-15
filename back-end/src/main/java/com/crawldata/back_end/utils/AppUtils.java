package com.crawldata.back_end.utils;

import java.nio.file.FileSystems;

/**
 * Created by Long on 1/5/2017.
 */
public class AppUtils {
    private AppUtils() {
    }
    public static String curDir = System.getProperty("user.dir");
    public static String cacheDir = curDir;
    public static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public static void doLoad() {
        try {
            if (curDir.endsWith(SEPARATOR)) curDir = curDir.substring(0, curDir.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
