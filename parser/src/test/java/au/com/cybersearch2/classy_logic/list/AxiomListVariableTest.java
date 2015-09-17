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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.TestEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.IntegerTerm;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomListVariableTest
 * @author Andrew Bowley
 * 29 Jan 2015
 */
public class AxiomListVariableTest 
{
	private static final String NAME = "AxiomListName";
	static QualifiedName QNAME = QualifiedName.parseName(NAME);
	
	private static final String[] CITY_NAME_HEIGHT =
	{
		"city(bilene, 1718)",
		"city(addis ababa, 8000)",
		"city(denver, 5280)",
		"city(flagstaff, 6970)",
		"city(jacksonville, 8)",
		"city(leadville, 10200)",
		"city(madrid, 1305)",
		"city(richmond, 19)",
		"city(spokane, 1909)",
		"city(wichita, 1305)"
	};


	@Test
	public void test_term_fixed_index_unify_evaluate_backup()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
		when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(false, true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "0");
		assertThat(variable.getName()).isEqualTo(NAME + "_5_0");
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.isEmpty()).isTrue();
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		assertThat(variable.getValue()).isEqualTo(13);
		Parameter otherTerm = new Parameter(NAME);
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(1);
		assertThat(otherTerm.getValue()).isEqualTo(13);
		variable.evaluate(1);
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		variable.backup(1);
		assertThat(variable.isEmpty()).isTrue();
	}

	@Test
	public void test_null_fixed_index_unify_evaluate_backup()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "0");
		assertThat(variable.getName()).isEqualTo(NAME + "_5_0");
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.isEmpty()).isTrue();
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(null);
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		assertThat(variable.isEmpty()).isTrue();
		Parameter otherTerm = new Parameter(NAME, Integer.valueOf(13));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(1);
		assertThat(variable.getValue()).isEqualTo(13);
		variable.evaluate(1);
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		variable.backup(1);
		assertThat(variable.isEmpty()).isTrue();
		
		otherTerm = new Parameter(NAME, Integer.valueOf(27));
		assertThat(variable.unifyTerm(otherTerm, 2)).isEqualTo(2);
		assertThat(variable.getValue()).isEqualTo(27);
	    verify(axiomTermListVariable, times(2)).assign(otherTerm);
	}

	@Test
	public void test_term_expression_index_unify_evaluate_backup()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		IntegerOperand x = new TestIntegerOperand("x", Integer.valueOf(5));
		Operand indexExpression = new TestEvaluator("y ", x, "++", null);
		AxiomListVariable variable = new AxiomListVariable(axiomList, indexExpression, "0");
		assertThat(variable.getName()).isEqualTo(NAME + "_index_0");
		assertThat(variable.axiomIndex).isEqualTo(-1);
		assertThat(variable.isEmpty()).isTrue();
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		assertThat(variable.isEmpty()).isTrue();
		// No unification possible
		Parameter otherTerm = new Parameter(NAME, Integer.valueOf(13));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(0);
		assertThat(variable.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		assertThat(x.getValue()).isEqualTo(6);
		assertThat(variable.backup(1)).isTrue();
		assertThat(variable.axiomExpression.isEmpty()).isTrue();
		assertThat(variable.isEmpty()).isTrue();
	}

	@Test
	public void test_term_expression_x2_index_unify_evaluate_backup()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		IntegerOperand x = new TestIntegerOperand("x", Integer.valueOf(5));
		Operand axiomExpression = new TestEvaluator("y ", x, "++", null);
		AxiomListVariable variable = new AxiomListVariable(axiomList, axiomExpression, "item");
		assertThat(variable.getName()).isEqualTo(NAME + "_index_item");
		assertThat(variable.axiomIndex).isEqualTo(-1);
		assertThat(variable.isEmpty()).isTrue();
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		IntegerOperand a = new TestIntegerOperand("a", Integer.valueOf(0));
		Operand termExpression = new TestEvaluator("b ", a, "++", null);
		when(axiomTermList.newVariableInstance(isA(Operand.class), isA(String.class), eq(1))).thenReturn(axiomTermListVariable);
		variable.setTermExpression(termExpression, 0);
		assertThat(variable.isEmpty()).isTrue();
		// No unification possible
		Parameter otherTerm = new Parameter(NAME, Integer.valueOf(13));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(0);
		assertThat(variable.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		assertThat(x.getValue()).isEqualTo(6);
		assertThat(variable.backup(1)).isTrue();
		assertThat(variable.axiomExpression.isEmpty()).isTrue();
		assertThat(variable.isEmpty()).isTrue();
		verify(axiomTermListVariable).evaluate(1);
	}

	@Test
	public void test_term_fixed_expression_index_unify_evaluate_backup()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "item");
		assertThat(variable.getName()).isEqualTo(NAME + "_5_item");
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.isEmpty()).isTrue();
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		when(axiomTermListVariable.backup(1)).thenReturn(true);
		IntegerOperand a = new TestIntegerOperand("a", Integer.valueOf(0));
		Operand termExpression = new TestEvaluator("b ", a, "++", null);
		when(axiomTermList.newVariableInstance(isA(Operand.class), isA(String.class),eq(1))).thenReturn(axiomTermListVariable);
		variable.setTermExpression(termExpression, 1);
		assertThat(variable.isEmpty()).isTrue();
		// No unification possible
		Parameter otherTerm = new Parameter(NAME, Integer.valueOf(13));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(0);
		assertThat(variable.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(variable.axiomIndex).isEqualTo(5);
		assertThat(variable.getValue()).isEqualTo(13);
		assertThat(variable.backup(1)).isTrue();
		assertThat(variable.isEmpty()).isTrue();
		verify(axiomTermListVariable).evaluate(1);
	}

	@Test
	public void test_expression_index_empty()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		Operand indexExpression = new TestIntegerOperand("x");
		AxiomListVariable variable = new AxiomListVariable(axiomList, indexExpression, "0");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		try
		{
			variable.evaluate(1);
			failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch(ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("Axiom index for list \"" + NAME + "\" is empty");
		}
	}

	@Test
	public void test_expression_index_invalid_type()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		Operand indexExpression = new TestBooleanOperand("x", Boolean.TRUE);
		AxiomListVariable variable = new AxiomListVariable(axiomList, indexExpression, "0");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
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
	public void test_expression_index_out_of_bounds()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(false);
		Operand indexExpression = new TestIntegerOperand("x", Integer.valueOf(5));
		AxiomListVariable variable = new AxiomListVariable(axiomList, indexExpression, "0");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		try
		{
			variable.evaluate(1);
			failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch(ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("\"" + NAME + "\" index 5 out of bounds");
		}
	}

	@Test
	public void test_assign()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(false, true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "0");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		Parameter param = new Parameter(Term.ANONYMOUS, Integer.valueOf(13));
		variable.assign(param);
		
	    verify(axiomTermListVariable).setValue(Integer.valueOf(13));
		assertThat(variable.getValue()).isEqualTo(13);
	}
	
	@Test
	public void test_assign_update()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "0");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(13));
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		variable.setTermIndex(0, 1);
		assertThat(variable.getValue()).isEqualTo(0);
		Parameter param = new Parameter(Term.ANONYMOUS, Integer.valueOf(13));
		variable.assign(param);
		
	    verify(axiomTermListVariable).setValue(Integer.valueOf(13));
		assertThat(variable.getValue()).isEqualTo(13);
	}

    @Test
	public void test_get_value_prior_to_evaluation_fixed_axiom_index()
	{
 		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(false);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "item");
		AxiomTermListVariable axiomTermListVariable = mock(AxiomTermListVariable.class);
		when(axiomTermListVariable.getValue()).thenReturn(new Null());
		when(axiomTermList.newVariableInstance(0, "0", 1)).thenReturn(axiomTermListVariable);
		when(axiomTermListVariable.backup(1)).thenReturn(true);
		IntegerOperand a = new TestIntegerOperand("a", Integer.valueOf(0));
		Operand termExpression = new TestEvaluator("b ", a, "++", null);
		when(axiomTermList.newVariableInstance(isA(Operand.class), isA(String.class), eq(1))).thenReturn(axiomTermListVariable);
		variable.setTermExpression(termExpression, 1);
		assertThat(variable.isEmpty()).isTrue();
		assertThat(variable.toString()).isEqualTo(NAME + "_5_item = <empty>");
		assertThat(variable.getValue()).isInstanceOf(Null.class);
		assertThat(variable.getLeftOperand()).isEqualTo(axiomTermListVariable);
		assertThat(variable.getRightOperand()).isNull();
 	}
    
    @Test
	public void test_get_value_prior_to_evaluation_expression_x2()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.getName()).thenReturn(NAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		IntegerOperand x = new TestIntegerOperand("x", Integer.valueOf(5));
		Operand axiomExpression = new TestEvaluator("y ", x, "++", null);
		AxiomListVariable variable = new AxiomListVariable(axiomList, axiomExpression, "item");
		IntegerOperand a = new TestIntegerOperand("a", Integer.valueOf(0));
		Operand termExpression = new TestEvaluator("b ", a, "++", null);
		variable.setTermExpression(termExpression, 0);
		assertThat(variable.isEmpty()).isTrue();
		assertThat(variable.toString()).isEqualTo(NAME + "_index_item = <empty>");
		assertThat(variable.getValue()).isInstanceOf(Null.class);
		assertThat(variable.getLeftOperand()).isNull();
		assertThat(variable.getRightOperand()).isNull();
 	}

	@Test
	public void test_integer_operand_ops()
	{
		AxiomList axiomList = mock(AxiomList.class);
		AxiomTermList axiomTermList = new AxiomTermList(QNAME, "AxiomKey");
		axiomTermList.setAxiom(new Axiom("AxiomKey", new IntegerTerm(13)));
		when(axiomList.getName()).thenReturn(NAME);
        when(axiomList.getQualifiedName()).thenReturn(QNAME);
		when(axiomList.hasItem(5)).thenReturn(true);
		when(axiomList.getItem(5)).thenReturn(axiomTermList);
		AxiomListVariable variable = new AxiomListVariable(axiomList, 5, "0");
		variable.setTermIndex(0, 1);
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestIntegerOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestIntegerOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestIntegerOperand("L", Long.valueOf(2)), OperatorEnum.GE, new TestIntegerOperand("R", Long.valueOf(2)))).isTrue();
		assertThat(variable.numberEvaluation(new TestIntegerOperand("L", Long.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Long.valueOf(5)))).isEqualTo(new Long(2));
		assertThat(variable.numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Long.valueOf(8)))).isEqualTo(new Long(9));
	}
	
    @Test 
    public void test_cities() throws Exception
    {
    	AxiomList axiomList = new AxiomList(QualifiedName.parseName("city_height_list"), "city");
    	final List<Axiom> cityList = new ArrayList<Axiom>();
		cityList.add(new Axiom("city", "bilene", 1718));
		cityList.add(new Axiom("city", "addis ababa", 8000));
		cityList.add(new Axiom("city", "denver", 5280));
		cityList.add(new Axiom("city", "flagstaff", 6970));
		cityList.add(new Axiom("city", "jacksonville", 8));
		cityList.add(new Axiom("city", "leadville", 10200));
		cityList.add(new Axiom("city", "madrid", 1305));
		cityList.add(new Axiom("city", "richmond", 19));
		cityList.add(new Axiom("city", "spokane", 1909));
		cityList.add(new Axiom("city", "wichita", 1305)); 
		int index = 0;
		for (Axiom axiom: cityList)
		{
			AxiomTermList axiomListOperand = new AxiomTermList(QNAME, axiom.getName());
			axiomListOperand.setAxiom(axiom);
			axiomList.assignItem(index, axiomListOperand);
			++index;
		}
		IntegerOperand x = new TestIntegerOperand("x", Integer.valueOf(0));
		Operand axiomExpression = new TestEvaluator("y ", x, "++", null);
		ItemListVariable<AxiomTermList> variable = axiomList.newVariableInstance(axiomExpression, "y", 1);
    	index = 0;
    	while (index < cityList.size())
    	{
    		variable.evaluate(1);
	    	assertThat(variable.getValue().toString()).isEqualTo(CITY_NAME_HEIGHT[index++]);
	    	variable.backup(1);
    	}
    	x.setValue(Long.valueOf(0));
		AxiomListVariable term0 = axiomList.newVariableInstance(x, 0, "0");
		AxiomListVariable term1 = axiomList.newVariableInstance(axiomExpression, 1, "1");
    	index = 0;
    	while (index < cityList.size())
    	{
    		term0.evaluate(1);
    		term1.evaluate(1);
	    	assertThat(term0.getValue()).isEqualTo(cityList.get(index).getTermByIndex(0).getValue());
	    	assertThat(term1.getValue()).isEqualTo(cityList.get(index++).getTermByIndex(1).getValue());
	    	term0.backup(1);
	    	term1.backup(1);
    	}
    }
 

}
