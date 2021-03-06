package com.psddev.ij.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Visitor class which collects annotations matching a filtered list within a file.
 */
public class AnnotationAccumulator extends PsiRecursiveElementWalkingVisitor {

    private List<PsiAnnotation> list = new ArrayList<PsiAnnotation>();
    private Set<String> nameFilters = new HashSet<String>();

    /**
     * Constructor.
     * @param nameFilters the fully-qualified annotation names to accumulate; if empty, all annotations are returned
     */
    public AnnotationAccumulator(String... nameFilters) {
        if (nameFilters != null) {
            this.nameFilters.addAll(Arrays.asList(nameFilters));
        }
    }

    @Override
    public void visitElement(PsiElement elt) {
        super.visitElement(elt);
        if (elt instanceof PsiAnnotation) {
            PsiAnnotation anno = (PsiAnnotation) elt;
            if (nameFilters.isEmpty() || nameFilters.contains(anno.getQualifiedName())) {
                list.add((PsiAnnotation) elt);
            }
        }
    }

    /**
     * Performs the traversal and accumulation of annotations.
     * @param file the file to traverse
     * @return the list of matching annotations, never {@code null}
     */
    public List<PsiAnnotation> execute(PsiJavaFile file) {
        file.accept(this);
        return list;
    }

    public List<PsiAnnotation> executeIncludeSuperClasses(PsiJavaFile file) {
        while (list.isEmpty() && file != null) {
            file.accept(this);

            if (list.isEmpty()) {
                file = PsiFileUtil.getSuperClassFile(file);
            }
        }
        return list;
    }

    public static PsiAnnotation findAnnotationIncludeSuperClasses(PsiJavaFile file, String... nameFilters) {
        AnnotationAccumulator a = new AnnotationAccumulator(nameFilters);
        List<PsiAnnotation> list = a.executeIncludeSuperClasses(file);
        return list.size() > 0 ? list.get(0) : null;
    }
}
