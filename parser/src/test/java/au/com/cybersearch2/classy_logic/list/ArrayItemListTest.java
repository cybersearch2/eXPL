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

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ListOperandTest
 * @author Andrew Bowley
 * 15 Jan 2015
 */
public class ArrayItemListTest 
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

        @Override
        public void setCursor(Cursor cursor)
        {
        }

    }
    

	private static final String NAME = "ListOperandName";
	private static final QualifiedName QNAME = QualifiedName.parseGlobalName(NAME);
	   
	@Test
	public void test_constructor()
	{
		ArrayItemList<Integer> listOperand = new ArrayItemList<Integer>(OperandType.INTEGER, QNAME);
		assertThat(listOperand.getName()).isEqualTo(NAME);
		assertThat(listOperand.isEmpty()).isTrue();
	}
	
	@Test
	public void test_assign()
	{
		ArrayItemList<Long> listOperand = new ArrayItemList<Long>(OperandType.INTEGER, QNAME);
		listOperand.setOffset(0);
		listOperand.setSize(4);
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
	}
/*	
	@Test
	public void test_new_variable_instance()
	{
		ArrayItemList<Long> intOperandList = new ArrayItemList<Long>(Long.class, QNAME);
		intOperandList.assignItem(0, Long.valueOf(21));
		ItemListVariable<Long> intListVariable = (ItemListVariable<Long>) intOperandList.newVariableInstance(new TestListItemSpec(0, "0"));
		intListVariable.evaluate(1);
		assertThat(intListVariable.getValue()).isEqualTo(21);
		intOperandList.assignItem(0, Long.valueOf(72));
		Operand expression = new TestIntegerOperand("test", Long.valueOf(0));
		intListVariable = (ItemListVariable<Long>) intOperandList.newVariableInstance(new TestListItemSpec(expression, "test"));
		intListVariable.evaluate(1);
		assertThat(intListVariable.getValue()).isEqualTo(72);
		ArrayItemList<Double> doubOperandList = new ArrayItemList<Double>(Double.class, QNAME);
		doubOperandList.assignItem(0, Double.valueOf(5.23));
		ItemListVariable<Double> doubListVariable = (ItemListVariable<Double>) doubOperandList.newVariableInstance(new TestListItemSpec(0, "0"));
		doubListVariable.evaluate(1);
		assertThat(doubListVariable.getValue()).isEqualTo(5.23);
		doubOperandList.assignItem(0, Double.valueOf(97.34));
		doubListVariable = (ItemListVariable<Double>) doubOperandList.newVariableInstance(new TestListItemSpec(expression, "test"));
		doubListVariable.evaluate(1);
		assertThat(doubListVariable.getValue()).isEqualTo(97.34);
		ArrayItemList<String> sOperandList = new ArrayItemList<String>(String.class, QNAME);
		sOperandList.assignItem(0, "testing123");
		ItemListVariable<String> sListVariable = (ItemListVariable<String>) sOperandList.newVariableInstance(new TestListItemSpec(0, "0"));
		sListVariable.evaluate(1);
		assertThat(sListVariable.getValue()).isEqualTo("testing123");
		sOperandList.assignItem(0, "xmas2014");
		sListVariable = (ItemListVariable<String>) sOperandList.newVariableInstance(new TestListItemSpec(expression, "test"));
		sListVariable.evaluate(1);
		assertThat(sListVariable.getValue()).isEqualTo("xmas2014");
		ArrayItemList<Boolean> boolOperandList = new ArrayItemList<Boolean>(Boolean.class, QNAME);
		boolOperandList.assignItem(0, Boolean.TRUE);
		ItemListVariable<Boolean> boolListVariable = (ItemListVariable<Boolean>) boolOperandList.newVariableInstance(new TestListItemSpec(0, "0"));
		boolListVariable.evaluate(1);
		assertThat(boolListVariable.getValue()).isTrue();
		boolOperandList.assignItem(0, Boolean.FALSE);
		boolListVariable = (ItemListVariable<Boolean>) boolOperandList.newVariableInstance(new TestListItemSpec(expression, "test"));
		boolListVariable.evaluate(1);
		assertThat(boolListVariable.getValue()).isFalse();
		ArrayItemList<BigDecimal> decOperandList = new ArrayItemList<BigDecimal>(BigDecimal.class, QNAME);
		decOperandList.assignItem(0, BigDecimal.TEN);
		ItemListVariable<BigDecimal> decListVariable = (ItemListVariable<BigDecimal>) decOperandList.newVariableInstance(new TestListItemSpec(0, "0"));
		decListVariable.evaluate(1);
		assertThat(decListVariable.getValue()).isEqualTo(BigDecimal.TEN);
		decOperandList.assignItem(0, BigDecimal.ONE);
		decListVariable = (ItemListVariable<BigDecimal>) decOperandList.newVariableInstance(new TestListItemSpec(expression, "test"));
		decListVariable.evaluate(1);
		assertThat(decListVariable.getValue()).isEqualTo(BigDecimal.ONE);
	}
	*/
}
