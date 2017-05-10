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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.TermPair;
import au.com.cybersearch2.classy_logic.query.Solution;
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
/*    
	@Test
	public void test_next()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		//when(owner.getNamePattern()).thenReturn(Pattern.compile("([^.]*)\\.([^.]*)"));
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(true);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(owner.getTermByName(TWO)).thenReturn(term1);
		when(templateOperand.isEmpty()).thenReturn(true);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		assertThat(solutionPairer.getPairList().size()).isEqualTo(1);
		TermPair pair = solutionPairer.getPairList().get(0);
		assertThat(pair.term1).isEqualTo(templateOperand);
		assertThat(pair.term2).isEqualTo(solutionOperand);
		
	}

	@Test
	public void test_next_no_Axiom_match()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(true);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(owner.getTermByName(TWO)).thenReturn(null);
		when(templateOperand.isEmpty()).thenReturn(true);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		assertThat(solutionPairer.getPairList().size()).isEqualTo(1);
		TermPair pair = solutionPairer.getPairList().get(0);
		assertThat(pair.term1).isEqualTo(templateOperand);
		assertThat(pair.term2).isEqualTo(solutionOperand);
		
	}

	@Test
	public void test_next_Solution_term_empty()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(true);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(true);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(owner.getTermByName(TWO)).thenReturn(term1);
		when(templateOperand.isEmpty()).thenReturn(true);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		assertThat(solutionPairer.getPairList().size()).isEqualTo(0);
	}

	@Test
	public void test_next_term_non_empty()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(true);
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(owner.getTermByName(TWO)).thenReturn(term1);
		when(templateOperand.isEmpty()).thenReturn(false);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		assertThat(solutionPairer.getPairList().size()).isEqualTo(0);
		
	}

	@Test
	public void test_next_non_empty_Axiom_term()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(false);
		when(term1.getValue()).thenReturn(new Integer(2));
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(solutionOperand.getValue()).thenReturn(new Integer(2));
		when(owner.getTermByName(TWO)).thenReturn(term1);
		when(templateOperand.isEmpty()).thenReturn(true);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		assertThat(solutionPairer.getPairList().size()).isEqualTo(1);
		TermPair pair = solutionPairer.getPairList().get(0);
		assertThat(pair.term1).isEqualTo(templateOperand);
		assertThat(pair.term2).isEqualTo(solutionOperand);
		
	}
	
	@Test
	public void test_next_exit()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Axiom oneAxiom = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		Operand solutionOperand = mock(Operand.class);
		when(solutionOperand.isEmpty()).thenReturn(false);
		when(oneAxiom.getTermByName(TWO)).thenReturn(solutionOperand);
		Term term1 = mock(Term.class);
		when(term1.isEmpty()).thenReturn(false);
		when(term1.getValue()).thenReturn(new Integer(2));
		when(templateOperand.getQualifiedName()).thenReturn(TWO_QNAME);
		when(solutionOperand.getName()).thenReturn(TWO);
		when(solutionOperand.getValue()).thenReturn(new Integer(3));
		when(owner.getTermByName(TWO)).thenReturn(term1);
		when(templateOperand.isEmpty()).thenReturn(true);
		Solution solution = new Solution();
		solution.put(CONTEXT_NAME.toString(), oneAxiom);
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isFalse();
		
	}

	@Test
	public void test_next_bad_name()
	{
		Axiom owner = mock(Axiom.class);
		Operand templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName(".two"));
		Solution solution = new Solution();
		SolutionPairer solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one."));
		solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one.three"));
		solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		templateOperand = mock(Operand.class);
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one"));
		solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
		when(templateOperand.getQualifiedName()).thenReturn(QualifiedName.parseGlobalName("one.two"));
		solutionPairer = new SolutionPairer(owner, solution, CONTEXT_NAME);
		assertThat(solutionPairer.next(templateOperand, 1)).isTrue();
	} 
*/
}
