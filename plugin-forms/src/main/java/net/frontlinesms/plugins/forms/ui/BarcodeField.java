package net.frontlinesms.plugins.forms.ui;

import net.frontlinesms.plugins.forms.ui.components.NiceComponent;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class BarcodeField extends /*TextField*/ NiceComponent{

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FCOMP_BARCODE);
	}
	
	@Override
	public String getIcon() {
		// TODO Auto-generated method stub
		return "barcode.png";
	}
	
/*	@Override
	public Container getDrawingComponent() {
		JPanel pn = new JPanel();
		pn.setLayout(new FlowLayout());
		//ImageIcon repIcon = new ImageIcon("src/main/resources/icons/components/repeatablesfield.png");
		ImageIcon repIcon = new ImageIcon(FormsThinletTabController.getCurrentInstance().getIcon(FormFieldType.GEOLOCATION));
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

