package net.frontlinesms.plugins.forms.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class DefaultValueDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane = new JPanel();
	
	private SurveyEditorController ctrl;
	
	private Survey selectedSurvey;
	
	private final static String ASSIGN_DEFAULT_VALUE = "plugin.defaultvaluedialog.buttonok";
	
	public DefaultValueDialog(SurveyEditorController ctrl , JDialog owner,Survey selectedSurvey) {
		super(owner, true);
		this.ctrl = ctrl;
		this.selectedSurvey = selectedSurvey;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new FlowLayout());
		contentPane = new JPanel();
		setContentPane(contentPane);
		initContentPanel();
		this.pack();
		this.centerDialog();
	}
	
	private void centerDialog(){
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = toolkit.getScreenSize();
		final int x = (screenSize.width - this.getWidth()) / 2;
		final int y = (screenSize.height - this.getHeight()) / 2;
		this.setLocation(x, y);
	}
	
	private void initContentPanel(){
		List<SurveyElement> elements = new ArrayList<SurveyElement>(selectedSurvey.getValues());
		elements.add(0, null);
		JComboBox combo = new JComboBox(elements.toArray());
		SurveyElement element = getDefaultElement(elements.toArray());
		JButton button = new JButton(InternationalisationUtils.getI18nString(ASSIGN_DEFAULT_VALUE));
		button.addActionListener(new AssignDefaultValue(this,selectedSurvey,combo));
		this.contentPane.add(combo);
		this.contentPane.add(button);
		if(element != null){
			combo.setSelectedItem(element);
		}
	}
	
	private SurveyElement getDefaultElement(Object[] items) {
		for (Object object : items) {
			SurveyElement el = (SurveyElement)object;
			if(el != null){
				if(el.isDefaultValue()){
					return el;
				}
			}
		}
		return null;
	}
	
	private class AssignDefaultValue implements ActionListener{
		
		private DefaultValueDialog dialog;
		
		private JComboBox combo;
		
		private Survey survey;
		
		public AssignDefaultValue(DefaultValueDialog dialog,Survey survey,JComboBox combo) {
			this.dialog = dialog;
			this.combo = combo;
			this.survey = survey;
		}
		
		public void actionPerformed(ActionEvent e) {
			SurveyElement selectedItem = (SurveyElement)combo.getSelectedItem();
			if (selectedItem == null) {
				removeDefaultValueInSurveyElement();
				ctrl.updateSurvey(this.survey);
			}else{
				removeDefaultValueInSurveyElement();
				selectedItem.setDefaultValue(true);
				ctrl.updateSurvey(this.survey);
			}
			this.dialog.dispose();
		}
		
		private void removeDefaultValueInSurveyElement(){
			for (SurveyElement el : selectedSurvey.getValues()) {
				if(null != el)
					el.setDefaultValue(false);
			}
		}
		
	}
}