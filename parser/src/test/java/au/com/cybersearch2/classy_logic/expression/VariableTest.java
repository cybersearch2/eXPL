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
package au.com.cybersearch2.classy_logic.expression;

import java.math.BigDecimal;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * VariableTest
 * @author Andrew Bowley
 * 17 Dec 2014
 */
public class VariableTest 
{
	static public String NAME = "Variable";

	@Test
	public void test_setDelegate()
	{
		Variable variable = new TestVariable(NAME);
		Parameter otherTerm = new Parameter("x", Float.valueOf("1.0f"));
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(1);
		assertThat(variable.getValue()).isEqualTo(otherTerm.getValue());
		assertThat(variable.delegate).isInstanceOf(AssignOnlyOperand.class);
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", BigDecimal.ONE);
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.delegate).isInstanceOf(BigDecimalOperand.class);
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Boolean.TRUE);
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.delegate).isInstanceOf(BooleanOperand.class);
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Double.valueOf("1.0"));
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.delegate).isInstanceOf(DoubleOperand.class);
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Integer.valueOf("1"));
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.delegate).isInstanceOf(IntegerOperand.class);
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", "1.0f");
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.delegate).isInstanceOf(StringOperand.class);
	}

	@Test
	public void test_assign()
	{
		Variable variable = new TestVariable(NAME);
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, BigDecimal.ONE));
	    assertThat(variable.isEmpty()).isFalse();
		assertThat(variable.delegate).isInstanceOf(BigDecimalOperand.class);
		assertThat(variable.getValue()).isEqualTo(BigDecimal.ONE);
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Boolean.TRUE));
	    assertThat(variable.isEmpty()).isFalse();
		assertThat(variable.delegate).isInstanceOf(BooleanOperand.class);
		assertThat(variable.getValue()).isEqualTo(Boolean.TRUE);
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Double.valueOf("1.0")));
	    assertThat(variable.isEmpty()).isFalse();
		assertThat(variable.delegate).isInstanceOf(DoubleOperand.class);
		assertThat(variable.getValue()).isEqualTo(Double.valueOf("1.0"));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Integer.valueOf("1")));
	    assertThat(variable.isEmpty()).isFalse();
		assertThat(variable.delegate).isInstanceOf(IntegerOperand.class);
		assertThat(variable.getValue()).isEqualTo(Integer.valueOf("1"));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, "1.0f"));
	    assertThat(variable.isEmpty()).isFalse();
		assertThat(variable.delegate).isInstanceOf(StringOperand.class);
		assertThat(variable.getValue()).isEqualTo("1.0f");
	}

	@Test
	public void test_newInstance()
	{
		Variable variable = new TestVariable(NAME);
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, BigDecimal.ONE));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Boolean.TRUE));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Double.valueOf("1.0")));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, Integer.valueOf("1")));
	    variable = new TestVariable(NAME);
	    variable.assign(new Parameter(Term.ANONYMOUS, "1.0f"));
	}

	@Test
	public void test_delegate()
	{
		Variable variable = new TestVariable(NAME);
		OperatorEnum[] assignOp = { OperatorEnum.ASSIGN , OperatorEnum.EQ, OperatorEnum.NE}; 
		assertThat(variable.getLeftOperandOps()).isEqualTo(assignOp);
		assertThat(variable.getRightOperandOps()).isEqualTo(assignOp);
		assertThat(variable.booleanEvaluation(new NullOperand(), OperatorEnum.EQ, new NullOperand())).isTrue();
		Parameter otherTerm = new Parameter("x", (BigDecimal)null);
		assertThat(variable.unifyTerm(otherTerm, 1)).isEqualTo(1);
		variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", "1.0f");
		assertThat(variable.unifyTerm(otherTerm, 2)).isEqualTo(2);
		assertThat(variable.delegate).isInstanceOf(StringOperand.class);
		
	}
	
	@Test
	public void test_empty_methods()
	{
		Variable variable = new TestVariable(NAME);
		assertThat(variable.getLeftOperand()).isNull();
		assertThat(variable.getRightOperand()).isNull();
		assertThat(variable.numberEvaluation(new TestIntegerOperand("L", Integer.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Integer.valueOf(5)))).isEqualTo(new Integer(0));
		assertThat(variable.numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Integer.valueOf(8)))).isEqualTo(new Integer(0));
		variable.evaluate(0);
	    variable = new TestVariable(NAME);
	    Parameter otherTerm = new Parameter("x", Boolean.TRUE);
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		// Boolean allows multiplication with number. Operator is ignored along with fact no boolean terms involved!
		assertThat(variable.numberEvaluation(new TestIntegerOperand("L", Integer.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Integer.valueOf(5)))).isEqualTo(new BigDecimal("35"));
		assertThat(variable.numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Integer.valueOf(8)))).isEqualTo(new Integer(0));
	    variable = new TestVariable(NAME);
	    otherTerm = new Parameter("x", "String");
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.numberEvaluation(new TestIntegerOperand("L", Integer.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Integer.valueOf(5)))).isEqualTo(new Integer(0));
		assertThat(variable.numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Integer.valueOf(8)))).isEqualTo(new Integer(0));
	}
	
	@Test
	public void test_operand_ops()
	{
		Variable variable = new TestVariable(NAME);
		Parameter otherTerm = new Parameter("x", BigDecimal.ONE);
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestBigDecimalOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestBigDecimalOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestBigDecimalOperand("L", BigDecimal.ZERO), OperatorEnum.LT, new TestBigDecimalOperand("R", BigDecimal.TEN))).isTrue();
		assertThat(variable.numberEvaluation(new TestBigDecimalOperand("L", BigDecimal.ONE), OperatorEnum.PLUS, new TestBigDecimalOperand("R", BigDecimal.TEN))).isEqualTo(new BigDecimal("11"));
		assertThat(variable.numberEvaluation(OperatorEnum.MINUS, new TestBigDecimalOperand("R", BigDecimal.TEN))).isEqualTo(new BigDecimal("-10"));
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Boolean.TRUE);
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestBooleanOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestBooleanOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestBooleanOperand("L", Boolean.FALSE), OperatorEnum.NE, new TestBooleanOperand("R", Boolean.TRUE))).isTrue();
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Double.valueOf("1.0"));
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestDoubleOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestDoubleOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestDoubleOperand("L", Double.valueOf(1.0)), OperatorEnum.LT, new TestDoubleOperand("R", Double.valueOf(0.5)))).isFalse();
		assertThat(variable.numberEvaluation(new TestDoubleOperand("L", Double.valueOf(2.0)), OperatorEnum.STAR, new TestDoubleOperand("R", Double.valueOf(3.0)))).isEqualTo(new Double("6.0"));
		assertThat(variable.numberEvaluation(OperatorEnum.MINUS, new TestDoubleOperand("R", Double.valueOf(99.9)))).isEqualTo(new Double(-99.9));
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", Integer.valueOf("1"));
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestIntegerOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestIntegerOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestIntegerOperand("L", Long.valueOf(2)), OperatorEnum.GE, new TestIntegerOperand("R", Long.valueOf(2)))).isTrue();
		assertThat(variable.numberEvaluation(new TestIntegerOperand("L", Long.valueOf(7)), OperatorEnum.XOR, new TestIntegerOperand("R", Long.valueOf(5)))).isEqualTo(new Long(2));
		assertThat(variable.numberEvaluation(OperatorEnum.INCR, new TestIntegerOperand("R", Long.valueOf(8)))).isEqualTo(new Long(9));
	    variable = new TestVariable(NAME);
		otherTerm = new Parameter("x", "1.0f");
		variable.unifyTerm(otherTerm, 1);
		variable.setDelegate(variable.getValueClass());
		assertThat(variable.getLeftOperandOps()).isEqualTo(new TestStringOperand("*").getLeftOperandOps());
		assertThat(variable.getRightOperandOps()).isEqualTo(new TestStringOperand("*").getRightOperandOps());
		assertThat(variable.booleanEvaluation(new TestStringOperand("L", "hello"), OperatorEnum.NE, new TestStringOperand("R", "world"))).isTrue();
	}
}
