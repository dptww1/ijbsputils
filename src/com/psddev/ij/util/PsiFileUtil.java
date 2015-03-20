package com.psddev.ij.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class PsiFileUtil {

    public static PsiJavaFile getSuperClassFile(PsiJavaFile file) {
        PsiClass[] classes = file.getClasses();
        if (classes != null && classes.length > 0) {
            PsiClass superClass = classes[0].getSuperClass();
            if (superClass != null) {
                PsiFile baseClassFile = superClass.getContainingFile();
                if (baseClassFile instanceof PsiJavaFile) {
                    return (PsiJavaFile) baseClassFile;
                }
            }
        }

        return null;
    }
}
