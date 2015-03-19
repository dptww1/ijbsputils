package com.psddev.ij;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Opens the JSP file specified in the @Renderer.Path annotation of a Java class.
 */
public class GoToJsp extends AnAction {

    public GoToJsp() {
        super("BSP");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PsiFile file = event.getData(LangDataKeys.PSI_FILE);
        if (file == null) {
            Messages.showInfoMessage(project, "Please select a Java class to navigate from.", "Information");
            return;
        }
        if (!(file instanceof PsiJavaFile)) {
            Messages.showInfoMessage(project, "You can only go to a JSP file from a Java file", "Information");
            return;
        }

        List<RendererPathInfo> paths = findRendererPaths(file);
        switch (paths.size()) {
            case 0:
                Messages.showInfoMessage(project, "This class has no @Renderer.Path annotations", "Information");
                break;

            case 1:
                openJsp(project, paths.get(0).getValue());
                break;

            default:
                int choice = Messages.showDialog("Where do you want to go?", "Multiple @Paths", pathsAsOptions(paths),
                                                 0, Messages.getQuestionIcon());
                if (0 <= choice && choice < paths.size()) {
                    openJsp(project, paths.get(choice).getValue());
                }
                break;
        }
    }

    private void openJsp(Project project, String path) {
        String fullPath = project.getBaseDir() + "/src/main/webapp" + path;
        if (fullPath.startsWith("file://")) {
            fullPath = fullPath.substring("file://".length());
        }
        File f = new File(fullPath);
        if (f.exists() && f.isFile() && f.canRead()) {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(f);
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vf, 0), true);
        } else {
            Messages.showInfoMessage(project, fullPath + " doesn't exist!", "Information");
        }
    }

    private List<RendererPathInfo> findRendererPaths(PsiFile file) {
        List<RendererPathInfo> list = new ArrayList<RendererPathInfo>();

        for (PsiAnnotation an : new AnnotationAccumulator("com.psddev.cms.db.Renderer.Path").execute(file)) {
            list.add(new RendererPathInfo(an));
        }

        return list;
    }

    private String[] pathsAsOptions(List<RendererPathInfo> paths) {
        String[] a = new String[paths.size()];
        for (int i = 0; i < paths.size(); ++i) {
            a[i] = paths.get(i).toString();
        }
        return a;
    }

    /**
     * Visitor class which collects annotations matching a filtered list within a file.
     */
    private static class AnnotationAccumulator extends PsiRecursiveElementWalkingVisitor {

        private List<PsiAnnotation> list = new ArrayList<PsiAnnotation>();
        private Set<String> nameFilters = new HashSet<String>();

        /**
         * Constructor.
         * @param nameFilters the fully-qualified annotation names to accumulate; if empty, all annotations are returned
         */
        public AnnotationAccumulator(String ...nameFilters) {
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
        public List<PsiAnnotation> execute(PsiFile file) {
            file.accept(this);
            return list;
        }
    }

    /**
     * Immutable class to hold the parameters of a @Renderer.Path annotation.
     */
    private static class RendererPathInfo {

        private String value;
        private String context;

        public RendererPathInfo(PsiAnnotation anno) {
            this.value = AnnotationUtil.getStringAttributeValue(anno, "value");
            this.context = AnnotationUtil.getStringAttributeValue(anno, "context");
        }

        public String getValue() {
            return value;
        }

        public String getContext() {
            return context;
        }

        @Override
        public String toString() {
            String ctx = context == null || context.isEmpty() ? "default" : context;
            return "<" + ctx + "> " + value;
        }
    }
}
