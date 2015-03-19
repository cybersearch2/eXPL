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
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * AxiomPairerTest
 * @author Andrew Bowley
 * 22 Dec 2014
 */
public class AxiomPairerTest 
{

	@Test
	public void test_next()
	{
		// Pairing case
		Axiom owner = mock(Axiom.class);
		Operand operand = mock(Operand.class);
		Term term1 = mock(Term.class);
		when(operand.getName()).thenReturn("x");
		when(owner.getTermByName("x")).thenReturn(term1);
		when(operand.isEmpty()).thenReturn(true);
		AxiomPairer axiomPairer= new AxiomPairer(owner);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(1);
		TermPair pair = axiomPairer.getPairList().get(0);
		assertThat(pair.term1).isEqualTo(operand);
		assertThat(pair.term2).isEqualTo(term1);
		
		// Template term not empty: Axiom value == Template value
		when(operand.isEmpty()).thenReturn(false);
		when(operand.getValue()).thenReturn(new Integer(2));
		when(term1.isEmpty()).thenReturn(false);
		when(term1.getValue()).thenReturn(new Integer(2));
		axiomPairer= new AxiomPairer(owner);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);
		
		// Axiom parameter is empty - should never happen
		when(term1.isEmpty()).thenReturn(true);
		axiomPairer= new AxiomPairer(owner);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);

		// Axiom value != Template value
		when(term1.isEmpty()).thenReturn(false);
		when(term1.getValue()).thenReturn(new Integer(3));
		axiomPairer= new AxiomPairer(owner);
		assertThat(axiomPairer.next(operand, 1)).isFalse();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);
         
		// Template term is anonymous
		when(operand.getName()).thenReturn("");
		axiomPairer= new AxiomPairer(owner);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);

	    // Matching term not found by name 
		when(operand.getName()).thenReturn("x");
		when(owner.getTermByName("x")).thenReturn(null);
		assertThat(axiomPairer.next(operand, 1)).isTrue();
		assertThat(axiomPairer.getPairList().size()).isEqualTo(0);
    }
}
