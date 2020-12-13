package com.huangyuanlove.plugin;

import com.huangyuanlove.plugin.ui.ElementBean;
import com.huangyuanlove.plugin.ui.ElementListPanel;
import com.huangyuanlove.plugin.ui.ShowInjectDialogInterface;
import com.huangyuanlove.plugin.util.GetLayoutFileUtil;
import com.huangyuanlove.plugin.util.NotificationUtil;
import com.huangyuanlove.plugin.util.Utils;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class GenerateCodeAction extends BaseGenerateAction implements DumbAware, ShowInjectDialogInterface {
    protected JFrame mDialog;
    private static final Logger log = Logger.getInstance(GenerateCodeAction.class);
    public GenerateCodeAction() {
        super(null);
    }

    protected GenerateCodeAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if(project ==null){
            return ;
        }
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(editor ==null){
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService
                    .showDumbModeNotification("ViewInject plugin is not available during indexing");
            return;
        }



        analyze(project, editor);
    }

    private void analyze(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiFile layout = GetLayoutFileUtil.getLayoutFileFromCaret(editor, file);
        if (layout == null) {
            NotificationUtil.showErrorNotification(project, "No layout found");
            return;
        }

        log.info("Layout file: " + layout.getVirtualFile());

        ArrayList<ElementBean> elements = GetLayoutFileUtil.getIDsFromLayout(layout);

        if (elements.isEmpty()) {
            NotificationUtil.showErrorNotification(project, "No IDs found in layout");

        } else {
            showDialog(project, editor, elements);
        }
    }

    protected void showDialog(Project project, Editor editor, ArrayList<ElementBean> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        log.info("element: " + elements);

        ElementListPanel elementListPanel = new ElementListPanel(project, editor, elements, this);
        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialog.getRootPane().setDefaultButton(elementListPanel.getConfirmButton());
        mDialog.getContentPane().add(elementListPanel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }


    @Override
    public void onCancel() {
        closeDialog();
    }

    @Override
    public void onConfirm(Project project, Editor editor, ArrayList<ElementBean> elements, boolean generateId) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        closeDialog();

        if (Utils.getInjectCount(elements) > 0 || Utils.getClickCount(elements) > 0) { // generate injections
            new InjectWriter(file, getTargetClass(editor, file), "Generate Injections", elements,generateId).execute();
        } else {
            NotificationUtil.showInfoNotification(project, "No injection was selected");
        }
    }


    protected void closeDialog() {
        if (mDialog == null) {
            return;
        }

        mDialog.setVisible(false);
        mDialog.dispose();
    }
}
