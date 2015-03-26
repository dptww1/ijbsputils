package com.psddev.ij.util;

public class PathUtil {
    /**
     * Returns part of path after given directory name.
     * <tt>pathBelow("/abc/def/ghi/file.txt", "def") => "/ghi/file.text"</tt>
     * <tt>pathBelow("/abc/def/ghi/file.txt", "xyz") => "/abc/def/ghi/file.text"</tt>
     *
     * @param completePath path to clip
     * @param dirName directory name at which to clip; this directory is not included in the resulting path
     *
     * @return the clipped path
     */
    public static String pathAfter(String completePath, String dirName) {
        if (completePath == null) {
            return null;
        }

        if (dirName == null || dirName.isEmpty()) {
            return completePath;
        }

        if (!dirName.startsWith("/")) {
            dirName = "/" + dirName;
        }

        if (!dirName.endsWith("/")) {
            dirName = dirName + "/";
        }

        int idx = completePath.indexOf(dirName);
        return idx >= 0 ? completePath.substring(idx + dirName.length() - 1) : completePath;
    }
}
