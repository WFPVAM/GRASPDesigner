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

package net.frontlinesms.plugins.forms.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.plugins.forms.ui.PreviewPanel;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * FComponent for displaying and editing a form currency field.
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
@SuppressWarnings("serial")
public class Separator extends NiceComponent {
	
	private static String LONG_ICON_NAME = "longline.png";
	
	private JPanel panel;
	
	/** @see FComponent#getDescription() */
	@Override
	public String getDescription() {
		return InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FCOMP_SEPARATOR);
	}

	/** @see FComponent#getIcon() */
	@Override
	public String getIcon() {
		return "line.png";
	}
	/** @see FComponent#getDrawingComponent() */
	@Override
	public Container getDrawingComponent() {
		Container cont = super.getDrawingComponent();
		Component icon = pn.getComponent(2);
		JLabel iconLabel = (JLabel) icon;
		iconLabel.setIcon(new ImageIcon(FrontlineUtils.getImage("/icons/components/" + LONG_ICON_NAME, PreviewPanel.class)));
		pn.remove(3);
		pn.remove(2);
		pn.remove(1);
		
		GridBagConstraints gbc_iconLabel = new GridBagConstraints();
		gbc_iconLabel.anchor = GridBagConstraints.WEST;
		gbc_iconLabel.fill = GridBagConstraints.VERTICAL;
		gbc_iconLabel.gridx = 1;
		gbc_iconLabel.gridwidth = 2;
		gbc_iconLabel.gridy = 0; 
		
		pn.add(icon,gbc_iconLabel);
		return cont; 

	}
	
	/*@Override
	public Container getDrawingComponent() {
		
		ComponentPanel panel = new ComponentPanel();

		panel.getNameLabel().setText(super.getDisplayName() + ":");
		panel.getIconLabel().setIcon(new ImageIcon(FrontlineUtils.getImage("/icons/components/" + getIcon(), PreviewPanel.class)));
		panel.remove(panel.getTextArea());
		
		
		
		FontMetrics m = panel.getTextArea().getFontMetrics(panel.getTextArea().getFont());
		

		
		panel.setPreferredSize(new Dimension(panel.getWidth(), 35));
		setRenderHeight(35);

		
		return panel;
	}
	*/
	
	/*@Override
	public void darken() {
		for (Component comp : panel.getComponents()) {
			comp.setEnabled(false);
		}
			
		panel.validate();
		panel.repaint();
	}
	
	@Override
	public void lighten() {
		for (Component comp : panel.getComponents()) {
			comp.setEnabled(true);
		}
			
		panel.validate();
		panel.repaint();
	}
*/
}
