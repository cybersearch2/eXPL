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
package au.com.cybersearch2.classy_logic;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;

/**
 * QueryParamsTest
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class QueryParamsTest 
{
	private static final String QUERY_SPEC_NAME = "QuerySpec";
	private static final String AXIOM_KEY = "AxiomKey";
	private static final String TEMPLATE_NAME = "TemplateName";
    private static final QualifiedName Q_AXIOM_KEY = new QualifiedName("", AXIOM_KEY);
    private static final QualifiedName Q_TEMPLATE_NAME = new QualifiedTemplateName("", TEMPLATE_NAME);

	@Test
	public void test_set_parameters_from_scope()
	{
		Scope scope = mock(Scope.class);
		AxiomSource axiomSource = mock(AxiomSource.class);
		Template template = mock(Template.class);
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		when(keyname.getAxiomKey()).thenReturn(Q_AXIOM_KEY);
		when(keyname.getTemplateName()).thenReturn(Q_TEMPLATE_NAME);
		when(scope.findAxiomSource(Q_AXIOM_KEY)).thenReturn(axiomSource);
		when(scope.getTemplate(Q_TEMPLATE_NAME)).thenReturn(template);
		querySpec.addKeyName(keyname);
		QueryParams queryParams = new QueryParams(scope, querySpec);
		queryParams.initialize();
		assertThat(queryParams.getAxiomCollection().getAxiomSource(AXIOM_KEY)).isEqualTo(axiomSource);
		assertThat(queryParams.getTemplateList().get(0)).isEqualTo(template);
	}

	@Test
	public void test_set_parameters_from_scope_no_axiom()
	{
		Scope scope = mock(Scope.class);
		Template template = mock(Template.class);
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		QualifiedName emptyAxiomName = new QualifiedName("");
		when(keyname.getAxiomKey()).thenReturn(emptyAxiomName);
	    when(keyname.getTemplateName()).thenReturn(Q_TEMPLATE_NAME);
		when(scope.getTemplate(Q_TEMPLATE_NAME)).thenReturn(template);
		querySpec.addKeyName(keyname);
		QueryParams queryParams = new QueryParams(scope, querySpec);
		queryParams.initialize();
		assertThat(queryParams.getTemplateList().get(0)).isEqualTo(template);
		verify(scope, never()).getAxiomSource("");
		verify(template, never()).setKey("");
	}

}
