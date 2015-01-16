package net.frontlinesms.ui.handler.importexport;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.frontlinesms.csv.CsvImporter;
import net.frontlinesms.csv.CsvParseException;
import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.ui.FormsThinletTabController;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.core.FileChooser;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.rmi.runtime.Log;

/**
 * This class handles the form prototype import dialog.
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
public class FormPrototypeImportDialogHandler extends ImportDialogHandler {

	private static final String ERROR_FORMID_ALREADY_ONDB = "importexport.import.forms.error.alreadyexisting";
	private static final String ERROR_BAD_XFORM = "importexport.import.forms.error.badxform";

	// NOTE: > and < are correctly displayed when the xml is loaded in xml
	// document (instead of &gt; and &lt;)
	static final String EQUAL = "=";
	static final String NOT_EQUAL = "!=";
	static final String GREATER = ">";
	static final String LESSER = "<";
	static final String GREATER_EQUALS = ">=";
	static final String LESSER_EQUALS = "<=";

	static final List<String> bindSymbols;
	static {
		bindSymbols = new ArrayList<String>();
		bindSymbols.add(" " + EQUAL + " ");
		bindSymbols.add(" " + NOT_EQUAL + " ");
		bindSymbols.add(" " + GREATER + " ");
		bindSymbols.add(" " + LESSER + " ");
		bindSymbols.add(" " + GREATER_EQUALS + " ");
		bindSymbols.add(" " + LESSER_EQUALS + " ");
	}
	private String currentImportingFormName;
	private String currentImportingFormId;
	private Set<Survey> importingSurveys = new HashSet<Survey>();

	private Object exportDialog;
	private boolean openChooseCompleted = false;

	public FormPrototypeImportDialogHandler(UiGeneratorController ui) {
		super(ui, EntityType.FORMS);
		// TODO Auto-generated constructor stub
	}

	@Override
	void doSpecialImport(String dataPath) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected CsvImporter getImporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setImporter(String filename) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendPreviewHeaderItems(Object header) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object[] getPreviewRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getWizardTitleI18nKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getOptionsFilePath() {
		return UI_FILE_OPTIONS_PANEL_FORM;
	}

	/**
	 * This method "breaks" the execution flow and takes the responsibility to
	 * create a custom dialog window. instead of letting superclass(es) do the
	 * work
	 */
	@Override
	public void showWizard() {
		exportDialog = uiController.loadComponentFromFile("/ui/core/importexport/pnImportFormPrototype.xml", this);
		uiController.add(exportDialog);
		// populateList(filter);
	}

	/**
	 * Method call when the "Import" button is pressed
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 */
	@Override
	public void doImport(String filename) throws UnsupportedEncodingException {
		File formFile = new File(filename);
		byte[] buffer = new byte[(int) formFile.length()];
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(formFile));
		} catch (FileNotFoundException e) {
			uiController.alert(e.getMessage());
		}
		try {
			dis.readFully(buffer);
		} catch (IOException e) {
			uiController.alert(e.getMessage());
		}
		String xmlContent = new String(buffer, "UTF-8");

		// open xml document
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		// docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputSource inStream = new InputSource();
		inStream.setCharacterStream(new StringReader(xmlContent));
//		System.out.println("xmlContent:" + xmlContent);
		inStream.setEncoding("UTF-8");
		org.w3c.dom.Document xmlDoc = null;
		try {
			xmlDoc = docBuilder.parse(inStream);
		} catch (SAXException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, InternationalisationUtils.getI18nString(ERROR_BAD_XFORM), "", JOptionPane.ERROR_MESSAGE);
			uiController.removeDialog(exportDialog);
			return;

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, InternationalisationUtils.getI18nString(ERROR_BAD_XFORM), "", JOptionPane.ERROR_MESSAGE);
			uiController.removeDialog(exportDialog);
			return;
		}
		xmlDoc.getDocumentElement().normalize();

		currentImportingFormId = xmlDoc.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
		System.out.println(currentImportingFormId);

		// get form name
		String formName = xmlDoc.getElementsByTagName("h:title").item(0).getFirstChild().getNodeValue();
		currentImportingFormName = formName;

		// initialize xpath expression compiler
		XPathFactory fact = XPathFactory.newInstance();
		XPath xpath = fact.newXPath();
		// xpath.setNamespaceContext(new Nam)
		XPathExpression expr = null;

		// Searches for existing forms with same ID on database
		if (FormsThinletTabController.getCurrentInstance().getPluginController().getFormDao().getFromId_IdMac(currentImportingFormId) != null) {
			uiController.infoMessage(InternationalisationUtils.getI18nString(ERROR_FORMID_ALREADY_ONDB));
		}

		else {

			// - - -
			// Search for bindings
			// - - -
			ArrayList<ImportBinding> bindList = new ArrayList<FormPrototypeImportDialogHandler.ImportBinding>();
			NodeList bindings = xmlDoc.getElementsByTagName("bind");
			System.out.println("Found " + bindings.getLength() + " bind section");
			for (int i = 0; i < bindings.getLength(); i++) {
				ImportBinding importBind = new ImportBinding();
				Element anElem = (Element) bindings.item(i);
				importBind.nodeset = anElem.getAttribute("nodeset");
				importBind.type = anElem.getAttribute("type");
				if (anElem.getAttribute("required").equals("true()")) {
					importBind.required = true;
				} else {
					importBind.required = false;
				}
				if (anElem.getAttribute("readonly").equals("true()")) {
					importBind.readOnly = true;
				} else {
					importBind.readOnly = false;
				}
				importBind.constraint = anElem.getAttribute("constraint");
				importBind.constraintMessage = anElem.getAttribute("jr:constraintMsg");
				importBind.relevant = anElem.getAttribute("relevant");
				importBind.generateNameReference();
				bindList.add(importBind);

				// gets the content of tag (for eventual use)
				String path = "/" + importBind.nodeset;
				System.out.println(path);
				try {
					expr = xpath.compile(path);
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					// Node tagContentNode = (Node)expr.evaluate(xmlDoc,
					// XPathConstants.NODE);
					// System.out.println(expr.evaluate(xmlDoc));
					importBind.tagContent = (String) expr.evaluate(xmlDoc);
					System.out.println("TAG CONTENT " + importBind.tagContent);
				} catch (XPathExpressionException e) { // TODO Auto-generated
														// catch
														// block
					e.printStackTrace();
				}
			}

			// search for components
			ArrayList<ImportingElement> importingElemList = new ArrayList<FormPrototypeImportDialogHandler.ImportingElement>();

			Element body = (Element) xmlDoc.getElementsByTagName("h:body").item(0);

			NodeList groups = body.getElementsByTagName("group");
			System.out.println("Found " + groups.getLength() + " groups");
			for (int i = 0; i < groups.getLength(); i++) {
				Element g = (Element) groups.item(i);
				// Check if the group is a field list or a repeatable group
				if (g.getAttribute("appearance").equals("field-list") && (g.getParentNode().getNodeName().equals("h:body"))) {
					// this is an "actual" group placed in the root

					/**
					 * CYCLE FOR NORMAL ITEMS!!!
					 */
					NodeList groupElements = groups.item(i).getChildNodes();
					System.out.println("Found " + groupElements.getLength() + " elements in this group");
					for (int j = 0; j < groupElements.getLength(); j++) {
						if (groupElements.item(j).getNodeType() == 1) {
							if (groupElements.item(j).getNodeName().equals("input")) {
								System.out.println("Found an input node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) groupElements.item(j);
								importingElem.ref = anElem.getAttribute("ref");
								importingElem.generateName();
								importingElem.type = "input";
								importingElem.typeAttribute = anElem.getAttribute("type");
								if (!importingElem.typeAttribute.contains("label")) {
									Node nodeItem = anElem.getElementsByTagName("label").item(0);
									Node nodeChild = nodeItem.getFirstChild();
									if(nodeChild==null)
									{
										String s="ciao";
										
									}
									String nodeVal = nodeChild.getNodeValue();
									
									importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
									
								} else {
									importingElem.label = anElem.getAttribute("labelvalue");
								}
								String calc = anElem.getAttribute("calc");
								if (!calc.equals(""))
									importingElem.calculated = calc;
								importingElemList.add(importingElem);
							}
							if (groupElements.item(j).getNodeName().equals("select")) {
								System.out.println("Found a select node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) groupElements.item(j);
								NodeList items = anElem.getElementsByTagName("item");
								Element uniqueItem = (Element) items.item(0);
								importingElem.label = uniqueItem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.ref = anElem.getAttribute("ref");
								importingElem.generateName();
								importingElem.type = "select";
								importingElem.typeAttribute = anElem.getAttribute("type");
								importingElemList.add(importingElem);

							}

							if (groupElements.item(j).getNodeName().equals("select1")) {
								System.out.println("Found a select1 node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) groupElements.item(j);
								importingElem.select1Aappearance = anElem.getAttribute("appearance");
								importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.ref = anElem.getAttribute("ref");
								importingElem.refListName = anElem.getAttribute("reflist");
								importingElem.generateName();
								NodeList itemList = anElem.getElementsByTagName("item");
								System.out.println("Found " + itemList.getLength() + " items in select1 node");
								for (int z = 0; z < itemList.getLength(); z++) {
									Element item = (Element) itemList.item(z);
									String labelValue = item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
									String itemValue = item.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
									if (!(labelValue.equals("Select") && itemValue.equals("0")))
										importingElem.select1Labels.add(item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue());
								}
								importingElem.type = "select1";
								importingElem.typeAttribute = anElem.getAttribute("type");

								// if this is a select1 for currency ignore it
								if (!importingElem.typeAttribute.equals("currency"))
									importingElemList.add(importingElem);
							}

						}
					}

				} else if ((g.getAttribute("appearance").equals("")) && (g.getAttribute("value").equals("repeatable-survey")) && (g.getParentNode().getNodeName().equals("h:body"))) {

					/**
					 * THIS IS FOR REPEATABLE WITH SURVEY!!
					 */
					// this is a repeatable group tied with a survey
					ImportingElement newRepElem = new ImportingElement();
					newRepElem.isRepContainer = true;
					//newRepElem.label = g.getElementsByTagName("label").item(0).getFirstChild().getNodeValue(); //LL 20-05-2014
					Node labelfirstchild = g.getElementsByTagName("label").item(0).getFirstChild();//LL 20-05-2014 gestito errore nel caso in cui una lable di un roster non viene valorizzata
					if( labelfirstchild != null){                                                   //LL 20-05-2014
						newRepElem.label = labelfirstchild.getNodeValue();                         //LL 20-05-2014
					}else{																			//LL 20-05-2014
						newRepElem.label = "";														//LL 20-05-2014
					}	
							
							
					
					
					
					String groupRef = g.getAttribute("ref");
					newRepElem.name = groupRef;
					newRepElem.ref = groupRef;
					newRepElem.refListName = g.getAttribute("reflist");

					// retrieves a list of survey elements examining the
					// repetitions
					NodeList surveyElements = g.getElementsByTagName("group");
					for (int j = 0; j < surveyElements.getLength(); j++) {
						Element el = (Element) surveyElements.item(j);
						if (el.getAttribute("appearance").equals("field-list")) {
							newRepElem.surveyValues.add(el.getElementsByTagName("label").item(0).getFirstChild().getNodeValue());
						}
					}

					// Cycle in the first repetition

					/**
					 * HERE IT SEARCHES IN THE FIRST REPETITION
					 */
					NodeList firstRep = surveyElements.item(0).getChildNodes();
					for (int j = 0; j < firstRep.getLength(); j++) {
						if (firstRep.item(j).getNodeType() == 1) {
							if (firstRep.item(j).getNodeName().equals("input")) {
								System.out.println("Found an input node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) firstRep.item(j);
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								// importingElem.label =
								// anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.typeAttribute = anElem.getAttribute("type");
								if (!importingElem.typeAttribute.contains("label")) {
									importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								} else {
									importingElem.label = anElem.getAttribute("labelvalue");
								}
								importingElem.generateName();
								importingElem.type = "input";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								String calc = anElem.getAttribute("calc");
								if (!calc.equals(""))
									importingElem.calculated = calc;
								newRepElem.repElements.add(importingElem);
							}
							if (firstRep.item(j).getNodeName().equals("select")) {
								System.out.println("Found a select node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) firstRep.item(j);
								NodeList items = anElem.getElementsByTagName("item");
								Element uniqueItem = (Element) items.item(0);
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								importingElem.label = uniqueItem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.typeAttribute = anElem.getAttribute("type");
								importingElem.generateName();
								importingElem.type = "select";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								newRepElem.repElements.add(importingElem);

							}

							if (firstRep.item(j).getNodeName().equals("select1")) {
								System.out.println("Found a select1 node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) firstRep.item(j);
								importingElem.select1Aappearance = anElem.getAttribute("appearance");
								importingElem.refListName = anElem.getAttribute("reflist");
								importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								NodeList itemList = anElem.getElementsByTagName("item");
								System.out.println("Found " + itemList.getLength() + " items in select1 node");
								for (int z = 0; z < itemList.getLength(); z++) {
									Element item = (Element) itemList.item(z);
									String labelValue = item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
									String itemValue = item.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
									if (!(labelValue.equals("Select") && itemValue.equals("0")))
										importingElem.select1Labels.add(item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue());

								}
								importingElem.type = "select1";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								importingElem.typeAttribute = anElem.getAttribute("type");
								importingElem.generateName();
								importingElem.repContainer = newRepElem;

								// if this is a select1 for currency ignore it
								if (!importingElem.typeAttribute.equals("currency"))
									newRepElem.repElements.add(importingElem);

							}

						}
					}

					System.out.println("Element in repeatable section: " + newRepElem.repElements.size());
					newRepElem.type = "repeatables";
					newRepElem.fieldType = FormFieldType.REPEATABLES;
					System.out.println(newRepElem.repElements.size());
					importingElemList.add(newRepElem);
				}

				else if ((g.getAttribute("appearance").equals("")) && (g.getParentNode().getNodeName().equals("h:body"))) {
					// this is a repeatable group
					ImportingElement newRepElem = new ImportingElement();
					newRepElem.isRepContainer = true;
					newRepElem.label = g.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
					Element repeat = (Element) g.getElementsByTagName("repeat").item(0);
					newRepElem.ref = repeat.getAttribute("nodeset");
					String jrCount = repeat.getAttribute("jr:count");

					String groupRef = g.getAttribute("ref");
					newRepElem.ref = groupRef;
					// check if the element has fixed repetitions
					if (!jrCount.equals("")) {
						// System.out.println(jrCount);
						jrCount = cutData(jrCount);
						// System.out.println(jrCount);
						newRepElem.numberOfReps = Integer.valueOf(xmlDoc.getElementsByTagName(jrCount).item(0).getFirstChild().getNodeValue().trim());

					}
					newRepElem.name = newRepElem.cutReferenceName(newRepElem.ref);

					/**
					 * CYCLE FOR REPEATABLE ITEMS!!!
					 */
					NodeList repElements = repeat.getElementsByTagName("group").item(0).getChildNodes();
					// System.out.println("Found " + repElements.getLength() +
					// " elements in this group");
					for (int j = 0; j < repElements.getLength(); j++) {
						if (repElements.item(j).getNodeType() == 1) {
							if (repElements.item(j).getNodeName().equals("input")) {
								System.out.println("Found an input node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) repElements.item(j);
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								// importingElem.label =
								// anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.typeAttribute = anElem.getAttribute("type");
								if (!importingElem.typeAttribute.contains("label")) {
									importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								} else {
									importingElem.label = anElem.getAttribute("labelvalue");
								}
								importingElem.generateName();
								importingElem.type = "input";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								String calc = anElem.getAttribute("calc");
								if (!calc.equals(""))
									importingElem.calculated = calc;
								newRepElem.repElements.add(importingElem);
							}
							if (repElements.item(j).getNodeName().equals("select")) {
								System.out.println("Found a select node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) repElements.item(j);
								NodeList items = anElem.getElementsByTagName("item");
								Element uniqueItem = (Element) items.item(0);
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								importingElem.label = uniqueItem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								importingElem.typeAttribute = anElem.getAttribute("type");
								importingElem.generateName();
								importingElem.type = "select";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								newRepElem.repElements.add(importingElem);

							}

							if (repElements.item(j).getNodeName().equals("select1")) {
								System.out.println("Found a select1 node");
								ImportingElement importingElem = new ImportingElement();
								Element anElem = (Element) repElements.item(j);
								importingElem.select1Aappearance = anElem.getAttribute("appearance");
								importingElem.refListName = anElem.getAttribute("reflist");
								importingElem.label = anElem.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
								NodeList itemList = anElem.getElementsByTagName("item");
								System.out.println("Found " + itemList.getLength() + " items in select1 node");
								for (int z = 0; z < itemList.getLength(); z++) {
									Element item = (Element) itemList.item(z);
									String labelValue = item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
									String itemValue = item.getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
									if (!(labelValue.equals("Select") && itemValue.equals("0")))
										importingElem.select1Labels.add(item.getElementsByTagName("label").item(0).getFirstChild().getNodeValue());

								}
								importingElem.type = "select1";
								// importingElem.detectFormFieldType();
								importingElem.isRepItem = true;
								importingElem.repContainer = newRepElem;
								importingElem.ref = groupRef + "/" + anElem.getAttribute("ref");
								importingElem.typeAttribute = anElem.getAttribute("type");
								importingElem.generateName();
								importingElem.repContainer = newRepElem;
								// if this is a select1 for currency ignore it
								if (!importingElem.typeAttribute.equals("currency"))
									newRepElem.repElements.add(importingElem);

							}

						}
					}
					newRepElem.type = "repeatables";
					newRepElem.fieldType = FormFieldType.REPEATABLES_BASIC;
					System.out.println(newRepElem.repElements.size());
					importingElemList.add(newRepElem);

				}
			}

			pairElementsAndBinding(importingElemList, bindList);

			// build a list of total elements including repeatables in
			// repeatable
			// containers
			List<ImportingElement> totalElements = new ArrayList<FormPrototypeImportDialogHandler.ImportingElement>();
			for (ImportingElement elem : importingElemList) {
				totalElements.add(elem);
				if (elem.fieldType == FormFieldType.REPEATABLES || elem.fieldType == FormFieldType.REPEATABLES_BASIC) {
					for (ImportingElement rep : elem.repElements) {
						totalElements.add(rep);
					}
				}

			}

			for (ImportingElement elem : importingElemList) {
				elem.getIndexFormName();
				elem.detectFormFieldType();
				elem.detectBindings(totalElements);
				elem.detectConstraints();
			}

			importingElemList = discoverSeparators(importingElemList);

			debugState((ImportBinding[]) bindList.toArray(new ImportBinding[bindList.size()]));
			debugState((ImportingElement[]) importingElemList.toArray(new ImportingElement[importingElemList.size()]));

			// begins creation of formfields
			for (ImportingElement el : importingElemList) {
				el.generateFormField();
			}

			// Creates relevant bindings
			for (ImportingElement el : importingElemList) {
				el.generateFormFieldBindings();
			}

			// creates a VisualForm to save
			Form importingForm = new Form(formName);
			int count = 1;
			for (ImportingElement el : importingElemList) {
				// check if the very last element is a separator. In this case
				// it is
				// ignpred
				if ((count == importingElemList.size()) && (el.fieldType == FormFieldType.SEPARATOR)) {
				} else {
					importingForm.addField(el.generatedFormField);
				}
				count++;
			}

			// Saves surveys

			System.out.println("number of unique importing surveys: " + importingSurveys.size());
			for (Survey s : importingSurveys) {
				FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().saveSurvey(s);
			}
			// set the saved unique surveys to the formfield
			for (ImportingElement el : totalElements) {
				if (el.generatedFormField.getSurvey() != null) {
					for (Survey s : importingSurveys) {
						if (s.getName().equals(el.generatedFormField.getSurvey().getName()))
							el.generatedFormField.setSurvey(s);
					}
				}
			}

			// saves the form

			VisualForm vForm = VisualForm.getVisualForm(importingForm);
			vForm.setBindingsPolicy("All");
			importingForm.setBindingsPolicy("All");
			importingForm.setId_flsmsId(currentImportingFormId);

			importingForm.setOwner(uiController.getFrontlineController().getUserDao().getSupervisorEmail());

			FormsThinletTabController.getCurrentInstance().getPluginController().getFormDao().saveForm(importingForm);
			FormsThinletTabController.getCurrentInstance().updateFormPublic(vForm.getComponents(), vForm.getComponents(), importingForm, vForm);

			// updates surveys
			for (Survey s : importingSurveys) {
				s.setOwner(importingForm);
				FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().updateSurvey(s);
			}
			FormsThinletTabController.getCurrentInstance().refresh();

			// close dialog
			uiController.removeDialog(exportDialog);

		}
	}

	@Override
	public void openChooseComplete(String filePath) {
		uiController.setText(uiController.find("tfFilename"), filePath);
		/*
		 * checks if the filepath points to an xml file to activate if
		 * (filePath.endsWith(".xml"))
		 */
		this.openChooseCompleted = true;
		validateSelection();

	}

	/**
	 * Override to use a filter for xml files
	 * 
	 */
	@Override
	public void showOpenModeFileChooser() {
		FileChooser fc = FileChooser.createFileChooser(this.uiController, this, "openChooseComplete");
		fc.setFileFilter(new FileNameExtensionFilter("FrontlineSMS Exported Form (" + ".xml" + ")", "xml"));
		fc.show();
	}

	/**
	 * This method is called when a contact is selected and it enables the
	 * import button.
	 */
	public void validateSelection() {
		if (openChooseCompleted)
			uiController.setEnabled(uiController.find("btDoImport"), true);
	}

	/**
	 * Prints debug informations about importinElements list
	 * 
	 * @param importingElements
	 */
	private void debugState(ImportingElement[] importingElements) {
		System.out.println("Debug elements fetched from body section");
		System.out.println("*******************");
		for (ImportingElement ie : importingElements) {

			System.out.println();
			System.out.println("Type: " + ie.type);
			System.out.println("FieldType: " + ie.fieldType);
			System.out.println("Name: " + ie.name);
			System.out.println("Label: " + ie.label);
			System.out.println("Position Index: " + ie.positionIndex);
			System.out.println("Bind Reference: " + ie.bindReference);
			for (ImportingElement el : ie.elementsToBindings.keySet()) {
				for (BindingContainer bc : ie.elementsToBindings.get(el)) {
					System.out.println("Binding:");
					System.out.println(el.name);
					System.out.println(bc.getbType());

				}

			}
			if (ie.fieldType == FormFieldType.REPEATABLES) {
				System.out.println("Number of repeatables contained in: " + ie.repElements.size());
				for (ImportingElement rep : ie.repElements) {
					System.out.println();
					System.out.println("Type: " + rep.type);
					System.out.println("FieldType: " + rep.fieldType);
					System.out.println("Name: " + rep.name);
					System.out.println("Label: " + rep.label);
					System.out.println("Bind Reference: " + rep.bindReference);
					for (ImportingElement el : rep.elementsToBindings.keySet()) {
						for (BindingContainer bc : rep.elementsToBindings.get(el)) {
							System.out.println("Binding:");
							System.out.println(el.name);
							System.out.println(bc.getbType());

						}

					}
				}
			}
		}
		System.out.println("****************");
	}

	/**
	 * Prints debug informations about imporBindings list
	 * 
	 * @param importingBinds
	 */
	private void debugState(ImportBinding[] importingBinds) {
		System.out.println("Debug elements fetched from bind sections");
		System.out.println("*******************");
		for (ImportBinding ib : importingBinds) {
			System.out.println();
			System.out.println("Type: " + ib.type);
			System.out.println("Nodeset: " + ib.nodeset);
			System.out.println("Constraint: " + ib.constraint);
			System.out.println("Relevant: " + ib.relevant);
			System.out.println("NameReference: " + ib.nameReference);
			System.out.println("IsRequired: " + ib.required);
			System.out.println("IsReadonly: " + ib.readOnly);
			System.out.println("Tag Content: " + ib.tagContent);

		}
		System.out.println("*******************");
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Pairs elements with respective bind elements
	 * 
	 * @param importingElements
	 * @param importBindings
	 */
	private void pairElementsAndBinding(List<ImportingElement> importingElements, List<ImportBinding> importBindings) {
		for (ImportingElement elem : importingElements) {
			for (ImportBinding bind : importBindings) {
				// System.out.println("elem name: "+elem.name);
				// System.out.println("bind name: "+bind.nameReference);
				if ((elem.fieldType != FormFieldType.SEPARATOR) && (elem.ref.equals(bind.nameReference))) {
					elem.bindReference = bind;
					// break;
				}
				if (elem.fieldType == FormFieldType.REPEATABLES) {
					// elem.bindReference = bind;
					for (ImportingElement repItem : elem.repElements) {
						// String repItemName = repItem.name;
						// System.out.println(repItemName);
						for (ImportBinding bind2 : importBindings) {
							if (repItem.ref.equals(bind2.nameReference)) {
								repItem.bindReference = bind2;
								break;
							}
						}
					}

				} else if (elem.fieldType == FormFieldType.REPEATABLES_BASIC) {
					// elem.bindReference = bind;
					for (ImportingElement repItem : elem.repElements) {
						// String repItemName = elem.name + "/" + repItem.name;
						// System.out.println(repItemName);
						for (ImportBinding bind2 : importBindings) {
							if (repItem.ref.equals(bind2.nameReference)) {
								repItem.bindReference = bind2;
								break;
							}
						}
					}

				}

			}
		}
	}

	/**
	 * Cuts "/data/" from a string
	 * 
	 * @return
	 */
	private static String cutData(String s) {
		// Cuts data from nodeset field
		String ret = new String();
		int beginCut = s.indexOf("/", 1);
		ret = s.substring(beginCut + 1);
		return ret;
	}

	/**
	 * Fabaris_a.zanchi
	 * 
	 * Discovers separators checking the indexes of the ImportingElement
	 * objects.
	 * 
	 * @param elements
	 * @return a list of ImportingElements objects enriched with elements
	 *         representing spearators
	 */
	private ArrayList<ImportingElement> discoverSeparators(ArrayList<ImportingElement> elements) {
		ArrayList<ImportingElement> ret = new ArrayList<FormPrototypeImportDialogHandler.ImportingElement>();
		int i = 0;
		for (ImportingElement el : elements) {
			if (el.positionIndex == i) {
				ret.add(el);
			} else {
				ImportingElement closeGroupElem = new ImportingElement();
				closeGroupElem.fieldType = FormFieldType.SEPARATOR;
				closeGroupElem.type = "separator";
				closeGroupElem.name = "section" + i;
				closeGroupElem.positionIndex = i;
				ret.add(closeGroupElem);
				ret.add(el);
				i++;
			}
			i++;
		}
		return ret;
	}

	/**
	 * Class modeling a temporary object to create an imported formField It
	 * stores information retrieved from the xform
	 * 
	 * @author a.zanchi
	 * 
	 */

	class ImportingElement {

		ImportBinding bindReference;
		FormFieldType fieldType;
		String type;
		String typeAttribute;
		String name;
		String label;
		String ref;
		String attributeType;
		String select1Aappearance;
		ArrayList<String> select1Labels = new ArrayList<String>();
		String refListName;
		HashMap<ImportingElement, List<BindingContainer>> elementsToBindings = new HashMap<FormPrototypeImportDialogHandler.ImportingElement, List<BindingContainer>>();
		ArrayList<ConstraintContainer> constraints = new ArrayList<ConstraintContainer>();
		FormField generatedFormField;
		boolean isRepContainer;
		ArrayList<ImportingElement> repElements = new ArrayList<FormPrototypeImportDialogHandler.ImportingElement>();
		int numberOfReps;
		boolean isRepItem;
		ArrayList<String> surveyValues = new ArrayList<String>();
		ImportingElement repContainer;
		String bindingsPolicy;
		String constraintsPolicy;
		int positionIndex;
		String calculated;

		public String generateName() {
			name = ref;
			while (name.contains("/")) {
				name = cutReferenceName(name);
			}
			return name;
		}

		/**
		 * Fabaris_a.zanchi
		 * 
		 * Dectects the FormFieldType of this ImportingElement using a crossed
		 * check of properties of ImportingElement and ImportingBinding
		 * 
		 */
		public void detectFormFieldType() {
			// checks for fields value AND field bindReference to determine the
			// FormFieldType

			if (type.equals("select")) {
				fieldType = FormFieldType.CHECK_BOX;
			} else if (type.equals("select1")) {
				if (select1Aappearance.equals("full")) {
					fieldType = FormFieldType.RADIO_BUTTON;
				} else if (select1Aappearance.equals("minimal")) {
					fieldType = FormFieldType.DROP_DOWN_LIST;
				}
			} else if (type.equals("input")) {
				if(bindReference==null){
					String s = "error";
				}
				if (bindReference.type.equals("date")) {
					fieldType = FormFieldType.DATE_FIELD;
				} else if (bindReference.type.equals("int")) {
					fieldType = FormFieldType.NUMERIC_TEXT_FIELD;
				} else if (bindReference.type.equals("decimal")&&(!typeAttribute.equals("currency"))) {
					fieldType = FormFieldType.NUMERIC_TEXT_FIELD;
				} else if (bindReference.type.equals("geopoint")) {
					fieldType = FormFieldType.GEOLOCATION;
				//************************************************
				} else if (bindReference.type.equals("barcode")) {
						fieldType = FormFieldType.BARCODE;
				//************************************************	
				} else if (bindReference.type.equals("image")) {
					fieldType = FormFieldType.IMAGE;
				//-------------------------------------------------
				} else if (bindReference.type.equals("binary")) {
					fieldType = FormFieldType.IMAGE;
				//-------------------------------------------------
				} else if (bindReference.type.equals("signature")) {
					fieldType = FormFieldType.SIGNATURE;
				//-------------------------------------------------
				} else if (typeAttribute.equals("label")) {
					fieldType = FormFieldType.TRUNCATED_TEXT;
					label = bindReference.tagContent;
				} else if (typeAttribute.equals("multi-label")) {
					fieldType = FormFieldType.WRAPPED_TEXT;
					label = bindReference.tagContent;
				} else if (typeAttribute.equals("currency")) {
					fieldType = FormFieldType.CURRENCY_FIELD;
				} else if (bindReference.type.equals("string") && (bindReference.constraintMessage.contains("email"))) {
					fieldType = FormFieldType.EMAIL_FIELD;
				} else if (bindReference.type.equals("string") && (bindReference.constraintMessage.contains("phonenumber"))) {
					fieldType = FormFieldType.PHONE_NUMBER_FIELD;
				} else if (typeAttribute.equals("tarea")) {
					fieldType = FormFieldType.TEXT_AREA;
				} else if (bindReference.type.equals("string")) {
					fieldType = FormFieldType.TEXT_FIELD;
				}
			}

			if (fieldType == FormFieldType.REPEATABLES || fieldType == FormFieldType.REPEATABLES_BASIC) {
				for (ImportingElement rep : repElements) {
					rep.detectFormFieldType();
				}
			}
		}

		/**
		 * Fabaris_A.zanchi
		 * 
		 * Detects the RELEVANT binding associations on the components parsing
		 * the relevant string stored in the ImportingBinding object
		 * 
		 * @param totalElements
		 */
		public void detectBindings(List<ImportingElement> totalElements) {
			if (this.fieldType == FormFieldType.REPEATABLES || this.fieldType == FormFieldType.REPEATABLES_BASIC) {
				for (ImportingElement repEl : this.repElements) {
					repEl.detectBindings(totalElements);
				}
			}
			if (bindReference != null) {
				String splitString = " and ";
				if (bindReference.relevant.contains(" and ")) {
					splitString = " and ";
					bindingsPolicy = FormField.AND_POLICY;
				}
				if (bindReference.relevant.contains(" or ")) {
					splitString = " or ";
					bindingsPolicy = FormField.OR_POLICY;
				}
				String[] relevantElements = bindReference.relevant.split(splitString);
				// System.out.println(relevantElements.length);
				for (String relevant : relevantElements) {
					// if (!relevant.equals("") &&
					// !relevant.contains("selected(")) {
					if (!relevant.equals("") && !relevant.contains("selected(")) {
						for (String t : bindSymbols) {
							if (relevant.contains(t.toString())) {

								String[] splitted = relevant.split(t.toString());

								String referenceName = null;
								// ****Check if relevant is on a component in
								// the same repeatable container***
								if (splitted[0].contains("./../")) {
									referenceName = splitted[0].substring(splitted[0].lastIndexOf("/") + 1);
									String prefix = splitReferenceName(this.ref)[0];
									referenceName = prefix + "/" + referenceName;
								} else
									referenceName = cutReferenceName(splitted[0].trim());
								// ***end of check***

								String bindValue = "";
								if(splitted.length == 1){
									bindValue = splitted[0];
								}else{
									bindValue = splitted[1];   
									
									
									//LL 27-03-2014 aggiunto il ciclo for per il caso in cui nella regola ci siano piu' di due "="
									for(int i=2; i<splitted.length; i++){
										bindValue = bindValue + " = " + splitted[i];
									}
									
									
									
									
								}
								BindingContainer newBinding = new BindingContainer();
								// populate BindingContainer
								// elminates the ' '
								if ((bindValue.charAt(0) == '\'') && (bindValue.charAt(bindValue.length() - 1) == '\''))
									bindValue = bindValue.substring(1, bindValue.length() - 1);
								if (t.equals(" " + EQUAL + " ") && bindValue.equals("")) {
									newBinding.setType(BindType.NULL);
								} else if (t.equals(" " + NOT_EQUAL + " ") && bindValue.equals("")) {
									newBinding.setType(BindType.NOT_NULL);
								} else if (t.equals(" " + EQUAL + " ") && !bindValue.equals("")) {
									newBinding.setType(BindType.EQUALS);
								} else if (t.equals(" " + NOT_EQUAL + " ") && !bindValue.equals("")) {
									newBinding.setType(BindType.NOT_EQUALS);
								} else if (t.equals(" " + GREATER + " ")) {
									newBinding.setType(BindType.GREATER_THAN);
								} else if (t.equals(" " + LESSER + " ")) {
									newBinding.setType(BindType.LESS_THAN);
								} else if (t.equals(" " + GREATER_EQUALS + " ")) {
									newBinding.setType(BindType.GREATER_EQUALS_THAN);
								} else if (t.equals(" " + LESSER_EQUALS + " ")) {
									newBinding.setType(BindType.LESS_EQUALS_THAN);
								}

								newBinding.setValue(bindValue);

								for (ImportingElement el : totalElements) {
									if (el.fieldType != FormFieldType.SEPARATOR) {
										System.out.println("element ref: " + el.ref);
										System.out.println("binding referencename: " + referenceName);
										if (el.ref.trim().equals(referenceName.trim())) {
											System.out.println("adding relevant binding");
											/*
											 * Checks if the element pointed in
											 * the relevant binding is a
											 * checkbox
											 */
											if (el.fieldType == FormFieldType.CHECK_BOX) {
												if (bindValue.equals("true")) {
													newBinding.setType(BindType.IS_CHECKED);
													newBinding.setValue("");
												}
											}
											/* end of check */
											if (elementsToBindings.containsKey(el)) {
												elementsToBindings.get(el).add(newBinding);
											} else {
												elementsToBindings.put(el, new ArrayList<BindingContainer>());
												elementsToBindings.get(el).add(newBinding);
											}
										}
									}
								}
							}
						}
					}
					/*
					 * This part is not used anymore since the change of the
					 * relevant string on the checkbox component
					 */
					else if (relevant.contains("selected(")) {

						// this is a check box
						int beginCut = relevant.indexOf(relevant) + 9;
						int endCut = relevant.indexOf(")", beginCut + 1);
						String selected = relevant.substring(beginCut, endCut);
						String splitted[] = selected.split(" , ");
						String referenceName = splitted[0];
						referenceName = cutReferenceName(referenceName);
						BindingContainer newBinding = new BindingContainer();
						newBinding.setType(BindType.IS_CHECKED);
						for (ImportingElement el : totalElements) {
							if (el.ref.trim().equals(referenceName.trim())) {
								if (elementsToBindings.containsKey(el)) {
									elementsToBindings.get(el).add(newBinding);
								} else {
									elementsToBindings.put(el, new ArrayList<BindingContainer>());
									elementsToBindings.get(el).add(newBinding);
								}
							}
						}
					}
				}
			}
		}

		/**
		 * Fabaris_A.zanchi
		 * 
		 * detects the constraints on the component analyzing the constraint
		 * string value of the associated ImportingBinding object
		 * 
		 */
		public void detectConstraints() {
			
//                    System.out.println(this.fieldType.toString());
                        
			if (this.fieldType == FormFieldType.REPEATABLES || this.fieldType == FormFieldType.REPEATABLES_BASIC) {
				for (ImportingElement repEl : this.repElements) {
					repEl.detectConstraints();
				}
			}

			if (bindReference != null) {
				String splitString = " and ";
				if (bindReference.constraint.contains(" and ")) {
					splitString = " and ";
					constraintsPolicy = FormField.AND_POLICY;
				}
				if (bindReference.constraint.contains(" or ")) {
					splitString = " or ";
					constraintsPolicy = FormField.OR_POLICY;
				}

				String[] constraintElements = bindReference.constraint.split(splitString);
				for (String constraint : constraintElements) {
					if (!constraint.equals("") && !constraint.contains("regex")) {
						for (String t : bindSymbols) {
							if (constraint.contains(t)) {
								if(this.name.toString() == "q43"){
									System.out.println("RB");
								}
								String[] splitted = constraint.split(t);
								String constraintValue = splitted[1];
								if ((constraintValue.charAt(0) == '\'') && (constraintValue.charAt(constraintValue.length() - 1) == '\'')) // deletes
																																			// the
																																			// 's
									constraintValue = constraintValue.substring(1, constraintValue.length() - 1);
								ConstraintContainer constCont = new ConstraintContainer();
								constCont.setValue(constraintValue);
								if (t.equals(" " + EQUAL + " "))
									constCont.setcNumber(ConstraintNumber.EQUALS);
								else if (t.equals(" " + NOT_EQUAL + " "))
									constCont.setcNumber(ConstraintNumber.NOT_EQUALS);
								else if (t.equals(" " + GREATER + " "))
									constCont.setcNumber(ConstraintNumber.GREATER_THAN);
								else if (t.equals(" " + LESSER + " "))
									constCont.setcNumber(ConstraintNumber.LESS_THAN);
								else if (t.equals(" " + GREATER_EQUALS + " "))
									constCont.setcNumber(ConstraintNumber.GREATER_EQUALS_THAN);
								else if (t.equals(" " + LESSER_EQUALS + " "))
									constCont.setcNumber(ConstraintNumber.LESS_EQUALS_THAN);

								this.constraints.add(constCont);
								break;
							}
						}
					}
				}
			}
		}

		public String cutReferenceName(String referenceName) {
			// Cuts data from nodeset field
			int beginCut = referenceName.indexOf("/", 1);
			referenceName = referenceName.substring(beginCut + 1);
			return referenceName;
		}

		/**
		 * Split the reference name in two parts: the parent path and the name
		 * with index (in this order)
		 * 
		 * @param refereceName
		 * @return array of length 2 with the two splitted parts
		 */
		public String[] splitReferenceName(String refereceName) {
			int cutIndex = refereceName.lastIndexOf("/");
			String[] ret = new String[2];
			ret[0] = refereceName.substring(0, cutIndex);
			ret[1] = refereceName.substring(cutIndex + 1);
			return ret;
		}

		/**
		 * Genereates a new FormField and pairs it with this ImportingElement
		 */
		void generateFormField() {

			if (fieldType != FormFieldType.SEPARATOR) {
				if (!isRepItem)
					cutIndexFromName();
				this.generatedFormField = new FormField(fieldType, label, null, name, bindReference.required);

				this.generatedFormField.setBindingsPolicy(this.bindingsPolicy);
				this.generatedFormField.setConstraintPolicy(this.constraintsPolicy);
				this.generatedFormField.setReadOnly(bindReference.readOnly);

				if (this.calculated != null) {
					this.generatedFormField.setCalculated(true);
					this.generatedFormField.setFormula(this.calculated);
				}

			} else {
				this.generatedFormField = new FormField(fieldType, label, null, name, false);
			}
			if ((fieldType == FormFieldType.DROP_DOWN_LIST) || (fieldType == FormFieldType.RADIO_BUTTON)) {
				// Creates a survey, saves and assigns it to the formfield
				// cutIndexFromName();
				Survey importingSurvey = new Survey();
				// importingSurvey.setName("imported_" +
				// currentImportingFormName + "_" + name);
				importingSurvey.setName(refListName);
				for (String label : select1Labels) {
					importingSurvey.addValue(label);
				}
				// FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().saveSurvey(importingSurvey);
				importingSurveys.add(importingSurvey);
				this.generatedFormField.setSurvey(importingSurvey);
			}
			if (fieldType == FormFieldType.REPEATABLES_BASIC) {
				// saves repeatables elements
				// cutIndexFromName();
				this.generatedFormField = new FormField(fieldType, label, null, name, false);
				for (ImportingElement reps : repElements) {
					reps.generateFormField();
					this.generatedFormField.addRepetable(reps.generatedFormField);
				}
				this.generatedFormField.setNumberOfRep(this.numberOfReps);
			}
			if (fieldType == FormFieldType.REPEATABLES) {
				// cutIndexFromName();
				this.generatedFormField = new FormField(fieldType, label, null, name, false);
				for (ImportingElement reps : repElements) {
					reps.generateFormField();
					this.generatedFormField.addRepetable(reps.generatedFormField);
				}
				Survey importingSurvey = new Survey();
				String visualName = null;
				if ((label != null) && (!label.equals(""))) {
					visualName = label;
				} else {
					visualName = name;
				}
				// importingSurvey.setName("imported_"/* +
				// currentImportingFormName + "_"*/ + visualName);
				importingSurvey.setName(refListName);
				for (String label : surveyValues) {
					importingSurvey.addValue(label);
				}
				// FormsThinletTabController.getCurrentInstance().getPluginController().getSurveyDao().saveSurvey(importingSurvey);
				importingSurveys.add(importingSurvey);
				this.generatedFormField.setSurvey(importingSurvey);

			}

		}

		/**
		 * Cuts index from name so that it is not duplicated when it is saved in
		 * a new form
		 */
		void cutIndexFromName() {
			// int stringLength = name.length();
			for (int i = name.length() - 1; i >= 0; i--) {
				if (name.charAt(i) == '_') {
					// System.out.println(name.substring(i + 1));
					try {
						positionIndex = Integer.parseInt(name.substring(i + 1));
					} catch (Exception e) {
						break;
					}
					name = name.substring(0, i);
					break;
				}
			}
		}

		/**
		 * Gets index from name
		 * 
		 */
		void getIndexFormName() {
			for (int i = name.length() - 1; i >= 0; i--) {
				if (name.charAt(i) == '_') {

					positionIndex = Integer.parseInt(name.substring(i + 1));

					break;
				}
			}
		}

		/**
		 * Creates bindings from this associated FormField Note: it must be
		 * called AFTER generateFormField has been invoked on VERY
		 * importingelement
		 */
		void generateFormFieldBindings() {
			// relevant
			for (ImportingElement el : elementsToBindings.keySet()) {
				for (BindingContainer bc : elementsToBindings.get(el)) {
					this.generatedFormField.addBinding(el.generatedFormField, bc);
				}
			}
			// constraints
			for (ConstraintContainer constCont : this.constraints) {
				this.generatedFormField.addConstraint(constCont);
			}
			if (this.fieldType == FormFieldType.REPEATABLES || this.fieldType == FormFieldType.REPEATABLES_BASIC) {
				for (ImportingElement rep : this.repElements) {
					rep.generateFormFieldBindings();
				}
			}
		}

		
	}

	/**
	 * Class modeling a binding in the xml for a single component
	 * 
	 * @author a.zanchi
	 * 
	 */
	class ImportBinding {

		// ImportingElement elementReference;
		String nodeset;
		String type;
		String constraint;
		String constraintMessage;
		String relevant;
		Boolean required;
		Boolean readOnly;
		String nameReference;
		String tagContent;

		public String generateNameReference() {
			// Cuts data from nodeset field
			int beginCut = nodeset.indexOf("/", 1);
			nameReference = nodeset.substring(beginCut + 1);
			return nameReference;
		}
	}

}
