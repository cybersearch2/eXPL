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

import java.math.BigDecimal;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.list.ArrayItemList;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * ListOperandTest
 * @author Andrew Bowley
 * 15 Jan 2015
 */
public class ArrayItemListTest 
{

	private static final String NAME = "ListOperandName";

	@Test
	public void test_constructor()
	{
		IntegerOperand proxy = new IntegerOperand(NAME);
		ArrayItemList<Integer> listOperand = new ArrayItemList<Integer>(Integer.class, proxy);
		assertThat(listOperand.getName()).isEqualTo(NAME);
		assertThat(listOperand.isEmpty()).isTrue();
		try
		{
			proxy.setName(NAME + 1);
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
		IntegerOperand proxy = new IntegerOperand(NAME);
		ArrayItemList<Long> listOperand = new ArrayItemList<Long>(Long.class, proxy);
		listOperand.assignItem(0, Long.valueOf(17));
		assertThat(listOperand.getItem(0)).isEqualTo(17);
		listOperand.assignItem(0, Long.valueOf(21));
		assertThat(listOperand.getItem(0)).isEqualTo(21);
		listOperand.assignItem(1, Long.valueOf(8));
		assertThat(listOperand.getItem(1)).isEqualTo(8);
		listOperand.assignItem(0, Long.valueOf(-1));
		assertThat(listOperand.getItem(0)).isEqualTo(-1);
		assertThat(listOperand.getItem(1)).isEqualTo(8);
		listOperand.assignItem(3, Long.valueOf(89));
		assertThat(listOperand.getItem(0)).isEqualTo(-1);
		assertThat(listOperand.getItem(1)).isEqualTo(8);
		assertThat(listOperand.getItem(3)).isEqualTo(89);
		try
		{
			listOperand.getItem(2);
		    failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch (ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo(NAME + " item 2 not found");
		}
		try
		{
			listOperand.assignItem(0, Double.valueOf(99.9));
		    failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch (ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("Cannot assign type java.lang.Double to List " + NAME);
		}
	}

	@Test
	public void test_new_variable_instance()
	{
		ArrayItemList<Long> intOperandList = new ArrayItemList<Long>(Long.class, new IntegerOperand(NAME));
		intOperandList.assignItem(0, Long.valueOf(21));
		ItemListVariable<Long> intListVariable = intOperandList.newVariableInstance(0, "0");
		intListVariable.evaluate(1);
		assertThat(intListVariable.getValue()).isEqualTo(21);
		intOperandList.assignItem(0, Long.valueOf(72));
		Operand expression = new IntegerOperand("test", Long.valueOf(0));
		intListVariable = intOperandList.newVariableInstance(expression, "test");
		intListVariable.evaluate(1);
		assertThat(intListVariable.getValue()).isEqualTo(72);
		ArrayItemList<Double> doubOperandList = new ArrayItemList<Double>(Double.class, new DoubleOperand(NAME));
		doubOperandList.assignItem(0, Double.valueOf(5.23));
		ItemListVariable<Double> doubListVariable = doubOperandList.newVariableInstance(0, "0");
		doubListVariable.evaluate(1);
		assertThat(doubListVariable.getValue()).isEqualTo(5.23);
		doubOperandList.assignItem(0, Double.valueOf(97.34));
		doubListVariable = doubOperandList.newVariableInstance(expression, "test");
		doubListVariable.evaluate(1);
		assertThat(doubListVariable.getValue()).isEqualTo(97.34);
		ArrayItemList<String> sOperandList = new ArrayItemList<String>(String.class, new StringOperand(NAME));
		sOperandList.assignItem(0, "testing123");
		ItemListVariable<String> sListVariable = sOperandList.newVariableInstance(0, "0");
		sListVariable.evaluate(1);
		assertThat(sListVariable.getValue()).isEqualTo("testing123");
		sOperandList.assignItem(0, "xmas2014");
		sListVariable = sOperandList.newVariableInstance(expression, "test");
		sListVariable.evaluate(1);
		assertThat(sListVariable.getValue()).isEqualTo("xmas2014");
		ArrayItemList<Boolean> boolOperandList = new ArrayItemList<Boolean>(Boolean.class, new BooleanOperand(NAME));
		boolOperandList.assignItem(0, Boolean.TRUE);
		ItemListVariable<Boolean> boolListVariable = boolOperandList.newVariableInstance(0, "0");
		boolListVariable.evaluate(1);
		assertThat(boolListVariable.getValue()).isTrue();
		boolOperandList.assignItem(0, Boolean.FALSE);
		boolListVariable = boolOperandList.newVariableInstance(expression, "test");
		boolListVariable.evaluate(1);
		assertThat(boolListVariable.getValue()).isFalse();
		ArrayItemList<BigDecimal> decOperandList = new ArrayItemList<BigDecimal>(BigDecimal.class, new BigDecimalOperand(NAME));
		decOperandList.assignItem(0, BigDecimal.TEN);
		ItemListVariable<BigDecimal> decListVariable = decOperandList.newVariableInstance(0, "0");
		decListVariable.evaluate(1);
		assertThat(decListVariable.getValue()).isEqualTo(BigDecimal.TEN);
		decOperandList.assignItem(0, BigDecimal.ONE);
		decListVariable = decOperandList.newVariableInstance(expression, "test");
		decListVariable.evaluate(1);
		assertThat(decListVariable.getValue()).isEqualTo(BigDecimal.ONE);
	}
}
