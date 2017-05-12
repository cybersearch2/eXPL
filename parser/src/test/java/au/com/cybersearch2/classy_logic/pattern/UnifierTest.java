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
import au.com.cybersearch2.classy_logic.interfaces.TermPairList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * UnifierTest
 * @author Andrew Bowley
 * 22 Dec 2014
 */
public class UnifierTest 
{
    static final String TEMPLATE_NAME = "TemplateName";
    static final String OPERAND_NAME = "x";
    
    class TestTermPairList implements TermPairList
    {
        public Operand term1;
        public Term term2;
        
        @Override
        public void add(Operand term1, Term term2)
        {
            this.term1 = term1;
            this.term2 = term2;
        }
  
        public void clear()
        {
            term1 = null;
            term2 = null;
        }
    }
 /*  
	@Test
	public void test_next()
	{
		// Pairing case
	    QualifiedName contextName = QualifiedName.parseTemplateName(TEMPLATE_NAME);
	    QualifiedName operandName = QualifiedName.parseName(OPERAND_NAME, contextName);
		Axiom axiom = mock(Axiom.class);
	    AxiomArchetype axiomArchetype = mock(AxiomArchetype.class);
	    when(axiom.getArchetype()).thenReturn(axiomArchetype);
		Operand operand = mock(Operand.class);
		Term term2 = mock(Term.class);
		when(operand.getName()).thenReturn(OPERAND_NAME);
		when(operand.getQualifiedName()).thenReturn(operandName);
		when(axiom.getTermByName(OPERAND_NAME)).thenReturn(term2);
		when(operand.isEmpty()).thenReturn(true);
		TestTermPairList termPairList = new TestTermPairList();
		AxiomPairer axiomPairer= new AxiomPairer(termPairList);
        axiomPairer.setAxiom(axiom);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(termPairList.term1).isEqualTo(operand);
        assertThat(termPairList.term2).isEqualTo(term2);
		
		// Template term not empty: Axiom value == Template value
		when(operand.isEmpty()).thenReturn(false);
		when(operand.getValue()).thenReturn(new Integer(2));
		when(term2.isEmpty()).thenReturn(false);
		when(term2.getValue()).thenReturn(new Integer(2));
		termPairList.clear();
		axiomPairer= new AxiomPairer(termPairList);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
        assertThat(termPairList.term1).isNull();
        assertThat(termPairList.term2).isNull();
		
		// Axiom parameter is empty - should never happen
		when(term2.isEmpty()).thenReturn(true);
        termPairList.clear();
        axiomPairer= new AxiomPairer(termPairList);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
        assertThat(termPairList.term1).isNull();
        assertThat(termPairList.term2).isNull();

 		// Axiom value != Template value
		when(term2.isEmpty()).thenReturn(false);
		when(term2.getValue()).thenReturn(new Integer(3));
        termPairList.clear();
        axiomPairer= new AxiomPairer(termPairList);
		assertThat(axiomPairer.next(operand, 1)).isFalse();
	    assertThat(termPairList.term1).isNull();
	    assertThat(termPairList.term2).isNull();
         
		// Template term is anonymous
		when(operand.getName()).thenReturn("");
        when(operand.getQualifiedName()).thenReturn(contextName);
		axiomPairer= new AxiomPairer(owner, contextName);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);

	    // Matching term not found by name 
		when(operand.getName()).thenReturn(OPERAND_NAME);
        when(operand.getQualifiedName()).thenReturn(operandName);
		when(owner.getTermByName(OPERAND_NAME)).thenReturn(null);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);
    } 
        */

}
