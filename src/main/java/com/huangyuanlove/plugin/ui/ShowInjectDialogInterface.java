package com.huangyuanlove.plugin.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

public interface ShowInjectDialogInterface {
     void onCancel();
     void onConfirm(Project project, Editor editor, ArrayList<ElementBean> elements, boolean splitOnclickMethods);
}
