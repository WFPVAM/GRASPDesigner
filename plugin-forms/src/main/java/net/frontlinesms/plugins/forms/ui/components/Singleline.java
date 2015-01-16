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
	
	@SuppressWarnings("serial")
	public class Singleline extends NiceComponent {
		
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

}
