package com.psddev.ij.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;

public class PsiDirectoryUtil {
    public static PsiDirectory findRoot(Project project) {
        PsiManager psiMgr = PsiManager.getInstance(project);
        for (VirtualFile vf : ProjectRootManager.getInstance(project).getContentRoots()) {
            return psiMgr.findDirectory(vf);
        }
        return null;
    }


    public static PsiDirectory findDirectoryPath(Project project, String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        PsiDirectory dir = findRoot(project);
        if (dir != null) {

            String[] pathComponents = path.split("/");

            if (pathComponents != null || pathComponents.length > 0) {
                for (int i = 0; i < pathComponents.length; ++i) {
                    dir = dir.findSubdirectory(pathComponents[i]);
                }
            }
        }

        return dir;
    }
}
