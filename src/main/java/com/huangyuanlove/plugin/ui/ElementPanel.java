package com.huangyuanlove.plugin.ui;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class ElementPanel extends JPanel {

    protected ElementListPanel mParent;
    protected ElementBean mElement;
    protected ArrayList<String> mGeneratedIDs;
    protected OnCheckBoxStateChangedListener mListener;
    // ui
    protected JLabel elementTypeJLabel;
    protected JCheckBox viewBindJCheckBox;
    protected JLabel elementIDJTextArea;
    protected JCheckBox clickResponderJCheckBox;
    protected JTextField mName;
    protected Color mNameDefaultColor;
    protected JBColor mNameErrorColor = new JBColor(new Color(0xFF0000), new Color(0xFF0000));

    public JCheckBox getViewBindJCheckBox() {
        return viewBindJCheckBox;
    }
    public JCheckBox getClickResponderJCheckBox(){
        return clickResponderJCheckBox;
    }

    public void setListener(final OnCheckBoxStateChangedListener onStateChangedListener) {
        this.mListener = onStateChangedListener;
    }

    public ElementPanel(ElementListPanel parent, ElementBean element, ArrayList<String> ids) {
        mElement = element;
        mParent = parent;
        mGeneratedIDs = ids;

        viewBindJCheckBox = new JCheckBox("BindView");
        if (!mGeneratedIDs.contains(element.getFullID())) {
            viewBindJCheckBox.setSelected(mElement.used);
        } else {
            viewBindJCheckBox.setSelected(false);
        }



        clickResponderJCheckBox = new JCheckBox("ClickResponder");

        elementTypeJLabel = new JLabel(mElement.name);
        elementTypeJLabel.setPreferredSize(new Dimension(150, 26));

        elementIDJTextArea = new JLabel(mElement.id);
        elementIDJTextArea.setPreferredSize(new Dimension(150, 0));

        mName = new JTextField(mElement.fieldName);
        mNameDefaultColor = mName.getBackground();
        mName.setPreferredSize(new Dimension(100, 26));
        mName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // empty
            }

            @Override
            public void focusLost(FocusEvent e) {
                syncElement();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 54));

        add(Box.createRigidArea(new Dimension(10, 0)));
        add(elementTypeJLabel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(elementIDJTextArea);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(viewBindJCheckBox);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(clickResponderJCheckBox);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mName);
        add(Box.createHorizontalGlue());

    }

    public void syncElement() {
        mElement.used = viewBindJCheckBox.isSelected();
        mElement.isClick = clickResponderJCheckBox.isSelected();
        mElement.fieldName = mName.getText();

        if (mElement.checkValidity()) {
            mName.setBackground(mNameDefaultColor);
        } else {
            mName.setBackground(mNameErrorColor);
        }

    }

    public class CheckListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            if (mListener != null) {
                mListener.changeState(viewBindJCheckBox.isSelected());
            }
        }
    }
}