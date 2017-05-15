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

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

/**
 * UnifierTest
 * @author Andrew Bowley
 * 22 Dec 2014
 */
public class UnifierTest 
{
    static final String TEMPLATE_NAME = "TemplateName";
    static final String OPERAND_NAME = "x";
    
	@Test
	public void test_next_pair()
	{
		// Pairing case
		Axiom axiom = mock(Axiom.class);
		Operand operand = mock(Operand.class);
		Term term2 = mock(Term.class);
		when(operand.getName()).thenReturn(OPERAND_NAME);
        when(axiom.getTermByIndex(1)).thenReturn(term2);
		when(operand.isEmpty()).thenReturn(true);
		when(operand.getArchetypeIndex()).thenReturn(0);
		Template template = mock(Template.class);
		when(template.getId()).thenReturn(3);
		int[] termMapping = new int[] { 1, 0};
		Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
 		assertThat(underTest.next(operand, 1)).isTrue();
 		verify(operand).unifyTerm(term2, 3);
	}
	
    @Test
    public void test_non_empty_match()
    {
		// Operand not empty: axiom value == operand value
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(term2.isEmpty()).thenReturn(false);
        when(term2.getValue()).thenReturn(new Integer(2));
        when(operand.getName()).thenReturn(OPERAND_NAME);
        when(axiom.getTermByIndex(1)).thenReturn(term2);
        when(operand.isEmpty()).thenReturn(false);
        when(operand.getValue()).thenReturn(new Integer(2));
        when(operand.getArchetypeIndex()).thenReturn(0);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
        assertThat(underTest.next(operand, 1)).isTrue();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }
    
    @Test
    public void test_term_empty()
    {
		// Axiom parameter is empty - should never happen
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(term2.isEmpty()).thenReturn(true);
        when(term2.getValue()).thenReturn(new Integer(2));
        when(operand.getName()).thenReturn(OPERAND_NAME);
        when(axiom.getTermByIndex(1)).thenReturn(term2);
        when(operand.isEmpty()).thenReturn(false);
        when(operand.getValue()).thenReturn(new Integer(2));
        when(operand.getArchetypeIndex()).thenReturn(0);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
        assertThat(underTest.next(operand, 1)).isTrue();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }
    
    @Test
    public void test_non_empty_non_match()
    {
 		// Axiom value != Template value
        // Operand not empty: axiom value == operand value
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(term2.isEmpty()).thenReturn(false);
        when(term2.getValue()).thenReturn(new Integer(3));
        when(operand.getName()).thenReturn(OPERAND_NAME);
        when(axiom.getTermByIndex(1)).thenReturn(term2);
        when(operand.isEmpty()).thenReturn(false);
        when(operand.getValue()).thenReturn(new Integer(2));
        when(operand.getArchetypeIndex()).thenReturn(0);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
        assertThat(underTest.next(operand, 1)).isFalse();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }
    
    @Test
    public void test_empty_name_operand()
    {
        // Operand is anonymous
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(operand.getName()).thenReturn("");
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
        assertThat(underTest.next(operand, 1)).isTrue();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }

    @Test
    public void test_no_pair()
    {
        // Term not paired
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(operand.getName()).thenReturn(OPERAND_NAME);
        when(operand.getArchetypeIndex()).thenReturn(-1);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Unifier underTest = new Unifier(template, axiom, termMapping, new Solution());
        assertThat(underTest.next(operand, 1)).isTrue();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }
    
    @Test
    public void test_solution_pair()
    {
        // Term not paired
        Axiom axiom = mock(Axiom.class);
        Operand operand = mock(Operand.class);
        Term term2 = mock(Term.class);
        when(operand.getName()).thenReturn(OPERAND_NAME);
        when(operand.getArchetypeIndex()).thenReturn(-1);
        Template template = mock(Template.class);
        when(template.getId()).thenReturn(3);
        int[] termMapping = new int[] { 1, 0};
        Solution solution = mock(Solution.class);
        Set<String> keyset = new HashSet<String>();
        keyset.add("key");
        when(solution.keySet()).thenReturn(keyset);
        SolutionPairer solutionPairer = mock(SolutionPairer.class);
        when(template.getSolutionPairer(solution)).thenReturn(solutionPairer);
        when(solutionPairer.next(operand, 0)).thenReturn(true);
        Unifier underTest = new Unifier(template, axiom, termMapping, solution);
        assertThat(underTest.next(operand, 1)).isTrue();
        verify(operand, times(0)).unifyTerm(term2, 3);
    }

}
