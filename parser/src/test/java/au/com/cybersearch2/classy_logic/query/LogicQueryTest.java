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
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.TestBigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.TestDoubleOperand;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TestStringOperand;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.UnificationPairer;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;
import au.com.cybersearch2.classy_logic.pattern.OperandWalker;
import au.com.cybersearch2.classy_logic.pattern.SolutionPairer;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.BigDecimalTerm;
import au.com.cybersearch2.classy_logic.terms.BooleanTerm;
import au.com.cybersearch2.classy_logic.terms.DoubleTerm;
import au.com.cybersearch2.classy_logic.terms.IntegerTerm;
import au.com.cybersearch2.classy_logic.terms.StringTerm;

/**
 * LogicQueryTest
 * @author Andrew Bowley
 * 31 Dec 2014
 */
public class LogicQueryTest 
{
    static final String NAME = "axiom_name";
	static final String KEY = "axiom_key";
  
    @Test
    public void test_setQueryStatusComplete()
    {
		AxiomSource axiomSource = mock(AxiomSource.class);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		logicQuery.setQueryStatusComplete();
		assertThat(logicQuery.getQueryStatus()).isEqualTo(QueryStatus.complete);
    }

	@Test
	public void test_unifySolution()
	{
		AxiomSource axiomSource = mock(AxiomSource.class);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		Solution solution = mock(Solution.class);
	    SolutionPairer pairer = mock(SolutionPairer.class);
        logicQuery.pairer = pairer;
		List<TermPair> pairList = new ArrayList<TermPair>();
		Term term1 = mock(Term.class);
		Term term2 = mock(Term.class);
		when(term1.unifyTerm(term2, 1)).thenReturn(1);
		TermPair termPair1 = new TermPair(term1, term2);
		pairList.add(termPair1);
		Term term3 = mock(Term.class);
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
		assertThat(logicQuery.unifySolution(solution, template)).isTrue();
		verify(pairer).setSolution(solution);
        Template template2 = mock(Template.class);
        when(template2.getId()).thenReturn(1);
		OperandWalker walker2 = mock(OperandWalker.class);
		when(walker2.visitAllNodes(pairer)).thenReturn(false);
		when(template2.getOperandWalker()).thenReturn(walker2);
		assertThat(logicQuery.unifySolution(solution, template2)).isFalse();
	}


	@Test
	public void test_iterate_empty_source()
	{
		Solution solution = new Solution();
		AxiomSource axiomSource = mock(AxiomSource.class);
		Template template = mock(Template.class);
	    final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Axiom next() {
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
	}


	@Test
	public void test_iterate_soution_found()
	{
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(KEY);
		Template template = mock(Template.class);
		when(template.getName()).thenReturn(NAME);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
		when(template.getKey()).thenReturn(KEY);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		Solution solution = new Solution();
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Axiom next() {
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		assertThat(logicQuery.iterate(solution, template)).isTrue();
		assertThat(solution.getAxiom(NAME)).isEqualTo(solutionAxiom);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.in_progress);
	}

	@Test
	public void test_iterate_solution_with_handler()
	{
		Solution solution = new Solution();
		SolutionHandler solutionHandler = mock(SolutionHandler.class);
        // SolutionHandler returns true, so retain solution
		when(solutionHandler.onSolution(solution)).thenReturn(true);
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(KEY);
		Template template = mock(Template.class);
		when(template.getKey()).thenReturn(KEY);
		when(template.getName()).thenReturn(NAME);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Axiom next() {
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource, solutionHandler);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		assertThat(logicQuery.iterate(solution, template)).isTrue();
		assertThat(solution.getAxiom(NAME)).isEqualTo(solutionAxiom);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.in_progress);
	}
	
	@Test
	public void test_iterate_no_solution_with_handler()
	{
		Solution solution = new Solution();
		SolutionHandler solutionHandler = mock(SolutionHandler.class);
		// SolutionHandler returns false, so discard solution
		when(solutionHandler.onSolution(solution)).thenReturn(false);
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
        when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = new Iterator<Axiom>(){
            int count = 0;
			@Override
			public boolean hasNext() {
				return count < 1;
			}

			@Override
			public Axiom next() {
				++count;
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource, solutionHandler);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
        Axiom blankAxiom = solution.getAxiom(NAME);
		assertThat(blankAxiom.getTermCount()).isEqualTo(0);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		verify(template).backup(true);
	}

	@Test
	public void test_iterate_not_fact()
	{
		Solution solution = new Solution();
		SolutionHandler solutionHandler = mock(SolutionHandler.class);
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
        when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(false);
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){
            int count = 0;
			@Override
			public boolean hasNext() {
				return count < 1;
			}

			@Override
			public Axiom next() {
				++count;
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource, solutionHandler);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
		Axiom blankAxiom = solution.getAxiom(NAME);
		assertThat(blankAxiom.getTermCount()).isEqualTo(0);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		verify(template).backup(true);
	}

	@Test
	public void test_iterate_end()
	{
		OperandWalker operandWalker = mock(OperandWalker.class);
		when(operandWalker.visitAllNodes(isA(UnificationPairer.class))).thenReturn(true);
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
        when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		when(template.getOperandWalker()).thenReturn(operandWalker);
		Solution solution = new Solution();
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){
            int count = 0;
			@Override
			public boolean hasNext() {
				return count < 1;
			}

			@Override
			public Axiom next() {
				++count;
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		assertThat(logicQuery.iterate(solution, template)).isTrue();
		assertThat(logicQuery.iterate(solution, template)).isFalse();
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
	}

	@Test
	public void test_iterate_short_circuit()
	{
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
		when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.SHORT_CIRCUIT);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		Solution solution = new Solution();
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){
            int count = 0;
			@Override
			public boolean hasNext() {
				return count < 1;
			}

			@Override
			public Axiom next() {
				++count;
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		verify(template).backup(true);
	}

	@Test
	public void test_iterate_unify_false()
	{
		AxiomSource axiomSource = mock(AxiomSource.class);
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
        when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		Solution solution = new Solution();
		when(axiom.unifyTemplate(template, solution)).thenReturn(false);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){
            int count = 0;
			@Override
			public boolean hasNext() {
				return count < 1;
			}

			@Override
			public Axiom next() {
				++count;
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		verify(template).backup(true);
	}


	@Test
	public void test_iterate_evaluation_exception()
	{
		AxiomSource axiomSource = mock(AxiomSource.class);
		LogicQuery logicQuery = new LogicQuery(axiomSource);
		Solution solution = new Solution();
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
        when(template.getTermCount()).thenReturn(1);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenThrow(new ExpressionException("Syntax error"));
		when(template.toString()).thenReturn("surface_area(km2)");
		final Axiom axiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		final Iterator<Axiom> iterator = 	new Iterator<Axiom>(){

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Axiom next() {
				return axiom;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		when(axiomSource.iterator()).thenReturn(iterator);
		try
		{
		    logicQuery.iterate(solution, template);
		    failBecauseExceptionWasNotThrown(QueryExecutionException.class);
		}
		catch(QueryExecutionException e)
		{
			assertThat(e.getMessage()).isEqualTo("Error evaluating: surface_area(km2)");
			assertThat(e.getCause().getMessage()).isEqualTo("Syntax error");
		}
	}

	@Test
	public void test_iterate_sole_soution_empty()
	{
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		Solution solution = new Solution();
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		LogicQuery logicQuery = new LogicQuery(new EmptyAxiomSource());
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		assertThat(logicQuery.iterate(solution, template)).isFalse();
        Axiom blankAxiom = solution.getAxiom(NAME);
		assertThat(blankAxiom.getTermCount()).isEqualTo(0);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
	}

	@Test
	public void test_iterate_sole_soution_found()
	{
		final Axiom axiom = mock(Axiom.class);
		Axiom solutionAxiom = mock(Axiom.class);
		when(axiom.getName()).thenReturn(NAME);
		Template template = mock(Template.class);
		OperandWalker operandWalker = mock(OperandWalker.class);
		when(operandWalker.visitAllNodes(isA(OperandVisitor.class))).thenReturn(true);
        when(template.getQualifiedName()).thenReturn(QualifiedName.parseTemplateName(NAME));
		when(template.getOperandWalker()).thenReturn(operandWalker);
		when(template.getKey()).thenReturn(NAME);
		when(template.evaluate()).thenReturn(EvaluationStatus.COMPLETE);
		when(template.isFact()).thenReturn(true);
		when(template.toAxiom()).thenReturn(solutionAxiom);
		Solution solution = new Solution();
		solution.put(NAME, solutionAxiom);
		when(axiom.unifyTemplate(template, solution)).thenReturn(true);
		LogicQuery logicQuery = new LogicQuery(new EmptyAxiomSource());
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
		assertThat(logicQuery.iterate(solution, template)).isTrue();
		assertThat(solution.getAxiom(NAME)).isEqualTo(solutionAxiom);
		assertThat(logicQuery.queryStatus).isEqualTo(QueryStatus.start);
	}

	@Test 
	public void test_axiom_listener()
	{
		Template template = new Template(new QualifiedTemplateName(QualifiedName.EMPTY, "template_name"));
		template.setKey(KEY);
		template.addTerm(new TestStringOperand("string"));
		template.addTerm(new TestIntegerOperand("integer"));
		template.addTerm(new TestDoubleOperand("string"));
		template.addTerm(new TestBooleanOperand("boolean"));
		template.addTerm(new TestBigDecimalOperand("decimal"));
		Solution solution = new Solution();
		final Axiom[] axioms = getTestAxioms();
		List<Axiom> axiomList = new ArrayList<Axiom>();
		axiomList.add(axioms[0]);
		axiomList.add(axioms[1]);
		final int[] count = new int[1];
        AxiomListener axiomListener = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				assertThat(axiom).isEqualTo(axioms[count[0]++]);
			}};
		LogicQuery query = new LogicQuery(new AxiomListSource(axiomList));
		query.setAxiomListener(axiomListener);
		query.iterate(solution, template);
		template.backup(true);
		query.iterate(solution, template);
		assertThat(count[0]).isEqualTo(2);
	}
	
	protected Axiom[] getTestAxioms()
	{
		Axiom[] axiomArray = new Axiom[2];
		Axiom axiom = new Axiom(KEY);
		axiom.addTerm(new StringTerm("String term 1"));
		axiom.addTerm(new IntegerTerm(1));
		axiom.addTerm(new DoubleTerm(1.0d));
		axiom.addTerm(new BooleanTerm(Boolean.TRUE));
		axiom.addTerm(new BigDecimalTerm("11111"));
		axiomArray[0] = axiom;
		Axiom axiom2 = new Axiom(KEY);
		axiom2.addTerm(new StringTerm("String term 2"));
		axiom2.addTerm(new IntegerTerm(2));
		axiom2.addTerm(new DoubleTerm(2.0d));
		axiom2.addTerm(new BooleanTerm(Boolean.FALSE));
		axiom2.addTerm(new BigDecimalTerm("22222"));
		axiomArray[1] = axiom2;
		return axiomArray;
	}

}
