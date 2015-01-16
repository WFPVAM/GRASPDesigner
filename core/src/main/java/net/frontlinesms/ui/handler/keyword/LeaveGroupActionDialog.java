package net.frontlinesms.ui.handler.keyword;

import static net.frontlinesms.FrontlineSMSConstants.COMMON_AUTO_LEAVE_GROUP;
import static net.frontlinesms.FrontlineSMSConstants.COMMON_KEYWORD;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * @author aga
 */
public class LeaveGroupActionDialog extends BaseGroupActionDialog {
	
	LeaveGroupActionDialog(UiGeneratorController ui, KeywordTabHandler owner) {
		super(ui, owner);
	}

	/** @see BaseGroupActionDialog#getDialogTitle() */
	@Override
	protected String getDialogTitle() {
		return InternationalisationUtils.getI18nString(COMMON_KEYWORD)
				+ " \"" + KeywordTabHandler.getDisplayableKeyword(super.getTargetKeyword()) + "\" "
				+ InternationalisationUtils.getI18nString(COMMON_AUTO_LEAVE_GROUP) + ":";
	}

	@Override
	public void save() {
		super.save(false);
	}
}
