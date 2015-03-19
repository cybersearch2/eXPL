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
package au.com.cybersearch2.classy_logic.query;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.LoopEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * CalculateChainQueryTest
 * @author Andrew Bowley
 * 12 Jan 2015
 */
public class CalculateChainQueryTest 
{

    @Test
	public void test_simple() 
	{
	    Map<String,Object> props = new HashMap<String,Object>(); 
	    props.put("n", Integer.valueOf(1));
	    props.put("limit", Integer.valueOf(3));
    	IntegerOperand n = new IntegerOperand("n");
    	IntegerOperand limit = new IntegerOperand("limit");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator calcExpression = new Evaluator("n", "++", n);
	    Evaluator testExpression = new Evaluator(n, "!=", limit);
	    Evaluator shortCircuit = new Evaluator(testExpression, "&&");
	    operandList.add(calcExpression);
	    operandList.add(shortCircuit);
	    Template template = new Template("loop");
		for (Operand operand: operandList)
			template.addTerm(operand);
    	LoopEvaluator loopy = new LoopEvaluator(template);
        Template calcTemplate = new Template("calc", n, loopy, limit);
        calcTemplate.addProperties(props);
        Solution solution = new Solution();
        
        CalculateChainQuery calculateChainQuery = new CalculateChainQuery(null, calcTemplate);
        calculateChainQuery.executeQuery(solution);
        assertThat(solution.getAxiom("calc").toString()).isEqualTo("calc(n = 3, loop = true, limit = 3)");
	}

    @Test
	public void test_factorial() 
	{
	    Map<String,Object> props = new HashMap<String,Object>(); 
    	props.put("factorial", Integer.valueOf(1));
    	props.put("n", Integer.valueOf(4));
    	props.put("i", Integer.valueOf(1));
    	IntegerOperand n = new IntegerOperand("n");
    	BigDecimalOperand factorial = new BigDecimalOperand("factorial");
    	IntegerOperand i = new IntegerOperand("i");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator factorialExpression = new Evaluator("factorial", factorial, "*=", i);
       	Evaluator iExpression = new Evaluator(i, "++");
 	    Evaluator testExpression = new Evaluator(iExpression, "!=", n);
	    Evaluator shortCircuit = new Evaluator(testExpression, "&&");
	    operandList.add(factorialExpression);
	    operandList.add(shortCircuit);
	    Template template = new Template("loop");
		for (Operand operand: operandList)
			template.addTerm(operand);
    	LoopEvaluator loopy = new LoopEvaluator(template);
        Template calcTemplate = new Template("factorial", n, factorial, i, loopy);
        calcTemplate.addProperties(props);

        Solution solution = new Solution();
        CalculateChainQuery calculateChainQuery = new CalculateChainQuery(null, calcTemplate);
        calculateChainQuery.executeQuery(solution);
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(n = 4, factorial = 24, i = 5, loop = true)");
        calculateChainQuery.backupToStart();
        assertThat(n.isEmpty()).isFalse();
        assertThat(factorial.isEmpty()).isFalse();
        assertThat(i.isEmpty()).isFalse();
        assertThat(factorialExpression.isEmpty()).isTrue();
        assertThat(iExpression.isEmpty()).isTrue();
        assertThat(testExpression.isEmpty()).isTrue();
        calculateChainQuery.executeQuery(solution);
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(n = 4, factorial = 24, i = 5, loop = true)");
	}

}
