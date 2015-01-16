/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
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

import static net.frontlinesms.FrontlineSMSConstants.ACTION_CANCEL;
import static net.frontlinesms.FrontlineSMSConstants.ACTION_SAVE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.repository.UserDao;
import net.frontlinesms.plugins.forms.contraints.tables.ui.ConstraintTable;
import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.Configuration;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.helper.PanelHelper;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.Separator;
import net.frontlinesms.plugins.forms.ui.components.TextArea;
import net.frontlinesms.plugins.forms.ui.components.TextField;
import net.frontlinesms.plugins.forms.ui.components.TruncatedText;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.plugins.forms.ui.components.WrappedText;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.SimpleConstraints;
import net.frontlinesms.ui.SimpleLayout;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

//import net.frontlinesms.plugins.forms.ui.PropertiesTable.FrontlineFormsCellRenderer;

/**
 * This class represents the main frame of the program.
 * 
 * @author Carlos Eduardo Genz <li>kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph,Mureed Al-Barghouthi
 *         www.fabaris.it <http://www.fabaris.it/>
 */

public class FormsEditorDialog extends JDialog {
        private SurveyEditorController surveyCtrl;
	private static final String separator = "Separator";
	private static final String multiLine = "MultiLine";
	private static final String singleLine = "SingleLine";
	private static int count = 0;
	private static final long serialVersionUID = 2071317022144454655L;
	private PropertiesTable propertiesTable;
	private DrawingPanel pnDrawing;

	// bindings (relevant) stuff
	private BindingTable bindingTable;
	private JScrollPane bindingScrollPane;
	private JComboBox policyComboBox;
	private ButtonGroup bindingPolicyGroup;
	private JRadioButton bindingPolicyRadio1;
	private JRadioButton bindingPolicyRadio2;
	private JButton removeBind;
	private JLabel policyLabel;
	JLabel surveyLabel;
	JButton surveyButton;

	// constraint stuff
	private ConstraintTable constrainTable;
	private JScrollPane constrainScrollPane;
	private JButton addConstraint;
	private JButton removeConstraint;
	private JButton addMessage;
	private JComboBox constraintPolicyComboBox;
	private JRadioButton constraintPolicyRadio1;
	private JRadioButton constraintPolicyRadio2;
	private ButtonGroup constraintPolicyGroup;
	private JLabel constraintPolicyLabel;

	//private AutoSaveThread autoSaveThread = null;
	// Fabaris_a.zanchi combo box for surveys
	private JComboBox surveyComboBox;
	private JPanel surveyPanel;
	private JPanel surveyButtonPanel;

	// Fabaris_a.zanchi
	private JCheckBox infinityRepCheck;
	private JTextField repNumbers;
	private JPanel basicRepPanel;

	private VisualForm current;
	public JTextField tfFormName;
	private JComboBox reqComboBox;

	// Fabaris_a.aknai
	private JCheckBox calcolatedChk;
	// Fabaris_a.aknai
	private JLabel formulaLbl;
	private JTextField formulaTf;
	private JPanel formulaPanel;
	// Fabaris_a.aknai
	private JScrollPane propertiesTablePanel;
	private FormulaEditorPanel formulaEditorPanel;
	// Fabaris_raji
	private JPanel newPanelVisibility;
	private JPanel newPanelValidity;
	private JPanel newPanelProperties;
	private JPanel constrainScrollPanel;
	TitledBorder constraintsTitleBorder;
	TitledBorder titledBorderBindings;
	private JLabel isCalculated = new JLabel("Is calculated");
	// Fabaris_A.zanchi temporary reference to previously selected survey
	Survey previousSurvey;

	// Fabaris_a.zanchi reference to owner frame
	Frame ownerFrame;
	public FormDao formDao;
	static Frame owner;

	
	private JLabel requiredLabel = new JLabel();
	private int time;
	//private AutoSaveThread autoSave = null;
	private static final String COMMON_PROPERTIES = "plugins.forms.properties";
	private static final String COMMON_BINDINGS = "plugins.forms.bindings";
	private static final String COMMON_CONSTRAINTS = "plugins.forms.constraints";
	private static final String BINDINGS_ADD = "plugins.forms.bindings.add";
	private static final String BINDINGS_REMOVE = "plugins.forms.bindings.remove";
	private static final String BINDING_ADD_MESSAGE = "plugin.forms.binding.addMessage";
	// private static final String COMMON_REPEATABLES =
	// "plugins.forms.repeatables";

	// Fabaris_a.zanchi strings for reference list change checks
	private static final String ALERT_COMPONENT_WITHSURVEY_INBINDING_MESSAGE = "plugin.forms.alert.component.withsurvey.inbinding.message";
	private static final String ALERT_COMPONENT_WITHSURVEY_INBINDING_LIST = "plugin.forms.alert.component.withsurvey.inbinding.list";
	private static final String ALERT_COMPONENT_WITHSURVEY_INBINDING_CONTINUE = "plugin.forms.alert.component.withsurvey.inbinding.continue";
	
        //Whether the SurveyListEditor is just opened. Uses to reselect the Survey List to the last selected item from SurveyListEditor.
        private boolean isSurveyListEditorOpened; 
	//private AutoSaveThread autoSaveThread = null;
	
	@SuppressWarnings("deprecation")
	public FormsEditorDialog(Frame owner) {
		
		/*
		 * Fabaris_a.zanchi modified the boolean of the super creator with false
		 * to allow the survey window to appear on top
		 */
		super(
				owner,
				"GRASP-Designer Tool "
						+ InternationalisationUtils
								.getI18nString(FormsThinletTabController.I18N_KEY_FORMS_EDITOR),
				true);
		// Fabaris_A.zanchi mantain reference to owner frame to open survey
		// dialog
		this.ownerFrame = owner;
                setIsSurveyListEditorOpened(true);
		FormsEditorDialog.count = 0;
		Border etched = (Border) BorderFactory.createEtchedBorder();
		propertiesTable = new PropertiesTable();
		// Make the content scrollable
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		JScrollPane scrollPane = new JScrollPane();
		topPanel.add(scrollPane, BorderLayout.CENTER);
		Container scrollContent = scrollPane.getViewport();
		JPanel scrollContentPanel = new JPanel();
		// scrollContent.add(scrollContentPanel, new SimpleConstraints(0, 0,
		// 800, 550));//commented by Fabaris_Raji

		scrollContent.add(scrollContentPanel, new SimpleConstraints(0, 0, 800,
				600)); // window
						// size(palette+preview)
						// 800,600

		Container innerContentPane = scrollContentPanel;
		SimpleLayout contentPaneLayout = new SimpleLayout();
		innerContentPane.setLayout(contentPaneLayout);

		// Fabaris_a.zanchi crea spazio per la tavola dei vincoli: la tabella
		// deve
		// essere in un pannello per utilizzare il DnD
		this.bindingTable = new BindingTable();
		JPanel bindingScrollPanel = new JPanel();
		bindingScrollPanel.setLayout(new BorderLayout());
		bindingScrollPane = new JScrollPane(this.bindingTable);

		// setto un border layout per il pannello diverso da quello di default
		// altrimenti la tabella non si vede correttamente
		// TitledBorder titledBorderBindings = new
		// TitledBorder(InternationalisationUtils.getI18nString(COMMON_BINDINGS));
		titledBorderBindings = new TitledBorder(
				InternationalisationUtils.getI18nString(COMMON_BINDINGS));// Fabaris_raji
		titledBorderBindings.setTitleFont(FrontlineUI.currentResourceBundle
				.getFont());
		bindingScrollPane.setBorder(titledBorderBindings);
		bindingScrollPanel.add(bindingScrollPane);
		// Fabaris_a.zanchi crea pulsante per rimuovere vincolo
		removeBind = new JButton(
				InternationalisationUtils.getI18nString(BINDINGS_REMOVE));

		pnDrawing = new DrawingPanel();

		// Fabaris_a.zanchi aggiunge il pannello della bindTable al DrawingPanel
		pnDrawing.addBindingPanel(bindingScrollPanel);

		// innerContentPane.add(pnDrawing, new SimpleConstraints(0, 0, 500,
		// 480)); //commented by Fabaris_Raji

		// innerContentPane.add(pnDrawing, new SimpleConstraints(0, 0,649,615));
		// // Background preview+palette
		innerContentPane.add(pnDrawing, new SimpleConstraints(0, 0, 649, 673)); // Background
																				// preview+palette

		innerContentPane.add(pnDrawing, new SimpleConstraints(0, 0, 649, 700)); // Background
		
		pnDrawing.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					
				}
				int checkA=1;
				checkA=checkA;
			}
		});

		// preview+palette
		propertiesTablePanel = new JScrollPane(propertiesTable);
		// Fabaris_a.zanchi aggiunge listener per azione di rimozione
		removeBind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// System.out.println("Pulsante Premuto");
				pnDrawing.removeSelectedBinding();
			}
		});
		// Fabaris_a.zanchi creates dropbox to select and-or policy
		policyLabel = new JLabel(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.BINDINGS_POLICY_LABEL));
		policyComboBox = new JComboBox(new Object[] { "All", "Any" });
		policyComboBox.setSelectedIndex(0);
		policyComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				getSelectedComponent().setBindingsPolicy(
						(String) policyComboBox.getSelectedItem());

			}
		});

		bindingPolicyRadio1 = new JRadioButton(FormField.AND_POLICY);
		bindingPolicyRadio1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println("All clicked");
				if (getSelectedComponent() != null)
					getSelectedComponent().setBindingsPolicy(
							FormField.AND_POLICY);
			}
		});
		bindingPolicyRadio2 = new JRadioButton(FormField.OR_POLICY);
		bindingPolicyRadio2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Any clicked");
				if (getSelectedComponent() != null)
					getSelectedComponent().setBindingsPolicy(
							FormField.OR_POLICY);
			}
		});
		bindingPolicyGroup = new ButtonGroup();
		bindingPolicyGroup.add(bindingPolicyRadio1);
		bindingPolicyGroup.add(bindingPolicyRadio2);

		propertiesTablePanel.setFont(FrontlineUI.currentResourceBundle
				.getFont());
		// We have to set the correct font for some languages
		TitledBorder titledBorder = new TitledBorder(
				InternationalisationUtils.getI18nString(COMMON_PROPERTIES));
		titledBorder.setTitleFont(FrontlineUI.currentResourceBundle.getFont());

		propertiesTablePanel.setBorder(titledBorder);

		// Fabaris_raji added new container for Properties Table,Required combo
		// box,survey panel and formula panel
		newPanelProperties = new JPanel(); // NewPanel for Properties section
		newPanelProperties.setLayout(contentPaneLayout);

		newPanelProperties.setOpaque(true);
		newPanelProperties.setBorder(etched);
		// innerContentPane.add(newPanel,new SimpleConstraints(550, 0, 360,
		// 118));
		newPanelProperties.add(propertiesTablePanel, new SimpleConstraints(5,
				0, 350, 118)); // 350
		// innerContentPane.add(propertiesTablePanel, new SimpleConstraints(550,
		// 0, 360, 118));
		// added by Fabaris_Raji size reduced by a.aknai - properties table
		// Fabaris_raji
		/*
		 * Added by Fabaris_a.aknai add formula editor
		 */

		formulaEditorPanel = new FormulaEditorPanel();
		formulaEditorPanel.setOpaque(true);
		// formulaEditorPanel.setBounds(550, 0, 360, 600);
		formulaEditorPanel.setBounds(650, 0, 360, 600);// Fabaris_raji modified
														// x value
		formulaEditorPanel.setVisible(false);

		innerContentPane.add(formulaEditorPanel);
		innerContentPane.setComponentZOrder(formulaEditorPanel, 0);
		// newPanelProperties.add(formulaEditorPanel,new SimpleConstraints(5, 0,
		// 350, 600));//Fabaris_raji
		// newPanelProperties.setComponentZOrder(formulaEditorPanel, 0);

		innerContentPane.add(formulaEditorPanel);
		innerContentPane.setComponentZOrder(formulaEditorPanel, 0);

		/*
		 * following code added by fabaris #a.aknai
		 */

		// add formula under properties table
		formulaPanel = new JPanel();
		formulaPanel
				.setLayout(new BoxLayout(formulaPanel, BoxLayout.LINE_AXIS));
		formulaLbl = new JLabel("Formula:");
		formulaTf = new JTextField();
		formulaTf.setEditable(false);
		formulaTf.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					PreviewComponent c = getSelectedComponent();
					if (c != null) {
						formulaEditorPanel.forComponent = c;
						if (c != null
								&& !c.isDefaultComponent()
								&& calcolatedChk.isSelected()
								&& (c.getType() == FormFieldType.CURRENCY_FIELD
										|| c.getType() == FormFieldType.NUMERIC_TEXT_FIELD
										|| c.getType() == FormFieldType.TEXT_FIELD || c
										.getType() == FormFieldType.TEXT_AREA)) {
							formulaEditorPanel.textArea
									.setText(c.getFormula() != null ? c
											.getFormula().toFormulaString()
											: "");
							showFormulaEditor();
						}
					}
				}
			}
		});
		calcolatedChk = new JCheckBox();
		calcolatedChk.setSelected(false);
		calcolatedChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				PreviewComponent pc = getSelectedComponent();
				FComponent comp = FormsEditorDialog.this.getSelectedComponent().getComponent();
				if((comp instanceof TextArea) ||  (comp instanceof TextField)){
					calcolatedChk.setSelected(false);
				}else{
					if (calcolatedChk.isSelected()) {
						if (pc != null) {
							pc.setCalculated(true);
							reqComboBox.setSelectedItem(false);
						}
						formulaTf.setEnabled(true);
						formulaLbl.setEnabled(true);
						reqComboBox.setEnabled(false);
					} else {
						if (pc != null)
							pc.setCalculated(false);
						formulaTf.setEnabled(false);
						formulaLbl.setEnabled(false);
						reqComboBox.setEnabled(true);
					}
				}
			}
		});
		
		formulaPanel.add(isCalculated);
		/*
		 * Fabaris_raji chkFormulaLabel=new JLabel("Is calculated");
		 * formulaPanel.add(chkFormulaLabel);
		 */

		formulaPanel.add(calcolatedChk);
		formulaPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		formulaPanel.add(formulaLbl);
		formulaPanel.add(formulaTf);
		formulaPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		// Fabaris_raji
		// innerContentPane.add(formulaPanel, new SimpleConstraints(550, 120,
		// 360, 20));

		newPanelProperties.add(formulaPanel, new SimpleConstraints(1, 120, 355,
				20));

		// Fabaris_raji

		// Fabaris_Raji to add combo box for required

		reqComboBox = new JComboBox(
				new Object[] { Boolean.TRUE, Boolean.FALSE });
		reqComboBox.setSelectedIndex(-1);
		reqComboBox.setEditable(true);
		reqComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Boolean str = (Boolean) reqComboBox.getSelectedItem();
				FComponent component = getSelectedComponent().getComponent();
				component.setRequired(str);
				showProperties();
			}
		});
		requiredLabel.setText(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.REQUIRED_LABEL));
		requiredLabel.setLabelFor(reqComboBox);
		// Fabaris_raji
		// innerContentPane.add(jlbl, new SimpleConstraints(552, 155, 60, 20));
		newPanelProperties.add(requiredLabel, new SimpleConstraints(10, 155, 60, 20));
		// innerContentPane.add(reqComboBox, new SimpleConstraints(612, 155, 60,
		// 21));
		newPanelProperties.add(reqComboBox, new SimpleConstraints(66, 155, 60,
				21));
		// innerContentPane.add(newPanelProperties, new SimpleConstraints(550,
		// 0, 360, 255));
		innerContentPane.add(newPanelProperties, new SimpleConstraints(650, 0,
				360, 255));

		newPanelVisibility = new JPanel(); // NewPanel for Bindings section
		newPanelVisibility.setLayout(contentPaneLayout);
		newPanelVisibility.setOpaque(true);
		newPanelVisibility.setBorder(etched);
		// Fabaris_raji
		// ---
		// Fabaris_a.zanchi --- Table for bindings (relevant) ----
		// bindingScrollPanel is defined up in the code
		// ---

		// innerContentPane.add(removeBind, new SimpleConstraints(810, /* 263
		// */413, 40, 20));
		newPanelVisibility
				.add(removeBind, new SimpleConstraints(75, 7, 60, 20));// Fabaris_raji
		// innerContentPane.add(policyComboBox, new SimpleConstraints(720, /*
		// 263 */413, 80, 25));
		newPanelVisibility.add(bindingPolicyRadio1, new SimpleConstraints(275,
				2, 80, 25));// Fabaris_raji
		newPanelVisibility.add(bindingPolicyRadio2, new SimpleConstraints(275,
				22, 80, 25));// Fabaris_raji
		// innerContentPane.add(policyLabel, new SimpleConstraints(555, /* 263
		// */413, 150, 20));
		newPanelVisibility.add(policyLabel, new SimpleConstraints(170, 7, 150,
				20));// Fabaris_raji
		// innerContentPane.add(bindingScrollPanel, new SimpleConstraints(550,
		// /* 290 */440, 360, /* 285 */185)); // shifted by 150 px
		// Fabaris_raji
		newPanelVisibility.add(bindingScrollPanel, new SimpleConstraints(5, 40,
				350, 133));
		// innerContentPane.add(newPanelVisibility, new SimpleConstraints(550,
		// 440, 360, 175));
		innerContentPane.add(newPanelVisibility, new SimpleConstraints(650,
				440, 360, 175));

		newPanelValidity = new JPanel(); // New panel for Constraints section
		newPanelValidity.setLayout(contentPaneLayout);
		newPanelValidity.setOpaque(true);
		newPanelValidity.setBorder(etched);

		// Fabaris_raji

		// ---
		// Fabaris_a.zanchi --- Table for Constraints ---
		// ---
		this.constrainTable = new ConstraintTable();
		constrainScrollPanel = new JPanel();
		constrainScrollPanel.setLayout(new BorderLayout());
		constrainScrollPane = new JScrollPane(this.constrainTable);
		constrainScrollPanel.add(constrainScrollPane);
		// innerContentPane.add(constrainScrollPanel, new SimpleConstraints(550,
		// 260, 360, 135));
		newPanelValidity.add(constrainScrollPanel, new SimpleConstraints(5, 50,
				350, 133));// Fabaris_Raji
		// TitledBorder constraintsTitleBorder = new
		// TitledBorder(InternationalisationUtils.getI18nString(COMMON_CONSTRAINTS));
		constraintsTitleBorder = new TitledBorder(
				InternationalisationUtils.getI18nString(COMMON_CONSTRAINTS));
		constraintsTitleBorder.setTitleFont(FrontlineUI.currentResourceBundle
				.getFont());
		constrainScrollPane.setBorder(constraintsTitleBorder);
		addConstraint = new JButton(
				InternationalisationUtils.getI18nString(BINDINGS_ADD));
		removeConstraint = new JButton(
				InternationalisationUtils.getI18nString(BINDINGS_REMOVE));
		// innerContentPane.add(addConstraint, new SimpleConstraints(760, /* 263
		// */230, 40, 20));
		//addMessage = new JButton(InternationalisationUtils.getI18nString(BINDING_ADD_MESSAGE));
		
		newPanelValidity.add(addConstraint, new SimpleConstraints(10,7, 60, 20));
		newPanelValidity.add(removeConstraint, new SimpleConstraints(75,7, 60, 20));
		//newPanelValidity.add(addMessage,new SimpleConstraints(10,30,100,20));
//		addMessage.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				ConstraintMessageDialog dialog = new ConstraintMessageDialog();
//			}
//		});
		// Listeners for buttons
		addConstraint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				constrainTable.addDefaultCostrain();
			}
		});

		removeConstraint.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				constrainTable.removeSelectedConstraint();
			}
		});

		// policy label and policy dropdown
		constraintPolicyLabel = new JLabel(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.CONSTRAINTS_POLICY_LABEL));
		constraintPolicyComboBox = new JComboBox(new Object[] { "All", "Any" });
		constraintPolicyComboBox.setSelectedIndex(0);
		constraintPolicyComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				getSelectedComponent().setConstraintPolicy(
						(String) constraintPolicyComboBox.getSelectedItem());
			}
		});
		constraintPolicyRadio1 = new JRadioButton(FormField.AND_POLICY);
		constraintPolicyRadio1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedComponent() != null)
					getSelectedComponent().setConstraintPolicy(
							FormField.AND_POLICY);
			}
		});
		constraintPolicyRadio2 = new JRadioButton(FormField.OR_POLICY);
		constraintPolicyRadio2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedComponent() != null)
					getSelectedComponent().setConstraintPolicy(
							FormField.OR_POLICY);
			}
		});
		constraintPolicyGroup = new ButtonGroup();
		constraintPolicyGroup.add(constraintPolicyRadio1);
		constraintPolicyGroup.add(constraintPolicyRadio2);

		// innerContentPane.add(constraintPolicyLabel, new
		// SimpleConstraints(550, 230, 100, 20));
		newPanelValidity.add(constraintPolicyLabel, new SimpleConstraints(170,
				7, 100, 20));// Fabaris_raji
		// innerContentPane.add(constraintPolicyComboBox, new
		// SimpleConstraints(660, 230, 80, 25));
		newPanelValidity.add(constraintPolicyRadio1, new SimpleConstraints(275,
				2, 80, 25));// Fabaris_raji
		newPanelValidity.add(constraintPolicyRadio2, new SimpleConstraints(275,
				22, 80, 25));
		// innerContentPane.add(newPanelValidity,new SimpleConstraints(550,
		// 255,360, 185));
		innerContentPane.add(newPanelValidity, new SimpleConstraints(650, 255,
				360, 185));
		// Fabaris_A.zanchi survey selector for combo box
		
		
		//**************************** 
		surveyLabel = new JLabel(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.SURVEY_LABEL));

		surveyComboBox = new JComboBox();
		if (surveyComboBox.getSelectedItem() != null)
			surveyComboBox.setToolTipText(surveyComboBox.getSelectedItem()
					.toString());
		else
			surveyComboBox.setToolTipText("");
		surveyComboBox.setRenderer(new ComboBoxRenderer());
		surveyComboBox.setPreferredSize(new Dimension(250, 21));
		surveyLabel.setLabelFor(surveyComboBox);
		surveyComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					if(getSelectedComponent() != null){
						getSelectedComponent().setSurvey(
								(Survey) surveyComboBox.getSelectedItem());
						if (surveyComboBox.getSelectedItem() != null)
							surveyComboBox.setToolTipText(surveyComboBox
									.getSelectedItem().toString());
						else
							surveyComboBox.setToolTipText("");
					}
				}
			}

		});

		// refreshSurveyComboBox();
		surveyButton = new JButton(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.SURVEY_EDITLABEL));
		surveyButton.setPreferredSize(new Dimension(70, 21));// Fabaris_raji
		surveyButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				openSurveyFrame();
                                setIsSurveyListEditorOpened(true);
			}
		});
		// surveyPanel = new JPanel();
		surveyPanel = new JPanel(
				(LayoutManager) new FlowLayout(FlowLayout.LEFT));// Fabaris_raji

		// surveyButtonPanel = new JPanel();
		surveyButtonPanel = new JPanel((LayoutManager) new FlowLayout(
				FlowLayout.RIGHT));// Fabaris_raji

		surveyPanel.add(surveyLabel);
		surveyPanel.add(surveyComboBox);
		surveyButtonPanel.add(surveyButton);

		// innerContentPane.add(surveyPanel, new SimpleConstraints(507, 180,
		// 300, 30));
		newPanelProperties.add(surveyPanel, new SimpleConstraints(4, 180, 280,
				36));// Fabaris_raji
		// innerContentPane.add(surveyButtonPanel, new SimpleConstraints(608,
		// 205, 130, 30));
		newPanelProperties.add(surveyButtonPanel, new SimpleConstraints(284,
				180, 74, 36));// Fabaris_raji

		/*
		 * surveyPanel.setVisible(false); surveyButtonPanel.setVisible(false);
		 */
		// Fabaris_raji
		surveyLabel.setEnabled(false);
		surveyComboBox.setEnabled(false);
		surveyButton.setEnabled(false);
		surveyPanel.setEnabled(false);
		surveyButtonPanel.setEnabled(false);

		tfFormName = new JTextField();
		tfFormName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------	15/10/2013
				/*
				autoSave.stopAutoSave();
				autoSave.kill();
				autoSave.interrupt();
				*/
				save();
			}
		});

		// Fabaris_a.zanchi "basic" repeatables
		infinityRepCheck = new JCheckBox();
		infinityRepCheck.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				if (repNumbers.isEnabled()) {
					repNumbers.setEnabled(false);
					getSelectedComponent().setBasicContainerFixed(false);
					// getSelectedComponent().setNumberOfReps(0);
				} else {
					repNumbers.setEnabled(true);
					getSelectedComponent().setBasicContainerFixed(true);
					if (getSelectedComponent().getNumberOfReps() <= 1) {
						repNumbers.setText("1");
						getSelectedComponent().setNumberOfReps(1);
					} else {
						repNumbers.setText(String
								.valueOf(getSelectedComponent()
										.getNumberOfReps()));
					}
				}
			}
		});
		infinityRepCheck
				.setLabel(InternationalisationUtils
						.getI18nString(FormsThinletTabController.REPEATABLE_BASIC_INFINITY));
		repNumbers = new JNumberTextField();
		repNumbers.setText("1");
		// repNumbersLabel = new
		// JLabel(InternationalisationUtils.getI18nString(FormsThinletTabController.REPEATABLES_BASIC_REPEATATIONS));
		repNumbers.setPreferredSize(new Dimension(30, 20));

		repNumbers.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				changeValue(e);

			}

			public void insertUpdate(DocumentEvent e) {
				changeValue(e);
			}

			public void changedUpdate(DocumentEvent e) {
				// changeValue();
			}

			public void changeValue(DocumentEvent e) {
				try {
					Integer.valueOf(repNumbers.getText().trim());
				} catch (Exception e1) {
					getSelectedComponent().setNumberOfReps(1);
					// repNumbers.setText("1");
				}
				try {
					if (Integer.valueOf(repNumbers.getText().trim()) > 0) {
						getSelectedComponent().setNumberOfReps(
								Integer.valueOf(repNumbers.getText().trim()));
					} else {
						getSelectedComponent().setNumberOfReps(1);
						// repNumbers.setText("1");
					}
				} catch (Exception e1) {
				}
			}
		});

		/*
		 * repNumbers.addFocusListener(new FocusListener() {
		 * 
		 * public void focusLost(FocusEvent arg0) { if
		 * (Integer.valueOf(repNumbers.getText().trim()) > 0) {
		 * getSelectedComponent
		 * ().setNumberOfReps(Integer.valueOf(repNumbers.getText().trim())); }
		 * else { getSelectedComponent().setNumberOfReps(1);
		 * repNumbers.setText("1"); } }
		 * 
		 * public void focusGained(FocusEvent arg0) { // TODO Auto-generated
		 * method stub
		 * 
		 * } });
		 */

		/*
		 * repNumbers.addKeyListener(new KeyListener() {
		 * 
		 * public void keyTyped(KeyEvent arg0) {
		 * 
		 * if ((arg0.getKeyChar() <= 0) && (arg0.getKeyChar() >= 9)) { if
		 * (!repNumbers.getText().equals("")) { repNumbers.setText(repNumbers
		 * .getText()+String.valueOf(arg0.getKeyChar())); } }
		 * getSelectedComponent ().setNumberOfReps(Integer.valueOf(repNumbers
		 * .getText().trim()));
		 * 
		 * 
		 * }
		 * 
		 * public void keyReleased(KeyEvent arg0) { // TODO Auto-generated
		 * method stub
		 * 
		 * }
		 * 
		 * public void keyPressed(KeyEvent arg0) { if (arg0.getKeyCode() ==
		 * KeyEvent.VK_ENTER) { try { Integer i =
		 * Integer.valueOf(repNumbers.getText().trim()); } catch (Exception e) {
		 * getSelectedComponent().setNumberOfReps(1); repNumbers.setText("1"); }
		 * if (Integer.valueOf(repNumbers.getText().trim()) > 0) {
		 * getSelectedComponent
		 * ().setNumberOfReps(Integer.valueOf(repNumbers.getText().trim())); }
		 * else { getSelectedComponent().setNumberOfReps(1);
		 * repNumbers.setText("1"); } }
		 * 
		 * else { getSelectedComponent().setNumberOfReps(Integer.valueOf(
		 * repNumbers.getText().trim())); }
		 * 
		 * } });
		 */

		// basicRepPanel = new JPanel();
		basicRepPanel = new JPanel((LayoutManager) new FlowLayout(
				FlowLayout.LEFT));// Fabaris_raji
		basicRepPanel.add(infinityRepCheck);
		basicRepPanel.add(repNumbers);

		// innerContentPane.add(basicRepPanel, new SimpleConstraints(507, 180,
		// 300, 30));

		newPanelProperties.add(basicRepPanel, new SimpleConstraints(1, 225,
				150, 28));// Fabaris_raji

		infinityRepCheck.setSelected(false);
		repNumbers.setEnabled(false);

		// basicRepPanel.setVisible(false);
		basicRepPanel.setEnabled(false);// Fabaris_raji
		setEnabledAll(basicRepPanel, false);

		JLabel formName = new JLabel(
				InternationalisationUtils
						.getI18nString(FormsThinletTabController.I18N_KEY_FORM_NAME)
						+ ": ");

		// formName.setIcon(new
		// ImageIcon(FrontlineUtils.getImage("/icons/form.png", getClass())));

		FontMetrics m = formName.getFontMetrics(formName.getFont());
		int width = m.stringWidth(formName.getText());// +
														// formName.getIcon().getIconWidth();//commented
														// by Fabaris_Raji
		// innerContentPane.add(formName, new SimpleConstraints(160, 490));
		// innerContentPane.add(formName, new SimpleConstraints(40, 674));
		innerContentPane.add(formName, new SimpleConstraints(
				450 + width + 160 + 10, 630));// 628
		// innerContentPane.add(tfFormName, new SimpleConstraints(160 + width +
		// 20, 488, 200, null)); //commented by Fabaris_Raji
		// innerContentPane.add(tfFormName, new SimpleConstraints(60 + width +
		// 20, 674, 200, null));
		innerContentPane.add(tfFormName, new SimpleConstraints(
				450 + width + 200 + 45, 630, 200, null));
		JButton btSave = new JButton(
				InternationalisationUtils.getI18nString(ACTION_SAVE),
				new ImageIcon(FrontlineUtils.getImage("/icons/tick.png",
						getClass())));
		btSave.setFont(FrontlineUI.currentResourceBundle.getFont());

		btSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//---------------------------------------------------- 15/10/2013
				/*
				autoSave.stopAutoSave();
				autoSave.kill();
				autoSave.interrupt();
				*/
				save();
			}
		});

		JButton btCancel = new JButton(
				InternationalisationUtils.getI18nString(ACTION_CANCEL),
				new ImageIcon(FrontlineUtils.getImage("/icons/cross.png",
						getClass())));
		btCancel.setFont(FrontlineUI.currentResourceBundle.getFont());

		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		


		// int posSaveButton = 160 + width + 20 + 200 + 20;
		int posSaveButton = 450 + width + 20 + 200 + 40;// Fabaris_raji
		// innerContentPane.add(btSave, new SimpleConstraints(posSaveButton,
		// 485)); //commented by Fabaris_Raji
		// innerContentPane.add(btSave, new SimpleConstraints(posSaveButton,
		// 630));

		
		innerContentPane.add(btSave, new SimpleConstraints(posSaveButton, 660));		
		FontMetrics btSaveMetrics = btSave.getFontMetrics(btSave.getFont());
	
		innerContentPane.add(btCancel, new SimpleConstraints(posSaveButton
				+ btSaveMetrics.stringWidth(btSave.getText())
				+ btSave.getIcon().getIconWidth() + 40, 660));

		// this.setResizable(true);//commented by Fabaris_raji
		this.setResizable(false);// Fabaris_raji
		// this.setSize(900, 560);//commented by Fabaris_raji to increase the
		// window size

		this.setSize(1024, 750);// Fabaris_Raji

		try {
			// This method is only available in Java6+. It might be sensible
			// just to ditch
			// the code altogether, although surely there is a pre-Java6 way to
			// set an icon
			// for a window???
			Method setIconImage = this.getClass().getDeclaredMethod(
					"setIconImage", Image.class);
			setIconImage.setAccessible(true);
			// setIconImage.invoke(this,
			// FrontlineUtils.getImage("/icons/frontline_icon.png",
			// getClass()));
			setIconImage.invoke(this,
					FrontlineUtils.getImage("/icons/designer.png", getClass()));
		} catch (Throwable t) {
			// We're running on pre-1.6 :)
		}
		centralise(owner);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // added by Fabaris
		
		addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				int response = JOptionPane
						.showConfirmDialog(
								null,
								InternationalisationUtils
										.getI18nString(FormsThinletTabController.CLOSE_DIALOG_MESSAGE),
								InternationalisationUtils
										.getI18nString(FormsThinletTabController.CLOSE_DIALOG_BUTTON1),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					//-------------------------------------------------- 15/10/2013
					/*
					FormsEditorDialog.this.autoSave.stopAutoSave();
					FormsEditorDialog.this.autoSave.kill();
					FormsEditorDialog.this.autoSave.interrupt();
					*/
					save();
				} else if (response == JOptionPane.NO_OPTION) {
					current = null;
					//-------------------------------------------------- 15/10/2013
					/*
					FormsEditorDialog.this.autoSave.stopAutoSave();
					FormsEditorDialog.this.autoSave.kill();
					FormsEditorDialog.this.autoSave.interrupt();
					*/
					dispose();
				}
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {

			}

			public void windowIconified(WindowEvent e) {

			}

			public void windowOpened(WindowEvent e) {

			}

		});

		/*
		 * Fabaris_a.zanchi listener to refresh survey combo box when survey
		 * window is closed
		 */
		addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void windowGainedFocus(WindowEvent arg0) {
				refreshSurveyComboBox();
				//refreshListsInForm();
			}
		});
		enablePropertyPanel(true);
		enableValidityPanel(false);
		enableVisibilityPanel(false);
		repaint();
		//initThread();								15/10/2013
	}
	
	private void initThread(){
		Configuration configuration = FormsThinletTabController.getCurrentInstance().getConfigurationDao().getConfiguration();
		if(configuration == null){
			this.time = 0;
		}else{
			//-----------------------------------------------	15/10/2013
			/*
			this.time = 30;//configuration.getAutoSave();
			this.autoSave  = new AutoSaveThread(this);
			this.autoSave.start();
			*/
		}
	}
	
	public int getAutoSaveTime(){
		return this.time;
	}
	
	public void showFormulaEditor() {
		propertiesTablePanel.setVisible(false);
		formulaPanel.setVisible(false);
		reqComboBox.setVisible(false);
		bindingScrollPane.setVisible(false);
		policyComboBox.setVisible(false);
		surveyPanel.setVisible(false);
		surveyButtonPanel.setVisible(false);
		basicRepPanel.setVisible(false);
		removeBind.setVisible(false);
		pnDrawing.bindPanelDT.setActive(false);
		constrainScrollPane.setVisible(false);
		addConstraint.setVisible(false);
		addConstraint.setVisible(false);
		removeConstraint.setVisible(false);
		formulaEditorPanel.setVisible(true);
		// Fabaris_raji
		newPanelVisibility.setVisible(false);
		newPanelValidity.setVisible(false);
		newPanelProperties.setVisible(false);
	}

	public void hideFormulaEditor() {
		propertiesTablePanel.setVisible(true);
		formulaPanel.setVisible(true);
		reqComboBox.setVisible(true);
		bindingScrollPane.setVisible(true);
		policyComboBox.setVisible(true);
		surveyPanel.setVisible(true);
		surveyButtonPanel.setVisible(true);
		basicRepPanel.setVisible(true);
		removeBind.setVisible(true);
		pnDrawing.bindPanelDT.setActive(true);
		constrainScrollPane.setVisible(true);
		addConstraint.setVisible(true);
		//addMessage.setVisible(true);
		removeConstraint.setVisible(true);
		formulaEditorPanel.setVisible(false);
		// Fabaris_raji
		newPanelVisibility.setVisible(true);
		newPanelValidity.setVisible(true);
		newPanelProperties.setVisible(true);
		showProperties();
	}

	private void cancel() {
		/*
		 * current = null; dispose();
		 */

		// following code added by Fabaris_a.aknai
		// following code asks for saving before form designer close
		int response = JOptionPane
				.showConfirmDialog(
						null,
						InternationalisationUtils
								.getI18nString(FormsThinletTabController.CLOSE_DIALOG_MESSAGE),
						InternationalisationUtils
								.getI18nString(FormsThinletTabController.CLOSE_DIALOG_BUTTON1),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			//------------------------------------------------- 15/10/2013
			/*
			FormsEditorDialog.this.autoSave.kill();
			FormsEditorDialog.this.autoSave.stopAutoSave();
			FormsEditorDialog.this.autoSave.interrupt();
			*/
			save();
		} else if (response == JOptionPane.NO_OPTION) {
			// cancel(); //edited by Fabaris_A.zanchi to avoid recursive calls.
			current = null;
			//------------------------------------------------- 15/10/2013
			/*
			FormsEditorDialog.this.autoSave.kill();
			FormsEditorDialog.this.autoSave.stopAutoSave();
			FormsEditorDialog.this.autoSave.interrupt();
			*/
			dispose();
		}
	}

	/*
	 * To control duplicate name field in the list of components from alert
	 * 
	 * Fabaris_Raji
	 */
	public boolean check(FComponent comp, String sname) {
		boolean check = false;
		int counter = 0;
		current = pnDrawing.getCurrent();
		List<PreviewComponent> componentslst = new ArrayList<PreviewComponent>();
		componentslst = current.getComponents();
		List<String> lista = new ArrayList<String>();

		for (int i = 0; i < componentslst.size(); ++i) {
			if (comp != componentslst.get(i).getComponent()) {
				lista.add(componentslst.get(i).getComponent().getName().toUpperCase());
			}

		}
		for (int j = 0; j < lista.size(); ++j) {

			if (lista.contains(sname.toUpperCase())) {
				counter++;
			}
		}
		if (counter == 0)
			check = false;
		else
			check = true;

		return check;

	}

	/*
	 * To control duplicate name field in repeatable section from properties
	 * Fabaris_Raji
	 */
	public boolean checkDuplicateRepeatables(FComponent comp, String name) {
		boolean check = false;
		int counter = 0;
		List<String> lista = new ArrayList<String>();
		current = pnDrawing.getCurrent();
		List<PreviewComponent> compList = new ArrayList<PreviewComponent>();
		compList = current.getComponents();
		int i = 0;
		for (PreviewComponent pc : compList) {
			if ((pc.getType() == FormFieldType.REPEATABLES)
					|| (pc.getType() == FormFieldType.REPEATABLES_BASIC)) {

				for (PreviewComponent rep : pc.getRepeatables()) {

					if (comp != rep.getComponent()) {
						lista.add(rep.getComponent().getName());
					}
					++i;
				}// end for
				for (int j = 0; j < lista.size(); ++j) {

					if (lista.contains(name)) {
						counter++;
					}// end if
				}// end for
				if (counter == 0)
					check = false;
				else
					check = true;

			}// end if

		}// end for

		return check;

	}

	/*
	 * To control duplicate name field in repeatable section from alert
	 * 
	 * Fabaris_Raji
	 */
	public boolean checkDuplicateRepeatables(
			List<PreviewComponent> componentslstRep, PreviewComponent newLabel,
			String name) {
		int counter = 0;
		boolean check = false;
		List<String> lista = new ArrayList<String>();
		for (int i = 0; i < componentslstRep.size(); ++i) {
			if (componentslstRep.get(i) != newLabel) {
				lista.add(componentslstRep.get(i).getComponent().getName());
			}

		}

		for (int j = 0; j < lista.size(); ++j) {

			if (lista.contains(name)) {
				counter++;
			}
		}
		if (check(newLabel.getComponent(), name)) {
			counter++;
		}

		if (counter == 0)
			check = false;
		else
			check = true;

		return check;

	}

	public PreviewComponent getComponentByName(String name) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < pnDrawing.getCurrent().getComponents().size(); i++) {
			PreviewComponent comp = pnDrawing.getCurrent().getComponents()
					.get(i);
			if (comp.getComponent().getName().equals(name))
				return comp;
			for (int j = 0; j < comp.getRepeatables().size(); j++) {
				PreviewComponent compRep = comp.getRepeatables().get(j);
				if (compRep.getComponent().getName().equals(name))
					return compRep;
			}
		}
		return null;
	}

	public PreviewComponent getComponentRepetable(PreviewComponent c) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < pnDrawing.getCurrent().getComponents().size(); i++) {
			PreviewComponent comp = pnDrawing.getCurrent().getComponents()
					.get(i);
			if (comp.equals(c))
				return null;
			for (int j = 0; j < comp.getRepeatables().size(); j++) {
				PreviewComponent compRep = comp.getRepeatables().get(j);
				if (compRep.equals(c))
					return comp;
			}
		}
		return null;
	}

	public int getComponentPosition(PreviewComponent c) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < pnDrawing.getCurrent().getComponents().size(); i++) {
			PreviewComponent comp = pnDrawing.getCurrent().getComponents()
					.get(i);
			if (comp.equals(c))
				return i;
		}
		return -1;
	}

	private void save() 
	{
		int counter = 0;// Fabaris_raji
		boolean check = false;
		List<String> lista = new ArrayList<String>(); // Fabaris_raji

		// Fabaris_a.aknai
		if (formulaEditorPanel.isVisible()) 
		{
			JOptionPane.showMessageDialog(this, "Please close formula editor area before save the form.");
			return;
		}
		current = pnDrawing.getCurrent();
		
		/* Control blank name field -Fabaris_Raji */
		List<PreviewComponent> componentslst = new ArrayList<PreviewComponent>();
		componentslst = current.getComponents();

		/* Control duplicate name field -Fabaris_Raji */
		HashSet set = new HashSet();
		for (int id = 0; id < componentslst.size(); ++id) {
			boolean val = false;
			val = set.add(componentslst.get(id).getComponent().getName());
			if (val == false) {
				JOptionPane
						.showMessageDialog(
								this,
								InternationalisationUtils
										.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_EXISTS));
				return;
			} else if (!checkForEmptyName(current.getComponents())) {
				return;
			}
			// Fabaris_a.zanchi Check for other errors too.
			else if (!checkForEmptyDropDownList(current.getComponents())) {
				return;
			} else if (!checkForEmptyRepeatables(current.getComponents())) {
				return;
			}

		}

		String name = tfFormName.getText();
		if (name.equals("")) {
			JOptionPane
					.showMessageDialog(
							this,
							InternationalisationUtils
									.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FORM_NAME_BLANK));
			return;
		}

		/*
		 * added to compare form name with the list of forms -Fabaris_raji
		 */
		//CICLA TUTTE LE FORM E FA UNA LISTA DI OGGETTI FORM
		lista = new ArrayList<String>();
		for (Form f : FormsThinletTabController.getCurrentInstance().formsDao.getAllForms()) {
			String formname = f.getName();
			if (!formname.equals(current.getName())) {
				lista.add(formname);
			}
		}

		//CICLA LA LISTA PRECEDENTE E AGGIORNA COUNT SE TROVA IL NOME DELLA FORM 
		for (int j = 0; j < lista.size(); ++j) {
			String sname = lista.get(j);
			if (sname.equalsIgnoreCase(name)) {
				counter++;
			}
		}

		if (counter == 0)
			check = false;
		else
			check = true;
		/*
		 * check true if form name already exists
		 */
		if (check) {
			JOptionPane.showMessageDialog(owner, "Form name already exists","Error", JOptionPane.WARNING_MESSAGE);
			tfFormName.setText("");
			tfFormName.requestFocus();
		}
		else // Fabaris_raji
		{
			current.setName(name);
			// Fabaris_a.aknai - RECUPERA LE CREDENZIALI DELL'UTENTE LOGGATO
			UserDao userDao = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
			String email;
			try {
				email = userDao.getSupervisorEmail();
			} catch (Exception e) {
				email = null;
			}
			if (email == null)
				if (current.owner == null)
					JOptionPane.showMessageDialog(this,"No supervisor user detected. the form will be saved with no owner.");
			dispose();
		}
	}

	
	void autosave(){
		FormsThinletTabController.getCurrentInstance()._formsList_editSelected(getVisualForm());
	}
	
	
	/**
	 * Centralises this dialog according to the frame owner.
	 */
	private void centralise(Frame owner) {
		Dimension screen_size = owner.getSize();
		Point p = owner.getLocation();
		this.setLocation(p.x + ((screen_size.width - this.getWidth()) >> 1),
				p.y + ((screen_size.height - this.getHeight()) >> 1));
	}

	/**
	 * Invokes the preview refresh.
	 */
	public void refreshPreview() {
		pnDrawing.refreshPreview();
	}

	void refreshConstraint() {
		this.constrainTable = new ConstraintTable();
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Called from the controller when a line of the bind table is modified
	 * (bind type changed)
	 * 
	 * @param pComponent
	 * @param oldTypeCont
	 * @param newTypeCont
	 */
	public void bindChanged(PreviewComponent pComponent,
			BindingContainer oldTypeCont, BindingContainer newTypeCont) {
		// retrieve selected Component
		PreviewComponent selectedComponent = getSelectedComponent();
		selectedComponent.updateBinding(pComponent, oldTypeCont, newTypeCont);
		// showProperties();
	}

	/**
	 * Fabaris_A.zanchi Changes the component of an existing binding
	 * 
	 * @param newComp
	 * @param bCont
	 * @param oldComp
	 */
	public void bindChangedComopnent(PreviewComponent newComp,
			BindingContainer bCont, PreviewComponent oldComp) {
		PreviewComponent selectedComponent = getSelectedComponent();
		selectedComponent.updateBindingComponent(newComp, bCont, oldComp);
	}

	/**
	 * Fabaris_a.zanchi Adds a new constraint in the selected component
	 * 
	 * @param cont
	 *            a {@link ConstraintContainer}
	 */
	public void addConstraint(ConstraintContainer cont) {
		PreviewComponent selecComponent = getSelectedComponent();
		if(selecComponent != null){
			selecComponent.addConstraint(cont);
		}
	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * @param cont
	 *            container with infos
	 * @param index
	 *            of container
	 */
	public void updateConstraint(ConstraintContainer cont, int index) {
		PreviewComponent selectedComponent = getSelectedComponent();
		if(selectedComponent != null){
			selectedComponent.updateConstraint(cont, index);
		}
	}

	/**
	 * Fabaris_a.zanchi Removes a constraint in the selected component
	 * 
	 * @param index
	 */
	public void removeConstraint(int index) {
		PreviewComponent selecComponent = getSelectedComponent();
		if(selecComponent != null){
			selecComponent.removeCostraint(index);
		}
	}

	/**
	 * Retrieves the selected preview component.
	 * 
	 * @return
	 */
	public PreviewComponent getSelectedComponent() {
		return pnDrawing.getSelectedComponent();
	}
	
        /**
	 * Shows properties of the selected component (ex: label, name, required) and maintains
         * the enability and visibility of controls in the right panel.
	 * 
         * @author  Saad Mansour
	 */
	public void showProperties() {
		pnDrawing.refreshPreview();
		propertiesTable.clean();
		if (getSelectedComponent() != null
				&& formulaEditorPanel.isVisible() == false) {
			PreviewComponent pc = getSelectedComponent();
			FComponent component = pc.getComponent();
			propertiesTable.addProperty(InternationalisationUtils.getI18nString(FComponent.PROPERTY_TYPE), component.getDescription());
			String label = component.getLabel();
			if (label == null)
				label = "";
			String name = component.getName();
			Boolean required = component.getRequired();
			propertiesTable.addProperty(InternationalisationUtils
					.getI18nString(FComponent.PROPERTY_LABEL), label);
			propertiesTable.addProperty(InternationalisationUtils.getI18nString(FComponent.PROPERTY_NAME), name);
			propertiesTable.addProperty(InternationalisationUtils.getI18nString(FComponent.PROPERTY_REQUIRED), required.toString());
			propertiesTablePanel.setEnabled(true);
			propertiesTable.setEnabled(true);
			formulaTf.setText("");
			if (pc.getFormula() != null)
				formulaTf.setText(pc.getFormula().toFormulaString());
			calcolatedChk.setSelected(pc.isCalculated());
			if (pc.isCalculated()) {
				reqComboBox.setSelectedItem(false);
			} else {
				reqComboBox.setSelectedItem(required);
			}
			if ((pc.getType().equals(FormFieldType.TEXT_FIELD) 
                                || pc.getType().equals(FormFieldType.TEXT_AREA))) {
				requiredLabel.setEnabled(true);
				reqComboBox.setEnabled(true);
				isCalculated.setEnabled(false);
				calcolatedChk.setEnabled(false);
				formulaLbl.setEnabled(false);
			} else if (pc.getType().equals(FormFieldType.CURRENCY_FIELD) 
                                || pc.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD) && !pc.isDefaultComponent()){
				formulaPanel.setEnabled(true);
				setEnabledAll(formulaPanel, true);
				if(pc.isCalculated()){
					reqComboBox.setEnabled(false);
				}else{
					reqComboBox.setEnabled(true);
				}
				requiredLabel.setEnabled(true);
			}else {
				formulaPanel.setEnabled(false);
				requiredLabel.setEnabled(false);
				reqComboBox.setEnabled(false);
				isCalculated.setEnabled(false);
				calcolatedChk.setEnabled(false);
				formulaLbl.setEnabled(false);
				setEnabledAll(formulaPanel, false);
			}
			if ((getSelectedComponent().getType() == FormFieldType.DROP_DOWN_LIST)
					|| (getSelectedComponent().getType() == FormFieldType.RADIO_BUTTON)
					|| getSelectedComponent().getType() == FormFieldType.REPEATABLES) {
				surveyLabel.setEnabled(true);
				surveyComboBox.setEnabled(true);
				surveyButton.setEnabled(true);
				surveyPanel.setEnabled(true);
				surveyButtonPanel.setEnabled(true);
				basicRepPanel.setEnabled(false);
				setEnabledAll(basicRepPanel, false);
                                this.surveyComboBox.setSelectedItem(getSelectedComponent().getSurvey());
				infinityRepCheck.setSelected(false);
				infinityRepCheck.setEnabled(false);
				repNumbers.setText("");
				repNumbers.setEnabled(false);
			} else if (getSelectedComponent().getType() == FormFieldType.REPEATABLES_BASIC) {
				infinityRepCheck.setEnabled(true);
				if (getSelectedComponent().isBasicContainerFixed()) {
					infinityRepCheck.setSelected(true);
					repNumbers.setEnabled(true);
					repNumbers.setText(String.valueOf((getSelectedComponent()
							.getNumberOfReps())));
				} else {
					infinityRepCheck.setSelected(false);
					repNumbers.setText("");
					repNumbers.setEnabled(false);
				}
				surveyLabel.setEnabled(false);
				surveyComboBox.setEnabled(false);
				surveyButton.setEnabled(false);
				surveyPanel.setEnabled(false);
				surveyButtonPanel.setEnabled(false);
			} else {
				surveyLabel.setEnabled(false);
				surveyComboBox.setEnabled(false);
				surveyButton.setEnabled(false);
				surveyPanel.setEnabled(false);
				surveyButtonPanel.setEnabled(false);
				basicRepPanel.setEnabled(false);
				setEnabledAll(basicRepPanel, false);
				infinityRepCheck.setSelected(false);
				infinityRepCheck.setEnabled(false);
				repNumbers.setText("");
				repNumbers.setEnabled(false);
			}
			if(getSelectedComponent().getType() == FormFieldType.GEOLOCATION
                                && !pc.isDefaultComponent()){
				this.enableValidityPanel(false);
				this.enableVisibilityPanel(false);
				requiredLabel.setEnabled(true);
				reqComboBox.setEnabled(true);
				reqComboBox.setSelectedItem(false);
			}
			//****************************************************
			if(getSelectedComponent().getType() == FormFieldType.BARCODE){
				this.enableValidityPanel(false);
				this.enableVisibilityPanel(false);
				requiredLabel.setEnabled(true);
				reqComboBox.setEnabled(true);
				reqComboBox.setSelectedItem(false);
			}
			//****************************************************
			if(getSelectedComponent().getType() == FormFieldType.IMAGE){
				this.enableValidityPanel(false);
				this.enableVisibilityPanel(false);
				requiredLabel.setEnabled(true);
				reqComboBox.setEnabled(true);
				reqComboBox.setSelectedItem(false);
			}
			//****************************************************
                        //Added by Mureed
			if(getSelectedComponent().getType() == FormFieldType.SIGNATURE){
				this.enableValidityPanel(false);
				this.enableVisibilityPanel(false);
				requiredLabel.setEnabled(true);
				reqComboBox.setEnabled(true);
				reqComboBox.setSelectedItem(false);
			}
			//****************************************************
                        
			else if ((getSelectedComponent().getType() == FormFieldType.DROP_DOWN_LIST)
					|| (getSelectedComponent().getType() == FormFieldType.RADIO_BUTTON)
					|| getSelectedComponent().getType() == FormFieldType.REPEATABLES
					|| getSelectedComponent().getType() == FormFieldType.REPEATABLES_BASIC
					|| getSelectedComponent().getType() == FormFieldType.EMAIL_FIELD
					|| getSelectedComponent().getType() == FormFieldType.PHONE_NUMBER_FIELD
					|| getSelectedComponent().getType() == FormFieldType.SEPARATOR
					|| getSelectedComponent().getType() == FormFieldType.WRAPPED_TEXT
					|| getSelectedComponent().getType() == FormFieldType.TRUNCATED_TEXT
					|| getSelectedComponent().getType() == FormFieldType.CHECK_BOX) {
				addConstraint.setEnabled(false);
				removeConstraint.setEnabled(false);
				constraintPolicyLabel.setEnabled(false);
				constraintPolicyComboBox.setEnabled(false);
				constraintPolicyRadio1.setSelected(false);
				constraintPolicyRadio1.setEnabled(false);
				constraintPolicyRadio2.setSelected(false);
				constraintPolicyRadio2.setEnabled(false);
				constrainTable.setEnabled(false);
				constrainScrollPane.setEnabled(false);
				constraintsTitleBorder.setTitleColor(Color.gray);

			} else {
				addConstraint.setEnabled(true);
				removeConstraint.setEnabled(true);
				constraintPolicyRadio1.setEnabled(true);
				constraintPolicyRadio2.setEnabled(true);
				constraintPolicyLabel.setEnabled(true);
				constraintPolicyComboBox.setEnabled(true);
				constrainScrollPane.setEnabled(true);
				constrainTable.setEnabled(true);
				constraintsTitleBorder.setTitleColor(Color.black);
			}
			if (getSelectedComponent().getType() == FormFieldType.SEPARATOR) {
				policyComboBox.setEnabled(false);
				bindingPolicyRadio1.setSelected(false);
				bindingPolicyRadio2.setSelected(false);
				bindingPolicyRadio1.setEnabled(false);
				bindingPolicyRadio2.setEnabled(false);
				policyLabel.setEnabled(false);
				bindingScrollPane.setEnabled(false);
				removeBind.setEnabled(false);
				bindingTable.setEnabled(false);
				titledBorderBindings.setTitleColor(Color.gray);
			} else {
				policyComboBox.setEnabled(true);
				bindingPolicyRadio1.setEnabled(true);
				bindingPolicyRadio2.setEnabled(true);
				policyLabel.setEnabled(true);
				bindingScrollPane.setEnabled(true);
				removeBind.setEnabled(true);
				bindingTable.setEnabled(true);
				titledBorderBindings.setTitleColor(Color.black);
			}
			bindingTable.clean();
			PreviewComponent selected = getSelectedComponent();
			Map<PreviewComponent, ArrayList<BindingContainer>> compToBind = selected.getComponentToBindType();
			if (compToBind != null) {
				Set<PreviewComponent> compSet = compToBind.keySet();
				for (PreviewComponent c : compSet) {
					for (BindingContainer b : compToBind.get(c)) {
						bindingTable.addBinding(c, b);
					}
				}
			}
			constrainTable.clean();
			if (selected.getConstraints() != null) {
				for (ConstraintContainer cont : selected.getConstraints()) {
					constrainTable.addConstrainToDisplay(cont);
				}
			}
			this.constrainTable.repaint();
			String bindingPolicy = selected.getBindingsPolicy();
			if (bindingPolicy.equals(FormField.AND_POLICY))
				bindingPolicyRadio1.setSelected(true);
			if (bindingPolicy.equals(FormField.OR_POLICY))
				bindingPolicyRadio2.setSelected(true);
			String constraintPolicy = selected.getConstraintPolicy();
			if (constraintPolicy.equals(FormField.AND_POLICY))
				constraintPolicyRadio1.setSelected(true);
			if (constraintPolicy.equals(FormField.OR_POLICY))
				constraintPolicyRadio2.setSelected(true);
		}
	}

	// Fabaris_raji added to enable or disable all components in a panel
	public void setEnabledAll(Object object, boolean state) {
		if (object instanceof Container) {
			Container c = (Container) object;
			Component[] components = c.getComponents();
			for (Component component : components) {
				setEnabledAll(component, state);
				component.setEnabled(state);
			}
		} else {
			if (object instanceof Component) {
				Component component = (Component) object;
				component.setEnabled(state);
			}
		}
	}

	public void setForm(VisualForm form) {
		pnDrawing.setForm(form);
		current = pnDrawing.getCurrent();
		tfFormName.setText(form.getName());
		refreshSurveyComboBox();
		this.policyComboBox.setSelectedItem(form.getBindingsPolicy());
	}

	/**
	 * Fabaris_a.zanchi Method invoked to change the painting of the binded
	 * component
	 * 
	 * @param comp
	 */
	public void highlightComponent(PreviewComponent comp, Color color) {
		pnDrawing.highlightComponent(comp, null);
	}

	public VisualForm getVisualForm() {
		return current;
	}

	/**
	 * Fabaris_a.zanchi Add default components to the form
	 * 
	 */
	public void addDefaultComponents(
	/* FComponent[] types */DefaultComponentDescriptor defCompDesc) {
		// this.pnDrawing.addDefaultComponents(types);
		this.pnDrawing.addDefaultComponents(defCompDesc);
	}

	/**
	 * Fabaris_a.zanchi shows the details of repeatable container
	 */
	public void showRepeatableContainerDetails() {
		pnDrawing.showRepeatableContainerDetails();
	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * Method to deselect components in both the preview panel and the
	 * repeatable preview panel
	 */
	public void deselectEverything() {
		pnDrawing.deselectEverything();

	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * called when the survey combo box must be refreshed (for example when some
	 * surveys has been added or removed)
	 * 
	 */
	private void refreshSurveyComboBox() {
	    if (isSurveyListEditorOpened) {
                Survey previousSelectedItem = (Survey)surveyComboBox.getSelectedItem();
		String formId = pnDrawing.getCurrent().getFormId();
		List<Survey> surveyList = null;
		if (formId != null) {
			Form currentForm = FormsThinletTabController.getCurrentInstance()
					.getPluginController().getFormDao().getFromId_IdMac(formId);
			surveyList = FormsThinletTabController.getCurrentInstance()
					.getPluginController().getSurveyDao()
					.getSurveyOwnedByForm(currentForm);
		} else {
			surveyList = current.getTemporarySurveys();
		}
		MutableComboBoxModel comboBoxModel = new DefaultComboBoxModel();
		//comboBoxModel.addElement(null);
		for (Survey s : surveyList) {
			System.out.println("survey elements length: "
					+ s.getValues().size());
			comboBoxModel.addElement(s); 
		}
		surveyComboBox.setModel(comboBoxModel);
		
//                //Returns selection to the selected item.
//                if(selectedItem != null)
//			surveyComboBox.setSelectedItem(selectedItem);
		//setSelectedSurveyItem(previousSelectedItem);
		//surveyComboBox.setSelectedItem(comboBoxModel.getElementAt(0).toString());	
                surveyComboBox.setSelectedItem(previousSelectedItem);
            }
            
            setIsSurveyListEditorOpened(false);
	}
        
        
        private void setSelectedSurveyItem(Survey previousSelectedItem)
        {
            if (isSurveyListEditorOpened == false) {
                surveyComboBox.setSelectedItem(previousSelectedItem);
            }
            else //Select the last selected item from SurveyListEditor
            {
                Survey selectedItemFromEditor = null;

                if (surveyCtrl != null) {
                    selectedItemFromEditor = (Survey)surveyCtrl.getSurveyEditor().getSurveyJList().getSelectedValue();
                }

                if (selectedItemFromEditor != null) {
                    surveyComboBox.setForeground(Color.gray);
                    surveyComboBox.setEnabled(false);
                    //enablePropertyPanel(false);
                    surveyComboBox.setSelectedItem(selectedItemFromEditor);
                    //enablePropertyPanel(ture);                    
                    surveyComboBox.setEnabled(true);   
                    surveyComboBox.setForeground(Color.black);
                }else
                    surveyComboBox.setSelectedItem(null);

                setIsSurveyListEditorOpened(false);
            }
        }
        
        private void setIsSurveyListEditorOpened(boolean isOpened)
        {
            isSurveyListEditorOpened = isOpened;
        }

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Method that assigns modified lists to components after the list editor
	 * dialog is closed
	 * 
	 */
	private void refreshListsInForm() {
	    if (isSurveyListEditorOpened) {
                String formId = pnDrawing.getCurrent().getFormId();
		List<Survey> surveyList = null;
		if (formId != null) {
			Form currentForm = FormsThinletTabController.getCurrentInstance()
					.getPluginController().getFormDao().getFromId_IdMac(formId);
			surveyList = FormsThinletTabController.getCurrentInstance()
					.getPluginController().getSurveyDao()
					.getSurveyOwnedByForm(currentForm);
		} else {
			surveyList = current.getTemporarySurveys();
		}
                
		if (current!=null && current.getComponents() != null) {
			for (PreviewComponent p : current.getComponents()) {
				for (Survey s : surveyList) {
					if (p.getSurvey() != null && p.getSurvey().getId() == s.getId() || p.isSelected()) {//s.getName() == p.getFormField().getName()) {//p.getSurvey().getId()) {
						p.setSurvey(s);
					}
				}
			}
			
			/*
			 for (int k = 0; k < current.getComponents().size(); k++) 
			 {
			 	PreviewComponent p = current.getComponents()
				for (Survey s : surveyList) {
					if (p.getSurvey() != null) { //&& s.getName() == p.getFormField().getName()) {//p.getSurvey().getId()) {
						p.setSurvey(s);
					}
				}
			}
			 */
		}    
            }
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * called when the survey editor is invoked pressing a button
	 * 
	 */
	private void openSurveyFrame() {
		surveyCtrl = new SurveyEditorController(
				pnDrawing.getCurrent(), ownerFrame, getSelectedComponent());
                Survey selectedSurveyItem = (Survey) surveyComboBox.getSelectedItem();
		surveyCtrl.showWindow(selectedSurveyItem);
	}
        
	/**
	 * Check if components in the form have empty name Fabaris_raji
	 */
	private boolean checkForEmptyName(List<PreviewComponent> compList) {
		HashMap<PreviewComponent, Integer> errorPc = new HashMap<PreviewComponent, Integer>();
		boolean displayWarning = false;
		boolean displayWarningRep = false;
		String errorRep = null;
		int j = 1;
		int i = 1;
		for (PreviewComponent pc : compList) {
			if ((pc.getType() == FormFieldType.REPEATABLES)
					|| (pc.getType() == FormFieldType.REPEATABLES_BASIC)) {
				{
					for (PreviewComponent rep : pc.getRepeatables()) {

						if (rep.getComponent().getName() == ""
								|| rep.getComponent().getName().length() == 0
								|| (rep.getComponent().getName() == null)) {

							displayWarningRep = true;
							errorPc.put(pc, i);

						}
						i++;

					}
				}
				if (displayWarningRep) {
					errorRep = InternationalisationUtils
							.getI18nString(FormsThinletTabController.ERROR_EMPTY_REP_NAME)
							+ "\n";

				} else {
					errorRep = "";

				}

			} // end if repeatables

			if (pc.getComponent().getName() == ""
					|| pc.getComponent().getName().length() == 0
					|| (pc.getComponent().getName() == null)) {
				displayWarning = true;
				errorPc.put(pc, j);
			}
			j++;
		}// end of if normal components

		if (displayWarning && displayWarningRep) {

			String errors = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_NAME_TITLE)
					+ "\n";
			for (PreviewComponent pc1 : errorPc.keySet()) {
				errors = errors
						+ InternationalisationUtils
								.getI18nString(
										FormsThinletTabController.ERROR_EMPTY_NAME_POSITION,
										pc1.getComponent().getName(), String
												.valueOf(errorPc.get(pc1)), pc1
												.getType().getNiceName())
						+ "\n";
			}

			String message = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_NAME);

			JOptionPane.showMessageDialog(this, message + "\n" + errors + "\n"
					+ errorRep);
			return false;

		} else if (displayWarning) {
			String errors = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_NAME_TITLE)
					+ "\n";
			for (PreviewComponent pc1 : errorPc.keySet()) {
				errors = errors
						+ InternationalisationUtils
								.getI18nString(
										FormsThinletTabController.ERROR_EMPTY_NAME_POSITION,
										pc1.getComponent().getName(), String
												.valueOf(errorPc.get(pc1)), pc1
												.getType().getNiceName())
						+ "\n";
			}

			String message = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_NAME);

			JOptionPane.showMessageDialog(this, message + "\n" + errors + "\n");
			return false;
		} else if (displayWarningRep) {
			JOptionPane.showMessageDialog(this, errorRep);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Fabaris_a.zanchi checks for empty drop down list (Without any component)
	 * if there are displays a warning
	 * 
	 * @param vForm
	 */
	private boolean checkForEmptyDropDownList(List<PreviewComponent> compList) {
		HashMap<PreviewComponent, Integer> errorPc = new HashMap<PreviewComponent, Integer>();
		boolean displayWarning = false;
		int i = 1;
		for (PreviewComponent pc : compList) {
			if (pc.getType() == FormFieldType.DROP_DOWN_LIST
					|| pc.getType() == FormFieldType.RADIO_BUTTON
					|| pc.getType() == FormFieldType.REPEATABLES) {
				if ((pc.getSurvey() == null)
						|| (pc.getSurvey().getValues() == null)
						|| (pc.getSurvey().getValues().size() == 0)) {
					displayWarning = true;
					errorPc.put(pc, i);
				}
			}
			if (pc.getType() == FormFieldType.REPEATABLES
					|| pc.getType() == FormFieldType.REPEATABLES_BASIC) {
				for (PreviewComponent rep : pc.getRepeatables()) {
					if (rep.getType() == FormFieldType.DROP_DOWN_LIST
							|| rep.getType() == FormFieldType.RADIO_BUTTON
							|| rep.getType() == FormFieldType.REPEATABLES) {
						if ((rep.getSurvey() == null)
								|| (rep.getSurvey().getValues() == null)
								|| (rep.getSurvey().getValues().size() == 0)) {
							displayWarning = true;
							if (!errorPc.containsKey(pc))
								errorPc.put(pc, i);
						}
					}
				}
			}
			i++;
		}
		if (displayWarning) {
			String errors = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST_TITLE)
					+ "\n";
			for (PreviewComponent pc : errorPc.keySet()) {
				errors = errors
						+ InternationalisationUtils
								.getI18nString(
										FormsThinletTabController.ERROR_EMPTY_LIST_POSITION,
										pc.getComponent().getName(), String
												.valueOf(errorPc.get(pc)), pc
												.getType().getNiceName())
						+ "\n";
			}
			String message = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST);
			// message = message + InternationalisationUtils.
			JOptionPane.showMessageDialog(this, message + "\n" + errors);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Fabaris.a_zanchi check for emptyRepeatables
	 * 
	 * @param compList
	 * @return
	 */
	private boolean checkForEmptyRepeatables(List<PreviewComponent> compList) {
		HashMap<PreviewComponent, Integer> errorPc = new HashMap<PreviewComponent, Integer>();
		boolean displayWarning = false;
		int i = 1;
		for (PreviewComponent pc : compList) {
			if ((pc.getType() == FormFieldType.REPEATABLES)
					|| (pc.getType() == FormFieldType.REPEATABLES_BASIC)) {
				if (pc.getRepeatables().size() == 0) {
					displayWarning = true;
					errorPc.put(pc, i);
				}
			}
			i++;
		}
		if (displayWarning) {
			String errors = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_LIST_TITLE)
					+ "\n";
			for (PreviewComponent pc : errorPc.keySet()) {
				errors = errors
						+ InternationalisationUtils
								.getI18nString(
										FormsThinletTabController.ERROR_EMPTY_LIST_POSITION,
										pc.getComponent().getName(), String
												.valueOf(errorPc.get(pc)), pc
												.getType().getNiceName())
						+ "\n";
			}
			String message = InternationalisationUtils
					.getI18nString(FormsThinletTabController.ERROR_EMPTY_REPEATABLE);
			// message = message + InternationalisationUtils.
			JOptionPane.showMessageDialog(this, message + "\n" + errors);
			return false;
		} else {
			return true;
		}
	}

	public DrawingPanel getDrawingPanel() {
		return pnDrawing;
	}


	/*
	 * Frame for Form name dialog -Fabaris_raji //added by Fabaris_raji 15 may
	 */
	public static FormNamePanel createAndShowGUIS(FormsEditorDialog mainFrame,
			VisualForm form) {
		String msg = InternationalisationUtils
				.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_FORMS);
		FormNamePanel pnl = new FormNamePanel(owner);
		pnl.setNameForm(msg, form, mainFrame); // added by Fabaris_raji 15 may
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		final int WIDTH = screenSize.width;
		final int HEIGHT = screenSize.height;
		pnl.setSize((WIDTH / 8), (HEIGHT / 8));
		pnl.setLocation(WIDTH / 8, WIDTH / 8);
		pnl.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pnl.setLocationRelativeTo(owner);
		pnl.setResizable(false);
		pnl.pack();
		pnl.setVisible(true);
		return pnl;
	}
	
	/*
	 * Frame for component name dialog -Fabaris_raji
	 */
	public static void createAndShowGUI(FComponent comp, boolean inRoster) {
		
		if((comp instanceof Separator)){			
			comp.setName(FormsEditorDialog.separator + PreviewPanel.getCountSeparator());
			//comp.setName(FormsEditorDialog.separator+FormsEditorDialog.count);
			//FormsEditorDialog.count++;
		}else if(comp instanceof TruncatedText) {
			if(inRoster){//se mi trovo in un roster
				//comp.setName(FormsEditorDialog.singleLine + ts2.hashCode());
				java.util.Date date= new java.util.Date();
				String ts2 = new Timestamp(date.getTime()).toString();
				Integer t2hash = ts2.hashCode();
				comp.setName(FormsEditorDialog.singleLine + getEndOfWidgetName(t2hash));
			}else{
				comp.setName(FormsEditorDialog.singleLine + getEndOfWidgetName(PreviewPanel.getCountSingleline()));
			}
			//FormsEditorDialog.count++;
		}else if(comp instanceof WrappedText){//LL
			if(inRoster){//se mi trovo in un roster
				java.util.Date date= new java.util.Date();
				String ts2 = new Timestamp(date.getTime()).toString();
				Integer t2hash = ts2.hashCode();
				comp.setName(FormsEditorDialog.multiLine + getEndOfWidgetName(t2hash));
			}else{
				comp.setName(FormsEditorDialog.multiLine + getEndOfWidgetName(PreviewPanel.getCountMultiline()));
				//FormsEditorDialog.count++;
			}
		}
		else{
			String msg = InternationalisationUtils
					.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_BLANK);
			JNamePanel pnl = new JNamePanel(owner,comp);
			pnl.getName(msg, comp);
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension screenSize = tk.getScreenSize();
			final int WIDTH = screenSize.width;
			final int HEIGHT = screenSize.height;
			pnl.setSize((WIDTH / 8), (HEIGHT / 8));
			pnl.setLocation(WIDTH / 8, WIDTH / 8);
			pnl.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pnl.setLocationRelativeTo(owner);
			pnl.setResizable(false);
			pnl.pack();
			pnl.setVisible(true);
		}
	}
	
	public static String getEndOfWidgetName(int t2hash){
		Integer tshasInteger = (Integer) t2hash;
		if(tshasInteger<0){
			return tshasInteger.toString().replace("-", "_");
		}else{
			return tshasInteger.toString();
		}
	}

	// Frame for copied form name-Fabaris_raji.

	public static void createAndShowGUI(List<String> list, Form selected,
			FormsThinletTabController ft) {

		FNamePanel pnl = new FNamePanel(owner);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		final int WIDTH = screenSize.width;
		final int HEIGHT = screenSize.height;
		pnl.setSize((WIDTH / 8), (HEIGHT / 8));
		pnl.setLocation(WIDTH / 8, WIDTH / 8);
		pnl.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pnl.setLocationRelativeTo(owner);
		pnl.setResizable(false);
		pnl.assignName(list, selected, ft);
		pnl.pack();
		pnl.setVisible(true);

	}

	// private void addMouseClickedEvent(Container container){
	// for (Component component : container.getComponents()) {
	// component.addMouseListener(new MyMouseListener(this.constrainTable));
	// if(component instanceof Container){
	// addMouseClickedEvent((Container)component);
	// }
	// }
	// }

	class ComboBoxRenderer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2903590945500923644L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index) {
					Survey sur = (Survey) surveyComboBox.getModel()
							.getElementAt(index);
					if (sur != null)
						list.setToolTipText(sur.getName());
				}
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}

	}
	
	public void enablePropertyPanel(boolean enable){
		PanelHelper.enableComponents(newPanelProperties, enable);
		this.newPanelProperties.revalidate();
		this.newPanelProperties.repaint();
	}
	
	public void enableVisibilityPanel(boolean enable){
		PanelHelper.enableComponents(newPanelVisibility, enable);
		this.newPanelVisibility.revalidate();
		this.newPanelVisibility.repaint();
	}
	
	public void enableValidityPanel(boolean enable){
		PanelHelper.enableComponents(newPanelValidity, enable);
		this.newPanelValidity.revalidate();
		this.newPanelValidity.repaint();
	}
	
	public void enableIsCalculatedControl(boolean isCalculated){
		this.calcolatedChk.setSelected(isCalculated);
		
	}
	
	public PropertiesTable getPropertyTable(){
		return this.propertiesTable;
	}
	
	public JTable getConstraintTable() {
		return this.constrainTable;
	}
	
	public void resetBindingTable(){
		this.bindingTable.clean();
	}
	
	public void resetPropertyTable() {
		propertiesTable.clean();
	}
	
	public void resetConstraintTable(){
		constrainTable.clean();
	}
	
	public void enableConstraintTable(boolean enabled){
		constrainTable.setEnabled(enabled);
	}
	
	public void enableBindingTable(boolean enabled){
		bindingTable.setEnabled(enabled);
	}
	
	public void enablePropertiesTable(boolean enabled){
		propertiesTable.setEnabled(enabled);
	}
	
	public void setRequiredValue(boolean required){
		this.reqComboBox.setSelectedItem(required);
	}
}