package com.psddev.ij.util;

import com.intellij.openapi.project.Project;

public class ViewUtil {
    public static String getTaglibPath(Project project, String path) {
        return PsdProjectUtil.findFirst(project, "taglibs." + getCompatibleTaglibExtension(path));
    }

    public static String getCompatibleTaglibExtension(String path) {
        return path.endsWith("ftl") ? "ftl" : "jsp";
    }

    public static String buildTaglibIncludeStatementText(String taglibPath) {
        String relativePath = PathUtil.pathAfter(taglibPath, "webapp");
        return taglibPath.endsWith("ftl") ? "[#include \"" + relativePath + "\"]"
                                          : "<%@ include file=\"" + relativePath + "\" %>";
    }
}
