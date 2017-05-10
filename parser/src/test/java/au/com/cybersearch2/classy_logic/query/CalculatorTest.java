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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.LoopEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestBigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.TestEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TestVariable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.TermPair;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.OperandWalker;
import au.com.cybersearch2.classy_logic.pattern.SolutionPairer;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TemplateArchetype;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * CalculatorTest
 * @author Andrew Bowley
 * 11 Jan 2015
 */
public class CalculatorTest 
{

	private static final String KEY = "AxiomKey";
	private static final String TEMPLATE_NAME = "TemplateName";
	private static final String CHOICE_NAME = "ChoiceName";
/*
	@Test
	public void test_unifySolution()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        SolutionPairer pairer = mock(SolutionPairer.class);
        calculator.pairer = pairer;
		List<TermPair> pairList = new ArrayList<TermPair>();
		Operand term1 = mock(Operand.class);
		Term term2 = mock(Term.class);
		when(term1.unifyTerm(term2, 1)).thenReturn(1);
		TermPair termPair1 = new TermPair(term1, term2);
		pairList.add(termPair1);
		Operand term3 = mock(Operand.class);
		Term term4 = mock(Term.class);
		when(term3.unifyTerm(term4, 1)).thenReturn(1);
		TermPair termPair2 = new TermPair(term3, term4);
		pairList.add(termPair2);
		when(pairer.getPairList()).thenReturn(pairList);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(1);
		OperandWalker walker = mock(OperandWalker.class);
		when(walker.visitAllNodes(pairer)).thenReturn(true);
		when(template.getOperandWalker()).thenReturn(walker);
		when(solution.size()).thenReturn(2);
		assertThat(calculator.unifySolution(solution, template)).isTrue();
		verify(pairer).setSolution(solution);
        Template template2 = mock(Template.class);
        when(template2.getId()).thenReturn(1);
		OperandWalker walker2 = mock(OperandWalker.class);
		when(walker2.visitAllNodes(pairer)).thenReturn(false);
		when(template2.getOperandWalker()).thenReturn(walker2);
		assertThat(calculator.unifySolution(solution, template2)).isFalse();
	}
*/
	@Test
	public void test_completeSolution()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
        when(template.getKey()).thenReturn(KEY);
        when(template.getName()).thenReturn(TEMPLATE_NAME);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(TEMPLATE_NAME));
        Axiom axiom = mock(Axiom.class);
        when(axiom.getName()).thenReturn(TEMPLATE_NAME);
        when(template.toAxiom()).thenReturn(axiom);
        when(template.getId()).thenReturn(1);
        assertThat(calculator.completeSolution(solution, template)).isTrue();
        verify(solution).put(TEMPLATE_NAME, axiom);
       
	}
	
	@Test
	public void test_short_circuit_completeSolution()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        when(template.evaluate()).thenReturn(EvaluationStatus.SHORT_CIRCUIT);
        assertThat(calculator.completeSolution(solution, template)).isTrue();
	}

   @Test
    public void test_choice_completeSolution()
    {
        Calculator calculator = new Calculator();
        Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        Choice choice = mock(Choice.class);
        Axiom axiom = mock(Axiom.class);
        when(axiom.getTermByIndex(0)).thenReturn(new Parameter("x"));
        calculator.axiom = axiom;
        when(choice.completeSolution(solution, template, axiom)).thenReturn(true);
        when(template.isChoice()).thenReturn(true);
        when(template.evaluate()).thenReturn(EvaluationStatus.SHORT_CIRCUIT);
        assertThat(calculator.completeSolution(solution, template)).isTrue();
    }
	    

	@Test
	public void test_skip_completeSolution()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        when(template.evaluate()).thenReturn(EvaluationStatus.SKIP);
        assertThat(calculator.completeSolution(solution, template)).isFalse();
	}
	
	@Test
	public void test_exception_completeSolution()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        when(template.toString()).thenReturn("MyTemplate()");
        ExpressionException expressionException = new ExpressionException("Parser error");
        when(template.evaluate()).thenThrow(expressionException);
        try
        {
            calculator.completeSolution(solution, template);
            failBecauseExceptionWasNotThrown(QueryExecutionException.class);
        }
        catch(QueryExecutionException e)
        {
        	assertThat(e.getMessage()).isEqualTo("Error evaluating: MyTemplate()");
        	assertThat(e.getCause()).isEqualTo(expressionException);
        }
	}

	@Test
	public void test_execute()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
        Template template = mock(Template.class);
        when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
        when(template.getKey()).thenReturn(KEY);
        when(template.getName()).thenReturn(TEMPLATE_NAME);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(TEMPLATE_NAME));
        Axiom axiom = mock(Axiom.class);
        when(axiom.getName()).thenReturn(KEY);
        when(axiom.unifyTemplate(template, solution)).thenReturn(true);
        Axiom solutionAxiom = mock(Axiom.class);
        when(solutionAxiom.getName()).thenReturn(TEMPLATE_NAME);
        when(template.toAxiom()).thenReturn(solutionAxiom);
        when(template.getId()).thenReturn(1);
        calculator.execute(axiom, template, solution);
        verify(solution).put(TEMPLATE_NAME, solutionAxiom);
	}
/*	
	@Test
	public void test_execute_no_seed_axiom()
	{
        Calculator calculator = new Calculator();
		Solution solution = mock(Solution.class);
		when(solution.size()).thenReturn(2);
		QualifiedName qualifiedTemplateName = QualifiedName.parseTemplateName(TEMPLATE_NAME);
        SolutionPairer pairer = new SolutionPairer(solution, qualifiedTemplateName);
        calculator.pairer = pairer;
		List<TermPair> pairList = pairer.getPairList();
		Operand term1 = mock(Operand.class);
		Term term2 = mock(Term.class);
		when(term1.unifyTerm(term2, 1)).thenReturn(1);
		TermPair termPair1 = new TermPair(term1, term2);
		pairList.add(termPair1);
		Operand term3 = mock(Operand.class);
		Term term4 = mock(Term.class);
		when(term3.unifyTerm(term4, 1)).thenReturn(1);
		TermPair termPair2 = new TermPair(term3, term4);
		pairList.add(termPair2);
        Template template = mock(Template.class);
        when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
        when(template.getKey()).thenReturn(KEY);
        when(template.getName()).thenReturn(TEMPLATE_NAME);
        when(template.getQualifiedName()).thenReturn(qualifiedTemplateName);
		OperandWalker walker = mock(OperandWalker.class);
		when(walker.visitAllNodes(pairer)).thenReturn(true);
		when(template.getOperandWalker()).thenReturn(walker);
        Axiom solutionAxiom = mock(Axiom.class);
        when(solutionAxiom.getName()).thenReturn(KEY);
        when(template.toAxiom()).thenReturn(solutionAxiom);
        when(template.getId()).thenReturn(1);
        calculator.execute(template, solution);
        verify(solution).put(TEMPLATE_NAME, solutionAxiom);
	}
*/	
    @Test
	public void test_simple() 
	{
    	IntegerOperand n = new TestIntegerOperand("n");
    	IntegerOperand limit = new TestIntegerOperand("limit");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator calcExpression = new TestEvaluator("n", "++", n);
	    Evaluator testExpression = new TestEvaluator(n, "<", limit);
	    Evaluator shortCircuit = new TestEvaluator(testExpression, "&&");
	    operandList.add(calcExpression);
	    operandList.add(shortCircuit);
        TemplateArchetype loopArchetype = new TemplateArchetype(parseTemplateName("loop"));
	    Template template = new Template(loopArchetype);
		for (Operand operand: operandList)
			template.addTerm(operand);
    	LoopEvaluator loopy = new LoopEvaluator(template);
        TemplateArchetype calcArchetype = new TemplateArchetype(parseTemplateName("calc"));
        Template calcTemplate = new Template(calcArchetype, n, loopy, limit);
        calcTemplate.putInitData("n", Long.valueOf(1));
        calcTemplate.putInitData("limit", Long.valueOf(3));
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate);
        assertThat(solution.getAxiom("calc").toString()).isEqualTo("calc(n=3, limit=3)");
	}
    
    @Test
	public void test_factorial() 
	{
    	IntegerOperand n = new TestIntegerOperand("n");
    	BigDecimalOperand factorial = new TestBigDecimalOperand("factorial");
    	IntegerOperand i = new TestIntegerOperand("i");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator factorialExpression = new TestEvaluator("factorial", factorial, "*=", i);
       	Evaluator iExpression = new TestEvaluator(i, "++");
 	    Evaluator testExpression = new TestEvaluator(iExpression, "<", n);
	    Evaluator shortCircuit = new TestEvaluator(testExpression, "&&");
	    operandList.add(factorialExpression);
	    operandList.add(shortCircuit);
	    TemplateArchetype loopArchetype = new TemplateArchetype(parseTemplateName("loop"));
	    Template template = new Template(loopArchetype);
		for (Operand operand: operandList)
			template.addTerm(operand);
    	LoopEvaluator loopy = new LoopEvaluator(template);
        TemplateArchetype factorialArchetype = new TemplateArchetype(parseTemplateName("factorial"));
        Template calcTemplate = new Template(factorialArchetype, n, factorialExpression, i, loopy);
        calcTemplate.putInitData("factorial", Integer.valueOf(1));
        calcTemplate.putInitData("n", Long.valueOf(4));
        calcTemplate.putInitData("i", Long.valueOf(1));
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate);
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(n=4, factorial=24, i=5)");
	}

    @Ignore // Test fails if template add term does not do operand map
    @Test
    public void test_axiom_listener()
    {
    	IntegerOperand n = new TestIntegerOperand("n");
    	BigDecimalOperand factorial = new TestBigDecimalOperand("factorial");
    	IntegerOperand i = new TestIntegerOperand("i");
    	List<Operand> operandList = new ArrayList<Operand>();
    	Evaluator factorialExpression = new TestEvaluator("factorial", factorial, "*=", i);
       	Evaluator iExpression = new TestEvaluator(i, "++");
 	    Evaluator testExpression = new TestEvaluator(iExpression, "<", n);
	    Evaluator shortCircuit = new TestEvaluator(testExpression, "&&");
	    operandList.add(factorialExpression);
	    operandList.add(shortCircuit);
        TemplateArchetype loopArchetype = new TemplateArchetype(parseTemplateName("loop"));
	    Template template = new Template(loopArchetype);
		for (Operand operand: operandList)
			template.addTerm(operand);
    	LoopEvaluator loopy = new LoopEvaluator(template);
        TemplateArchetype factorialArchetype = new TemplateArchetype(parseTemplateName("factorial"));
        Template calcTemplate = new Template(factorialArchetype, n, factorialExpression, i, loopy);
        calcTemplate.putInitData("factorial", Long.valueOf(1));
        calcTemplate.putInitData("i", Long.valueOf(1));
        Solution solution = new Solution();
        AxiomListener axiomListener = new AxiomListener(){
            int result[] = new int[] { 1, 2, 6, 24 };
            int cycle;
			@Override
			public void onNextAxiom(QualifiedName qname, Axiom axiom) 
			{
				//System.out.println(axiom.toString());
				BigDecimal factorial = (BigDecimal)(axiom.getTermByName("factorial").getValue());
				Integer i = (Integer)(axiom.getTermByName("i").getValue());
				assertThat(factorial.intValue()).isEqualTo(result[cycle]);
				assertThat(i.intValue()).isEqualTo(cycle + 2);
				++cycle;
			}};
		for (int count = 1; count < 5; count++)
		{
			Calculator calculator = new Calculator();
			calculator.setAxiomListener(axiomListener);
	        calcTemplate.putInitData("n", Long.valueOf(count));
			calculator.iterate(solution, calcTemplate);
			calcTemplate.backup(false);
		}
    }
   
    @Test
    public void test_choice()
    {
    	// (0, 0, 1.00)
        Parameter threshold = new Parameter("threshold", Integer.valueOf(0));
        Parameter base = new Parameter("threshold", Integer.valueOf(0));
        Parameter percent = new Parameter("percent", Double.valueOf(1.00));
    	Axiom axiom0 = new Axiom(CHOICE_NAME, threshold, base, percent);
    	threshold = new Parameter("threshold", Integer.valueOf(12000));
    	base = new Parameter("threshold", Integer.valueOf(120));
    	percent = new Parameter("percent", Double.valueOf(1.00));
    	Axiom axiom1 = new Axiom(CHOICE_NAME, threshold, base, percent);
    	ArrayList<Axiom> choiceAxiomList = new ArrayList<Axiom>();
    	choiceAxiomList.add(axiom0);
    	choiceAxiomList.add(axiom1);
        TemplateArchetype choiceArchetype = new TemplateArchetype(parseTemplateName(CHOICE_NAME));
    	Template template = new Template(choiceArchetype);
    	template.setChoice(true);
    	Operand expression = new TestEvaluator(new TestVariable("amount"), "<", new TestIntegerOperand(Term.ANONYMOUS, Integer.valueOf(12000)));
    	Evaluator evaluator0 = new TestEvaluator("amount", expression, "&&"); 
    	template.addTerm(evaluator0);
    	expression = new TestBooleanOperand(Term.ANONYMOUS, Boolean.TRUE);
    	Evaluator evaluator1 = new TestEvaluator("amount", expression, "&&"); 
    	template.addTerm(evaluator1);
		Calculator calculator = new Calculator();
		Choice choice = mock(Choice.class);
		calculator.setChoice(choice);
		Long seedAmount = Long.valueOf(123000);
		Axiom seedAxiom = new Axiom("Seed", new Parameter("amount", seedAmount));
		template.setKey("Seed");
		Solution solution = new Solution();
		calculator.execute(seedAxiom, template, solution);
		verify(choice).completeSolution(solution, template, seedAxiom);

		/*
		//System.out.println(solution.getAxiom(CHOICE_NAME).toString());
		assertThat(solution.getAxiom(CHOICE_NAME).toString()).isEqualTo("ChoiceName(amount = 123000, threshold = 12000, threshold = 120, percent = 1.0)");
		template.backup(false);
		solution.reset();
		seedAxiom = new Axiom("Seed", new Parameter("amount", Integer.valueOf(11999)));
		calculator.execute(seedAxiom, template, solution);
		//System.out.println(solution.getAxiom(CHOICE_NAME).toString());
		assertThat(solution.getAxiom(CHOICE_NAME).toString()).isEqualTo("ChoiceName(amount = 11999, threshold = 0, threshold = 0, percent = 1.0)");
		*/
    }

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }
}
