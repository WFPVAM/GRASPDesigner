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
package net.frontlinesms.plugins.forms.exceptions;

import net.frontlinesms.plugins.forms.FormFormula;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;

/**
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 * Used For errors parsing formula from String
 */
public class FormFormulaException extends Exception {

	public static final int UNDEFINED = -1;
	public static final int UNEXPECTED_CLOSE_PARENTESIS = 0;
	public static final int EXPECTED_CLOSE_PARENTESIS = 1;
	public static final int INVALID_CHAR = 2;
	public static final int EXPECTED_APOS = 3;
	public static final int COMPONENT_NOT_FOUND = 4;
	public static final int EMPTY_PARENTESIS = 5;
	public static final int OPERATION_EXPECTED = 6;
	public static final int EXPECTED_VALUE_BEFORE_OPERATION = 7;
	public static final int EXPECTED_VALUE_AFTER_OPERATION = 8;
	public static final int UNDEFINED_FORMULA_PART = 9;
	public static final int INVALID_OPERATION = 10;
	public static final int EXPECTED_RETURN_NUMBER = 11;
	public static final int UNEXPECTED_FOR_COMPONENT_TYPE = 12;
	public static final int CYCLIC_FORMULA = 13;
	public static final int EMPTY_FORMULA = 14;
	public static final int UNSUPPOERTED_COMPONENT = 15;
	
	public int type = -1; 
	public int count = 0;
	public char chr = ' ';
	public String text = new String();
	public int position =-1;
	public FormFormula formulaPart;
	/**
	 * 
	 */
	public FormFormulaException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public FormFormulaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public FormFormulaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormFormulaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public static FormFormulaException createUnexpectedParentesisException(int count, int position){
		FormFormulaException ex = new FormFormulaException("Unexpected close parentesis at position:"+position);
		ex.count = count;
		ex.position = position;
		ex.type = UNEXPECTED_CLOSE_PARENTESIS;
		return ex;
	}
	public static FormFormulaException createComponentNotFoundException(String name, int position){
		FormFormulaException ex = new FormFormulaException("Conponent '"+name+"' at position:"+position+" not found.");
		ex.count = 0;
		ex.text = name;
		ex.position = position;
		ex.type = COMPONENT_NOT_FOUND;
		return ex;
	}
	public static FormFormulaException createExpectedParentesisException(int count, int position){
		FormFormulaException ex = new FormFormulaException("Expected "+ count + " close parentesis");
		ex.count = count;
		ex.position = position;
		ex.type = EXPECTED_CLOSE_PARENTESIS;
		return ex;
	}
	public static FormFormulaException createExpectedAposException(int position){
		FormFormulaException ex = new FormFormulaException("Expected apostroph to close constant text");
		ex.count = 0;
		ex.position = position;
		ex.type = EXPECTED_APOS;
		return ex;
	}
	public static FormFormulaException createIncalidCharException(int position, char chr){
		FormFormulaException ex = new FormFormulaException("Invalid char '"+chr+"' outside of static text at position:"+position);
		ex.count = 0;
		ex.position = position;
		ex.chr = chr;
		ex.type = INVALID_CHAR;
		return ex;
	}
	public static FormFormulaException createUnsupportedComponetException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Invalid type of component '"+cf.component.getComponent().getDescription()+"' at position "+cf.openPosition);
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = UNSUPPOERTED_COMPONENT;
		ex.formulaPart = cf;
		return ex;
	}
	public static FormFormulaException createEmptyParentesisException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Expected some value in parentesis.");
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = EMPTY_PARENTESIS;
		return ex;
	}
	public static FormFormulaException createOperationExpectedException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Expected OPERATION formulaPart type after value or component at position:"+cf.openPosition);
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = OPERATION_EXPECTED;
		return ex;
	}
	public static FormFormulaException createExpectedValueBeforeException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Expected value or component before operation sign '"+cf.text+"' at position:"+cf.openPosition);
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = EXPECTED_VALUE_BEFORE_OPERATION;
		ex.text = cf.text;
		return ex;
	}
	public static FormFormulaException createExpectedValueAfterException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Expected value or component after operation sign '"+cf.text+"' at position:"+(cf.openPosition+1));
		ex.count = 0;
		ex.position = cf.openPosition+1;
		ex.chr = ' ';
		ex.type = EXPECTED_VALUE_AFTER_OPERATION;
		ex.text = cf.text;
		return ex;
	}
	public static FormFormulaException createUndefinedFormulaPartException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("An undefined formula part at position:"+cf.openPosition);
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = UNDEFINED_FORMULA_PART;
		return ex;
	}
	public static FormFormulaException createInvalidOperationException(FormFormula cf){
		FormFormulaException ex = new FormFormulaException("Invalid operation sign '"+cf.text+"' for FormulaPart returning string. Position:"+cf.openPosition);
		ex.count = 0;
		ex.position = cf.openPosition;
		ex.chr = ' ';
		ex.type = INVALID_OPERATION;
		ex.text = cf.text;
		return ex;
	}
	public static FormFormulaException createExpectedReturnNumberException(){
		FormFormulaException ex = new FormFormulaException("Expected return is number but formula returns String");
		ex.count = 0;
		ex.position = 0;
		ex.chr = ' ';
		ex.type = EXPECTED_RETURN_NUMBER;
		return ex;
	}
	public static FormFormulaException createUnexpectedForComponentTypeException(PreviewComponent pc){
		FormFormulaException ex = new FormFormulaException("Component type '"+pc.getComponent().getDescription()+"' can not be a calcolated field.");
		ex.count = 0;
		ex.position = 0;
		ex.chr = ' ';
		ex.type = UNEXPECTED_FOR_COMPONENT_TYPE;
		return ex;
	}
	public static FormFormulaException createCyclicFormulaException(PreviewComponent pc){
		FormFormulaException ex = new FormFormulaException("Formula for component '"+pc.getComponent().getName()+"' can not contain it self.");
		ex.count = 0;
		ex.position = 0;
		ex.chr = ' ';
		ex.type = CYCLIC_FORMULA;
		ex.text = pc.getComponent().getName();
		return ex;
	}
	public static FormFormulaException createEmptyFormulaException(PreviewComponent pc){
		FormFormulaException ex = new FormFormulaException("Formula can't be empty.");
		ex.count = 0;
		ex.position = 0;
		ex.chr = ' ';
		ex.type = EMPTY_FORMULA;
		return ex;
	}
}
