package com.psddev.ij.action;

import com.psddev.ij.util.AnnotationAccumulator;
import com.psddev.ij.util.PathUtil;

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
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        String fullRelativePath = "/src/main/webapp" + path;
        String fileSystemPath = project.getBaseDir() + fullRelativePath;
        if (fileSystemPath.startsWith("file://")) {
            fileSystemPath = fileSystemPath.substring("file://".length());
        }

        File f = new File(fileSystemPath);
        if (f.exists()) {
            if (!f.isFile() || !f.canRead()) {
                Messages.showInfoMessage(project, "Selected file:\n\n" + fileSystemPath + "\n\nisn't a file, or isn't readable", "Information");
            }

        } else {
            int yn = Messages.showOkCancelDialog((Project) null, "Selected path:\n\n" + fileSystemPath + "\n\ndoesn't exist.  Create it?", "Missing file",
                                                 "Yes", "No", Messages.getQuestionIcon());
            if (yn == Messages.YES) {
                f = createJspFileSkeleton(project, fileSystemPath);
            }
        }
        if (f.exists()) {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f);
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vf, 0), true);
        }
    }

    private File createJspFileSkeleton(Project project, String path) {
        try {
            String taglibPath = "/path/to/taglibs.jsp";
            PsiFile[] taglibFiles = FilenameIndex.getFilesByName(project, "taglibs.jsp", GlobalSearchScope.allScope(project));
            if (taglibFiles != null && taglibFiles.length > 0) {
                taglibPath = PathUtil.pathAfter(taglibFiles[0].getVirtualFile().getPath(), "webapp");
            }

            FileWriter fw = new FileWriter(path);
            fw.write("<%@ include file=\"" + taglibPath + "\" %>");
            fw.close();

            return new File(path);

        } catch (IOException e) {
            e.printStackTrace();  // TODO: real logging
            return null;
        }
    }

    /**
     * Returns path part (only) of file path.
     * <tt>"/some/path/to/some/file.ext" => "/some/path/to/some"</tt>
     * <tt>"pathlessFile.ext" => ""</tt>
     * @param filePath path to examine; not {@code null}
     * @return the path part
     */
    private String pathOnly(String filePath) {
        int idx = filePath.lastIndexOf("/");
        return idx >= 0 ? filePath.substring(0, idx) : "";
    }

    private List<RendererPathInfo> findRendererPaths(PsiJavaFile file) {
        List<RendererPathInfo> list = new ArrayList<RendererPathInfo>();

        for (PsiAnnotation an : new AnnotationAccumulator("com.psddev.cms.db.Renderer.Path").executeIncludeSuperClasses(file)) {
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
