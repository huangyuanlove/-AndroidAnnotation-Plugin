package com.huangyuanlove.plugin;

import com.huangyuanlove.plugin.ui.ElementBean;
import com.huangyuanlove.plugin.util.NotificationUtil;
import com.huangyuanlove.plugin.util.Utils;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;

public class InjectWriter extends WriteCommandAction.Simple {

    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected ArrayList<ElementBean> mElements;
    protected PsiElementFactory mFactory;
    //true:则生成@BindView(id = R.id.xxx)
    //false:@BindView(idStr = "xxx")
    protected boolean generateId;
    public InjectWriter(PsiFile file, PsiClass clazz, String command, ArrayList<ElementBean> elements, boolean generateId) {
        super(clazz.getProject(), command);

        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mElements = elements;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
        this.generateId = generateId;

    }

    @Override
    public void run() throws Throwable {

        if (Utils.getInjectCount(mElements) > 0) {
            generateFields();
        }
        if (Utils.getClickCount(mElements) > 0) {
            generateClick();
        }
        NotificationUtil.showInfoNotification(mProject, String.valueOf(Utils.getInjectCount(mElements)) + " injections and " + String.valueOf(Utils.getClickCount(mElements)) + " onClick added to " + mFile.getName());


        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }


    private void generateClick() {
        for (ElementBean element : mElements) {
            if (element.isClick) {
                StringBuilder method = new StringBuilder();
                method.append("@ClickResponder(" + element.getGenerateValue(generateId) + ")");
                method.append("public void onClick" + Utils.capitalize(element.fieldName) + " (View v) {}");
                mClass.add(mFactory.createMethodFromText(method.toString(), mClass));
            }
        }
    }



    /**
     * Create fields for injections inside main class
     */
    protected void generateFields() {
        // add injections into main class
        for (ElementBean element : mElements) {
            if (!element.used) {
                continue;
            }

            StringBuilder injection = new StringBuilder();
            injection.append("@BindView");
            injection.append('(');
            injection.append(element.getGenerateValue(generateId));
            injection.append(") ");
            if (element.nameFull != null && element.nameFull.length() > 0) { // custom package+class
                injection.append(element.nameFull);
            } else if (Constant.paths.containsKey(element.name)) { // listed class
                injection.append(Constant.paths.get(element.name));
            } else { // android.widget
                injection.append("android.widget.");
                injection.append(element.name);
            }
            injection.append(" ");
            injection.append(element.fieldName);
            injection.append(";");

            mClass.add(mFactory.createFieldFromText(injection.toString(), mClass));
        }
    }

}