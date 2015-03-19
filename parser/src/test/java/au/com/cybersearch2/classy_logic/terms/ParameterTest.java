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
package au.com.cybersearch2.classy_logic.terms;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * ParameterTest
 * @author Andrew Bowley
 * 16 Nov 2014
 */
public class ParameterTest 
{
	final static String VALUE = "value";
	final static String NAME = "myParam";


	@SuppressWarnings("unchecked")
	@Test 
	public void testNonEmptyANONYMOUSConstructor()
	{
		Parameter parameter = new Parameter(Term.ANONYMOUS, VALUE);
		assertThat(parameter.getName()).isEqualTo(Term.ANONYMOUS);
		assertThat(parameter.isEmpty()).isFalse();
		assertThat(parameter.getValue()).isEqualTo(VALUE);
		assertThat((Class<String>)(parameter.getValueClass())).isEqualTo(String.class);
	}

	@SuppressWarnings("unchecked")
	@Test 
	public void testNonEmptyNamedConstructor()
	{
		Parameter parameter = new Parameter(NAME, VALUE);
		assertThat(parameter.getName()).isEqualTo(NAME);
		assertThat(parameter.isEmpty()).isFalse();
		assertThat(parameter.getValue()).isEqualTo(VALUE);
		assertThat((Class<String>)(parameter.getValueClass())).isEqualTo(String.class);
	}

	@Test 
	public void testNullNamedConstructor()
	{
		@SuppressWarnings("unused")
		Parameter parameter = null;
		try
		{
			parameter = new Parameter(null, VALUE);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch (IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null");
		}
	}
	
	@Test 
	public void testSetNameTwice()
	{
		String NAME1 = "myParam1";
		String NAME2 = "myParam21";
		Parameter parameter = new Parameter(NAME1, VALUE);
		try
		{
			parameter.setName(NAME2);
			failBecauseExceptionWasNotThrown(IllegalStateException.class);
		}
		catch (IllegalStateException e)
		{
			assertThat(e.getMessage()).isEqualTo("Assigning name \"" + NAME2 + "\" to Term already named \"" + NAME1 + "\" not allowed");
		}
	}
	
	@Test 
	public void testBackup()
	{
		// Backup if Parameter id and is non-zero and passed id matches is zero
		Parameter parameter = new Parameter(NAME, VALUE);
		assertThat(parameter.backup(0)).isFalse();
		parameter = new Parameter(NAME, VALUE);
		assertThat(parameter.backup(1)).isFalse();
		parameter = new Parameter(NAME, VALUE);
		parameter.id = 1;
		assertThat(parameter.backup(2)).isFalse();
		parameter = new Parameter(NAME, VALUE);
		parameter.id = 1;
		assertThat(parameter.backup(1)).isTrue();
		assertThat(parameter.isEmpty()).isTrue();
		assertThat(parameter.getValue()).isInstanceOf(Null.class);
		assertThat(parameter.id).isEqualTo(0);
		parameter = new Parameter(NAME, VALUE);
		parameter.id = 1;
		assertThat(parameter.backup(0)).isTrue();
		assertThat(parameter.isEmpty()).isTrue();
		assertThat(parameter.getValue()).isInstanceOf(Null.class);
		assertThat(parameter.id).isEqualTo(0);
	}
	
	@Test 
	public void testUnifySuccess()
	{
		// Case 1 - Unify called on empty Parameter with non-empty other
		Parameter otherParam = mock(Parameter.class);
		when(otherParam.isEmpty()).thenReturn(false);
		when(otherParam.getValue()).thenReturn(VALUE);
		Parameter parameter = new Parameter(NAME);
		parameter.id = 1;
		assertThat(parameter.unifyTerm(otherParam, 2)).isEqualTo(2);
		assertThat(parameter.getValue()).isEqualTo(VALUE);
		assertThat(parameter.id).isEqualTo(2);
		assertThat(parameter.isEmpty()).isFalse();
		// Case 2 - Unify called on non-empty Parameter with empty other.
		// Other gets unify() call
		Parameter parameter2 = new Parameter(NAME, VALUE);
		parameter2.id = 1;
		Parameter otherParam2 = mock(Parameter.class);
		when(otherParam2.isEmpty()).thenReturn(true);
		when(otherParam2.unifyTerm(parameter2, 2)).thenReturn(2);
		assertThat(parameter2.unifyTerm(otherParam2, 2)).isEqualTo(2);
		assertThat(parameter2.getValue()).isEqualTo(VALUE);
		assertThat(parameter2.id).isEqualTo(1);
	}

	@Test 
	public void testUnifyFail()
	{
		// Case 1 - both non-empty
		Parameter otherParam = mock(Parameter.class);
		when(otherParam.isEmpty()).thenReturn(false);
		Parameter parameter = new Parameter(NAME, VALUE);
		parameter.id = 1;
		assertThat(parameter.unifyTerm(otherParam, 2)).isEqualTo(0);
		assertThat(parameter.getValue()).isEqualTo(VALUE);
		assertThat(parameter.id).isEqualTo(1);
		assertThat(parameter.isEmpty()).isFalse();
		// Case 2 - both empty
		Parameter parameter2 = new Parameter(NAME);
		Parameter otherParam2 = mock(Parameter.class);
		when(otherParam2.isEmpty()).thenReturn(true);
		assertThat(parameter2.unifyTerm(otherParam2, 2)).isEqualTo(0);
		assertThat(parameter2.getValue()).isInstanceOf(Null.class);
		assertThat(parameter2.id).isEqualTo(0);
	}
	
	@Test 
	public void testToString()
	{
		Parameter parameter = new Parameter(NAME, VALUE);
		assertThat(parameter.toString()).isEqualTo(NAME + " = " + VALUE);
		Parameter parameter2 = new Parameter(NAME);
		assertThat(parameter2.toString()).isEqualTo(NAME + " = <empty>");
		Parameter parameter3 = new Parameter(Term.ANONYMOUS, VALUE);
		assertThat(parameter3.toString()).isEqualTo(VALUE);
		Parameter parameter4 = new Parameter(Term.ANONYMOUS);
		assertThat(parameter4.toString()).isEqualTo("null");
	}
	
	@Test 
	public void testEquals()
	{
		Parameter parameter = new Parameter(NAME, VALUE);
		assertThat(parameter.equals(null)).isFalse();
		assertThat(parameter.equals(VALUE)).isFalse();
		assertThat(parameter.equals(new Parameter(Term.ANONYMOUS))).isFalse();
		assertThat(parameter.equals(new Parameter(Term.ANONYMOUS, VALUE))).isTrue();
		Parameter parameter2 = new Parameter(NAME);
		assertThat(parameter2.equals(new Parameter(NAME, VALUE))).isFalse();
	}
}
