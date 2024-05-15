package com.crawldata.back_end.utils;

/**
 * Utility class for file-related operations.
 */
public class FileUtils {

    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    private FileUtils() {
    }

    /**
     * Validates a file path by replacing forward slashes with the file separator
     * defined in the {@link AppUtils} class.
     *
     * @param path The file path to validate.
     * @return The validated file path.
     */
    public static synchronized String validate(String path) {
        return path.replace("/", AppUtils.SEPARATOR);
    }
}
