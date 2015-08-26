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
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomListSpec;
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
	static final QualifiedName QNAME = QualifiedName.parseName(LIST_NAME);
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_item_list_getListVariable()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		@SuppressWarnings("rawtypes")
		ItemListVariable itemListVariable = mock(ItemListVariable.class);
		when(itemList.newVariableInstance(0, "0", 0)).thenReturn(itemListVariable);
		assertThat(operandMap.getListVariable(itemList, LIST_NAME, 0, "0")).isEqualTo(itemListVariable);
	}

	@Test
	public void test_axiom_list_getListVariable()
	{
        String LIST_VAR_NAME = LIST_NAME + ".0.0";
        QualifiedName Q_LIST_VAR_NAME = new QualifiedName(LIST_VAR_NAME, QualifiedName.ANONYMOUS);
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		AxiomList axiomList = mock(AxiomList.class);
	    when(axiomList.getQualifiedName()).thenReturn(QNAME);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomListVariable.getName()).thenReturn(LIST_VAR_NAME);
		when(axiomListVariable.getQualifiedName()).thenReturn(Q_LIST_VAR_NAME);
		when(axiomList.newVariableInstance(eq(0), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		AxiomListSpec axiomListSpec = mock(AxiomListSpec.class);
		when(axiomListSpec.getListName()).thenReturn(LIST_NAME);
		when(axiomListSpec.getAxiomList()).thenReturn(axiomList);
		when(axiomListSpec.getAxiomIndex()).thenReturn(0);
        when(axiomListSpec.getTermIndex()).thenReturn(0);
        when(axiomListSpec.getSuffix()).thenReturn("0");
		assertThat(operandMap.getListVariable(axiomListSpec)).isEqualTo(axiomListVariable);
		operandMap.addOperand(axiomListVariable);
        assertThat(operandMap.operandMap.get(Q_LIST_VAR_NAME)).isEqualTo(axiomListVariable);
		assertThat(operandMap.getListVariable(axiomListSpec)).isEqualTo(axiomListVariable);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_item_list_newListVariableInstance()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
        when(itemList.getName()).thenReturn(LIST_NAME);
		//operandMap.listMap.put(LIST_NAME, itemList);
		@SuppressWarnings("rawtypes")
		ItemListVariable itemListVariable = mock(ItemListVariable.class);
		Operand expression = mock(Operand.class);
		when(expression.isEmpty()).thenReturn(true);
		when(expression.getName()).thenReturn("X");
		when(itemList.newVariableInstance(expression, "X", 0)).thenReturn(itemListVariable);
		assertThat(operandMap.newListVariableInstance(itemList, expression)).isEqualTo(itemListVariable);
	}
	
	@Test
	public void test_axiom_list_newListVariableInstance_ee()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		AxiomList axiomList = mock(AxiomList.class);
        AxiomListSpec axiomListSpec = mock(AxiomListSpec.class);
        when(axiomListSpec.getListName()).thenReturn(LIST_NAME);
        when(axiomListSpec.getAxiomList()).thenReturn(axiomList);
        when(axiomListSpec.getAxiomIndex()).thenReturn(-1);
        when(axiomListSpec.getTermIndex()).thenReturn(-1);
        when(axiomListSpec.getSuffix()).thenReturn("0");
		Operand axiomExpression = mock(Operand.class);
		when(axiomExpression.isEmpty()).thenReturn(true);
		when(axiomListSpec.getAxiomExpression()).thenReturn(axiomExpression);
		Operand termExpression = mock(Operand.class);
		when(termExpression.isEmpty()).thenReturn(true);
		when(axiomListSpec.getTermExpression()).thenReturn(termExpression);
        AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(axiomExpression), eq(termExpression), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(axiomListSpec)).isEqualTo(axiomListVariable);
	}
	
	@Test
	public void test_axiom_list_newListVariableInstance_ie()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		AxiomList axiomList = mock(AxiomList.class);
		Operand axiomExpression = new IntegerOperand(QualifiedName.ANONYMOUS, Long.valueOf(0));
        AxiomListSpec axiomListSpec = mock(AxiomListSpec.class);
        when(axiomListSpec.getListName()).thenReturn(LIST_NAME);
        when(axiomListSpec.getAxiomList()).thenReturn(axiomList);
        when(axiomListSpec.getAxiomIndex()).thenReturn(0);
        when(axiomListSpec.getTermIndex()).thenReturn(-1);
        when(axiomListSpec.getSuffix()).thenReturn("0");
        when(axiomListSpec.getAxiomExpression()).thenReturn(axiomExpression);
        Operand termExpression = mock(Operand.class);
        when(termExpression.isEmpty()).thenReturn(true);
        when(axiomListSpec.getTermExpression()).thenReturn(termExpression);
        AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(0), eq(termExpression), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(axiomListSpec)).isEqualTo(axiomListVariable);
	}
	
	@Test
	public void test_axiom_list_newListVariableInstance_ei()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		AxiomList axiomList = mock(AxiomList.class);
		Operand termExpression = new IntegerOperand(QualifiedName.ANONYMOUS, Long.valueOf(0));
        AxiomListSpec axiomListSpec = mock(AxiomListSpec.class);
        when(axiomListSpec.getListName()).thenReturn(LIST_NAME);
        when(axiomListSpec.getAxiomList()).thenReturn(axiomList);
        when(axiomListSpec.getAxiomIndex()).thenReturn(-1);
        when(axiomListSpec.getTermIndex()).thenReturn(0);
        when(axiomListSpec.getSuffix()).thenReturn("0");
        Operand axiomExpression = mock(Operand.class);
        when(axiomExpression.isEmpty()).thenReturn(true);
        when(axiomListSpec.getAxiomExpression()).thenReturn(axiomExpression);
        when(axiomListSpec.getTermExpression()).thenReturn(termExpression);
        AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(axiomExpression), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(axiomListSpec)).isEqualTo(axiomListVariable);
	}
	
	@Test
	public void test_axiom_list_newListVariableInstance_ii()
	{
		OperandMap operandMap = new OperandMap(QualifiedName.ANONYMOUS);
		AxiomList axiomList = mock(AxiomList.class);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		Operand axiomExpression = new IntegerOperand(QualifiedName.ANONYMOUS, Long.valueOf(0));
		Operand termExpression = new IntegerOperand(QualifiedName.ANONYMOUS, Long.valueOf(0));
        AxiomListSpec axiomListSpec = mock(AxiomListSpec.class);
        when(axiomListSpec.getListName()).thenReturn(LIST_NAME);
        when(axiomListSpec.getAxiomList()).thenReturn(axiomList);
        when(axiomListSpec.getAxiomIndex()).thenReturn(0);
        when(axiomListSpec.getTermIndex()).thenReturn(0);
        when(axiomListSpec.getSuffix()).thenReturn("0");
        when(axiomListSpec.getAxiomExpression()).thenReturn(axiomExpression);
        when(axiomListSpec.getTermExpression()).thenReturn(termExpression);
		AxiomListVariable axiomListVariable = mock(AxiomListVariable.class);
		when(axiomList.newVariableInstance(eq(0), eq(0), isA(String.class))).thenReturn(axiomListVariable);
		assertThat(operandMap.newListVariableInstance(axiomListSpec)).isEqualTo(axiomListVariable);
	}
}
