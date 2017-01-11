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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * TemplateTest
 * @author Andrew Bowley
 * 11 Dec 2014
 */
public class TemplateTest 
{
	final static String NAME = "myStruct";
	final static String KEY = "myKey";
	
	Operand parameter1;
	Operand parameter2;

	@Test
	public void test_Constructor_terms_list()
	{
		Template testTemplate = new Template(parseTemplateName(NAME), getTermList());
		int id = testTemplate.getId();
		assertThat(testTemplate.getName()).isEqualTo(NAME);
		assertThat(testTemplate.getKey()).isEqualTo(NAME);
		assertThat(id).isEqualTo(Template.referenceCount.get());
		assertThat(testTemplate.termMap.get("PARAMETER1")).isEqualTo(parameter1);
		assertThat(testTemplate.termMap.get("PARAMETER2")).isEqualTo(parameter2);
		testTemplate.evaluate();
		verify(parameter1).evaluate(id);
		verify(parameter2).evaluate(id);
	}
	
	@Test
	public void test_Constructor_terms_list_key()
	{
		Template testTemplate = new Template(KEY, parseTemplateName(NAME), getTermList());
		int id = testTemplate.getId();
		assertThat(testTemplate.getName()).isEqualTo(NAME);
		assertThat(testTemplate.getKey()).isEqualTo(KEY);
		assertThat(id).isEqualTo(Template.referenceCount.get());
		assertThat(testTemplate.termMap.get("PARAMETER1")).isEqualTo(parameter1);
		assertThat(testTemplate.termMap.get("PARAMETER2")).isEqualTo(parameter2);
		testTemplate.evaluate();
		verify(parameter1).evaluate(id);
		verify(parameter2).evaluate(id);
	}
	
	@Test
	public void test_Constructor_terms_array()
	{
		Template testTemplate = new Template(parseTemplateName(NAME), getParameterArray());
		int id = testTemplate.getId();
		assertThat(testTemplate.getName()).isEqualTo(NAME);
		assertThat(testTemplate.getKey()).isEqualTo(NAME);
		assertThat(id).isEqualTo(Template.referenceCount.get());
		assertThat(testTemplate.termMap.get("PARAMETER1")).isEqualTo(parameter1);
		assertThat(testTemplate.termMap.get("PARAMETER2")).isEqualTo(parameter2);
		testTemplate.evaluate();
		verify(parameter1).evaluate(id);
		verify(parameter2).evaluate(id);
	}
	
	@Test
	public void test_Constructor_terms_array_key()
	{
		Template testTemplate = new Template(KEY, parseTemplateName(NAME), getParameterArray());
		int id = testTemplate.getId();
		assertThat(testTemplate.getName()).isEqualTo(NAME);
		assertThat(testTemplate.getKey()).isEqualTo(KEY);
		assertThat(id).isEqualTo(Template.referenceCount.get());
		assertThat(testTemplate.termMap.get("PARAMETER1")).isEqualTo(parameter1);
		assertThat(testTemplate.termMap.get("PARAMETER2")).isEqualTo(parameter2);
		testTemplate.evaluate();
		verify(parameter1).evaluate(id);
		verify(parameter2).evaluate(id);
	}
	
	@Test
	public void test_Constructor_no_terms()
	{
		List<Operand> paramList = new ArrayList<Operand>();
		try
		{
			new Template(parseTemplateName(NAME), paramList);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch (IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"termList\" is empty");
	    }
		try
		{
			new Template(parseTemplateName(NAME), new Operand[0]);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch (IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"terms\" is empty");
	    }
	}

	@Test
	public void test_backup()
	{
		List<Operand> paramList = new ArrayList<Operand>();
		Operand parameter = mock(Operand.class);
		when(parameter.getName()).thenReturn("test-parameter1");
		paramList.add(parameter);
		Template testTemplate = new Template(parseTemplateName(NAME), paramList);	
		when(parameter.backup(Template.referenceCount.get())).thenReturn(true);
		assertThat(testTemplate.backup(true)).isTrue();
		when(parameter.backup(Template.referenceCount.get())).thenReturn(false);
		assertThat(testTemplate.backup(true)).isFalse();
		when(parameter.backup(0)).thenReturn(true);
		assertThat(testTemplate.backup(false)).isTrue();
		when(parameter.backup(0)).thenReturn(false);
		assertThat(testTemplate.backup(false)).isFalse();
		Operand parameter2 = mock(Operand.class);
		when(parameter2.getName()).thenReturn("test-parameter2");
		paramList.add(parameter2);
		testTemplate = new Template(parseTemplateName(NAME) ,paramList);	
		when(parameter2.backup(Template.referenceCount.get())).thenReturn(false);
		when(parameter.backup(Template.referenceCount.get())).thenReturn(true);
		assertThat(testTemplate.backup(true)).isTrue();
		when(parameter.backup(Template.referenceCount.get())).thenReturn(false);
		assertThat(testTemplate.backup(true)).isFalse();
		when(parameter2.backup(0)).thenReturn(false);
		when(parameter.backup(0)).thenReturn(true);
		assertThat(testTemplate.backup(false)).isTrue();
		when(parameter.backup(0)).thenReturn(false);
		assertThat(testTemplate.backup(false)).isFalse();
	}

	@Test
	public void test_setKey()
	{
		Template testTemplate = new Template(KEY, parseTemplateName(NAME), getTermList());
		testTemplate.setKey(KEY +"!");
		assertThat(testTemplate.getKey()).isEqualTo(KEY+"!");
	}
	
	protected List<Operand> getTermList()
	{
		List<Operand> paramList = new ArrayList<Operand>();
		parameter1 = mock(Operand.class);
		when(parameter1.getName()).thenReturn("parameter1");
		when(parameter1.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
		parameter2 = mock(Operand.class);
		when(parameter2.getName()).thenReturn("parameter2");
		when(parameter2.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
		paramList.add(parameter1);
		paramList.add(parameter2);
		return paramList;
	}
	
	protected Operand[] getParameterArray()
	{
	    Operand[] paramArray = new Operand[2];
		parameter1 = mock(Operand.class);
		when(parameter1.getName()).thenReturn("parameter1");
		when(parameter1.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
		parameter2 = mock(Operand.class);
		when(parameter2.getName()).thenReturn("parameter2");
		when(parameter2.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
		paramArray[0] = parameter1;
		paramArray[1] = parameter2;
		return paramArray;
	}

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }
}
