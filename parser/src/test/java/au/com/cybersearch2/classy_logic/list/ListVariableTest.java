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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;

import au.com.cybersearch2.classy_logic.expression.TestBigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.TestDoubleOperand;
import au.com.cybersearch2.classy_logic.expression.TestEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TestStringOperand;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.BigDecimalOperator;
import au.com.cybersearch2.classy_logic.operator.BooleanOperator;
import au.com.cybersearch2.classy_logic.operator.DoubleOperator;
import au.com.cybersearch2.classy_logic.operator.IntegerOperator;
import au.com.cybersearch2.classy_logic.operator.StringOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ListVariableTest
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public class ListVariableTest 
{
    class TestListItemSpec implements ListItemSpec
    {
        String suffix;
        int index;
        Operand indexExpression;
 
        public TestListItemSpec(int index, String suffix)
        {
            this.index = index;
            this.suffix = suffix;
        }
        
        public TestListItemSpec(Operand indexExpression, String suffix)
        {
            this.indexExpression = indexExpression;
            index = -1;
            this.suffix = suffix;
        }
        
        @Override
        public String getListName()
        {
            return NAME;
        }

        @Override
        public QualifiedName getQualifiedListName()
        {
            return new QualifiedName(getVariableName(NAME, suffix), QNAME);
        }

        @Override
        public int getItemIndex()
        {
            return index;
        }

        @Override
        public Operand getItemExpression()
        {
            return indexExpression;
        }

        @Override
        public String getSuffix()
        {
            return suffix;
        }
        
        @Override
        public void setSuffix(String suffix)
        {
            this.suffix = suffix;
        }

        /**
         * Returns variable name given list name and suffix
         * @param listName
         * @param suffix
         * @return String
         */
        protected String getVariableName(String listName, String suffix)
        {
            return NAME + "_" + suffix;
        }

        @Override
        public QualifiedName getVariableName()
        {
            return null;
        }

        @Override
        public void assemble(ItemList<?> itemList)
        {
        }

        @Override
        public boolean evaluate(ItemList<?> itemList, int id)
        {
            return false;
        }

        @Override
        public void setItemIndex(int appendIndex)
        {
        }

    }
    
	private static final String NAME = "ListOperandName";
    static QualifiedName QNAME = QualifiedName.parseName(NAME);
/*
	@Test
	public void test_fixed_index_unify_evaluate_backup()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(5)).thenReturn(false, true);
		when(itemList.getItem(5)).thenReturn(Integer.valueOf(13));
		ItemListVariable<Integer> variable = new ItemListVariable<Integer>(itemList, new IntegerOperator(), new TestListItemSpec(5, "5"));
		Parameter otherTerm = new Parameter(NAME, Integer.valueOf(13));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(1);
		//variable.unifyTerm(otherTerm, 1);
		variable.evaluate(1);
		assertThat(variable.index).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		variable.backup(1);
		assertThat(variable.isEmpty()).isTrue();
	    verify(itemList).assignItem(5, Integer.valueOf(13));
	}
	
	@Test
	public void test_expression_index_unify_evaluate_backup()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(5)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(5)).thenReturn(Long.valueOf(13));
		IntegerOperand x = new TestIntegerOperand("x", Long.valueOf(5));
		Operand indexExpression = new TestEvaluator("y", x, "++");
		ItemListVariable<Long> variable = new ItemListVariable<Long>(itemList, new IntegerOperator(), new TestListItemSpec(indexExpression, "y"));
		Parameter otherTerm = new Parameter(NAME, Long.valueOf(13));
		// No unification possible
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(0);
		assertThat(variable.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(variable.index).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13L);
		assertThat(indexExpression.getValue()).isEqualTo(5L);
		assertThat(x.getValue()).isEqualTo(6);
		assertThat(variable.backup(1)).isTrue();
		assertThat(variable.indexExpression.isEmpty()).isTrue();
		assertThat(variable.isEmpty()).isTrue();
	}
	
	@Test
	public void test_expression_index_empty()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
        when(itemList.getName()).thenReturn(NAME);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(5)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(5)).thenReturn(Integer.valueOf(13));
		Operand indexExpression = new TestIntegerOperand("x");
		ItemListVariable<Integer> variable = new ItemListVariable<Integer>(itemList, new IntegerOperator(), new TestListItemSpec(indexExpression, "x"));
		try
		{
			variable.evaluate(1);
			failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch(ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("Index for list \"" + NAME + "\" is empty");
		}
	}
	
	@Test
	public void test_expression_index_invalid_type()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
        when(itemList.getName()).thenReturn(NAME);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(5)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(5)).thenReturn(Integer.valueOf(13));
		Operand indexExpression = new TestBooleanOperand("x", Boolean.TRUE);
		ItemListVariable<Integer> variable = new ItemListVariable<Integer>(itemList, new IntegerOperator(), new TestListItemSpec(indexExpression, "x"));
		try
		{
			variable.evaluate(1);
			failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch(ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("\"" + NAME + "[true]\" is not a valid value");
		}
	}
	
	@Test
	public void test_assign()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(false);
		when(itemList.getLength()).thenReturn(10);
		ItemListVariable<BigDecimal> variable = new ItemListVariable<BigDecimal>(itemList, new BigDecimalOperator(), new TestListItemSpec(0, "0"));
		variable.assign(new Parameter(Term.ANONYMOUS, BigDecimal.ONE));
	    verify(itemList).assignItem(0, BigDecimal.ONE);
	}
		
    @Test
	public void test_assign_update()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(BigDecimal.TEN);
		ItemListVariable<BigDecimal> variable = new ItemListVariable<BigDecimal>(itemList, new BigDecimalOperator(), new TestListItemSpec(0, "0"));
		variable.assign(new Parameter(Term.ANONYMOUS, BigDecimal.ONE));
	    verify(itemList).assignItem(0, BigDecimal.ONE);
	}
       
    @Test
	public void test_get_value_update()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
        when(itemList.getName()).thenReturn(NAME);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(BigDecimal.TEN, BigDecimal.ZERO);
		ItemListVariable<BigDecimal> variable = new ItemListVariable<BigDecimal>(itemList, new BigDecimalOperator(), new TestListItemSpec(0, "ListOperandName.0"));
		assertThat(variable.toString()).isEqualTo("ListOperandName.0=10");
		Parameter param = new Parameter(NAME);
		assertThat(variable.unifyTerm(param, 1)).isEqualTo(1);
		assertThat(param.getValue()).isEqualTo(BigDecimal.ZERO);
		assertThat(variable.toString()).isEqualTo("ListOperandName.0=0");
	}
    
    @Test
	public void test_get_value_unitialised()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
        when(itemList.getName()).thenReturn(NAME);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		Operand expression = new TestIntegerOperand("x", Integer.valueOf(5));
		ItemListVariable<BigDecimal> variable = new ItemListVariable<BigDecimal>(itemList, new BigDecimalOperator(), new TestListItemSpec(expression, "ListOperandName.x"));
		assertThat(variable.toString()).isEqualTo("ListOperandName.x=<empty>");
		assertThat(variable.getValue()).isNull();
		verify(itemList).hasItem(-1);
	}
    
	@Test
	public void test_big_decimal_operand_ops()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(BigDecimal.TEN);
		ItemListVariable<BigDecimal> variable = new ItemListVariable<BigDecimal>(itemList, new BigDecimalOperator(), new TestListItemSpec(0, "0"));
		Parameter otherTerm = new Parameter("x");
		variable.unifyTerm(otherTerm, 1);
		assertThat(otherTerm.getValue()).isEqualTo(BigDecimal.TEN);
		assertThat(variable.getOperator().getLeftOperandOps()).isEqualTo(new TestBigDecimalOperand("*").getOperator().getLeftOperandOps());
		assertThat(variable.getOperator().getRightOperandOps()).isEqualTo(new TestBigDecimalOperand("*").getOperator().getRightOperandOps());
		assertThat(variable.getOperator().booleanEvaluation(new TestBigDecimalOperand("L", BigDecimal.ZERO), OperatorEnum.LT, new TestBigDecimalOperand("R", BigDecimal.TEN))).isTrue();
		assertThat(variable.getOperator().numberEvaluation(new TestBigDecimalOperand("L", BigDecimal.ONE), OperatorEnum.PLUS, new TestBigDecimalOperand("R", BigDecimal.TEN))).isEqualTo(new BigDecimal("11"));
		assertThat(variable.getOperator().numberEvaluation(OperatorEnum.MINUS, new TestBigDecimalOperand("R", BigDecimal.TEN))).isEqualTo(new BigDecimal("-10"));
	}

	@Test
	public void test_boolean_operand_ops()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(Boolean.TRUE);
		ItemListVariable<Boolean> variable = new ItemListVariable<Boolean>(itemList, new BooleanOperator(), new TestListItemSpec(0, "0"));
		Parameter otherTerm = new Parameter("x");
		variable.unifyTerm(otherTerm, 1);
		assertThat(otherTerm.getValue()).isEqualTo(Boolean.TRUE);
		assertThat(variable.getOperator().getLeftOperandOps()).isEqualTo(new TestBooleanOperand("*").getOperator().getLeftOperandOps());
		assertThat(variable.getOperator().getRightOperandOps()).isEqualTo(new TestBooleanOperand("*").getOperator().getRightOperandOps());
		assertThat(variable.getOperator().booleanEvaluation(new TestBooleanOperand("L", Boolean.FALSE), OperatorEnum.NE, new TestBooleanOperand("R", Boolean.TRUE))).isTrue();
	}
	
	@Test
	public void test_integer_operand_ops()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(Long.valueOf(13L));
		ItemListVariable<Long> variable = new ItemListVariable<Long>(itemList, new IntegerOperator(), new TestListItemSpec(0, "0"));
		Parameter otherTerm = new Parameter("x");
		variable.unifyTerm(otherTerm, 1);
		assertThat(otherTerm.getValue()).isEqualTo(13L);
		assertThat(variable.getOperator().getLeftOperandOps()).isEqualTo(new TestIntegerOperand("*").getOperator().getLeftOperandOps());
		assertThat(variable.getOperator().getRightOperandOps()).isEqualTo(new TestIntegerOperand("*").getOperator().getRightOperandOps());
		assertThat(variable.getOperator().booleanEvaluation(new TestIntegerOperand("L", Long.valueOf(2)), OperatorEnum.GE, new TestIntegerOperand("R", Long.valueOf(2)))).isTrue();
		assertThat(variable.getOperator().numberEvaluation(new TestIntegerOperand("L", Long.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Long.valueOf(5)))).isEqualTo(new Long(2));
		assertThat(variable.getOperator().numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Integer.valueOf(8)))).isEqualTo(new Long(9));
	}

	@Test
	public void test_double_operand_ops()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn(Double.valueOf(55.98d));
		ItemListVariable<Double> variable = new ItemListVariable<Double>(itemList, new DoubleOperator(), new TestListItemSpec(0, "0"));
		Parameter otherTerm = new Parameter("x");
		variable.unifyTerm(otherTerm, 1);
		assertThat(otherTerm.getValue()).isEqualTo(55.98d);
		assertThat(variable.getOperator().getLeftOperandOps()).isEqualTo(new TestDoubleOperand("*").getOperator().getLeftOperandOps());
		assertThat(variable.getOperator().getRightOperandOps()).isEqualTo(new TestDoubleOperand("*").getOperator().getRightOperandOps());
		assertThat(variable.getOperator().booleanEvaluation(new TestDoubleOperand("L", Double.valueOf(1.0)), OperatorEnum.LT, new TestDoubleOperand("R", Double.valueOf(0.5)))).isFalse();
		assertThat(variable.getOperator().numberEvaluation(new TestDoubleOperand("L", Double.valueOf(2.0)), OperatorEnum.STAR, new TestDoubleOperand("R", Double.valueOf(3.0)))).isEqualTo(new Double("6.0"));
		assertThat(variable.getOperator().numberEvaluation(OperatorEnum.MINUS, new TestDoubleOperand("R", Double.valueOf(99.9)))).isEqualTo(new Double(-99.9));
	}
	
	@Test
	public void test_string_operand_ops()
	{
		@SuppressWarnings("rawtypes")
		ItemList itemList = mock(ItemList.class);
		when(itemList.getQualifiedName()).thenReturn(QNAME);
		when(itemList.hasItem(0)).thenReturn(true);
		when(itemList.getLength()).thenReturn(10);
		when(itemList.getItem(0)).thenReturn("1.0f");
		ItemListVariable<String> variable = new ItemListVariable<String>(itemList, new StringOperator(), new TestListItemSpec(0, "0"));
		Parameter otherTerm = new Parameter("x");
		variable.unifyTerm(otherTerm, 1);
		assertThat(otherTerm.getValue()).isEqualTo("1.0f");
		assertThat(variable.getOperator().getLeftOperandOps()).isEqualTo(new TestStringOperand("*").getOperator().getLeftOperandOps());
		assertThat(variable.getOperator().getRightOperandOps()).isEqualTo(new TestStringOperand("*").getOperator().getRightOperandOps());
		assertThat(variable.getOperator().booleanEvaluation(new TestStringOperand("L", "hello"), OperatorEnum.NE, new TestStringOperand("R", "world"))).isTrue();
	}
	*/
}
