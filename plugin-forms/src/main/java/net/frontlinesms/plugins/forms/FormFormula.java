/**
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
 * Copyright © 2012 ,Fabaris s.r.l
 * This file is part of GRASP Designer Tool.  
 * GRASP Designer Tool is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.  
 * GRASP Designer Tool is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRASP Designer Tool. 
 * If not, see <http://www.gnu.org/licenses/>
 */
package net.frontlinesms.plugins.forms;

import java.util.ArrayList;

import org.hibernate.annotations.ForceDiscriminator;

import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.exceptions.FormFormulaException;
import net.frontlinesms.plugins.forms.ui.FormsUiController;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;

/**
 * This class represents a formula used on a calculated field.
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph       
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
public class FormFormula {

	public static final int TYPE_UNDEFINED = -1;
	public static final int TYPE_FORMULA = 0;
	public static final int TYPE_OPERATION = 1;
	public static final int TYPE_COMPONENT = 2;
	public static final int TYPE_TEXT = 3;
	public static final int TYPE_DOUBLE= 4;
	public static final int TYPE_INTEGER = 5;

	public static final int OPERATION_NONE = -1;
	public static final int OPERATION_ADD = 0;
	public static final int OPERATION_SUBSTRACT = 1;
	public static final int OPERATION_MULTIPLY = 2;
	public static final int OPERATION_DIVIDE = 3;
	

	public static final int RETURN_UNDEFINED = -1;
	public static final int RETURN_STRING = 0;
	public static final int RETURN_INTEGER = 1;
	public static final int RETURN_DOUBLE = 2;
	
	public ArrayList<FormFormula> formulaParts = new ArrayList<FormFormula>();
	private int type = -1;
	public PreviewComponent component;
	public int operation = -1;
	public String text = new String();
	public FormFormula parrentFormula;
	public int integerNumber = 0;
	public double doubleNumber= 0;
	public int openPosition = -1;
	public int closePosition = -1;
	public PreviewComponent forComponent;
	public VisualForm form;
	/**
	 * 
	 */
	public FormFormula(int type, int operation, PreviewComponent comp) {
		this.type = type;
		this.operation = operation;
		this.component = comp;
	}
	
	public static FormFormula createComponentFormulaPart(String name, PreviewComponent comp){
		boolean isInteger = true;
		boolean isDouble = true;
		int integer = 0;
		double d = 0;
		try{
			integer = Integer.parseInt(name);
		}catch(Exception e){
			isInteger = false;
		}
		if(isInteger){
			FormFormula ff = new FormFormula(TYPE_INTEGER, OPERATION_NONE, null);
			ff.integerNumber = integer;
			return ff;
		}
		try{
			d = Double.parseDouble(name);
		}catch(Exception e){
			isDouble = false;
		}
		if(isDouble){
			FormFormula ff = new FormFormula(TYPE_DOUBLE, OPERATION_NONE, null);
			ff.doubleNumber = d;
			return ff;
		}

		FormFormula ff = new FormFormula(TYPE_COMPONENT, OPERATION_NONE, comp);
		ff.text = name;
		return ff;
	}
	public static FormFormula createAddFormulaPart(int position){
		FormFormula ff =  new FormFormula(TYPE_OPERATION, OPERATION_ADD, null);
		ff.text="+";
		ff.openPosition = position;
		ff.closePosition = position;
		return ff;
	}
	public static FormFormula createSubstractFormulaPart(int position){
		FormFormula ff = new FormFormula(TYPE_OPERATION, OPERATION_SUBSTRACT, null);
		ff.text="-";
		ff.openPosition = position;
		ff.closePosition = position;
		return  ff;
	}
	public static FormFormula createMultiplyFormulaPart(int position){
		FormFormula ff =  new FormFormula(TYPE_OPERATION, OPERATION_MULTIPLY, null);
		ff.text="*";
		ff.openPosition = position;
		ff.closePosition = position;
		return ff;
	}
	public static FormFormula createDivideFormulaPart(int position){
		FormFormula ff = new FormFormula(TYPE_OPERATION, OPERATION_DIVIDE, null);
		ff.text="/";
		ff.openPosition = position;
		ff.closePosition = position;
		return  ff;
	}
	public static FormFormula createFormulaFormulaPart(FormFormula parrentFormula){
		FormFormula ff = new FormFormula(TYPE_FORMULA, OPERATION_NONE, null);
		ff.parrentFormula = parrentFormula;
		return ff;
	}
	public static FormFormula createTextFormulaPart(String txt){
		FormFormula ff = new FormFormula(TYPE_TEXT, OPERATION_NONE, null);
		ff.text = txt == null ? "" : txt;
		return ff;
	}
	public static FormFormula parseFormula(String formulaString, PreviewComponent forComponent, VisualForm form) throws FormFormulaException{
		FormFormula rootFormula = createFormulaFormulaPart(null);
		rootFormula.form = form;
		rootFormula.forComponent = forComponent;
		int openParentesis = 0;
		int closeParentesis = 0;
		boolean isInString = false;
		boolean isInComponent = false;
		FormFormula currentFormula = rootFormula;
		StringBuilder currentText = new StringBuilder();
		for(int i=0;i<formulaString.length();i++){
			char chr = formulaString.charAt(i);
			if(isInString){
				if(chr == '\''){
					if(i==formulaString.length()-1 || formulaString.charAt(i+1) != '\''){
						FormFormula ff = FormFormula.createTextFormulaPart(currentText.toString());
						ff.openPosition=(i-currentFormula.toString().length()-1);
						ff.closePosition=(i);
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInString = false;
					}else{
						currentText.append(chr);
						i++;
					}
				}else{
					currentText.append(chr);
				}
			}else{
				if(isInComponent){
					if(isAcceptedChar(chr)){
						currentText.append(chr);
					}else if(chr=='\''){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						isInString=true;
					}else if(chr=='+'){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						currentFormula.formulaParts.add(createAddFormulaPart(i));
					}else if(chr=='-'){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						currentFormula.formulaParts.add(createSubstractFormulaPart(i));
					}else if(chr=='/'){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						currentFormula.formulaParts.add(createDivideFormulaPart(i));
					}else if(chr=='*'){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						currentFormula.formulaParts.add(createMultiplyFormulaPart(i));
					}else if(chr == '('){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;
						currentFormula = createFormulaFormulaPart(currentFormula);
						currentFormula.openPosition = i;
						currentFormula.parrentFormula.formulaParts.add(currentFormula);
						openParentesis++;
					}else if(chr == ')'){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						ff.openPosition = i-currentText.toString().length();
						ff.closePosition = i-1;
						currentFormula.formulaParts.add(ff);
						currentFormula.closePosition=i;
						currentText = new StringBuilder();
						isInComponent=false;
						closeParentesis++;
						if(closeParentesis > openParentesis){
							throw FormFormulaException.createUnexpectedParentesisException(closeParentesis-openParentesis, i);
						}
						currentFormula = currentFormula.parrentFormula;
					}else if((int) chr == 9 ||(int)chr == 10 || (int)chr == 13 || (int)chr==32){
						PreviewComponent c = getComponentByName(currentText.toString(), form);
						FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
						if(c == null && ff.type==TYPE_COMPONENT){
							throw FormFormulaException.createComponentNotFoundException(currentText.toString(), i-currentText.toString().length());
						}
						currentFormula.formulaParts.add(ff);
						currentText = new StringBuilder();
						isInComponent=false;						
					}else{
						throw FormFormulaException.createIncalidCharException(i, chr);
					}
				}else{
					if(isAcceptedChar(chr)){
						isInComponent = true;
						currentText.append(chr);
					}else if(chr == '\''){
						isInString = true;
					}else if(chr=='+'){
						currentFormula.formulaParts.add(createAddFormulaPart(i));
					}else if(chr=='-'){
						currentFormula.formulaParts.add(createSubstractFormulaPart(i));
					}else if(chr=='/'){
						currentFormula.formulaParts.add(createDivideFormulaPart(i));
					}else if(chr=='*'){
						currentFormula.formulaParts.add(createMultiplyFormulaPart(i));
					}else if(chr == '('){
						currentFormula = createFormulaFormulaPart(currentFormula);
						currentFormula.openPosition = i;
						currentFormula.parrentFormula.formulaParts.add(currentFormula);
						openParentesis++;
					}else if(chr == ')'){
						closeParentesis++;
						if(closeParentesis > openParentesis){
							throw FormFormulaException.createUnexpectedParentesisException(closeParentesis-openParentesis, i);
						}
						currentFormula.closePosition = i;
						currentFormula = currentFormula.parrentFormula;
					}else if((int) chr == 9 || (int)chr == 13 || (int)chr==32 || (int)chr == 10){
						//skip character						
					}else{
						throw FormFormulaException.createIncalidCharException(i, chr);
					}
				}// if(isInComponent)
			}// if(isInTxt)
		}//end for
		
		if(openParentesis>closeParentesis){
			throw FormFormulaException.createExpectedParentesisException(openParentesis-closeParentesis, formulaString.length()-1);
		}
		if(isInString){
			throw FormFormulaException.createExpectedAposException(formulaString.length()-1);
		}
		if(isInComponent){
			PreviewComponent c = getComponentByName(currentText.toString(), form);
			FormFormula ff = createComponentFormulaPart(currentText.toString(), c);
			if(c == null && ff.type==TYPE_COMPONENT){
				throw FormFormulaException.createComponentNotFoundException(currentText.toString(), formulaString.length()-1-currentText.toString().length()+1);
			}
			currentFormula.formulaParts.add(ff);
			currentText = new StringBuilder();
			isInComponent=false;	
		}
		validateFormula(rootFormula);
		return rootFormula;
	}
	
	public static void validateFormula(FormFormula ff) throws FormFormulaException{
		for(int i=0;i<ff.formulaParts.size();i++){
			FormFormula cf = ff.formulaParts.get(i);
			if(cf.type == TYPE_FORMULA){
				if(cf.formulaParts.size()== 0) throw FormFormulaException.createEmptyParentesisException(cf);
				FormFormula.validateFormula(cf);
				if(i<ff.formulaParts.size()-1){
					if(ff.formulaParts.get(i+1).type != TYPE_OPERATION) throw FormFormulaException.createOperationExpectedException(ff.formulaParts.get(i+1));
				}
			}else if(cf.type == TYPE_COMPONENT){
				if( !cf.component.getType().equals(FormFieldType.TEXT_AREA) &&
						!cf.component.getType().equals(FormFieldType.TEXT_FIELD) &&
						!cf.component.getType().equals(FormFieldType.DATE_FIELD)&&
						!cf.component.getType().equals(FormFieldType.DROP_DOWN_LIST) &&
						!cf.component.getType().equals(FormFieldType.EMAIL_FIELD) &&
						!cf.component.getType().equals(FormFieldType.PHONE_NUMBER_FIELD) &&
						!cf.component.getType().equals(FormFieldType.CHECK_BOX) &&
						!cf.component.getType().equals(FormFieldType.RADIO_BUTTON)&&
						!cf.component.getType().equals(FormFieldType.CURRENCY_FIELD) &&
						!cf.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
					throw FormFormulaException.createUnsupportedComponetException(cf);
				}
				if(i<ff.formulaParts.size()-1){
					if(ff.formulaParts.get(i+1).type != TYPE_OPERATION) throw FormFormulaException.createOperationExpectedException(ff.formulaParts.get(i+1));
				}
			}else if(cf.type==TYPE_DOUBLE){
				if(i<ff.formulaParts.size()-1){
					if(ff.formulaParts.get(i+1).type != TYPE_OPERATION) throw FormFormulaException.createOperationExpectedException(ff.formulaParts.get(i+1));
				}
			}else if(cf.type == TYPE_INTEGER){
				if(i<ff.formulaParts.size()-1){
					if(ff.formulaParts.get(i+1).type != TYPE_OPERATION) throw FormFormulaException.createOperationExpectedException(ff.formulaParts.get(i+1));
				}
			}else if(cf.type == TYPE_TEXT){
				if(i<ff.formulaParts.size()-1){
					if(ff.formulaParts.get(i+1).type != TYPE_OPERATION) throw FormFormulaException.createOperationExpectedException(ff.formulaParts.get(i+1));
				}
			}else if(cf.type == TYPE_OPERATION){
				if(i==0) throw FormFormulaException.createExpectedValueBeforeException(cf);
				if(i==ff.formulaParts.size()-1) throw FormFormulaException.createExpectedValueAfterException(cf);
				if(ff.formulaParts.get(i+1).type == TYPE_OPERATION) throw FormFormulaException.createExpectedValueAfterException(cf);
				if(FormFormula.getReturnType(ff)==RETURN_STRING && cf.operation!=OPERATION_ADD) throw FormFormulaException.createInvalidOperationException(cf);
			}else{
				throw FormFormulaException.createUndefinedFormulaPartException(cf);
			}
			if(ff.forComponent!=null){
				if((ff.forComponent.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD) || 
					ff.forComponent.getType().equals(FormFieldType.CURRENCY_FIELD))){
					if(FormFormula.getReturnType(ff)==RETURN_STRING) throw FormFormulaException.createExpectedReturnNumberException();
				}else if(!ff.forComponent.getType().equals(FormFieldType.TEXT_AREA)&&
						 !ff.forComponent.getType().equals(FormFieldType.TEXT_FIELD)){
					throw FormFormulaException.createUnexpectedForComponentTypeException(ff.forComponent);
				}
				if(FormFormula.isInFormula(ff, ff.forComponent)){
					throw FormFormulaException.createCyclicFormulaException(ff.forComponent);
				}
			}
		}
	}
	public static int getReturnType(FormFormula ff){
		int currentRT = RETURN_UNDEFINED;
		if(ff.type == TYPE_FORMULA){
			for(int i=0;i<ff.formulaParts.size();i++){
				FormFormula cf = ff.formulaParts.get(i);
				int frt = FormFormula.getReturnType(cf);
				if(frt== RETURN_STRING){
					return RETURN_STRING;
				}else if(frt == RETURN_DOUBLE){
					currentRT = RETURN_DOUBLE;
				}else if(frt == RETURN_INTEGER){
					if(currentRT == RETURN_UNDEFINED)currentRT = RETURN_INTEGER;
				}
			}
		}else if(ff.type == TYPE_COMPONENT){
			//System.out.println("component:"+ff.component.getComponent().getDescription());
			if( ff.component.getType().equals(FormFieldType.TEXT_AREA) ||
					ff.component.getType().equals(FormFieldType.TEXT_FIELD) ||
					ff.component.getType().equals(FormFieldType.DATE_FIELD)||
					ff.component.getType().equals(FormFieldType.DROP_DOWN_LIST) ||
					ff.component.getType().equals(FormFieldType.EMAIL_FIELD) ||
					ff.component.getType().equals(FormFieldType.PHONE_NUMBER_FIELD) ||
					ff.component.getType().equals(FormFieldType.CHECK_BOX)||
					ff.component.getType().equals(FormFieldType.RADIO_BUTTON)){
				return RETURN_STRING;
			}else if(ff.component.getType().equals(FormFieldType.CURRENCY_FIELD)){
				currentRT = RETURN_DOUBLE;
			}else if(ff.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
				if(currentRT == RETURN_UNDEFINED) currentRT = RETURN_INTEGER;
			}
		}else if(ff.type == TYPE_TEXT){
			return RETURN_STRING;
		}else if(ff.type==TYPE_DOUBLE){
			currentRT = RETURN_DOUBLE;
		}else if(ff.type == TYPE_INTEGER){
			if(currentRT == RETURN_UNDEFINED) currentRT = RETURN_INTEGER;
		}else if(ff.type == TYPE_FORMULA){
			
		}
		return currentRT;
	}
	public String getFormulaTree(){
		StringBuilder sb = new StringBuilder();
		String spacer = new String();
		for(int i=0;i<getIndent();i++){
			spacer += "  ";
		}
		spacer += ">";
		for(int j=0;j<formulaParts.size();j++){
			if(formulaParts.get(j).type == TYPE_COMPONENT){
				sb.append(spacer + "COMPONENT:"+formulaParts.get(j).text+"-"+formulaParts.get(j).component+" - "+FormFormula.getReturnTypeString(formulaParts.get(j)));
			}else if(formulaParts.get(j).type == TYPE_OPERATION){
				int o = formulaParts.get(j).operation;
				if(o == OPERATION_ADD){
					sb.append(spacer + "OPERARION:"+"+");
				}else if(o == OPERATION_SUBSTRACT){
					sb.append(spacer + "OPERARION:"+"-");
				}else if(o == OPERATION_MULTIPLY){
					sb.append(spacer + "OPERARION:"+"*");
				}else if(o == OPERATION_DIVIDE){
					sb.append(spacer + "OPERARION:"+"/");
				}else if(o == OPERATION_NONE){
					sb.append(spacer + "OPERARION:"+"none");
				}
			}else if(formulaParts.get(j).type == TYPE_TEXT){
				sb.append(spacer + "TEXT:"+formulaParts.get(j).text+" - "+FormFormula.getReturnTypeString(formulaParts.get(j)));
			}else if(formulaParts.get(j).type == TYPE_UNDEFINED){
				sb.append(spacer + "UNDEFINED"+" - "+FormFormula.getReturnTypeString(formulaParts.get(j)));
			}else if(formulaParts.get(j).type == TYPE_FORMULA){
				sb.append(spacer + "Formula:"+FormFormula.getReturnTypeString(formulaParts.get(j))+"\n"+formulaParts.get(j).getFormulaTree());
			}else if(formulaParts.get(j).type == TYPE_INTEGER){
				sb.append(spacer + "INTEGER:"+formulaParts.get(j).integerNumber+" - "+FormFormula.getReturnTypeString(formulaParts.get(j)));
			}else if(formulaParts.get(j).type == TYPE_DOUBLE){
				sb.append(spacer + "DOUBLE:"+formulaParts.get(j).doubleNumber+" - "+FormFormula.getReturnTypeString(formulaParts.get(j)));
			}
			if(j<formulaParts.size()-1){
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	public int getIndent(){
		if(parrentFormula != null){
			return parrentFormula.getIndent()+1;
		}else{
			return 0;
		}
	}
	private static boolean isAcceptedChar(char chr){
		if((int)chr >= 65 && (int)chr <= 90){
			return true;
		}else if((int)chr >= 97 && (int)chr <= 122){
			return true;
		}else if((int)chr >= 48 && (int)chr <= 57){
			return true;
		}else if(chr == '_' || chr=='.'){
			return true;
		}
		return false;
	}
	public static String getReturnTypeString(FormFormula ff){
		int rt = getReturnType(ff);
		if(rt == RETURN_DOUBLE){
			return "DOUBLE";
		}else if(rt == RETURN_INTEGER){
			return "INTEGER";
		}else if(rt == RETURN_STRING){
			return "STRING";
		}else{
			return "UNDEFINED";
		}
	}

	public String toFormulaString(){
		StringBuffer sb = new StringBuffer();
		if(type==TYPE_FORMULA){
			if(parrentFormula!=null) sb.append("(");
			for(int i=0;i<formulaParts.size();i++){
				sb.append(formulaParts.get(i).toFormulaString());
			}
			if(parrentFormula!=null) sb.append(")");
		}else if(type==TYPE_COMPONENT){
			sb.append(component.getComponent().getName());
		}else if(type == TYPE_DOUBLE){
			sb.append(doubleNumber);
		}else if(type == TYPE_INTEGER){
			sb.append(integerNumber);
		}else if(type == TYPE_OPERATION){
			sb.append(" "+text+" ");
		}else if(type==TYPE_TEXT){
			sb.append("'"+text.replace("'","''")+"'");
		}
		return sb.toString();
	}
	public String toXFormString(PreviewComponent comp){
		if(comp==null)return "";
		VisualForm cForm = getRootFormula().form;
		StringBuffer sb = new StringBuffer();
		PreviewComponent rep = getComponentRepetable(comp, cForm);
		if(getReturnType(this)==RETURN_STRING){
			sb.append("concat(");
		}else if(parrentFormula != null){
			sb.append("(");
		}
		for(int i=0;i<formulaParts.size();i++){
			FormFormula cf = formulaParts.get(i);
			if(cf.type==TYPE_FORMULA){
				sb.append(cf.toXFormString(comp));
			}else if(cf.type == TYPE_DOUBLE){
				sb.append(cf.doubleNumber);
			}else if(cf.type==TYPE_INTEGER){
				sb.append(cf.integerNumber);
			}else if(cf.type == TYPE_OPERATION){
				if(getReturnType(this)==RETURN_STRING){
					sb.append(", ");
				}else{
					if(cf.operation == OPERATION_DIVIDE){
						sb.append(" div ");
					}else{
						sb.append(" "+cf.text+" ");
					}
				}
			}else if(cf.type == TYPE_TEXT){
				String[] array = cf.text.split("''");
				for(int j=0;j<array.length;j++){
					sb.append("'"+array[j].replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("\"", "&quot;")+"'");
					if(j<array.length-1) sb.append(", /data/@apos, ");
				}
			}else if(cf.type == TYPE_COMPONENT){
				int position = getComponentPosition(cf.component, cForm);
				String pos = position >= 0 ? "_"+position : "";
				PreviewComponent crep = getComponentRepetable(cf.component, cForm);
				if(crep==null){
					sb.append("/data/"+cf.component.getComponent().getName()+pos);
				}else if(crep.getType().equals(FormFieldType.REPEATABLES)){
					int position2 = getComponentPosition(crep, cForm);
					String pos2 = position2 >= 0 ? "_"+position2 : "";
					if(crep.equals(rep)){
						sb.append("./../"+cf.component.getComponent().getName()+pos);
					}else{
						if(crep.getSurvey()!=null){
							if(cf.component.getType().equals(FormFieldType.CURRENCY_FIELD) ||
									cf.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
								sb.append("(");
							}else{
								sb.append("Concat(");
							}
							for(int x=0;x<crep.getSurvey().getValues().size();x++){
								String cSurvey = crep.getSurvey().getValues().get(x).getValue();
								sb.append("/data/"+crep.getComponent().getName()+pos2+"/item"+x+"/"+cf.component.getComponent().getName()+pos);
								if(x<crep.getSurvey().getValues().size()-1){
									if(cf.component.getType().equals(FormFieldType.CURRENCY_FIELD) ||
											cf.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
										sb.append("+");
									}else{
										sb.append(",");
									}
								}
							}
							if(cf.component.getType().equals(FormFieldType.CURRENCY_FIELD) ||
									cf.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
								sb.append(")");
							}else{
								sb.append(",'')");
							}
						}else{
							sb.append("null");
						}
					}
				}else{
					int position2 = getComponentPosition(crep, cForm);
					String pos2 = position2 >= 0 ? "_"+position2 : "";
					if(crep.equals(rep)){
						sb.append("./../"+cf.component.getComponent().getName()+pos);
					}else if(cf.component.getType().equals(FormFieldType.CURRENCY_FIELD) ||
							cf.component.getType().equals(FormFieldType.NUMERIC_TEXT_FIELD)){
						System.out.println(cf.component.getComponent().getDescription()+"="+"number");
						sb.append("sum(/data/"+crep.getComponent().getName()+pos2+"/"+cf.component.getComponent().getName()+pos+")");
					}else{
						System.out.println(cf.component.getComponent().getDescription()+"="+"string");
						sb.append("concat(/data/"+crep.getComponent().getName()+pos2+"/"+cf.component.getComponent().getName()+pos+")");
					}
				}
			}
		}
		if(getReturnType(this)==RETURN_STRING){
			sb.append(",'')");
		}else if(parrentFormula != null){
			sb.append(")");
		}
		
		
		
		
		return sb.toString();
	}
	
	public static boolean isInFormula(FormFormula ff, PreviewComponent pc){
		for(int i=0;i<ff.formulaParts.size();i++){
			FormFormula current = ff.formulaParts.get(i);
			if(current.type==TYPE_COMPONENT && current.component.equals(pc)){
				return true;
			}else if(current.type == TYPE_FORMULA){
				boolean isInFormulaPart = FormFormula.isInFormula(current, pc);
				if(isInFormulaPart) return true;
			}
		}
		return false;
	}

	public static PreviewComponent getComponentByName(String name, VisualForm form) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < form.getComponents().size(); i++) {
			PreviewComponent comp = form.getComponents().get(i);
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

	public PreviewComponent getComponentRepetable(PreviewComponent c, VisualForm form) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < form.getComponents().size(); i++) {
			PreviewComponent comp = form.getComponents().get(i);
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
	public int getComponentPosition(PreviewComponent c, VisualForm form) {
		// System.out.println("chiedo componente:"+name);
		for (int i = 0; i < form.getComponents().size(); i++) {
			PreviewComponent comp = form.getComponents().get(i);
			if (comp.equals(c))
				return i;
		}
		return -1;
	}

	public FormFormula getRootFormula() {
		if(this.parrentFormula!=null)return this.parrentFormula.getRootFormula();
		return this;
	}
}
