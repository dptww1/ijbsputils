package com.psddev.ij.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class EditorUtil {
    public static VirtualFile openFile(Project project, File f) {
        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f);
        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vf, 0), true);
        return vf;
    }
}
