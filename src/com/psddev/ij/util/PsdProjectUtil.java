package com.psddev.ij.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

public class PsdProjectUtil {
    public static String findFirst(Project project, String filename) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, filename, GlobalSearchScope.allScope(project));
        return files != null && files.length > 0 ? files[0].getVirtualFile().getPath() : null;
    }

    public static String fileSystemPath(Project project, String relativePath) {
        String path = project.getBasePath() + relativePath;
        return path.startsWith("file://") ? path.substring("file://".length()) : path;
    }
}
