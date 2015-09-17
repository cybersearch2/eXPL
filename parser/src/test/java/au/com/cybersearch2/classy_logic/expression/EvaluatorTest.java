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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.EvaluationUtils;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.terms.StringTerm;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * OperatorTest
 * @author Andrew Bowley
 * 2 Dec 2014
 */
public class EvaluatorTest 
{
	
	private static final String EVAL_NAME = "myEvaluator";
	private static final String EVALUATION_FAIL = "Cannot evaluate ";
	static public String NAME = "Operand";
	
	static class UnaryTestData
	{
		public UnaryTestData(long testValue, String operator, String result)
		{
			this.operand = new Long(testValue);
			this.operator = operator;
			this.result = result;
		}
		
		public UnaryTestData(long testValue, String operator, String result, boolean assign)
		{
			this.operand = new Long(testValue);
			this.operator = operator;
			this.result = result;
			this.assign = assign;
		}
		
		public UnaryTestData(double testValue, String operator, String result)
		{
			this.operand = new Double(testValue);
			this.operator = operator;
			this.result = result;
		}
		
		public UnaryTestData(String testValue, String operator, String result)
		{
			this.operand = new BigDecimal(testValue);
			this.operator = operator;
			this.result = result;
		}

		public UnaryTestData(boolean testValue, String operator, String result)
		{
			this.operand = new Boolean(testValue);
			this.operator = operator;
			this.result = result;
		}
		
        public UnaryTestData(String operator, String result) 
        {
			this.operator = operator;
			this.result = result;
		}

		public UnaryTestData(Null null1, String operator, String result) 
		{
			this.operand = null1;
			this.operator = operator;
			this.result = result;
		}

		Object operand;
		String operator;
		String result;
		boolean assign;
	}
	
	static class BinaryTestData extends UnaryTestData
	{

		public BinaryTestData(boolean testValue, Object operand2, String operator, String result) 
		{
			super(testValue, operator, result);
			this.operand2 = operand2;
		}

		public BinaryTestData(long testValue, Object operand2, String operator, String result,
				boolean assign) 
		{
			super(testValue, operator, result, assign);
			this.operand2 = operand2;
		}

		public BinaryTestData(long testValue, Object operand2, String operator, String result) 
		{
			super(testValue, operator, result);
			this.operand2 = operand2;
		}

		public BinaryTestData(double testValue, Object operand2, String operator, String result) 
		{
			super(testValue, operator, result);
			this.operand2 = operand2;
		}

		public BinaryTestData(String testValue, Object operand2, String operator, String result) 
		{
			super(testValue, operator, result);
			this.operand2 = operand2;
		}

		public BinaryTestData(Object operand2, String operator, String result) 
		{
			super(operator, result);
			this.operand2 = operand2;
		}

		public BinaryTestData(Null testValue, Object operand2, String operator, String result) 
		{
			super(testValue, operator, result);
			this.operand2 = operand2;
		}

	    Object operand2;
	}
	
	@Test
	public void test_postfix_unary_constructor()
	{
		Operand leftTerm = mock(Operand.class);
		Evaluator unaryPostfix = new TestEvaluator(leftTerm, "++");
		assertThat(unaryPostfix.left).isEqualTo(leftTerm);
		assertThat(unaryPostfix.right).isNull();
		assertThat(unaryPostfix.getName()).isEmpty();
		assertThat(unaryPostfix.shortCircuitOnFalse).isFalse();
		assertThat(unaryPostfix.shortCircuitOnTrue).isFalse();
	}
	
	@Test
	public void test_named_postfix_unary_constructor()
	{
		Operand leftTerm = mock(Operand.class);
		Evaluator unaryPostfix = new TestEvaluator(EVAL_NAME, leftTerm, "++");
		assertThat(unaryPostfix.left).isEqualTo(leftTerm);
		assertThat(unaryPostfix.right).isNull();
		assertThat(unaryPostfix.getName()).isEqualTo(EVAL_NAME);
		assertThat(unaryPostfix.shortCircuitOnFalse).isFalse();
		assertThat(unaryPostfix.shortCircuitOnTrue).isFalse();
	}
	
	@Test
	public void test_prefix_unary_constructor()
	{
		Operand rightTerm = mock(Operand.class);
		Evaluator unaryPrefix = new TestEvaluator("++", rightTerm);
		assertThat(unaryPrefix.right).isEqualTo(rightTerm);
		assertThat(unaryPrefix.left).isNull();
		assertThat(unaryPrefix.getName()).isEmpty();
		assertThat(unaryPrefix.shortCircuitOnFalse).isFalse();
		assertThat(unaryPrefix.shortCircuitOnTrue).isFalse();
	}
	
	@Test
	public void test_named_prefix_unary_constructor()
	{
		Operand rightTerm = mock(Operand.class);
		Evaluator unaryPrefix = new TestEvaluator(EVAL_NAME, "++", rightTerm);
		assertThat(unaryPrefix.right).isEqualTo(rightTerm);
		assertThat(unaryPrefix.left).isNull();
		assertThat(unaryPrefix.getName()).isEqualTo(EVAL_NAME);
		assertThat(unaryPrefix.shortCircuitOnFalse).isFalse();
		assertThat(unaryPrefix.shortCircuitOnTrue).isFalse();
	}
	
	@Test
	public void test_binary_constructor()
	{
		Operand leftTerm = mock(Operand.class);
		Operand rightTerm = mock(Operand.class);
		Evaluator binary = new TestEvaluator(leftTerm, "+", rightTerm);
		assertThat(binary.right).isEqualTo(rightTerm);
		assertThat(binary.left).isEqualTo(leftTerm);
		assertThat(binary.getName()).isEmpty();
		assertThat(binary.shortCircuitOnFalse).isFalse();
		assertThat(binary.shortCircuitOnTrue).isFalse();
	}
	
	@Test
	public void test_named_binary_constructor()
	{
		Operand leftTerm = mock(Operand.class);
		Operand rightTerm = mock(Operand.class);
		Evaluator binaryPrefix = new TestEvaluator(EVAL_NAME, leftTerm, "+", rightTerm);
		assertThat(binaryPrefix.right).isEqualTo(rightTerm);
		assertThat(binaryPrefix.left).isEqualTo(leftTerm);;
		assertThat(binaryPrefix.getName()).isEqualTo(EVAL_NAME);
		assertThat(binaryPrefix.shortCircuitOnFalse).isFalse();
		assertThat(binaryPrefix.shortCircuitOnTrue).isFalse();
		binaryPrefix = new TestEvaluator(EVAL_NAME, leftTerm, "||", rightTerm);
		assertThat(binaryPrefix.shortCircuitOnTrue).isTrue();
		binaryPrefix = new TestEvaluator(EVAL_NAME, leftTerm, "&&", rightTerm);
		assertThat(binaryPrefix.shortCircuitOnFalse).isTrue();
	}

	@SuppressWarnings("unchecked")
    @Test
	public void testOperandAssign()
	{
		Operand leftTerm = new Variable(QualifiedName.parseGlobalName("left"));
		Operand rightTerm = mock(Operand.class);
		Long value = Long.valueOf(76);
		when(rightTerm.getValue()).thenReturn(value);
		when((Class<Long>)(rightTerm.getValueClass())).thenReturn(Long.class);
		//Evaluator evaluator = new TestEvaluator(leftTerm, "=", rightTerm);
		assertThat(EvaluationUtils.assignRightToLeft(leftTerm, rightTerm, 1)).isEqualTo(value);
        assertThat(leftTerm.getValue()).isEqualTo(76L);
		leftTerm = new Variable(QualifiedName.parseGlobalName("left"));
		rightTerm = mock(Operand.class);
		Float floatValue = Float.valueOf(63.0f);
		when(rightTerm.getValue()).thenReturn(floatValue);
        when((Class<Float>)(rightTerm.getValueClass())).thenReturn(Float.class);
		//Evaluator evaluator = new TestEvaluator(leftTerm, "=", rightTerm);
		assertThat(EvaluationUtils.assignRightToLeft(leftTerm, rightTerm, 1)).isInstanceOf(Null.class);
        assertThat(leftTerm.getValue()).isEqualTo(63.0f);
	}

	@Test
	public void test_to_string()
	{
		boolean leftIsEmpty = true;
		String leftName = "LeftName";
		Object leftValue = Long.valueOf(237);
		String leftToString = "x+y";
		boolean rightIsEmpty = true;
		String rightName = "RightName";
		Object rightValue = Long.valueOf(732);
		String rightToString = "a+b";
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "LeftName!=RightName");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=RightName");
		leftIsEmpty = true;
		rightIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "LeftName!=732");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=732");
		leftName = "";
		leftIsEmpty = true;
		rightIsEmpty = true;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "x+y!=RightName");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=RightName");
		leftIsEmpty = true;
		rightIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "x+y!=732");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=732");
		leftName = "LeftName";
		leftIsEmpty = true;
		rightIsEmpty = true;
		rightName = "";
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "LeftName!=a+b");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=a+b");
		leftIsEmpty = true;
		rightIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "LeftName!=732");
		leftIsEmpty = false;
		testToString(leftIsEmpty, leftName, leftValue, leftToString, rightIsEmpty, rightName, rightValue, rightToString, "237!=732");
		leftIsEmpty = true;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "++", "LeftName++");
		leftIsEmpty = false;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "++", "237++");
		leftName = "";
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "++", "237++");
		leftIsEmpty = true;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "++", "evaluator = <empty>"); 
		leftIsEmpty = true;
		leftName = "LeftName";
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "&&", "evaluator?x+y");
		leftIsEmpty = false;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "&&", "evaluator?x+y");
		leftName = "";
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "&&", "evaluator?x+y");
		leftIsEmpty = true;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "&&", "evaluator?x+y"); 
		leftIsEmpty = true;
		leftName = "LeftName";
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "||", "evaluator:x+y");
		leftIsEmpty = false;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "||", "evaluator:x+y");
		leftName = "";
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "||", "evaluator:x+y");
		leftIsEmpty = true;
		testLeftToString(leftIsEmpty, leftName, leftValue, leftToString, "||", "evaluator:x+y"); 

		rightIsEmpty = true;
		rightName = "RightName";
		testRightToString(rightIsEmpty, rightName, rightValue, rightToString, "++", "++RightName");
		rightIsEmpty = false;
		testRightToString(rightIsEmpty, rightName, rightValue, rightToString, "++", "++732");
		rightName = "";
		testRightToString(rightIsEmpty, rightName, rightValue, rightToString, "++", "++732");
		rightIsEmpty = true;
		testRightToString(rightIsEmpty, rightName, rightValue, rightToString, "++", "++a+b");
}

	protected void testToString(
			boolean leftIsEmpty,
			String leftName,
			Object leftValue,
			String leftToString,
			boolean rightIsEmpty,
			String rightName,
			Object rightValue,
			String rightToString,
			String expectedText)
	{
		Operand leftTerm = mock(Operand.class);
		Operand rightTerm = mock(Operand.class);
		when(leftTerm.isEmpty()).thenReturn(leftIsEmpty);
		when(rightTerm.isEmpty()).thenReturn(rightIsEmpty);
		when(leftTerm.getName()).thenReturn(leftName);
		when(rightTerm.getName()).thenReturn(rightName);
		when(leftTerm.getValue()).thenReturn(leftValue);
		when(rightTerm.getValue()).thenReturn(rightValue);
		when(leftTerm.toString()).thenReturn(leftToString);
		when(rightTerm.toString()).thenReturn(rightToString);
		Evaluator evaluator = new TestEvaluator("evaluator", leftTerm, "!=", rightTerm);
		assertThat(evaluator.toString()).isEqualTo(expectedText);
	}
	
	protected void testLeftToString(
			boolean leftIsEmpty,
			String leftName,
			Object leftValue,
			String leftToString,
			String operator,
			String expectedText)
	{
		Operand leftTerm = mock(Operand.class);
		when(leftTerm.isEmpty()).thenReturn(leftIsEmpty);
		when(leftTerm.getName()).thenReturn(leftName);
		when(leftTerm.getValue()).thenReturn(leftValue);
		when(leftTerm.toString()).thenReturn(leftToString);
		Evaluator evaluator = new TestEvaluator("evaluator", leftTerm, operator, null);
		assertThat(evaluator.toString()).isEqualTo(expectedText);
	}
	
	protected void testRightToString(
			boolean rightIsEmpty,
			String rightName,
			Object rightValue,
			String rightToString,
			String operator,
			String expectedText)
	{
		Operand rightTerm = mock(Operand.class);
		when(rightTerm.isEmpty()).thenReturn(rightIsEmpty);
		when(rightTerm.getName()).thenReturn(rightName);
		when(rightTerm.getValue()).thenReturn(rightValue);
		when(rightTerm.toString()).thenReturn(rightToString);
		Evaluator evaluator = new TestEvaluator("evaluator", null, operator, rightTerm);
		assertThat(evaluator.toString()).isEqualTo(expectedText);
	}
	
	@Test
	public void testUnaryPrefixEvaluation()
	{
		UnaryTestData[] testData = new UnaryTestData[]
		{
			new UnaryTestData(2, "~", NAME + " = -3"),
			new UnaryTestData(7, "-", NAME + " = -7"),
			new UnaryTestData(-99, "+", NAME + " = -99"),
			new UnaryTestData(Integer.MAX_VALUE, "~", NAME + " = " + (~Integer.MAX_VALUE )),
			new UnaryTestData(Integer.MAX_VALUE, "-", NAME + " = -" + Integer.MAX_VALUE),
			new UnaryTestData(Integer.MAX_VALUE, "+", NAME + " = " + Integer.MAX_VALUE),
			new UnaryTestData("9876.12", "-", NAME + " = -9876.12"),
			new UnaryTestData("-99999", "+", NAME + " = -99999"),
			new UnaryTestData(true, "!", NAME + " = false"),
			new UnaryTestData(false, "!", NAME + " = true"),
			new UnaryTestData(11, "++", NAME + " = 12", true),
			new UnaryTestData(11, "--", NAME + " = 10", true),
		};
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).longValue());
			else if (datum.operand instanceof BigDecimal)
				operand = new TestBigDecimalOperand(NAME, ((BigDecimal)datum.operand));
			else if (datum.operand instanceof Boolean)
				operand = new TestBooleanOperand(NAME, ((Boolean)datum.operand));
			Evaluator evaluator = doUnaryPrefixEvaluationTest(datum.operator, operand);
			assertThat(evaluator.toString()).isEqualTo(datum.result);
			if (datum.assign)
				assertThat(operand.toString()).isEqualTo(NAME + " = " + evaluator.getValue());
			else
			    assertThat(operand.toString()).isEqualTo(NAME + " = " + datum.operand.toString());
		}
	}

	@Test
	public void testUnaryPostfixEvaluation()
	{
		UnaryTestData[] testData = new UnaryTestData[]
		{
			new UnaryTestData(11, "++", NAME + " = 11"),
			new UnaryTestData(11, "--", NAME + " = 11"),
		};
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			Evaluator evaluator = doUnaryPostfixEvaluationTest(datum.operator, operand);
			assertThat(evaluator.toString()).isEqualTo(datum.result);
			int adjust = -1;
			if (datum.operator.equals("++"))
				adjust = 1;
			Object evalValue = evaluator.getValue();
			assertThat(operand.toString()).isEqualTo(NAME + " = " + (((Long)evalValue).intValue() + adjust));
		}
		Evaluator evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isTrue", Boolean.TRUE), "&&");
		StringTerm testTerm = new StringTerm("Hello world");
		evaluator.unifyTerm(testTerm, 1);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(testTerm.getValue());
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isFalse", Boolean.FALSE), "&&");
		evaluator.unifyTerm(testTerm, 1);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.SHORT_CIRCUIT);
		assertThat(evaluator.getValue()).isEqualTo(testTerm.getValue());
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isFalse", Boolean.FALSE), "||");
		evaluator.unifyTerm(testTerm, 1);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(testTerm.getValue());
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isTrue", Boolean.TRUE), "||");
		evaluator.unifyTerm(testTerm, 1);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.SHORT_CIRCUIT);
		assertThat(evaluator.getValue()).isEqualTo(testTerm.getValue());
	}

	@Test
	public void testBinaryEvaluation()
	{
		BinaryTestData[] testData = new BinaryTestData[]
		{
			new BinaryTestData(new Null(), new TestIntegerOperand("weight", 5), "==", NAME + " = false"),
			new BinaryTestData(2, null, "=", NAME + " = 2", true),
			new BinaryTestData(101, new TestIntegerOperand("height", 102), "<", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 100), ">", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 101), "==", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 102), "<=", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 101), "<=", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 100), ">=", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 101), ">=", NAME + " = true"),
			new BinaryTestData(101, new TestIntegerOperand("height", 101), "!=", NAME + " = false"),
			new BinaryTestData(101, new TestIntegerOperand("height", 102), "!=", NAME + " = true"),
			new BinaryTestData(24, new TestIntegerOperand("weight", 7), "+", NAME + " = 31"),
			new BinaryTestData(76, new TestIntegerOperand("weight", 32), "-", NAME + " = 44"),
			new BinaryTestData(5, new TestIntegerOperand("weight", 5), "*", NAME + " = 25"),
			new BinaryTestData(12, new TestIntegerOperand("weight", 4), "/", NAME + " = 3"),
			new BinaryTestData(7, new TestIntegerOperand("mask", 15), "&", NAME + " = 7"),
			new BinaryTestData(1, new TestIntegerOperand("mask", 4), "|", NAME + " = 5"),
			new BinaryTestData(15, new TestIntegerOperand("mask", 7), "^", NAME + " = 8"),
			new BinaryTestData(82, new TestIntegerOperand("remainder", 9), "%", NAME + " = 1"),
			new BinaryTestData(24, new TestIntegerOperand("weight", 7), "+=", NAME + " = 31", true),
			new BinaryTestData(76, new TestIntegerOperand("weight", 32), "-=", NAME + " = 44", true),
			new BinaryTestData(5, new TestIntegerOperand("weight", 5), "*=", NAME + " = 25", true),
			new BinaryTestData(12, new TestIntegerOperand("weight", 4), "/=", NAME + " = 3", true),
			new BinaryTestData(7, new TestIntegerOperand("mask", 15), "&=", NAME + " = 7", true),
			new BinaryTestData(1, new TestIntegerOperand("mask", 4), "|=", NAME + " = 5", true),
			new BinaryTestData(15, new TestIntegerOperand("mask", 7), "^=", NAME + " = 8", true),
			new BinaryTestData(82, new TestIntegerOperand("remainder", 9), "%=", NAME + " = 1", true)
		};
		for (BinaryTestData datum: testData)
		{
			if (datum.operand2 == null)
			{
				System.out.println("?" + datum.operand + datum.operator);
				Operand operand = new TestIntegerOperand("x");
				Operand operand2 = null;
				if (datum.operand instanceof Long)
					operand2 = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
				else if (datum.operand instanceof BigDecimal)
					operand2 = new TestBigDecimalOperand(NAME, ((BigDecimal)datum.operand));
				else if (datum.operand instanceof Boolean)
					operand2 = new TestBooleanOperand(NAME, ((Boolean)datum.operand));
				Evaluator evaluator = doBinaryEvaluationTest(operand, datum.operator, operand2);
				assertThat(evaluator.toString()).isEqualTo(datum.result);
				if (datum.assign)
					assertThat(operand.toString()).isEqualTo("x = " + evaluator.getValue());
				else
				    assertThat(operand.toString()).isEqualTo(NAME + " = " + datum.operand.toString());
			}
			else
			{
				System.out.println(datum.operand + datum.operator + datum.operand2);
				Operand operand = null;
				Operand operand2 = (Operand) datum.operand2;
				if (datum.operand instanceof Long)
					operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
				else if (datum.operand instanceof BigDecimal)
					operand = new TestBigDecimalOperand(NAME, ((BigDecimal)datum.operand));
				else if (datum.operand instanceof Boolean)
					operand = new TestBooleanOperand(NAME, ((Boolean)datum.operand));
				else if (datum.operand instanceof Null)
					operand = new TestNullOperand("L");
				Evaluator evaluator = doBinaryEvaluationTest(operand, datum.operator, operand2);
				assertThat(evaluator.toString()).isEqualTo(datum.result);
				if (datum.operand instanceof Null)
					assertThat(operand.toString()).isEqualTo("L = null");
				else if (datum.assign )
					assertThat(operand.toString()).isEqualTo(NAME + " = " + evaluator.getValue());
				else
				    assertThat(operand.toString()).isEqualTo(NAME + " = " + datum.operand.toString());
			}
		}
		System.out.println("x==null");
		Evaluator evaluator = new TestEvaluator(NAME, new TestIntegerOperand("x", Long.MAX_VALUE), "==", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = false");
		System.out.println("x!=null");
		evaluator = new TestEvaluator(NAME, new TestIntegerOperand("x", Long.MAX_VALUE), "!=", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = true");
		System.out.println("null!=null");
		evaluator = new TestEvaluator(NAME, new TestNullOperand("x"), "!=", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = false");
		System.out.println("null==null");
		evaluator = new TestEvaluator(NAME, new TestNullOperand("x"), "==", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = true");
		System.out.println("NaN==null");
		evaluator = new TestEvaluator(NAME, new TestDoubleOperand("x", new Double(Double.NaN)), "==", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = false");
		System.out.println("NaN!=null");
		evaluator = new TestEvaluator(NAME, new TestDoubleOperand("x", new Double(Double.NaN)), "!=", new TestNullOperand("y"));
		evaluator.evaluate(1);
		assertThat(evaluator.toString()).isEqualTo(NAME + " = true");
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isTrue", Boolean.TRUE), "&&", new TestBooleanOperand("isTrue", Boolean.TRUE));
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(Boolean.TRUE);
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isFalse", Boolean.FALSE), "&&", new TestBooleanOperand("isTrue", Boolean.TRUE));
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(Boolean.FALSE);
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isTrue", Boolean.TRUE), "||", new TestBooleanOperand("isTrue", Boolean.TRUE));
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(Boolean.TRUE);
		evaluator = new TestEvaluator(NAME, new TestBooleanOperand("isFalse", Boolean.FALSE), "||", new TestBooleanOperand("isFalse", Boolean.FALSE));
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(evaluator.getValue()).isEqualTo(Boolean.FALSE);
	}

	@Test
	public void test_isNaN()
	{
		//Evaluator evaluator = new TestEvaluator(NAME, "++", new TestIntegerOperand("R"));
		assertThat(EvaluationUtils.isNaN((Object)null)).isTrue();
		assertThat(EvaluationUtils.isNaN(new Null())).isTrue();
		assertThat(EvaluationUtils.isNaN(Double.valueOf(Double.NaN))).isTrue();
		assertThat(EvaluationUtils.isNaN(Long.MAX_VALUE)).isFalse();
	}
	
	@Test 
	public void test_unify()
	{
		Parameter otherTerm = new Parameter("O", Long.MAX_VALUE);
		Operand leftOperand = mock(Operand.class);
		when(leftOperand.unifyTerm(otherTerm, 1)).thenReturn(1);
		when(leftOperand.getValue()).thenReturn(Long.MAX_VALUE);
		when(leftOperand.getLeftOperandOps()).thenReturn(new OperatorEnum[] { OperatorEnum.INCR, OperatorEnum.PLUS } );
		Operand rightOperand = mock(Operand.class);
		when(rightOperand.unifyTerm(otherTerm, 1)).thenReturn(1);
		when(rightOperand.getValue()).thenReturn(Long.MAX_VALUE);
		when(rightOperand.getRightOperandOps()).thenReturn(new OperatorEnum[] { OperatorEnum.INCR, OperatorEnum.PLUS  });
		Evaluator evaluator = new TestEvaluator(NAME, "++", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		assertThat(evaluator.getValue()).isEqualTo(Long.MAX_VALUE);
		assertThat(evaluator.isEmpty()).isFalse();
		evaluator = new TestEvaluator(NAME, leftOperand, "++");
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		assertThat(evaluator.getValue()).isEqualTo(Long.MAX_VALUE);
		assertThat(evaluator.isEmpty()).isFalse();
		evaluator = new TestEvaluator(NAME, leftOperand, "+", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		assertThat(evaluator.getValue()).isEqualTo(Long.MAX_VALUE);
		assertThat(evaluator.isEmpty()).isFalse();
	}
	
	@Test 
	public void test_backup()
	{
		Parameter otherTerm = new Parameter("O", Long.MAX_VALUE);
		Operand leftOperand = mock(Operand.class);
		when(leftOperand.unifyTerm(otherTerm, 1)).thenReturn(1);
		when(leftOperand.getValue()).thenReturn(Long.MAX_VALUE);
		when(leftOperand.getLeftOperandOps()).thenReturn(new OperatorEnum[] { OperatorEnum.INCR, OperatorEnum.PLUS } );
		when(leftOperand.backup(anyInt())).thenReturn(true);
		Operand rightOperand = mock(Operand.class);
		when(rightOperand.unifyTerm(otherTerm, 1)).thenReturn(1);
		when(rightOperand.getValue()).thenReturn(Long.MAX_VALUE);
		when(rightOperand.getRightOperandOps()).thenReturn(new OperatorEnum[] { OperatorEnum.INCR, OperatorEnum.PLUS  });
		when(rightOperand.backup(anyInt())).thenReturn(true);
		Evaluator evaluator = new TestEvaluator(NAME, "++", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		Parameter MAX_LONG = new Parameter(Term.ANONYMOUS, Long.MAX_VALUE);
		MAX_LONG.setId(1);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(0)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
		evaluator = new TestEvaluator(NAME, "++", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		when(rightOperand.backup(2)).thenReturn(false);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(2)).isFalse();
		when(rightOperand.backup(anyInt())).thenReturn(true);
		assertThat(evaluator.backup(1)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
		evaluator = new TestEvaluator(NAME, leftOperand, "++");
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(0)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
		evaluator = new TestEvaluator(NAME, leftOperand, "++");
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		when(leftOperand.backup(2)).thenReturn(false);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(2)).isFalse();
		when(leftOperand.backup(anyInt())).thenReturn(true);
		assertThat(evaluator.backup(1)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
		evaluator = new TestEvaluator(NAME, leftOperand, "+", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(0)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
		evaluator = new TestEvaluator(NAME, leftOperand, "+", rightOperand);
		assertThat(evaluator.unify(otherTerm, 1)).isEqualTo(1);
		when(leftOperand.backup(2)).thenReturn(false);
		when(rightOperand.backup(2)).thenReturn(false);
		evaluator.assign(MAX_LONG);
		assertThat(evaluator.backup(2)).isFalse();
		when(leftOperand.backup(1)).thenReturn(true);
		when(rightOperand.backup(1)).thenReturn(true);
		assertThat(evaluator.backup(1)).isTrue();
		assertThat(evaluator.getValue()).isInstanceOf(Null.class);
		assertThat(evaluator.isEmpty()).isTrue();
	}

	@Test
	public void test_invalid_UnaryPrefixEvaluation()
	{
		UnaryTestData[] testData = new UnaryTestData[]
		{
			new UnaryTestData(2, "==", EVALUATION_FAIL + "==2"),
			new UnaryTestData(2, "||", EVALUATION_FAIL + "||2"),
			new UnaryTestData("-", "Right term is empty"),
			new UnaryTestData(new Null(), "-", EVALUATION_FAIL + "-null")
		};
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Double)
				operand = new TestDoubleOperand(NAME, ((Double)datum.operand).doubleValue());
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			try
			{
			    doUnaryPrefixEvaluationTest(datum.operator, operand);
			    failBecauseExceptionWasNotThrown(ExpressionException.class);
			}
			catch (ExpressionException e)
			{
				assertThat(e.getMessage()).isEqualTo(datum.result);
			}
		}
	}
	
	@Test
	public void test_invalid_UnaryPostfixEvaluation()
	{
		UnaryTestData[] testData = new UnaryTestData[]
		{
			new UnaryTestData(2, "==", EVALUATION_FAIL + "2=="),
			new UnaryTestData(2, "%", EVALUATION_FAIL + "2%"),
			new UnaryTestData("++", "Left term is empty"),
			new UnaryTestData(new Null(), "++", EVALUATION_FAIL + "null++")
		};
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			try
			{
			    doUnaryPostfixEvaluationTest(datum.operator, operand);
			    failBecauseExceptionWasNotThrown(ExpressionException.class);
			}
			catch (ExpressionException e)
			{
				assertThat(e.getMessage()).isEqualTo(datum.result);
			}
		}
	}
	
	@Test
	public void test_invalid_BinaryPostfixEvaluation()
	{
		BinaryTestData[] testData = new BinaryTestData[]
		{                      
			new BinaryTestData("15", new TestIntegerOperand("mask", 7), "^", EVALUATION_FAIL + "15^7"),
			new BinaryTestData(15, new TestBigDecimalOperand("mask", new BigDecimal("7")), "^", EVALUATION_FAIL + "15^7"),
			new BinaryTestData(2, new TestIntegerOperand("height", 102), "||", EVALUATION_FAIL + "2||102"),
			new BinaryTestData(new TestIntegerOperand("height", 102), "+", "Left term is empty"),
			new BinaryTestData(2, new TestIntegerOperand("height"), "+", "Right term is empty"),
			new BinaryTestData(new Null(),new TestIntegerOperand("height", 102), "+", EVALUATION_FAIL + "null+102"),
			new BinaryTestData(102, new TestNullOperand("height"), "+", EVALUATION_FAIL + "102+null"),
			new BinaryTestData(new Double("NaN"), new TestIntegerOperand("height", 102), "||", EVALUATION_FAIL + "NaN||102"),
			new BinaryTestData(new TestDoubleOperand("height", new Double("NaN")), "+", "Left term is empty"),
			new BinaryTestData(new Double("NaN"), new TestIntegerOperand("height"), "+", "Right term is empty")
		};
		for (BinaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			Operand operand2 = (Operand) datum.operand2;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Double)
				operand = new TestDoubleOperand(NAME, ((Double)datum.operand).doubleValue());
			else if (datum.operand instanceof BigDecimal)
				operand = new TestBigDecimalOperand(NAME, ((BigDecimal)datum.operand));
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			try
			{
				doBinaryEvaluationTest(operand, datum.operator, operand2);
			    failBecauseExceptionWasNotThrown(ExpressionException.class);
			}
			catch (ExpressionException e)
			{
				assertThat(e.getMessage()).isEqualTo(datum.result);
			}
		}
	}

	@Test
	public void test_NaN_Evaluation()
	{
		UnaryTestData[] testData = new UnaryTestData[]
		{
			new UnaryTestData(new Double("NaN"), "-", NAME + " = NaN"),
			new UnaryTestData(new Double("NaN"), "+", NAME + " = NaN"),
			new UnaryTestData(new Double("NaN"), "~", NAME + " = NaN"),
			new UnaryTestData(new Double("NaN"), "++", NAME + " = NaN"),
			new UnaryTestData(new Double("NaN"), "--", NAME + " = NaN")
		};
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Double)
				operand = new TestDoubleOperand(NAME, ((Double)datum.operand).doubleValue());
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			Evaluator evaluator = doUnaryPrefixEvaluationTest(datum.operator, operand);
			assertThat(evaluator.toString()).isEqualTo(datum.result);
		}
		for (UnaryTestData datum: testData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Double)
				operand = new TestDoubleOperand(NAME, ((Double)datum.operand).doubleValue());
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			Evaluator evaluator = doUnaryPostfixEvaluationTest(datum.operator, operand);
			assertThat(evaluator.toString()).isEqualTo(datum.result);
		}
		BinaryTestData[] binaryTestData = new BinaryTestData[]
		{                      
			new BinaryTestData(new Double("NaN"), new TestIntegerOperand("mask", 7), "^", NAME + " = NaN"),
			new BinaryTestData(new Double("NaN"), new TestBigDecimalOperand("mask", new BigDecimal("7")), "^", NAME + " = NaN"),
			new BinaryTestData(new Double("NaN"), new TestBigDecimalOperand("mask", new BigDecimal("7")), "^", NAME + " = NaN"),
			new BinaryTestData(new Double("NaN"), new TestNullOperand("height"), "+", NAME + " = NaN"),
			new BinaryTestData(new Null(), new TestDoubleOperand("height", new Double("NaN")), "+", NAME + " = NaN")
		};
		for (BinaryTestData datum: binaryTestData)
		{
			System.out.println(datum.operator + datum.operand);
			Operand operand = null;
			Operand operand2 = (Operand) datum.operand2;
			if (datum.operand instanceof Long)
				operand = new TestIntegerOperand(NAME, ((Long)datum.operand).intValue());
			else if (datum.operand instanceof Double)
				operand = new TestDoubleOperand(NAME, ((Double)datum.operand).doubleValue());
			else if (datum.operand instanceof BigDecimal)
				operand = new TestBigDecimalOperand(NAME, ((BigDecimal)datum.operand));
			else if (datum.operand instanceof Null)
				operand = new TestNullOperand("R");
			Evaluator evaluator = doBinaryEvaluationTest(operand, datum.operator, operand2);
			assertThat(evaluator.toString()).isEqualTo(datum.result);
		}
	}
	

	protected Evaluator doUnaryPrefixEvaluationTest(String operator, Operand term)
	{
		if (term == null)
			term = new TestIntegerOperand("R");
		Evaluator evaluator = new TestEvaluator(NAME, operator, term);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		return evaluator;
	}
	
	protected Evaluator doUnaryPostfixEvaluationTest(String operator, Operand term)
	{
		if (term == null)
			term = new TestIntegerOperand("L");
		Evaluator evaluator = new TestEvaluator(NAME, term, operator);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		return evaluator;
	}

	protected Evaluator doBinaryEvaluationTest(Operand left, String operator, Operand right)
	{
		if (left == null)
			left = new TestIntegerOperand("L");
		if (right == null)
			right = new TestIntegerOperand("R");
		Evaluator evaluator = new TestEvaluator(NAME, left, operator, right	);
		assertThat(evaluator.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		return evaluator;
	}
	
}
