package net.frontlinesms.plugins.forms.ui.components;

import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class SignatureField extends NiceComponent
{
	@Override
	public String getDescription() {
		return InternationalisationUtils.getI18nString(FormsThinletTabController.I18N_FCOMP_SIGNATURE);
	}
	
	@Override
	public String getIcon() {
		return "signature.png";
	}
}

