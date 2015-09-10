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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.forms.FormFormula;
import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.BindingTableModel;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PaletteComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.Icon;
import net.frontlinesms.ui.SimpleConstraints;
import net.frontlinesms.ui.SimpleLayout;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This class represents the UI for palette and preview components.
 * 
 * @author Carlos Eduardo Genz <li>kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class DrawingPanel extends JPanel{
	// private static final int HEIGHT = 480; //commented by Fabaris_Raji
	private static final int HEIGHT = 690;// Fabaris_raji
	private static final long serialVersionUID = -623321310155700309L;

	private static final String CALCULATED_ALERT_MESSAGE = "plugin.forms.calculated.alert.deletingmessage";
	private static final String CALCULATED_ALERT_COMPONENT = "plugin.forms.calculated.alert.deletingcomponents";
	private static final String ALERT_ONDELETING = "plugins.forms.alert.ondeleting";
	private static final String ALERT_ONDELETING_TITLE = "plugins.forms.alert.ondeleting.title";
	private static final String ALERT_COMPONENT_NOT_ALLOWED = "plugins.forms.alert.component.notallowed";
	private static final String ALERT_ONDROP_WITHIN_DEFAULTCOMPONENTS = "plugins.forms.alert.drop.ondefault";

	/**
	 * Fabaris_a.zanchi indicates the quantity of default components. its vaule
	 * is -1 if there are no default components
	 */
	private int defaultComponents = -1;

	private PalettePanel pnPalette;
	private PreviewPanel pnPreview;

	public PreviewPanel getPnPreview() {
		return pnPreview;
	}

	public void setPnPreview(PreviewPanel pnPreview) {
		this.pnPreview = pnPreview;
	}

	// Fabaris_a.zanchi
	private boolean isRepeatableOpen = false;
	/*
	 * Fabaris_a.zanchi instance variable to disable the drop when container
	 * component details are displayed
	 */
	DropTarget pnPreviewDrop;

	// Fabaris_a.zanchi layered pane to contain the preview panels
	// private JLayeredPane layeredPane;
	// Fabaris_a.zanchi preview panel for the repeatable section
	private PreviewPanelRepeatables pnRepeatablePreview;

	public PreviewPanelRepeatables getPnRepeatablePreview() {
		return pnRepeatablePreview;
	}

	private DragListener dragListener;

	private JScrollPane scrollPreview;
	// Fabaris_a.zanchi scroll for repetable preview panel:
	private JScrollPane scrollRepeatablePreview;

	private DragSource source;

	// Fabaris_a.zanchi riferimento al bind Panel, su cui deve funzionare il drag&drop
	private JPanel bindPanel;

	// Fabaris_a.zanchi riferimento al tabbed pane
	private JTabbedPane tabbedPane;

	public DrawingPanel() {
		dragListener = new DragListener(this);
		source = new DragSource();
		source.addDragSourceMotionListener(dragListener);

		setLayout(new SimpleLayout());

		JLabel bin = new JLabel(new ImageIcon(FrontlineUtils.getImage(Icon.BIN, getClass())));
		bin.setToolTipText(InternationalisationUtils.getI18nString(FormsThinletTabController.TOOLTIP_DRAG_TO_REMOVE));
		// add(bin, new SimpleConstraints(470, HEIGHT - 40)); //commented by
		// Fabaris_Raji
		add(bin, new SimpleConstraints(160, HEIGHT - 60));
		JLabel labelSentenceDeleteKey = new JLabel(InternationalisationUtils.getI18nString(FormsThinletTabController.SENTENCE_DELETE_KEY) + ".");
		JLabel labelSentenceUpKey = new JLabel(InternationalisationUtils.getI18nString(FormsThinletTabController.SENTENCE_UP_KEY) + ".");
		JLabel labelSentenceDownKey = new JLabel(InternationalisationUtils.getI18nString(FormsThinletTabController.SENTENCE_DOWN_KEY) + ".");

		// We have to set the correct font for some languages
		//Fabaris_Raji
		Object font=UIManager.get("Label.font"); 
		labelSentenceDeleteKey.setFont(FrontlineUI.currentResourceBundle.getFont());		
		labelSentenceDeleteKey.setFont(new Font(font.toString(), Font.PLAIN, 10));	//Fabaris_raji
		labelSentenceUpKey.setFont(FrontlineUI.currentResourceBundle.getFont());
		labelSentenceUpKey.setFont(new Font(font.toString(), Font.PLAIN, 10));
		labelSentenceDownKey.setFont(FrontlineUI.currentResourceBundle.getFont());
		labelSentenceDownKey.setFont(new Font(font.toString(), Font.PLAIN, 10));

		/*
		 * commented by Fabaris_Raji add(labelSentenceDeleteKey, new
		 * SimpleConstraints(255, HEIGHT -47)); add(labelSentenceUpKey, new
		 * SimpleConstraints(255, HEIGHT - 32)); add(labelSentenceDownKey, new
		 * SimpleConstraints(255, HEIGHT - 17));
		 */
		// Fabaris_raji
		add(labelSentenceDeleteKey, new SimpleConstraints(5, HEIGHT - 63));
		add(labelSentenceUpKey, new SimpleConstraints(5, HEIGHT - 47));
		add(labelSentenceDownKey, new SimpleConstraints(5, HEIGHT - 32));

		pnPalette = new PalettePanel(dragListener, source);

		JScrollPane sp1 = new JScrollPane(pnPalette);
		sp1.setBorder(null);

		/*
		 * commented by Fabaris_Raji sp1.setPreferredSize(new Dimension(250,
		 * HEIGHT)); add(sp1, new SimpleConstraints(0, 0, 250, HEIGHT));
		 */

		// Fabaris_raji
		sp1.setPreferredSize(new Dimension(0, HEIGHT - 70));
		add(sp1, new SimpleConstraints(0, 0, 200, HEIGHT - 70));

		// Fabaris_a.zanchi creation of a layeredPane
		/*
		 * layeredPane = new JLayeredPane(); layeredPane.setPreferredSize(new
		 * Dimension(340, HEIGHT - 50));
		 */

		pnPreview = new PreviewPanel(dragListener, source, this);
		// Fabaris_a.zanchi
		
		pnRepeatablePreview = new PreviewPanelRepeatables(dragListener, source, this);
		scrollPreview = new JScrollPane(pnPreview);
		scrollPreview.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPreview.setBorder(null);

		// Fabaris_a.zanchi
		scrollRepeatablePreview = new JScrollPane(pnRepeatablePreview);
		scrollRepeatablePreview.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// scrollRepeatablePreview.setBorder(null);
		scrollRepeatablePreview.setBorder(getBorder()); // Fabaris_raji
		// scrollPreview.setPreferredSize(new Dimension(250, HEIGHT -
		// 50));//commented by Fabaris_Raji

		scrollPreview.setPreferredSize(new Dimension(450, HEIGHT - 70));// fabaris_raji
		scrollPreview.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// Fabaris_a.zanchi
		// scrollRepeatablePreview.setPreferredSize(new Dimension(340, HEIGHT -
		// 50));
		scrollRepeatablePreview.setPreferredSize(new Dimension(450, HEIGHT - 70)); // Fabaris_raji
		scrollRepeatablePreview.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		/*
		 * commented by Fabaris_Raji add(scrollPreview, new
		 * SimpleConstraints(250, 0, 250, HEIGHT - 50));
		 */

		/* layeredPane.add(pnPreview,0); */

		add(scrollPreview, new SimpleConstraints(200, 0, 450, HEIGHT - 70));// Fabaris_raji
		new DropTarget(bin, DnDConstants.ACTION_COPY, dragListener);
		pnPreviewDrop = new DropTarget(pnPreview, DnDConstants.ACTION_COPY, dragListener);
		// Fabaris_a.zanchi
		new DropTarget(pnRepeatablePreview, DnDConstants.ACTION_COPY, dragListener);

	}

	/**
	 * n Adds the supplied component to the preview in the supplied position.
	 * 
	 * @param component
	 *            Component to be added.
	 * @param x
	 * @param y
	 */
	public void addToPreview(PaletteComponent component, int x, int y) {
		// Fabaris_a.zanchi checks if the component is added after the default
		// components.
		PreviewComponent newLabel = new PreviewComponent();// Fabaris_raji

		if (pnPreview.getIndex(y) > defaultComponents - 1 || defaultComponents == -1) {
			// PreviewComponent newLabel = new PreviewComponent();
			newLabel.setComponent(component.getComponent().clone());
			int index = pnPreview.getIndex(y);
			int lastDefaultIndex = 0;
			for (PreviewComponent pc : pnPreview.getForm().getComponents()) {
				if (pc.isDefaultComponent())
					lastDefaultIndex = pnPreview.getForm().getComponents().indexOf(pc);
			}
			if ((newLabel.getType() == FormFieldType.REPEATABLES || newLabel.getType() == FormFieldType.REPEATABLES_BASIC) && index <= lastDefaultIndex) {
			} else {
				pnPreview.addComponent(newLabel, x, y);
				this.validate();
				this.repaint();
				// Added by Fabaris_raji to give name
				newLabel.setSelected(true);
				/*
				 * String str =JOptionPane.showInputDialog(null,
				 * InternationalisationUtils.
				 * getI18nString(FormsThinletTabController
				 * .I18N_KEY_MESSAGE_FIELD_NAME_BLANK));
				 */

				// Added by Fabaris_raji for name dialog

				FormsEditorDialog.createAndShowGUI(newLabel.getComponent(),false);
				
				String str = newLabel.getComponent().getName();
				if (str != null) {
					newLabel.getComponent().setName(str);
				}

				if (str == null)
					JOptionPane.showMessageDialog(
							null,
							InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_INVALID).replace("\\n",
									System.getProperty("line.separator")));

				FormsUiController.getInstance().showProperties(); // Fabaris_m.cilione
																	// and
																	// Fabaris_a.zanchi
				FormsUiController.getInstance().editLabelOnPropertyField();
			}
		} else {
			// shows an alert say it is impossible to drop component in default
			// section
			JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ALERT_ONDROP_WITHIN_DEFAULTCOMPONENTS));
		}

	}

	/**
	 * Fabaris_a.zanchi private accessor for the method addToPreview used for
	 * the default components n Adds the supplied component to the preview in
	 * the supplied position.
	 * 
	 * @param component
	 *            Component to be added.
	 * @param x
	 * @param y
	 * @return
	 */
	private void addToPreviewPrivate(PaletteComponent component, int x, int y) {
		PreviewComponent newLabel = new PreviewComponent();
		newLabel.setDefaultComponent(true);
		newLabel.setComponent(component.getComponent().clone());
		// A.zanchi Sets the default component as required
		newLabel.getComponent().setRequired(true);
		pnPreview.addComponent(newLabel, x, y);
		this.validate();
		this.repaint();

	}

	/**
	 * Retrieves the selection from the preview.
	 * 
	 * @return
	 */
	public PreviewComponent getSelectedComponent() {
		return pnPreview.getSelectedItem() == null ? pnRepeatablePreview.getSelectedItem() : pnPreview.getSelectedItem();
	}
	
	
	/**
	 * Moves the supplied component in the preview to the supplied position.
	 * 
	 * @param component
	 *            Component to be moved.
	 * @param x
	 * @param y
	 */
	public void moveComponentInPreview(Component component, int x, int y) {
		// Fabaris_a.zanchi checks if the component is moved after the default
		// components.
		PreviewComponent pComp = pnPreview.getPComponentFromComponent(component);
		if (!pComp.isDefaultComponent()) { //avoid moving default components
			if (pnPreview.getIndex(y) > defaultComponents - 1)
				pnPreview.moveComponent(component, x, y);
			else {
				JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ALERT_ONDROP_WITHIN_DEFAULTCOMPONENTS));
			}
		}
	}

	/**
	 * Fabaris_a.zanchi Moves the supplied component in the preview of a
	 * repeatable container to the supplied position
	 * 
	 * @param component
	 * @param x
	 * @param y
	 */
	public void moveComponentInRepeatablePreview(Component component, int x, int y) {
		pnRepeatablePreview.moveComponent(component, x, y);
	}

	/**
	 * Fabaris_a.zanchi Method to add a palette component in the preview of a
	 * repeatable section
	 * 
	 * @param component
	 * @param x
	 * @param y
	 */
	public void addToRepeatablePreview(PaletteComponent component, int x, int y) {
		PreviewComponent newLabel = new PreviewComponent();
		newLabel.setComponent(component.getComponent().clone());
		if ((newLabel.getType() != FormFieldType.REPEATABLES) && (newLabel.getType() != FormFieldType.REPEATABLES_BASIC) && (newLabel.getType() != FormFieldType.SEPARATOR)
				&& (newLabel.getType() != FormFieldType.GEOLOCATION) && (newLabel.getType() != FormFieldType.BARCODE)  && (newLabel.getType() != FormFieldType.IMAGE)
                        && (newLabel.getType() != FormFieldType.SIGNATURE) // Added by Mureed
                        ) {
			pnRepeatablePreview.addComponent(newLabel, x, y);
			this.validate();
			this.repaint();
		}
		else {
			JOptionPane.showMessageDialog(this, InternationalisationUtils.getI18nString(ALERT_COMPONENT_NOT_ALLOWED));
			return;
		}
		// Fabaris_raji
		FormsUiController.getInstance().deSelectAllBeforeShowProperties();
		newLabel.setSelected(true);
		FormsEditorDialog.createAndShowGUI(newLabel.getComponent(),true);// Fabaris_raji
																	// alert for
																	// name of
																	// the
																	// component

		String str = newLabel.getComponent().getName();
		if (str != null) {
			newLabel.getComponent().setName(str);
		}
		if (str == null) {
			newLabel.getComponent().setName(str);
		}

		List<PreviewComponent> componentslstRep = new ArrayList<PreviewComponent>();
		componentslstRep = pnRepeatablePreview.getCurrentRepeatableSection().getRepeatables();
		// Fabaris_raji check for duplicate names in repeatables section
		boolean check = FormsUiController.getInstance().getMainFrame().checkDuplicateRepeatables(componentslstRep, newLabel, newLabel.getComponent().getName());
		if (check) {
			if (!str.equals("")) {
				JOptionPane.showMessageDialog(null, InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_KEY_MESSAGE_FIELD_NAME_EXISTS));
				newLabel.getComponent().setName("");
				FormsEditorDialog.createAndShowGUI(newLabel.getComponent(),true);//LL aggiunto ultimo parametro per bug sui nomi dei multiline

			}
		}
		FormsUiController.getInstance().showProperties();

	}

	/**
	 * Refreshes the preview.
	 */
	public void refreshPreview() {
		if (!isRepeatableOpen)
			pnPreview.refresh();
		// showSelectedComponent();
		pnRepeatablePreview.refresh();
	}

	/**
	 * Method invoked when the user moves the mouse during a drag and drop
	 * action. <br>
	 * We use the supplied position to draw a line in the preview to help the
	 * user to choose where to drop the component.
	 * 
	 * @param x
	 * @param y
	 */
	public void dragMoved(int x, int y) {
		Point p = pnPreview.getLocationOnScreen();
		Rectangle r = pnPreview.getBounds();
		if ((x > p.x && x <= p.x + r.width) && (y > p.y && y <= p.y + r.height)) {
			pnPreview.setDragY(y - p.y);
		} else {
			pnPreview.setDragY(-1);
		}
		pnPreview.validate();
		pnPreview.repaint();
	}

	/**
	 * Removes the selected component from the preview.
	 * 
	 * @param c
	 */
	public void deleteFromPreview(Component c) {
		// Fabaris_a.zanchi checks if the component index is out of the default
		// components range
		if (isRepeatableOpen) {

		} else if (pnPreview.indexOfComponent(c) > defaultComponents - 1 || defaultComponents == -1  || pnPreview.getPComponentFromComponent(c).getType()==FormFieldType.SEPARATOR) {
			// Checks if the component is a default one
			if (!pnPreview.getPComponentFromComponent(c).isDefaultComponent() || pnPreview.getPComponentFromComponent(c).getType()==FormFieldType.SEPARATOR) {

				/*
				 * // Fabaris_A.zanchi asks for simple confirmation int goOn =
				 * JOptionPane.showConfirmDialog(this,
				 * InternationalisationUtils.getI18nString(ALERT_ONDELETING),
				 * InternationalisationUtils
				 * .getI18nString(ALERT_ONDELETING_TITLE),
				 * JOptionPane.YES_NO_OPTION); if (goOn !=
				 * JOptionPane.YES_OPTION) return;
				 */

				
				// Fabaris_A.zanchi asks for simple confirmation
				
				//new DownAction("Down", null, "This is the down button", new Integer(KeyEvent.VK_DOWN)
				
//				JButton b1 = new JButton("Yes");  
//			    JButton b2 = new JButton("No");  
//			    b1.setMnemonic('Y');  
//			    b2.setMnemonic('N');  
//			    JButton[] options = {b1,b2};  
//			    
//			    b1.addActionListener(new ActionListener() {
//			    	public void actionPerformed(ActionEvent ae){  
//				        System.out.println("Yes");  
//				      }  
//				});
//			    
//			    b2.addActionListener(new ActionListener(){  
//			      public void actionPerformed(ActionEvent ae){  
//			        System.out.println("Cancel");  
//			      }  
//			    });  
//			    int goOn = JOptionPane.showOptionDialog(this,  InternationalisationUtils.getI18nString(ALERT_ONDELETING), InternationalisationUtils.getI18nString(ALERT_ONDELETING_TITLE),
//			    		-1,2,null, options, options[1]);  
//			    System.exit(0);  
				
				
				
				int goOn = JOptionPane.showOptionDialog(this, InternationalisationUtils.getI18nString(ALERT_ONDELETING),
						InternationalisationUtils.getI18nString(ALERT_ONDELETING_TITLE), JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION, null,
						new Object[] { "Yes", "No" }, "No");
				if (goOn == JOptionPane.YES_OPTION) {
					// return;

					// Fabaris_a.aknai
					// check for preview components using the 'c' component in
					// the
					// formula and poposing the alert for the user
					StringBuilder sb = new StringBuilder();
					// sb.append("This field is used in the following fields formula, deleting these fields formula will be lost.\n");
					sb.append(InternationalisationUtils.getI18nString(CALCULATED_ALERT_MESSAGE) + "\n");
					boolean showConfirmation = false;
					ArrayList<PreviewComponent> toEreaseFormula = new ArrayList<PreviewComponent>();
					for (int i = 0; i < getCurrent().getComponents().size(); i++) {
						PreviewComponent pc = getCurrent().getComponents().get(i);
						if (pc.getFormula() != null) {
							if (FormFormula.isInFormula(pc.getFormula(), pnPreview.getPComponentFromComponent(c))) {
								sb.append("In the field at position " + (i + 1) + " named '" + pc.getComponent().getName() + "'\n");
								toEreaseFormula.add(pc);
								showConfirmation = true;
							}
						}
						for (int j = 0; j < pc.getRepeatables().size(); j++) {
							PreviewComponent rpc = pc.getRepeatables().get(j);
							if (rpc.getFormula() != null) {
								if (FormFormula.isInFormula(rpc.getFormula(), pnPreview.getPComponentFromComponent(c))) {
									// sb.append("In the repetable at position "
									// +
									// (i + 1) + " for field named '" +
									// rpc.getComponent().getName() + "'\n");
									sb.append(InternationalisationUtils.getI18nString(CALCULATED_ALERT_COMPONENT, String.valueOf(i + 1), rpc.getComponent().getName()) + "\n");
									toEreaseFormula.add(rpc);
									showConfirmation = true;
								}
							}
						}
					}
					sb.append("Do you want to continue?");
					// if a confirmation is needed show confirmation
					// if response is differet form yes cancel the deletion
					// else delete formula from components using component and
					// delete component
					if (showConfirmation) {
						int response = JOptionPane.showConfirmDialog(this, sb.toString(), "Confirmation", JOptionPane.YES_NO_OPTION);
						if (response != JOptionPane.YES_OPTION)
							return;
						for (int x = 0; x < toEreaseFormula.size(); x++) {
							toEreaseFormula.get(x).setFormula(null);
						}
					}
					this.removeComponentFromBindings(c);
					pnPreview.removeComponent(c);
					this.validate();
					this.repaint();
					FormsUiController.getInstance().showProperties();
				}
			}
		}
	}

	/**
	 * Fabaris_a.zachi Removes the selected component from the repeatable
	 * section preview
	 * 
	 * @param c
	 */
	public void deleteFromRepatablePreview(Component c) {
		// Fabaris_A.zanchi asks for simple confirmation
		int goOn = JOptionPane.showOptionDialog(this, InternationalisationUtils.getI18nString(ALERT_ONDELETING), InternationalisationUtils.getI18nString(ALERT_ONDELETING_TITLE),
				JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION, null, new Object[] { "Yes", "No" }, "No");
		if (goOn != JOptionPane.YES_OPTION)
			return;

		pnRepeatablePreview.removeComponent(c);
	}

	public VisualForm getCurrent() {
		return pnPreview.getForm();
	}

	/**
	 * Prepare the preview for the supplied form.
	 * 
	 * @param form
	 * @param edit
	 * @param edit
	 */
	public void setForm(VisualForm form) {
		
		//sets the existing components in the first section as default components
		if (form.getComponents().size() != 0) {
			int defComps = DefaultComponentDescriptor.UNMODIFIABLE_COMPONENT_INDEX + 1;
                        int formComponentSize = form.getComponents().size();
                        int defCompsSize;
                        if ((defComps <=  formComponentSize)
                                && (form.getComponents().get(4).getComponent().getName().equals("gps"))
                                && (form.getComponents().get(4).getComponent().getLabel().equals("Geographic Location (GPS)"))) {
                            defCompsSize = defComps;
                        }else
                        {
                            DefaultComponentDescriptor.UNMODIFIABLE_COMPONENT_INDEX = 4;
                            defCompsSize = 5;
                        }

			System.out.println(defCompsSize);
			for (int i = 0; i < defCompsSize; i++) {
				form.getComponents().get(i).setDefaultComponent(true);
			}
		}
		pnPreview.showForm(form);
	}

	/*
	 * added by a.aknai
	 */
	public DropTarget bindPanelDT;

	/**
	 * a.zanchi Accepts the panel containing the Binding Table to create a
	 * DropTarget on it
	 * 
	 * @param bindPanel
	 *            An object of type Jpanel representing the panel containing the
	 *            Binding Table
	 */
	public void addBindingPanel(JPanel bindPanel) {
		// a.zanchi aggiunge il drop target per la tabella dei vincoli
		this.bindPanel = bindPanel;
		bindPanelDT = new DropTarget(bindPanel, DnDConstants.ACTION_COPY, dragListener);
		/*
		 * a.zanchi crea una drag gesture sulla tabella dei vincoli per spostare
		 * i suoi elementi nel cestino JScrollPane sp = (JScrollPane)
		 * bindPanel.getComponent(0); BindingTable bindTable = (BindingTable)
		 * sp.getViewport().getView();
		 * source.createDefaultDragGestureRecognizer(bindTable,
		 * DnDConstants.ACTION_COPY, dragListener);
		 */

	}

	public void addTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
		new DropTarget(tabbedPane, DnDConstants.ACTION_COPY, dragListener);
	}

	public void addTextAreaPanel(JPanel textareaPanel) {

		// this.textareaPanel = textareaPanel;

	}

	/**
	 * Fabaris_a.zanchi Method to activate the insertion of a new row in the
	 * table of Repeatables
	 * 
	 * @param c
	 *            object of type Component. Refers to the component dropped on
	 *            the table.
	 */
	/*
	 * public void insertNewRepeatable(Component c) {
	 * System.out.println("called repetable insertion"); PreviewComponent
	 * selected; if ((selected = getSelectedComponent()) != null) {
	 * PreviewComponent droppedComponent =
	 * pnPreview.getPComponentFromComponent(c); selected =
	 * pnPreview.getSelectedItem(); if (droppedComponent != selected) {
	 * JScrollPane sp = (JScrollPane) tabbedPane.getSelectedComponent();
	 * RepetableTable repTable = (RepetableTable) sp.getViewport().getView();
	 * RepetableTableModel model = (RepetableTableModel) repTable.getModel();
	 * model.addRow(new Object[] { droppedComponent });
	 * selected.addRepeatable(droppedComponent); } } }
	 */
	/**
	 * Fabaris_a.zanchi Method to activate the insertion of a new row in the
	 * Table of Bindings
	 * 
	 * @param c
	 *            object of type Component. Refers to the component dropped on
	 *            the table.
	 */
	public void insertNewBinding(Component c) {
		PreviewComponent selected;
		if ((selected = getSelectedComponent()) != null) {
			// System.out.println(pnPreview.getPComponentFromComponent(c).getComponent().getLabel());
			PreviewComponent droppedComponent = pnPreview.getPComponentFromComponent(c);
			if (droppedComponent == null) {
				// this make inserting new binding possible between repeatables
				// components
				droppedComponent = pnRepeatablePreview.getPComponentFromComponent(c);
			}
			// selected = pnPreview.getSelectedItem();
			// check the selected component and the dropped component to avoid
			// creating a "self-binding"
			if (droppedComponent != selected) {
				if (droppedComponent.getType() != FormFieldType.SEPARATOR && droppedComponent.getType() != FormFieldType.REPEATABLES
						&& droppedComponent.getType() != FormFieldType.REPEATABLES_BASIC && droppedComponent.getType() != FormFieldType.WRAPPED_TEXT
						&& droppedComponent.getType() != FormFieldType.TRUNCATED_TEXT) {
					int droppedIndex = pnPreview.indexOfComponent(c);
					int selectedIndex = pnPreview.indexOfComponent(selected.getDrawComponent());
					boolean allowBinding = false;

					if (!isRepeatableOpen) {
						// repeatables panel is closed
						if (droppedIndex < selectedIndex)
							allowBinding = true;
					} else {
						if (droppedIndex != -1) {
							// we are dropping an element NOT in repeatables
							// panel
							int repeatableContainerIndex = pnPreview.indexOfComponent(pnRepeatablePreview.getCurrentRepeatableSection().getDrawComponent());
							if (droppedIndex < repeatableContainerIndex)
								allowBinding = true;
						} else {
							// we are creating a binding between components in a
							// repeatable section
							droppedIndex = pnRepeatablePreview.indexOfComponent(c);
							selectedIndex = pnRepeatablePreview.indexOfComponent(selected.getDrawComponent());
							if (droppedIndex < selectedIndex)
								allowBinding = true;
						}
					}

					// This check makes sure there is not a binding on a
					// component who will be compiled later in the form
					if (allowBinding) {
						// get the table of bindings from panel
						JScrollPane sp = (JScrollPane) bindPanel.getComponent(0);

						// we are managing a binding
						BindingTable bindTable = (BindingTable) sp.getViewport().getView();
						// add row to the list
						BindingTableModel tModel = (BindingTableModel) bindTable.getModel();
						BindingContainer bCont = new BindingContainer(BindType.EQUALS, "", 0, 0);
						tModel.addRow(new Object[] { droppedComponent, bCont });
						selected.addBinding(droppedComponent, bCont);
					} else {
						// message for drop of a component not preceeding the
						// component with conditions
						String errorMessage = InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_DROP_DISABLE_NOPREVIOUS);
						JOptionPane.showMessageDialog(this, errorMessage);
					}
				} else {
					// message for drop of not allowed components
					String errorMessage = InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_DROP_DISABLED_ONTYPE, droppedComponent.getType().getNiceName());
					JOptionPane.showMessageDialog(this, errorMessage);
				}
			} else {
				// message for drop on component itself
				String errorMessage = InternationalisationUtils.getI18nString(FormsThinletTabController.ERROR_DROP_DISABLED_ONSELF).replace("\\n",
						System.getProperty("line.separator"));
				JOptionPane.showMessageDialog(this, errorMessage);
			}
		}
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Retrieve the Binding to remove from the table of bindings and removes it
	 * from the selected PreviewComponent
	 */
	public void removeSelectedBinding() {
		JScrollPane sp = (JScrollPane) bindPanel.getComponent(0);
		BindingTable bindTable = (BindingTable) sp.getViewport().getView();
		Object[] rowToRemove = bindTable.removeSelectedBinding();
		if(rowToRemove != null && rowToRemove.length != 0){
			PreviewComponent cToRemove = (PreviewComponent) rowToRemove[0];
			BindingContainer bCont = (BindingContainer) rowToRemove[1];
			PreviewComponent selected = getSelectedComponent();
			selected.removeBinding(cToRemove, bCont);
		}
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * removes the selected component from the bindings it is in.
	 * 
	 * @param c
	 */
	public void removeComponentFromBindings(Component c) {
		PreviewComponent removedComponent = pnPreview.getPComponentFromComponent(c);
		List<PreviewComponent> allComponents = pnPreview.getForm().getComponents();
		for (PreviewComponent comp : allComponents) {
			comp.removeAllBindings(removedComponent);
		}
	}

	/**
	 * Fabaris_a.zanchi Method to set the bindings AND-OR policy Please use
	 * strings "All" and "Any"
	 * 
	 * @param policy
	 */
	public void setFormBindingsPolicy(String policy) {
		this.pnPreview.getForm().setBindingsPolicy(policy);
	}

	/**
	 * Fabaris_a.zanchi Method to retrieve the Binding Policy returns "All" or
	 * "Any"
	 * 
	 * @return
	 */
	public String getFormBindingsPolicy() {
		if (this.pnPreview.getForm() != null)
			return this.pnPreview.getForm().getBindingsPolicy();
		else
			return null;
	}

	/**
	 * Fabaris_a.zanchi Highlights a component
	 * 
	 * @param comp
	 */
	public void highlightComponent(PreviewComponent comp, Color color) {
		refreshPreview();
		// Color for the highlighted component: a light green!
		if (color == null)
			comp.getDrawComponent().setBackground(PreviewComponent.HIGHLIGHT_BACKGROUND);
		else
			comp.getDrawComponent().setBackground(color);

	}

	/**
	 * Fabaris_a.zanchi Add default components to the new form
	 */
	public void addDefaultComponents(/* FComponent[] types */DefaultComponentDescriptor defCompDesc) {

		if (this.getCurrent().getComponents().size() == 0) {
			/*
			 * Here the UNMODIFIABLE_COMPONENT_INDEX is added with 1 because it
			 * is an index but in the methods it is used a position (index + 1)
			 */
			this.defaultComponents = DefaultComponentDescriptor.UNMODIFIABLE_COMPONENT_INDEX + 1;
			for (FComponent fType : defCompDesc.getDefaultComponents()) {
				PaletteComponent pComp = this.pnPalette.getPaletteFromComponent(fType);
				addToPreviewPrivate(pComp, 1, 100);

			}
		} else { // this means the form is in editing state
			this.defaultComponents = DefaultComponentDescriptor.UNMODIFIABLE_COMPONENT_INDEX + 1;
		}
	}

	/**
	 * Fabaris_a.zanchi shows the details of repeatable container
	 */
	public void showRepeatableContainerDetails() {
		if (getSelectedComponent().getType() == FormFieldType.REPEATABLES || getSelectedComponent().getType() == FormFieldType.REPEATABLES_BASIC) {
			if (isRepeatableOpen == false) {

				PreviewComponent selected = getSelectedComponent();
				// int index =
				// pnPreview.indexOfComponent(selected.getDrawComponent());
				Rectangle pnPreviewRect = new Rectangle(pnPreview.getParent().getLocationOnScreen().x, pnPreview.getParent().getLocationOnScreen().y, 340, 550);
				int breakPoint = ((pnPreviewRect.y + pnPreviewRect.y + pnPreviewRect.height) / 2) + 50;

				int offset = selected.getDrawComponent().getLocationOnScreen().y; // +
																					// selected.getDrawComponent().getHeight();
				pnPreviewDrop.setActive(false);

				scrollPreview.setWheelScrollingEnabled(false);
				scrollPreview.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				pnPreview.darken();
				pnPreview.setIdle(true);

				scrollPreview.getActionMap().put("unitScrollUp", new AbstractAction() {

					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub

					}
				});
				scrollPreview.getActionMap().put("unitScrollDown", new AbstractAction() {

					public void actionPerformed(ActionEvent arg0) {

					}
				});

				// * if the repeatable section component is a little below the
				// * half of the panel its preview panel is drawn on top

				if (offset < breakPoint) {
					// add(scrollRepeatablePreview, new SimpleConstraints(200,
					// offset - pnPreviewRect.y + 30, 340, (pnPreviewRect.y +
					// pnPreviewRect.height) - (offset + 30)));
					add(scrollRepeatablePreview, new SimpleConstraints(200, offset - pnPreviewRect.y + 50, 450, (pnPreviewRect.y + pnPreviewRect.height) - (offset - 30)));
					// Fabaris_raji modified 340 to 450
				} else
					// add(scrollRepeatablePreview, new SimpleConstraints(200,
					// 20, 340, HEIGHT - 50 - (HEIGHT - offset) - 80));
					// add(scrollRepeatablePreview, new SimpleConstraints(200,
					// 20, 340, offset - pnPreviewRect.y - 20));
					add(scrollRepeatablePreview, new SimpleConstraints(200, 20, 450, offset - pnPreviewRect.y - 30));// Fabaris_raji
																														// modified
																														// 340
																														// to
																														// 450
				this.setComponentZOrder(scrollRepeatablePreview, 0);
				// this.validate();
				// this.repaint();
				pnRepeatablePreview.setCurrentRepeatableSection(selected);
				isRepeatableOpen = true;
				this.validate();

			} else {
				
				if (getSelectedComponent() == pnRepeatablePreview.getCurrentRepeatableSection()) {
					this.remove(scrollRepeatablePreview);
					this.setComponentZOrder(scrollPreview, 0);
					this.validate();
					isRepeatableOpen = false;
					pnPreviewDrop.setActive(true);
					scrollPreview.setWheelScrollingEnabled(true);
					scrollPreview.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
					pnPreview.setIdle(false);
					scrollPreview.getActionMap().put("unitScrollUp", new AbstractAction() {

						public void actionPerformed(ActionEvent arg0) {
							int currentValue = scrollPreview.getVerticalScrollBar().getValue();
							scrollPreview.getVerticalScrollBar().setValue(currentValue - 20);

						}
					});
					scrollPreview.getActionMap().put("unitScrollDown", new AbstractAction() {

						public void actionPerformed(ActionEvent arg0) {
							int currentValue = scrollPreview.getVerticalScrollBar().getValue();
							scrollPreview.getVerticalScrollBar().setValue(currentValue + 20);

						}
					});

					pnPreview.lighten();
				}
				// highlightComponent(getSelectedComponent(), null);
			}
		}
		// highlightComponent(getSelectedComponent(), null);
		pnPreview.refresh();
		this.validate();
		this.repaint();
	}

	/**
	 * Fabaris_a.zanchi Method override to allow correct drawing of overlapping
	 * scrollViews (preview panel and repeatable section preview panel
	 */
	@Override
	public boolean isOptimizedDrawingEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * Deselect every component in both the preview panel and repeatable preview
	 * panel
	 */
	public void deselectEverything() {
		pnPreview.deselectAll();
		if (pnRepeatablePreview != null)
			pnRepeatablePreview.deselectAll();

	}

	/**
	 * Fabaris_A.zanchi scrolls the scroll preview to include the selected
	 * component in the viewing area
	 * 
	 */
	public void showSelectedComponent() {
		PreviewComponent pc = this.getSelectedComponent();
		if (pc != null) {
			// this.scrollPreview.scrollRectToVisible(pc.getComponent().getDrawingComponent().getBounds());

		}
	}

	public boolean isRepeatableOpen() {
		return isRepeatableOpen;
	}

	public void setRepeatableOpen(boolean isRepeatableOpen) {
		this.isRepeatableOpen = isRepeatableOpen;
	}
}