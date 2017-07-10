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
import static org.mockito.Mockito.mock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TemplateOperand;
import au.com.cybersearch2.classy_logic.expression.TestBigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.TestEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TemplateArchetype;

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
	    props.put("n", Long.valueOf(1));
	    props.put("limit", Long.valueOf(3));
    	IntegerOperand n = new TestIntegerOperand("n");
    	IntegerOperand limit = new TestIntegerOperand("limit");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator calcExpression = new TestEvaluator("n", "++", n);
	    Evaluator testExpression = new TestEvaluator(n, "!=", limit);
	    Evaluator shortCircuit = new TestEvaluator(testExpression, "&&");
	    operandList.add(calcExpression);
	    operandList.add(shortCircuit);
	    TemplateArchetype loopArchetype = new TemplateArchetype(parseTemplateName("loop"));
	    Template template = new Template(loopArchetype);
		for (Operand operand: operandList)
			template.addTerm(operand);
    	TemplateOperand loopy = new TemplateOperand(template);
        TemplateArchetype calcArchetype = new TemplateArchetype(parseTemplateName("calc"));
        Template calcTemplate = new Template(calcArchetype, n, loopy, limit);
        calcTemplate.setProperties(props);
        Solution solution = new Solution();
        
        CalculateChainQuery calculateChainQuery = new CalculateChainQuery(null, calcTemplate, null);
        calculateChainQuery.executeQuery(solution, new ArrayDeque<Template>(), mock(ExecutionContext.class));
        assertThat(solution.getAxiom("calc").toString()).isEqualTo("calc(n=3, limit=3)");
	}   
    
    @Test
	public void test_factorial() 
	{
	    Map<String,Object> props = new HashMap<String,Object>(); 
    	props.put("factorial", Long.valueOf(1));
    	props.put("n", Long.valueOf(4));
    	props.put("i", Long.valueOf(1));
    	IntegerOperand n = new TestIntegerOperand("n");
    	BigDecimalOperand factorial = new TestBigDecimalOperand("factorial");
    	IntegerOperand i = new TestIntegerOperand("i");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator factorialExpression = new TestEvaluator("factorial", factorial, "*=", i);
       	Evaluator iExpression = new TestEvaluator(i, "++");
 	    Evaluator testExpression = new TestEvaluator(iExpression, "!=", n);
	    Evaluator shortCircuit = new TestEvaluator(testExpression, "&&");
	    operandList.add(factorialExpression);
	    operandList.add(shortCircuit);
        TemplateArchetype loopArchetype = new TemplateArchetype(parseTemplateName("loop"));
	    Template template = new Template(loopArchetype);
		for (Operand operand: operandList)
			template.addTerm(operand);
    	TemplateOperand loopy = new TemplateOperand(template);
        TemplateArchetype factorialArchetype = new TemplateArchetype(parseTemplateName("factorial"));
        Template calcTemplate = new Template(factorialArchetype, n, factorial, i, loopy);
        calcTemplate.setProperties(props);

        Solution solution = new Solution();
        CalculateChainQuery calculateChainQuery = new CalculateChainQuery(null, calcTemplate, null);
        calculateChainQuery.executeQuery(solution, new ArrayDeque<Template>(), mock(ExecutionContext.class));
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(n=4, factorial=24, i=5)");
        calculateChainQuery.backupToStart();
        assertThat(n.isEmpty()).isTrue();
        assertThat(factorial.isEmpty()).isTrue();
        assertThat(i.isEmpty()).isTrue();
        assertThat(factorialExpression.isEmpty()).isTrue();
        assertThat(iExpression.isEmpty()).isTrue();
        assertThat(testExpression.isEmpty()).isTrue();
        calculateChainQuery.executeQuery(solution, new ArrayDeque<Template>(), mock(ExecutionContext.class));
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(n=4, factorial=24, i=5)");
	}

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }
}
