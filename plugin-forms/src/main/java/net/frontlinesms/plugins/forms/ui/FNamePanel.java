/*
 *GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
 * Copyright © 2012 ,Fabaris s.r.l
 * This file is part of GRASP Designer Tool.  
 *  GRASP Designer Tool is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.  
 *  GRASP Designer Tool is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with GRASP Designer Tool. 
 *  If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.forms.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.frontlinesms.data.repository.UserDao;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldAndBinding;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.repository.SurveyDao;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;

/**
 * Class to represent dialog box to receive name of a copied Form
 *
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>
 */
public class FNamePanel extends JDialog {

    private static final long serialVersionUID = 1L;
    String text = null;
    JTextField nameField = new JTextField(15);
    int counter = 0;
    Form clone;
    Form form;
    boolean check = false;
    Frame owner;
    DrawingPanel pnDrawing;
    FormsEditorDialog mainFrame;

    public FNamePanel(Frame owner) {
        super(owner, true);
    }

    public void assignName(final List<String> lista, final Form selected, final FormsThinletTabController ft) {

        JButton okbtn;
        okbtn = new JButton("OK");
        JPanel buttonPanel = new JPanel();
        JLabel jl = new JLabel("Please enter a name for the form : ");
        buttonPanel.add(jl);
        buttonPanel.add(nameField);
        buttonPanel.add(okbtn);

        add(buttonPanel);
        nameField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    text = nameField.getText().trim();

                    for (int j = 0; j < lista.size(); ++j) {
                        String sname = lista.get(j);
                        if (sname.equalsIgnoreCase(text)) {
                            counter++;
                        }
                    }

                    if (counter == 0) {
                        check = false;
                    } else {
                        check = true;
                    }
                    if (!check) {

                        setName(text, selected, ft);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(owner, "Form name already exists", "Error", JOptionPane.WARNING_MESSAGE);
                        FormsEditorDialog.createAndShowGUI(lista, selected, ft);
                    }

                    dispose();
                }

            }

            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }
        });

        okbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                text = nameField.getText().trim();

                for (int j = 0; j < lista.size(); ++j) {
                    String sname = lista.get(j);
                    if (sname.equalsIgnoreCase(text)) {
                        counter++;

                    }
                }

                if (counter == 0) {
                    check = false;
                } else {
                    check = true;
                }
                if (!check) {

                   setName(text, selected, ft);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(owner, "Form name already exists", "Error", JOptionPane.WARNING_MESSAGE);
                    dispose();
                    FormsEditorDialog.createAndShowGUI(lista, selected, ft);

                }
                dispose();
               
            }

        });

    }// Method to create and copy clone of a Form

    public void setName(String text, Form selected, FormsThinletTabController ft) {

        HashMap<FormField, FormField> oldCompsTOnewComps = new HashMap<FormField, FormField>();
        assert (selected != null) : "Duplicate Form button should not be enabled if there is no form selected!";
        clone = new Form(text);

        SurveyDao surveyDao = FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao();
        Set<Survey> surveyList = new HashSet<Survey>();
        for (FormField oldField : selected.getFields()) {

            FormField newField = new FormField(oldField.getType(), oldField.getLabel(), oldField.getX_form(), oldField.getName(), oldField.getRequired());
            newField.setCalculated(oldField.getCalculated());
            // Fabaris_a.aknai cloning formula
            newField.setFormula(oldField.getFormula());
            newField.setReadOnly(oldField.isReadOnly());
            newField.setNumberOfRep(oldField.getNumberOfRep());

            // Map to trace new formfields ownership
            oldCompsTOnewComps.put(oldField, newField);

            if (oldField.getType() == FormFieldType.DROP_DOWN_LIST || oldField.getType() == FormFieldType.RADIO_BUTTON) {
                Survey newSurvey = oldField.getSurvey().clone();
                newField.setSurvey(newSurvey);
                //surveyDao.saveSurvey(newSurvey);
                surveyList.add(newSurvey);
                for (Survey sur : surveyList) {
                    if (newSurvey.getName().equals(sur.getName())) {
                        newField.setSurvey(sur);
                    }
                }
            }
            if (oldField.getType() == FormFieldType.REPEATABLES || oldField.getType() == FormFieldType.REPEATABLES_BASIC) {
                newField.setSurvey(oldField.getSurvey());
                for (FormField rep : oldField.getRepetables()) {
                    FormField newRep = new FormField(rep.getType(), rep.getLabel(), rep.getX_form(), rep.getName(), rep.getRequired());
                    newRep.setCalculated(rep.getCalculated());
                    // Fabaris_a.aknai cloning formula
                    newRep.setFormula(rep.getFormula());
                    newRep.setReadOnly(rep.isReadOnly());

                    if (rep.getSurvey() != null) {
                        Survey newSurvey = rep.getSurvey().clone();
                        newRep.setSurvey(newSurvey);
                        //surveyDao.saveSurvey(newSurvey);
                        surveyList.add(newSurvey);
                        for (Survey sur : surveyList) {
                            if (newSurvey.getName().equals(sur.getName())) {
                                newRep.setSurvey(sur);
                            }
                        }
                    }

                    // copy costraints
                    for (ConstraintContainer cont : rep.getConstraints()) {
                        newRep.addConstraint(cont.clone());
                    }

                    newRep.setBindingsPolicy(rep.getBindingsPolicy());
                    newRep.setConstraintPolicy(rep.getConstraintPolicy());
                    newField.addRepetable(newRep);

                    // Map to trace new formfields ownership
                    oldCompsTOnewComps.put(rep, newRep);
                }
                if (oldField.getSurvey() != null) {
                    Survey newSurvey = oldField.getSurvey().clone();
                    newField.setSurvey(newSurvey);
                    //surveyDao.saveSurvey(newSurvey);
                    surveyList.add(newSurvey);
                    for (Survey sur : surveyList) {
                        if (newSurvey.getName().equals(sur.getName())) {
                            newField.setSurvey(sur);
                        }
                    }
                }
            }
            clone.addField(newField, oldField.getPositionIndex());
            // copy costraints
            for (ConstraintContainer cont : oldField.getConstraints()) {
                newField.addConstraint(cont.clone());
            }

            newField.setConstraintPolicy(oldField.getConstraintPolicy());
            newField.setBindingsPolicy(oldField.getBindingsPolicy());

        }

        // cycle again for bindings
        for (FormField oldField : selected.getFields()) {
            for (FormFieldAndBinding fb : oldField.getBindingCouples()) {
                oldCompsTOnewComps.get(oldField).addBinding(oldCompsTOnewComps.get(fb.getfField()), fb.getbContainer().clone());
            }
            if (oldField.getType() == FormFieldType.REPEATABLES || oldField.getType() == FormFieldType.REPEATABLES_BASIC) {
                for (FormField oldRep : oldField.getRepetables()) {
                    for (FormFieldAndBinding fb : oldRep.getBindingCouples()) {
                        oldCompsTOnewComps.get(oldRep).addBinding(oldCompsTOnewComps.get(fb.getfField()), fb.getbContainer().clone());
                    }
                }
            }

        }

        for (Survey sur : surveyList) {
            surveyDao.saveSurvey(sur);
        }

        clone.setBindingsPolicy(selected.getBindingsPolicy());
        clone.setDesignerVersion(selected.getDesignerVersion());

        ft.formsDao.saveForm(clone);
        UserDao user = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
       // String idMacchina = user.getAdminFrontlineSMS_ID();
        String fsms_id =UUID.randomUUID().toString();
        clone.setId_flsmsId(clone.getId() + "_" + fsms_id);
        VisualForm vForm = VisualForm.getVisualForm(clone);
        ft.updateForm(vForm.getComponents(), vForm.getComponents(), clone, vForm);

        for (Survey sur : surveyList) {
            sur.setOwner(clone);
            surveyDao.updateSurvey(sur);
        }

        ft.refresh();
    }

}
