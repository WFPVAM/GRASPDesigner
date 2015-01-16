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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTextField;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingConstants;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashSet;
import javax.swing.BorderFactory;
import net.frontlinesms.ui.SimpleConstraints;

/**
 * Simple window to manage surveys
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class SurveysEditor extends JDialog {

	private SurveyEditorController ctrl;
	private JPanel contentPane;

	private JList surveyList;
	private JList elementsList;
	private JTextField txtNewSurvey;
	private JTextArea newElement;

	public static final String ERROR_NO_SURVEY_SELECTED = "plugin.forms.survey.error.noselected";
	public static final String ERROR_SURVEY_USED = "plugin.forms.survey.error.listused";
	public static final String ERROR_SURVEY_USED_INCURRENT = "plugin.forms.survey.error.listused.incurrent";
	public static final String HELP_STRING = "plugin.forms.survey.help";
	public static final String NAME_NOT_VALID = "plugin.forms.survey.error.invalidname";
	public static final String LIST_ALREADY_USED_UNMODIFIABLE = "plugin.forms.survey.error.listused.unmodifiable";
	public static final String SURVEY_LABEL = "plugin.forms.survey.label";
	public static final String SURVEY_EDITLABEL = "plugin.forms.survey.editlabel";
	public static final String SURVEY_LIST = "plugin.forms.survey.list";
	public static final String SURVEY_LISTELEMENTS = "plugin.forms.survey.listelements";
	public static final String SURVEY_ADD = "plugin.forms.survey.add";
	public static final String SURVEY_REMOVE = "plugin.forms.survey.remove";
	public static final String SURVEY_DEFAULT_VALUE = "plugin.forms.survey.defaultvalue";
	public static final String SURVEY_COPY = "plugin.forms.survey.copy";
	public static final String SURVEY_RENAME = "plugin.forms.survey.rename";
	public static final String SURVERY_ENTER_NEWNAME = "plugin.forms.survey.rename.newname";
	public static final String ERROR_NO_DUPLICATES = "plugin.forms.survey.error.duplicatename";

	private JLabel helpLabel;
	private JButton renameListBtn;
	private JButton copyListBtn;
        private JButton addButtonSurvey;
	private JButton removeButtonSurvey;
	private JButton addElementButton;
	private JButton removeElementButton;
	private JButton defaultValueButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SurveysEditor frame = new SurveysEditor(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 * 
	 * @wbp.parser.constructor
	 */
	public SurveysEditor(Frame owner) {
		super(owner, true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setResizable(false);
		JLabel availableSurveyLabel = new JLabel(InternationalisationUtils.getI18nString(SURVEY_LIST));
		availableSurveyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		availableSurveyLabel.setBounds(139, 30, 300, 16);
		contentPane.add(availableSurveyLabel);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(139, 58, 300, 239);
		contentPane.add(scrollPane);
		surveyList = new JList();
		surveyList.setCellRenderer(new SurveyRenderer());
		// surveyList.setCellRenderer(new MyCellRenderer());
		surveyList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showElementsOfSurvey();
			}
		});
		surveyList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				showElementsOfSurvey();
			}
		});
		scrollPane.setViewportView(surveyList);
		JLabel componentsLabel = new JLabel(InternationalisationUtils.getI18nString(SURVEY_LISTELEMENTS));
		componentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		componentsLabel.setBounds(481, 30, 300, 16);
		contentPane.add(componentsLabel);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(481, 58, 300, 239);
		contentPane.add(scrollPane_1);
		elementsList = new JList();
		elementsList.setCellRenderer(new SurveyRenderer());
		scrollPane_1.setViewportView(elementsList);
		removeElementButton = new JButton(InternationalisationUtils.getI18nString(SURVEY_REMOVE));
		removeElementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// remove element
				deleteSelectedElement();
			}
		});
		removeElementButton.setBounds(697, 298, 84, 29);
		contentPane.add(removeElementButton);
		addElementButton = new JButton(InternationalisationUtils.getI18nString(SURVEY_ADD));
		addElementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add element
				// showInsertElementBox();
				insertNewElement();
			}
		});
		addElementButton.setBounds(598, 298, 84, 29);
		contentPane.add(addElementButton);
		addButtonSurvey = new JButton(InternationalisationUtils.getI18nString(SURVEY_ADD));
		addButtonSurvey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add survey
				// showInsertSurveyBox();
				insertNewSurvey();
			}
		});
		addButtonSurvey.setBounds(230, 298, 84, 29);
		contentPane.add(addButtonSurvey);
		defaultValueButton = new JButton(InternationalisationUtils.getI18nString(SURVEY_DEFAULT_VALUE));
		defaultValueButton.setBounds(480, 298, 104, 29);
		defaultValueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opneDefaultValueDialog();
			}
		});
		//defaultValueButton.setEnabled(false);
		contentPane.add(defaultValueButton);
		removeButtonSurvey = new JButton(InternationalisationUtils.getI18nString(SURVEY_REMOVE));
		removeButtonSurvey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// deletes a survey
				deleteSelectedSurvey();
			}
		});
		removeButtonSurvey.setBounds(349, 298, 90, 29);
		contentPane.add(removeButtonSurvey);
		
		
		
		
		
		//JScrollPane scrollNewSurvey = new JScrollPane();
		//scrollNewSurvey.setBounds(139, 339, 300, 25);
		//contentPane.add(scrollNewSurvey);
		txtNewSurvey = new JTextField();
                txtNewSurvey.setBorder(BorderFactory.createLineBorder(Color.gray));
                txtNewSurvey.setBounds(139, 339, 300, 19);
		//txtaNewSurvey.setLineWrap(true);
		//scrollNewSurvey.setViewportView(txtNewSurvey);
		txtNewSurvey.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Enter pressed for survey");
					// insert a survey
					insertNewSurvey();
				}
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					txtNewSurvey.setText("");
					txtNewSurvey.setVisible(true);
				}
			}
		});
		txtNewSurvey.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent arg0) {
				// newSurvey.setText("");
				// newSurvey.setVisible(true);
			}

			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		//txtaNewSurvey.setColumns(10);
//                JPanel pnl = new JPanel();
//                pnl.setBounds(139, 339, 301, 20);
//                pnl.add(txtNewSurvey);
                contentPane.add(txtNewSurvey);
                
		JScrollPane scrollNewElement = new JScrollPane();
		scrollNewElement.setBounds(481, 339, 300, 63);
		contentPane.add(scrollNewElement);

		newElement = new JTextArea();
		scrollNewElement.setViewportView(newElement);
		newElement.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Enter pressed for elements");
					// insert a new element in selected survey
					//insertNewElement();															//---------------------------- disable enter key press
					//newElement.append("\n");
				}
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					// newElement.setText("");
					// newElement.setVisible(true);
				}
			}
		});
		newElement.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent arg0) {
				// newElement.setText("");
				// newElement.setVisible(true);
			}

			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		newElement.setColumns(10);

		helpLabel = new JLabel("\n");
		helpLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		helpLabel.setForeground(Color.GRAY);
		helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		helpLabel.setBounds(139, 428, 642, 30);
		contentPane.add(helpLabel);
		newElement.setVisible(true);
		txtNewSurvey.setVisible(true);

		helpLabel.setText(InternationalisationUtils.getI18nString(HELP_STRING));

		copyListBtn = new JButton(InternationalisationUtils.getI18nString(SURVEY_COPY));
		copyListBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				copySelectedSurvey();
			}
		});
		copyListBtn.setBounds(10, 78, 117, 29);
		contentPane.add(copyListBtn);

		renameListBtn = new JButton(InternationalisationUtils.getI18nString(SURVEY_RENAME));
		renameListBtn.setBounds(10, 111, 117, 29);
		contentPane.add(renameListBtn);
		renameListBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				renameSelectedSurvey();
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosed(WindowEvent e) {
		        SurveysEditor.this.setCloseOperation();
		    }
		});
	}
	
	
	private void setCloseOperation(){
	}

	/**
	 * Constructor with controller parameter
	 * 
	 * @param ctrl
	 *            {@link SurveyEditorController} instance
	 */
	public SurveysEditor(SurveyEditorController ctrl, Frame owner) {
		this(owner);
		this.ctrl = ctrl;
		populateSurveyList();
		// ctrl.test();
	}

	private void showInsertElementBox() {
		if (surveyList.getSelectedIndex() != -1) {
			newElement.setVisible(true);
			newElement.requestFocusInWindow();
		}
	}

	private void showInsertSurveyBox() {
		txtNewSurvey.setVisible(true);
		txtNewSurvey.requestFocusInWindow();
	}

	private void insertNewSurvey() {
                Survey newServeyItem = null;
		if (!txtNewSurvey.getText().trim().equals("")) {
                        //Can add just one survey each time.
			String newSurveyName = txtNewSurvey.getText().trim();
			defaultValueButton.setEnabled(true);
			List<String> allSurveyNames = new ArrayList<String>();
			for (Survey s : ctrl.getAllSurveysForCurrentForm()) {
				allSurveyNames.add(s.getName());
			}
                        List<String> newSurveyNames = new ArrayList<String>();
                        newSurveyNames.add(newSurveyName);
			if (checkForDuplicateValues(newSurveyNames, allSurveyNames)) {
				for (String s : newSurveyNames) {
					newServeyItem = ctrl.addNewSurveyWithName(s.trim());
				}
				txtNewSurvey.setText("");
				// newSurvey.setVisible(false);
				populateSurveyList();
			} else
				return;
		}
                surveyList.setSelectedValue(newServeyItem, true);
	}
	
	
	private void opneDefaultValueDialog(){
		if (surveyList.getSelectedValue() != null && ((Survey)surveyList.getSelectedValue()).getValues().size() != 0 ){
			Survey selectedSurvey = (Survey) surveyList.getSelectedValue();
			DefaultValueDialog dialog = new DefaultValueDialog(ctrl,this,selectedSurvey);
			dialog.setVisible(true);
			dialog.setAlwaysOnTop(true);
			dialog.toFront();
		}
	}
	
	private void deleteSelectedSurvey() {
		int[] indices = surveyList.getSelectedIndices();

		for (int i : indices) {
			Survey sur = (Survey) surveyList.getModel().getElementAt(i);
			List<Form> formsUsingList = null;
			// check for any form with this survey
			if ((formsUsingList = ctrl.checkIfListUsed(sur)).size() != 0) {
				String message = InternationalisationUtils.getI18nString(ERROR_SURVEY_USED, sur.getName()) + "\n";
				for (Form f : formsUsingList) {
					message = message + f.getName() + "\n";
				}
				JOptionPane.showMessageDialog(this, message);
				return;
			}
			// check for any previewcomponent in current editing form with this
			// survey
			HashMap<PreviewComponent, Integer> pcUsingList = null;
			if ((pcUsingList = ctrl.checkIfListUsedInCurrent(sur)).size() != 0) {
				String message = InternationalisationUtils.getI18nString(ERROR_SURVEY_USED_INCURRENT, sur.getName()) + "\n";
				for (PreviewComponent pc : pcUsingList.keySet()) {
					message = message
							+ InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST_POSITION, pc.getComponent().getName(),
									String.valueOf(pcUsingList.get(pc)), pc.getType().toString()) + "\n";
				}
				JOptionPane.showMessageDialog(this, message);
				return;
			}
			ctrl.removeSurvey(sur);
		}
		populateSurveyList();
		showElementsOfSurvey();
	}

	private void deleteSelectedElement() {
		Survey selectedSurvey = (Survey) surveyList.getSelectedValue();
		Object[] selectedElements = elementsList.getSelectedValues();
		HashMap<PreviewComponent, Integer> pcUsingList = null;
		if ((pcUsingList = ctrl.checkIfListUsedInCurrent(selectedSurvey)).size() != 0) {
			String message = InternationalisationUtils.getI18nString(ERROR_SURVEY_USED_INCURRENT, selectedSurvey.getName()) + "\n";
			for (PreviewComponent pc : pcUsingList.keySet()) {
				message = message
						+ InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST_POSITION, pc.getComponent().getName(),
								String.valueOf(pcUsingList.get(pc)), pc.getType().toString()) + "\n";
			}
			JOptionPane.showMessageDialog(this, message);
			return;
		} else {
			for (Object element : selectedElements) {
				this.ctrl.removeElementFromSurvey(selectedSurvey, ((SurveyElement) element).getValue());
			}
		}
		showElementsOfSurvey();
	}

	private void insertNewElement() {
		// String newElement = this.newElement.getText().trim();
		if (this.surveyList.getSelectedValue() == null) {
			JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ERROR_NO_SURVEY_SELECTED));
		}
		Survey selectedSurvey = (Survey) surveyList.getSelectedValue();

		HashMap<PreviewComponent, Integer> pcUsingList = null;
		if ((pcUsingList = ctrl.checkIfListUsedInCurrent(selectedSurvey)).size() != 0) {
			String message = InternationalisationUtils.getI18nString(ERROR_SURVEY_USED_INCURRENT, selectedSurvey.getName()) + "\n";
			for (PreviewComponent pc : pcUsingList.keySet()) {
				message = message
						+ InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST_POSITION, pc.getComponent().getName(),
								String.valueOf(pcUsingList.get(pc)), pc.getType().toString()) + "\n";
			}
			JOptionPane.showMessageDialog(this, message);
			return;
		}

		else {
			if (!newElement.getText().trim().equals("")) {
				List<String> values = splitTextArea(newElement.getText().trim());
				/*
				 * for (String s : values) { char c = s.charAt(0); if ((c >= '0'
				 * && c <= '9') || c == '#' || c == '@' || c == '#' || c == '%'
				 * || c == '$' || c == '.' || c == '-' || c == '_') {
				 * JOptionPane.showMessageDialog(this,
				 * InternationalisationUtils.getI18nString(NAME_NOT_VALID));
				 * return; } }
				 */
				List<String> allElements = new ArrayList<String>();
				if(selectedSurvey != null){
					for (SurveyElement el : selectedSurvey.getValues()) {
						allElements.add(el.getValue());
					}
					if (checkForDuplicateValues(values, allElements)) {
						for (String s : values) {
							this.ctrl.addElementInSurvey(selectedSurvey, s.trim());
						}
						this.newElement.setText("");
					}
				}
			}

		} /*
		 * else { JOptionPane.showMessageDialog(this,
		 * InternationalisationUtils.getI18nString
		 * (LIST_ALREADY_USED_UNMODIFIABLE).replace("\\n",
		 * System.getProperty("line.separator"))); }
		 */
		// this.newElement.setVisible(false);
		showElementsOfSurvey();
	}

	private void populateSurveyList() {
		DefaultListModel listModel = new DefaultListModel();
		// System.out.println(ctrl.getAllSurveys().size());
		List<Survey> surveys = ctrl.getAllSurveys();
		Collections.sort(surveys);
		for (Survey s : surveys) {
			listModel.addElement(s);
		}
		this.surveyList.setModel(listModel);
		surveyList.validate();
	}

	private void showElementsOfSurvey() {
		Survey selected = (Survey) surveyList.getSelectedValue();
		if (selected != null) {
			if (ctrl.getOwnerForm() == null && selected.getOwner() != null) {
				addElementButton.setEnabled(false);
				removeElementButton.setEnabled(false);
				removeButtonSurvey.setEnabled(false);
				renameListBtn.setEnabled(false);
				newElement.setEnabled(false);
			} else if (ctrl.getOwnerForm() != null && selected.getOwner() != null) {
				if (!selected.getOwner().getId_flsmsId().equals(ctrl.getOwnerForm().getId_flsmsId())) {
					removeButtonSurvey.setEnabled(false);
					addElementButton.setEnabled(false);
					removeElementButton.setEnabled(false);
					renameListBtn.setEnabled(false);
					newElement.setEnabled(false);
				} else {
					removeButtonSurvey.setEnabled(true);
					addElementButton.setEnabled(true);
					removeElementButton.setEnabled(true);
					renameListBtn.setEnabled(true);
					newElement.setEnabled(true);
				}
			} else {
				removeButtonSurvey.setEnabled(true);
				addElementButton.setEnabled(true);
				removeElementButton.setEnabled(true);
				renameListBtn.setEnabled(true);
				newElement.setEnabled(true);
			}

			DefaultListModel listModel = new DefaultListModel();
			for (SurveyElement element : selected.getValues()) {
				listModel.addElement(element);
			}
			this.elementsList.setModel(listModel);
			elementsList.validate();
		} else {
			// if selected is null empty the list of elements
			emptyElementsList();
		}
	}

	private void emptyElementsList() {
		System.out.println("empty elements list");
		this.elementsList.setModel(new DefaultListModel());
		this.elementsList.validate();
	}

	private List<String> splitTextArea(String text) {
		List<String> values = new ArrayList<String>();
		CharSequence[] delimeters = {"\r\n","\n"}; //Deleted comma ","
		int j = 0;
		for(int i = 0 ; i < delimeters.length ; i++){
			if(text.contains(delimeters[i])){
				j = i;
				break;
			}
		}
		StringTokenizer tokenizer = new StringTokenizer(text.trim(), delimeters[j].toString());
		if (tokenizer.hasMoreTokens() == false) {
			values.add(text.trim());
		} else {
			while (tokenizer.hasMoreTokens()) {
				values.add(tokenizer.nextToken());
			}
		}
		return values;

	}

	private void copySelectedSurvey() {
		Survey newSurvey = null;
                Object[] selectedValues = surveyList.getSelectedValues();
		if (selectedValues.length != 0) {
			if (selectedValues.length == 1) {
				String name = JOptionPane.showInputDialog(this, InternationalisationUtils.getI18nString(SURVEY_RENAME));
				if (name != null) {
					try {
						newSurvey = ctrl.copySurveyWithName((Survey) surveyList.getSelectedValue(), name);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ERROR_NO_DUPLICATES));
						return;
					}
				}
			} else {
				for (Object obj : selectedValues) {
					// Survey selected = (Survey) surveyList.getSelectedValue();
					Survey selected = (Survey) obj;
					if (selected != null) {
						newSurvey = ctrl.copySurvey(selected);
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ERROR_NO_SURVEY_SELECTED));
		}
		populateSurveyList();
                surveyList.setSelectedValue(newSurvey, true);
	}

	private void renameSelectedSurvey() {
		String newName = JOptionPane.showInputDialog(this, InternationalisationUtils.getI18nString(SURVERY_ENTER_NEWNAME));
		if (newName == null)
			return;
		Survey selected = (Survey) surveyList.getSelectedValue();
		for (Survey s : ctrl.getAllSurveysForCurrentForm()) {
			if (s.getName().equals(newName)) {
				JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ERROR_NO_DUPLICATES));
				return;
			}
		}
		// selected.setName(newName);
		// ctrl.updateSurvey(selected);
		ctrl.renameSurvey(selected, newName);
		this.repaint();
	}

	private boolean isListUsed(Survey survey) {
		return false;
	}

	/**
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 *         class modeling a jlist row that can be greyed out
	 */
	class SurveyRenderer extends DefaultListCellRenderer {

		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		@Override
		public Component getListCellRendererComponent(JList arg0, Object arg1, int index, boolean isSelected, boolean hasFocus) {

			JLabel line = (JLabel) defaultRenderer.getListCellRendererComponent(arg0, arg1, index, isSelected, hasFocus);
			Survey sur = null;
			if (arg1 instanceof Survey)
				sur = (Survey) arg1;
			else if (arg1 instanceof SurveyElement)
				sur = (Survey) surveyList.getSelectedValue();
			if (sur != null && ctrl.getOwnerForm() == null && sur.getOwner() != null) {
				line.setEnabled(false);
			} else if (sur != null && ctrl.getOwnerForm() != null && sur.getOwner() != null) {

				if (sur != null && !sur.getOwner().getId_flsmsId().equals(ctrl.getOwnerForm().getId_flsmsId())) {
					line.setEnabled(false);
				}
			}
			return line;
		}

	}

	private boolean checkForDuplicateValues(List<String> newValues, List<String> allValues) {
		boolean error = false;
		for (String value : newValues) {
			if (allValues.contains(value)) {
				error = true;
			}
		}
		if (error) {
			JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ERROR_NO_DUPLICATES));
			return false;
		} else
			return true;

	}
        
//        private void setSelectedSurveyItem()
//        {
//            ctrl.setCurrentSelectedSurvey((Survey) surveyList.getSelectedValue());
//        }
        
        public JList getSurveyJList()
        {
            return surveyList;
        }
}
