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
package au.com.cybersearch2.classy_logic.pattern;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.operator.OperatorTerm;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * SolutionPairerTest
 * @author Andrew Bowley
 * 23 Dec 2014
 */
public class SolutionPairerTest 
{
    static final String TEMPLATE_NAME = "TemplateName";
    static final String ONE = "one";
    static final String TWO = "two";
    static QualifiedName CONTEXT_NAME = QualifiedName.parseTemplateName(TEMPLATE_NAME);
    static QualifiedName ONE_QNAME = QualifiedName.parseName(ONE, CONTEXT_NAME);
    static QualifiedName TWO_QNAME = QualifiedName.parseName(TWO, CONTEXT_NAME);
    

	@Test
	public void test_next_Solution_operand_empty()
	{
		// Pairing case
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		OperatorTerm solutionTerm = mock(OperatorTerm.class);
		Operator operator = mock(Operator.class);
        Operator operator2 = mock(Operator.class);
		when(solutionTerm.getOperator()).thenReturn(operator);
		Trait trait = mock(Trait.class);
		when(trait.getOperandType()).thenReturn(OperandType.INTEGER);
		when(operator.getTrait()).thenReturn(trait);
		when(templateOperand.isEmpty()).thenReturn(true);
		when(templateOperand.getOperator()).thenReturn(operator2);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionTerm);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionTerm.getName()).thenReturn(TWO);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		Template template = mock(Template.class);
		when(template.getQualifiedName()).thenReturn(CONTEXT_NAME);
		when(template.getId()).thenReturn(3);
		SolutionPairer solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		verify(templateOperand).unifyTerm(solutionTerm, 3);
		verify(operator2).setTrait(trait);
	}
    
	@Test
	public void test_next_operand_non_empty_non_match()
	{
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		OperatorTerm solutionTerm = mock(OperatorTerm.class);
		when(solutionTerm.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionTerm);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionTerm.getName()).thenReturn(TWO);
		when(templateOperand.isEmpty()).thenReturn(false);
		when(templateOperand.getValue()).thenReturn(Long.valueOf(99l));
        when(solutionTerm.getValue()).thenReturn(Long.valueOf(71l));
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
	    Template template = mock(Template.class);
	    when(template.getQualifiedName()).thenReturn(CONTEXT_NAME);
		SolutionPairer solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isFalse();
        verify(templateOperand, times(0)).unifyTerm(solutionTerm, 3);
	}
	
	@Test
    public void test_next_operand_non_empty_match()
    {
        Axiom oneAxiom = mock(Axiom.class);
        Operand templateOperand = mock(Operand.class);
        OperatorTerm solutionTerm = mock(OperatorTerm.class);
        when(solutionTerm.isEmpty()).thenReturn(false);
        when(oneAxiom.getTermByName(TWO)).thenReturn(solutionTerm);
        when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
        when(solutionTerm.getName()).thenReturn(TWO);
        when(templateOperand.isEmpty()).thenReturn(false);
        when(templateOperand.getValue()).thenReturn(Long.valueOf(99l));
        when(solutionTerm.getValue()).thenReturn(Long.valueOf(99l));
        Solution solution = new Solution();
        solution.put(CONTEXT_NAME.toString(), oneAxiom);
        Template template = mock(Template.class);
        when(template.getQualifiedName()).thenReturn(CONTEXT_NAME);
        SolutionPairer solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
        assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
    } 

	// TODO - Review test - ".two" and "one." are invalid
	@Test
	public void test_next_bad_name()
	{
		Operand templateOperand = mock(Operand.class);
		//when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName(".two"));
		Solution solution = new Solution();
		SolutionPairer solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		//assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		//templateOperand = mock(Operand.class);
		//when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one."));
		//solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		//assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		//templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one.three"));
		solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isFalse();
		templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one"));
		solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isFalse();
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one.two"));
		solutionPairer = new SolutionPairer(solution, 3, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isFalse();
	} 
}
