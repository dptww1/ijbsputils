package com.psddev.ij.action;

import com.psddev.ij.util.PathUtil;

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
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoToModel extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PsiFile file = event.getData(LangDataKeys.PSI_FILE);
        if (file == null) {
            Messages.showInfoMessage(project, "Please select a Renderer (JSP or FTL) to navigate from.", "Information");
            return;
        }
        String curPath = file.getVirtualFile().getCanonicalPath();
        if (curPath == null || (!curPath.endsWith(".ftl") && !curPath.endsWith(".jsp"))) {
            Messages.showInfoMessage(project, "You can only go to a Model from a renderer file", "Information");
            return;
        }

        List<VirtualFile> models = findModelsForView(project, curPath);
        switch (models.size()) {
            case 0:
                Messages.showInfoMessage(project, "There's no model with a @RenderPath annotation referencing this file", "Information");
                break;

            case 1:
                openModelPath(project, models.get(0).getCanonicalPath());
                break;

            default:
                int choice = Messages.showDialog("Which model?", "Multiple models", modelsAsOptions(models),
                                                 0, Messages.getQuestionIcon());
                if (0 <= choice && choice < models.size()) {
                    openModelPath(project, models.get(choice).getCanonicalPath());
                }
                break;
        }
    }

    private String[] modelsAsOptions(List<VirtualFile> models) {
        String[] a = new String[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            a[i] = PathUtil.pathAfter(models.get(i).getCanonicalPath(), "java");
        }
        return a;
    }

    private void openModelPath(Project project, String path) {
        if (path.startsWith("file://")) {
            path = path.substring("file://".length());
        }

        File f = new File(path);
        if (f.exists() && f.isFile() && f.canRead()) {
            VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f);
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vf, 0), true);
        } else {
            Messages.showInfoMessage(project, "Selected model:\n\n" + path + "\n\nisn't a file, or isn't readable", "Information");
        }
    }

    private List<VirtualFile> findModelsForView(Project project, String curPath) {
        final String rendererRegExp = "(?s)" +
                                      "^.*" +
                                      "@(Renderer\\.)?Path\\s*\\(" +
                                      "(\\s*context\\s*=\\s*\".*?\"\\s*,)?" +
                                      "(\\s*value\\s*=\\s*)?" +
                                      "\"" + PathUtil.pathAfter(curPath, "webapp") + "\"" +
                                      ".*$";
        List<VirtualFile> models = new ArrayList<VirtualFile>();

        Collection<VirtualFile> vfiles = FilenameIndex.getAllFilesByExt(project, "java");
        for (VirtualFile vf : vfiles) {

            // vfiles will include classes within JARS, but we omit them because 1) they slow
            // things down significantly, and 2) they are likely irrelevant anyway.
            if (vf.getCanonicalPath().contains("/.m2/repository")) {
                continue;
            }

            try {
                String contents = new String(vf.contentsToByteArray());
                if (contents.matches(rendererRegExp)) {
                    models.add(vf);
                }
            } catch (IOException e) {
                Messages.showInfoMessage(vf.getCanonicalPath(), "Can't read file contents!");
            }
        }

        return models;
    }
}
