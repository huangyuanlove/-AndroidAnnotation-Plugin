package com.huangyuanlove.plugin.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class ElementListPanel extends JPanel {
    protected Project mProject;
    protected Editor mEditor;
    protected ArrayList<ElementBean> mElements = new ArrayList<>();
    protected ArrayList<String> mGeneratedIDs = new ArrayList<>();
    protected ArrayList<ElementPanel> mEntries = new ArrayList<>();
    protected ShowInjectDialogInterface showInjectDialogListener;
    protected JCheckBox generateIdCheckBox;
    protected JCheckBox selectAllBindViewJCheckBox;
    protected JCheckBox selectAllClickResponderJCheckBox;
    protected JButton mConfirm;
    protected JButton mCancel;

    private final OnCheckBoxStateChangedListener selectAllViewBindListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            for (final ElementPanel entry : mEntries) {
                entry.getViewBindJCheckBox().setSelected(checked);
            }
        }
    };
    private final OnCheckBoxStateChangedListener selectAllClickResponderListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            for (final ElementPanel entry : mEntries) {
                entry.getClickResponderJCheckBox().setSelected(checked);
            }
        }
    };

    public ElementListPanel(Project project, Editor editor, ArrayList<ElementBean> elements, ShowInjectDialogInterface showInjectDialogListener) {
        mProject = project;
        mEditor = editor;
        this.showInjectDialogListener = showInjectDialogListener;
        if (elements != null) {
            mElements.addAll(elements);
        }


        setPreferredSize(new Dimension(640, 360));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addInjections();
        addButtons();
    }

    protected void addInjections() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        int cnt = 0;
        for (ElementBean element : mElements) {
            ElementPanel entry = new ElementPanel(this, element, mGeneratedIDs);


            if (cnt > 0) {
                injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            injectionsPanel.add(entry);
            cnt++;

            mEntries.add(entry);

        }
        injectionsPanel.add(Box.createVerticalGlue());
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
        contentPanel.add(scrollPane);

        add(contentPanel, BorderLayout.CENTER);
        refresh();
    }

    protected void addButtons() {


        JPanel holderPanel = new JPanel();
        holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        holderPanel.add(Box.createHorizontalGlue());
        add(holderPanel, BorderLayout.PAGE_END);

        generateIdCheckBox = new JCheckBox("default @BindView(idStr = \"xxxx\"), for checked @BindView(id = R.id.xxx)");
        generateIdCheckBox.setSelected(false);
        generateIdCheckBox.setForeground(JBColor.RED);
        add(generateIdCheckBox, BorderLayout.PAGE_END);

        selectAllBindViewJCheckBox = new JCheckBox("Select all BindView");
        selectAllBindViewJCheckBox.setSelected(true);
        selectAllBindViewJCheckBox.setForeground(JBColor.ORANGE);
        selectAllBindViewJCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(selectAllViewBindListener !=null){
                    selectAllViewBindListener.changeState(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        add(selectAllBindViewJCheckBox, BorderLayout.PAGE_END);

        selectAllClickResponderJCheckBox = new JCheckBox("Select all ClickResponder");
        selectAllClickResponderJCheckBox.setForeground(JBColor.magenta);
        selectAllClickResponderJCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(selectAllClickResponderListener!=null){
                    selectAllClickResponderListener.changeState(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        add(selectAllClickResponderJCheckBox, BorderLayout.PAGE_END);

        mCancel = new JButton();
        mCancel.setAction(new CancelAction());
        mCancel.setPreferredSize(new Dimension(120, 26));
        mCancel.setText("Cancel");
        mCancel.setVisible(true);

        mConfirm = new JButton();
        mConfirm.setAction(new ConfirmAction());
        mConfirm.setPreferredSize(new Dimension(120, 26));
        mConfirm.setText("Confirm");
        mConfirm.setVisible(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(mCancel);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(mConfirm);

        add(buttonPanel, BorderLayout.PAGE_END);
        refresh();
    }

    protected void refresh() {
        revalidate();

        if (mConfirm != null) {
            mConfirm.setVisible(mElements.size() > 0);
        }
    }

    protected boolean checkValidity() {
        boolean valid = true;

        for (ElementBean element : mElements) {
            if (!element.checkValidity()) {
                valid = false;
            }
        }

        return valid;
    }

    public JButton getConfirmButton() {
        return mConfirm;
    }

    protected class ConfirmAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            boolean valid = checkValidity();

            for (ElementPanel entry : mEntries) {
                entry.syncElement();
            }

            if (valid) {
                if (showInjectDialogListener != null) {
                    showInjectDialogListener.onConfirm(mProject, mEditor, mElements, generateIdCheckBox.isSelected());
                }
            }
        }
    }

    protected class CancelAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            if (showInjectDialogListener != null) {
                showInjectDialogListener.onCancel();
            }
        }
    }
}
