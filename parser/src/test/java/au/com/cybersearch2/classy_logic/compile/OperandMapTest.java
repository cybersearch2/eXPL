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
package au.com.cybersearch2.classy_logic.compile;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomListVariable;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;


/**
 * OperandMapTest
 * @author Andrew Bowley
 * 7 Feb 2015
 */
public class OperandMapTest 
{

	static public final String LIST_NAME = "ListName";
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_item_list_getListVariable()
	{
		OperandMap operandMap = new OperandMap();
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		@SuppressWarnings("rawtypes")
		ItemListVariable itemListVariable = mock(ItemListVariable.class);
		when(itemList.newVariableInstance(0, "0")).thenReturn(itemListVariable);
		assertThat(operandMap.getListVariable(itemList, LIST_NAME, 0, "0")).isEqualTo(itemListVariable);
		assertThat(operandMap.operandMap.get(LIST_NAME + ".0")).isEqualTo(itemListVariable);
		assertThat(operandMap.getListVariable(itemList, LIST_NAME, 0, "0")).isEqualTo(itemListVariable);
	}

	@Test
	public void test_axiom_list_getListVariable()
	{
		OperandMap operandMap = new OperandMap();
		AxiomList axiomList = mock(AxiomList.class);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(0), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.getListVariable(axiomList, LIST_NAME, 0, 0, "0")).isEqualTo(axiomListVariable);
		assertThat(operandMap.operandMap.get(LIST_NAME + ".0.0")).isEqualTo(axiomListVariable);
		assertThat(operandMap.getListVariable(axiomList, LIST_NAME, 0, 0, "0")).isEqualTo(axiomListVariable);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_item_list_newListVariableInstance()
	{
		OperandMap operandMap = new OperandMap();
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		operandMap.listMap.put(LIST_NAME, itemList);
		@SuppressWarnings("rawtypes")
		ItemListVariable itemListVariable = mock(ItemListVariable.class);
		Operand expression = mock(Operand.class);
		when(expression.isEmpty()).thenReturn(true);
		when(expression.getName()).thenReturn("X");
		when(itemList.newVariableInstance(expression, "X")).thenReturn(itemListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, expression)).isEqualTo(itemListVariable);
	}
	
	@Test
	public void test_axiom_list_newListVariableInstance_ee()
	{
		OperandMap operandMap = new OperandMap();
		AxiomList axiomList = mock(AxiomList.class);
		operandMap.listMap.put(LIST_NAME, axiomList);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		Operand axiomExpression = mock(Operand.class);
		when(axiomExpression.isEmpty()).thenReturn(true);
		Operand termExpression = mock(Operand.class);
		when(termExpression.isEmpty()).thenReturn(true);
		when(axiomList.newVariableInstance(eq(axiomExpression), eq(termExpression), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, axiomExpression, termExpression)).isEqualTo(axiomListVariable);
	}

	@Test
	public void test_axiom_list_newListVariableInstance_ie()
	{
		OperandMap operandMap = new OperandMap();
		AxiomList axiomList = mock(AxiomList.class);
		operandMap.listMap.put(LIST_NAME, axiomList);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		Operand axiomExpression = new IntegerOperand(Term.ANONYMOUS, Long.valueOf(0));
		Operand termExpression = mock(Operand.class);
		when(termExpression.isEmpty()).thenReturn(true);
		when(axiomList.newVariableInstance(eq(0), eq(termExpression), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, axiomExpression, termExpression)).isEqualTo(axiomListVariable);
	}

	@Test
	public void test_axiom_list_newListVariableInstance_ei()
	{
		OperandMap operandMap = new OperandMap();
		AxiomList axiomList = mock(AxiomList.class);
		operandMap.listMap.put(LIST_NAME, axiomList);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		Operand axiomExpression = mock(Operand.class);
		when(axiomExpression.isEmpty()).thenReturn(true);
		Operand termExpression = new IntegerOperand(Term.ANONYMOUS, Long.valueOf(0));
		when(axiomList.newVariableInstance(eq(axiomExpression), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, axiomExpression, termExpression)).isEqualTo(axiomListVariable);
	}

	@Test
	public void test_axiom_list_newListVariableInstance_ii()
	{
		OperandMap operandMap = new OperandMap();
		AxiomList axiomList = mock(AxiomList.class);
		operandMap.listMap.put(LIST_NAME, axiomList);
		Operand axiomExpression = new IntegerOperand(Term.ANONYMOUS, Long.valueOf(0));
		Operand termExpression = new IntegerOperand(Term.ANONYMOUS, Long.valueOf(0));
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(0), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, axiomExpression, termExpression)).isEqualTo(axiomListVariable);
		assertThat(operandMap.operandMap.get(LIST_NAME + ".0.0")).isEqualTo(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(LIST_NAME, axiomExpression, termExpression)).isEqualTo(axiomListVariable);
	}


}
