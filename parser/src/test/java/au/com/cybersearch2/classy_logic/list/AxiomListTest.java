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
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * AxiomListTest
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomListTest 
{
	private static final String NAME = "ListOperandName";
	private static final String KEY = "AxiomKey";

	@Test
	public void test_constructor()
	{
		AxiomList axiomList = new AxiomList(NAME, KEY);
		assertThat(axiomList.getName()).isEqualTo(NAME);
		assertThat(axiomList.getLength()).isEqualTo(0);
		assertThat(axiomList.hasItem(0)).isFalse();
		assertThat(axiomList.isEmpty()).isTrue();
		try
		{
			axiomList.proxy.setName(NAME + 1);
		    failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch (IllegalStateException e)
		{
			assertThat(e.getMessage()).isEqualTo("Assigning name \"ListOperandName1\" to Term already named \"ListOperandName\" not allowed");
		}
	}

	@Test
	public void test_assign()
	{
		AxiomList axiomList = new AxiomList(NAME, KEY);
		AxiomTermList axiomTermList = new AxiomTermList(KEY, KEY);
		axiomList.assignItem(0, axiomTermList);
		assertThat(axiomList.getItem(0)).isEqualTo(axiomTermList);
		AxiomTermList axiomOperandList2 = new AxiomTermList(KEY + 1, KEY + 1);
		axiomList.assignItem(0, axiomOperandList2);
		assertThat(axiomList.getItem(0)).isEqualTo(axiomOperandList2);
		assertThat(axiomList.getLength()).isEqualTo(1);
		assertThat(axiomList.hasItem(0)).isTrue();
		assertThat(axiomList.isEmpty()).isFalse();
	}
	
	@Test
	public void test_new_variable_instance()
	{
		AxiomList axiomList = new AxiomList(NAME, KEY);
		AxiomTermList axiomTermList = new AxiomTermList(KEY, KEY);
		axiomList.assignItem(0, axiomTermList);
		ItemListVariable<AxiomTermList> axiomListVariable = axiomList.newVariableInstance(0, "0", 1);
		axiomListVariable.evaluate(1);
		assertThat(axiomListVariable.getValue()).isEqualTo(axiomTermList);
		AxiomTermList axiomOperandList2 = new AxiomTermList(KEY + 1, KEY + 1);
		axiomList.assignItem(0, axiomOperandList2);
		Operand expression = new IntegerOperand("test", Integer.valueOf(0));
		axiomListVariable = axiomList.newVariableInstance(expression, "test", 1);
		axiomListVariable.evaluate(1);
		assertThat(axiomListVariable.getValue()).isEqualTo(axiomOperandList2);
	}
	
	@Test
	public void test_axiom_listener()
	{
		AxiomList axiomList = new AxiomList(NAME, KEY);
		AxiomListener axiomListener = axiomList.getAxiomListener();
		Axiom axiom1 = new Axiom("one");
		axiomListener.onNextAxiom(axiom1);
		AxiomTermList axiomOperandList1 = axiomList.getItem(0);
		assertThat(axiomOperandList1.toString()).isEqualTo("one()");
		Axiom axiom2 = new Axiom("two");
		axiomListener.onNextAxiom(axiom2);
		AxiomTermList axiomOperandList2 = axiomList.getItem(1);
		assertThat(axiomOperandList2.toString()).isEqualTo("two()");
	}
}
