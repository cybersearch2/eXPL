/**
    Copyright (C) 2014  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.classy_logic.list;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.BigDecimalTerm;
import au.com.cybersearch2.classy_logic.terms.BooleanTerm;
import au.com.cybersearch2.classy_logic.terms.DoubleTerm;
import au.com.cybersearch2.classy_logic.terms.IntegerTerm;
import au.com.cybersearch2.classy_logic.terms.StringTerm;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * AxiomTermListTest
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public class AxiomTermListTest 
{
	private static final String NAME = "ListOperandName";
    static QualifiedName QNAME = QualifiedName.parseName(NAME);
	private static final String KEY = "AxiomKey";
	private static final String OUT_OF_BOUNDS_MESSAGE = "AxiomTermList \"ListOperandName\" index 0 out of bounds";
	
	
	@Test
	public void test_constructor()
	{
		AxiomTermList axiomTermList = new AxiomTermList(QNAME, KEY);
		assertThat(axiomTermList.getName()).isEqualTo(NAME);
		assertThat(axiomTermList.getKey()).isEqualTo(KEY);
		assertThat(axiomTermList.getLength()).isEqualTo(0);
		assertThat(axiomTermList.hasItem(0)).isFalse();
		assertThat(axiomTermList.isEmpty()).isTrue();
		assertThat(axiomTermList.getItem(0)).isNull();
		Axiom axiom = getTestAxiom();
		axiomTermList.setAxiom(axiom);
		for (int i = 0; i < axiom.getTermCount(); i++)
			assertThat(axiomTermList.getItem(i)).isEqualTo(axiom.getTermByIndex(i));
		for (int i = 0; i < axiom.getTermCount(); i++)
		{
			ItemListVariable<Object> listVariable = axiomTermList.newVariableInstance(i, Long.toString(i), 1);
			assertThat(listVariable.getValue()).isEqualTo(axiom.getTermByIndex(i).getValue());
		}
		for (int i = 0; i < axiom.getTermCount(); i++)
		{
			IntegerOperand expression = new TestIntegerOperand("" + i);
			expression.assign(Long.valueOf(i));
			ItemListVariable<Object> listVariable = axiomTermList.newVariableInstance(expression, Long.toString(i), 1);
			listVariable.evaluate(1);
			assertThat(listVariable.getValue()).isEqualTo(axiom.getTermByIndex(i).getValue());
		}
	}
	
	@Test
	public void test_verify()
	{
		AxiomTermList axiomOperandList = new AxiomTermList(QNAME, KEY);
		try
		{
			axiomOperandList.assignItem(0, new Object());
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch(IllegalStateException e)
		{
			assertThat(e.getMessage()).isEqualTo(OUT_OF_BOUNDS_MESSAGE);
		}
		axiomOperandList.newVariableInstance(0, "0", 1);
		ItemListVariable<Object> variable1 = axiomOperandList.newVariableInstance(new TestIntegerOperand("x"), "x", 1);
		assertThat(variable1).isInstanceOf(AxiomTermListVariable.class);
		axiomOperandList.setAxiom(new Axiom(KEY));
		try
		{
			axiomOperandList.assignItem(0, new Object());
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch(IllegalStateException e)
		{
			assertThat(e.getMessage()).isEqualTo(OUT_OF_BOUNDS_MESSAGE);
		}
		axiomOperandList.newVariableInstance(0, "0", 1);
		ItemListVariable<Object> variable2 = axiomOperandList.newVariableInstance(new TestIntegerOperand("x"), "x", 1);
		assertThat(variable2).isInstanceOf(AxiomTermListVariable.class);
		assertThat(variable1).isNotEqualTo(variable2);
	}
	
	protected Axiom getTestAxiom()
	{
		Axiom axiom = new Axiom(KEY);
		axiom.addTerm(new StringTerm("Hello world!"));
		axiom.addTerm(new IntegerTerm(21));
		axiom.addTerm(new DoubleTerm(41.5d));
		axiom.addTerm(new BooleanTerm(Boolean.TRUE));
		axiom.addTerm(new BigDecimalTerm("11034"));
		return axiom;
	}
}
