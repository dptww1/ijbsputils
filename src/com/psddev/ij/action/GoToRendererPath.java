package com.psddev.ij.action;

import com.psddev.ij.util.AnnotationAccumulator;

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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens the file specified in the @Renderer.Path annotation of a Java class.
 */
public class GoToRendererPath extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PsiFile file = event.getData(LangDataKeys.PSI_FILE);
        if (file == null) {
            Messages.showInfoMessage(project, "Please select a Java class to navigate from.", "Information");
            return;
        }
        if (!(file instanceof PsiJavaFile)) {
            Messages.showInfoMessage(project, "You can only go to a Renderer Path file from a Java file", "Information");
            return;
        }

        List<RendererPathInfo> paths = findRendererPaths((PsiJavaFile) file);
        switch (paths.size()) {
            case 0:
                Messages.showInfoMessage(project, "This class has no @Renderer.Path annotations", "Information");
                break;

            case 1:
                openRenderPath(project, paths.get(0).getValue());
                break;

            default:
                int choice = Messages.showDialog("Where do you want to go?", "Multiple @Paths", pathsAsOptions(paths),
                                                 0, Messages.getQuestionIcon());
                if (0 <= choice && choice < paths.size()) {
                    openRenderPath(project, paths.get(choice).getValue());
                }
                break;
        }
    }

    private void openRenderPath(Project project, String path) {
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

    private List<RendererPathInfo> findRendererPaths(PsiJavaFile file) {
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
