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

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.PluginManager;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.plugins.forms.ui.FormsUiController;
import net.frontlinesms.ui.Icon;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**CLass modeling a Repeatable Field
 * 
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 *
 */
@SuppressWarnings("serial")
public class Repeatables extends /*TextField*/ NiceComponent{

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FCOMP_REPEATABLES);
	}

	@Override
	public String getIcon() {
		// TODO Auto-generated method stub
		return "repeatablesfield.png";
	}

/*	@Override
	public Container getDrawingComponent() {
		JPanel pn = new JPanel();
		pn.setLayout(new FlowLayout());
		//ImageIcon repIcon = new ImageIcon("src/main/resources/icons/components/repeatablesfield.png");
		ImageIcon repIcon = new ImageIcon(FormsThinletTabController.getCurrentInstance().getIcon(FormFieldType.REPEATABLES));
		JLabel desc = new JLabel(super.getDisplayLabel() + ":");
		//JLabel desc = new JLabel(super.getDisplayLabel() + ":");
		desc.setForeground(new Color(0,0,128));
		desc.setBackground(new Color(255,36,0));
		JLabel iconLabel = new JLabel(repIcon);
		pn.add(desc);
		pn.add(iconLabel);
		int width = desc.getFontMetrics(desc.getFont()).stringWidth(desc.getText());
		width = renderWidth - width;
		return pn;
	
	}*/
	
	

	
}
