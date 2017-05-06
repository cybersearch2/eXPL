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

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.operator.IntegerOperator;
import au.com.cybersearch2.classy_logic.operator.StringOperator;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * TemplateTest
 * @author Andrew Bowley
 * 11 Dec 2014
 */
public class TemplateTest 
{
	final static String NAME = "myStruct";
	final static String KEY = "myKey";

	TemplateArchetype templateArchitype;
	Operand parameter1;
	Operand parameter2;
	Operator operator1;
	Operator operator2;

	@Before
	public void setUp()
	{
	    operator1 = new StringOperator();
	    operator2 = new IntegerOperator();
	    templateArchitype = mock(TemplateArchetype.class);
	    when(templateArchitype.getQualifiedName()).thenReturn(parseTemplateName(NAME));
	    when(templateArchitype.getName()).thenReturn(NAME);
	    when(templateArchitype.isMutable()).thenReturn(true);
	}
	
	@Test
	public void test_Constructor_terms_list()
	{
	    TermMetaData termMetaData1 = mock(TermMetaData.class);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
	    when(templateArchitype.analyseTerm(parameter1, 0)).thenReturn(termMetaData1);
        when(templateArchitype.analyseTerm(parameter2, 1)).thenReturn(termMetaData2);
		Template testTemplate = new Template(templateArchitype, getTermList());
		int id = testTemplate.getId();
		assertThat(testTemplate.getName()).isEqualTo(NAME);
		assertThat(testTemplate.getKey()).isEqualTo(NAME);
		assertThat(id).isEqualTo(Template.referenceCount.get());
		verify(templateArchitype, times(2)).addTerm(isA(TermMetaData.class));
		verify(templateArchitype).clearMutable();
		testTemplate.evaluate();
		verify(parameter1).evaluate(id);
		verify(parameter2).evaluate(id);
	}
	
	@Test
	public void test_Constructor_terms_list_key()
	{
        TermMetaData termMetaData1 = mock(TermMetaData.class);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter1, 0)).thenReturn(termMetaData1);
        when(templateArchitype.analyseTerm(parameter2, 1)).thenReturn(termMetaData2);
        Template testTemplate = new Template(KEY, templateArchitype, getTermList());
        int id = testTemplate.getId();
        assertThat(testTemplate.getName()).isEqualTo(NAME);
        assertThat(testTemplate.getKey()).isEqualTo(KEY);
        assertThat(id).isEqualTo(Template.referenceCount.get());
        verify(templateArchitype, times(2)).addTerm(isA(TermMetaData.class));
        verify(templateArchitype).clearMutable();
        testTemplate.evaluate();
        verify(parameter1).evaluate(id);
        verify(parameter2).evaluate(id);
	}
		
	@Test
	public void test_Constructor_terms_array()
	{
        TermMetaData termMetaData1 = mock(TermMetaData.class);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter1, 0)).thenReturn(termMetaData1);
        when(templateArchitype.analyseTerm(parameter2, 1)).thenReturn(termMetaData2);
        Template testTemplate = new Template(templateArchitype, getParameterArray());
        int id = testTemplate.getId();
        assertThat(testTemplate.getName()).isEqualTo(NAME);
        assertThat(testTemplate.getKey()).isEqualTo(NAME);
        assertThat(id).isEqualTo(Template.referenceCount.get());
        verify(templateArchitype, times(2)).addTerm(isA(TermMetaData.class));
        verify(templateArchitype).clearMutable();
        testTemplate.evaluate();
        verify(parameter1).evaluate(id);
        verify(parameter2).evaluate(id);
	}
		
	@Test
	public void test_Constructor_terms_array_key()
	{
        TermMetaData termMetaData1 = mock(TermMetaData.class);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter1, 0)).thenReturn(termMetaData1);
        when(templateArchitype.analyseTerm(parameter2, 1)).thenReturn(termMetaData2);
        Template testTemplate = new Template(KEY, templateArchitype, getParameterArray());
        int id = testTemplate.getId();
        assertThat(testTemplate.getName()).isEqualTo(NAME);
        assertThat(testTemplate.getKey()).isEqualTo(KEY);
        assertThat(id).isEqualTo(Template.referenceCount.get());
        verify(templateArchitype, times(2)).addTerm(isA(TermMetaData.class));
        verify(templateArchitype).clearMutable();
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
			new Template(templateArchitype, paramList);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch (IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"terms\" is empty");
	    }
		try
		{
			new Template(templateArchitype, new Operand[0]);
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
        when(parameter.getOperator()).thenReturn(operator1);
		paramList.add(parameter);
        TermMetaData termMetaData1 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter, 0)).thenReturn(termMetaData1);
		Template testTemplate = new Template(templateArchitype, paramList);	
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
        when(parameter2.getOperator()).thenReturn(operator1);
		paramList.add(parameter2);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter2, 0)).thenReturn(termMetaData2);
		testTemplate = new Template(templateArchitype ,paramList);	
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
        TermMetaData termMetaData1 = mock(TermMetaData.class);
        TermMetaData termMetaData2 = mock(TermMetaData.class);
        when(templateArchitype.analyseTerm(parameter1, 0)).thenReturn(termMetaData1);
        when(templateArchitype.analyseTerm(parameter2, 1)).thenReturn(termMetaData2);
		Template testTemplate = new Template(KEY, templateArchitype, getTermList());
		testTemplate.setKey(KEY +"!");
		assertThat(testTemplate.getKey()).isEqualTo(KEY+"!");
	}
	
	protected List<Operand> getTermList()
	{
		List<Operand> paramList = new ArrayList<Operand>();
		parameter1 = mock(Operand.class);
		when(parameter1.getName()).thenReturn("parameter1");
		when(parameter1.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
		when(parameter1.getOperator()).thenReturn(operator1);
		parameter2 = mock(Operand.class);
		when(parameter2.getName()).thenReturn("parameter2");
		when(parameter2.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
        when(parameter2.getOperator()).thenReturn(operator2);
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
        when(parameter1.getOperator()).thenReturn(operator1);
		parameter2 = mock(Operand.class);
		when(parameter2.getName()).thenReturn("parameter2");
		when(parameter2.evaluate(anyInt())).thenReturn(EvaluationStatus.COMPLETE);
        when(parameter2.getOperator()).thenReturn(operator2);
		paramArray[0] = parameter1;
		paramArray[1] = parameter2;
		return paramArray;
	}

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }
}
