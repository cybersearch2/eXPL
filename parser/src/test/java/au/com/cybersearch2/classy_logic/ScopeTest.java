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
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ScopeTest
 * @author Andrew Bowley
 * 16 Feb 2015
 */
public class ScopeTest 
{
	static Map<QualifiedName, List<AxiomListener>> EMPTY_AXIOM_LISTENER_MAP = Collections.emptyMap();
	
	private static final String AXIOM_KEY = "AxiomKey";
	private static final String TEMPLATE_NAME = "TemplateName";
	private static final String SCOPE_NAME = "ScopeName";
    static QualifiedName Q_AXIOM_NAME = new QualifiedName(SCOPE_NAME, AXIOM_KEY);
    static QualifiedName Q_AXIOM1_NAME = new QualifiedName(SCOPE_NAME, AXIOM_KEY + 1);
    static QualifiedName Q_TEMPLATE_NAME = new QualifiedTemplateName(SCOPE_NAME, TEMPLATE_NAME);
    static QualifiedName GLOBAL_Q_AXIOM_NAME = new QualifiedName(QualifiedName.EMPTY, AXIOM_KEY);
    static QualifiedName GLOBAL_Q_TEMPLATE_NAME = new QualifiedTemplateName(QualifiedName.EMPTY, TEMPLATE_NAME);

	@Before
	public void setUp()
	{
    }
	

	@Test
	public void test_internal_global_scope()
	{
		Scope globalScope = mock(Scope.class);
        Map<String, Scope> scopeMap = new HashMap<String, Scope>();
        scopeMap.put(QueryProgram.GLOBAL_SCOPE, globalScope);
        Scope scope = new Scope(scopeMap, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
        scopeMap.put(SCOPE_NAME, scope);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		AxiomSource axiomSource = mock(AxiomSource.class);
		when(globalParserAssembler.getAxiomSource(GLOBAL_Q_AXIOM_NAME)).thenReturn(axiomSource);
		Template template = mock(Template.class);
		when(globalParserAssembler.getTemplate(QualifiedName.parseTemplateName(TEMPLATE_NAME))).thenReturn(template);
		assertThat(scope.getAxiomSource(GLOBAL_Q_AXIOM_NAME)).isEqualTo(axiomSource);
		assertThat(scope.getTemplate(Q_TEMPLATE_NAME)).isEqualTo(template);
		scope.getParserAssembler().createAxiom(Q_AXIOM_NAME);
		scope.getParserAssembler().createTemplate(QualifiedName.parseTemplateName(TEMPLATE_NAME), false);
	}
	
	@Test
	public void test_global_scope_precedence()
	{
		Scope globalScope = mock(Scope.class);
		Map<String, Scope> scopeMap = new HashMap<String, Scope>();
		scopeMap.put(QueryProgram.GLOBAL_SCOPE, globalScope);
		Scope scope = new Scope(scopeMap, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		scopeMap.put(SCOPE_NAME, scope);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		scope.getParserAssembler().createAxiom(Q_AXIOM_NAME);
		scope.getParserAssembler().addAxiom(Q_AXIOM_NAME, new Parameter("x"));
		scope.getParserAssembler().saveAxiom(Q_AXIOM_NAME);
		scope.getParserAssembler().createTemplate(new QualifiedTemplateName(SCOPE_NAME, TEMPLATE_NAME), false);
		assertThat(scope.getAxiomSource(Q_AXIOM_NAME).iterator().next()).isNotNull();
		assertThat(scope.getTemplate(Q_TEMPLATE_NAME)).isNotNull();
		verify(globalParserAssembler, times(0)).getAxiomSource(Q_AXIOM_NAME);
		verify(globalParserAssembler, times(0)).getTemplate(new QualifiedTemplateName(QualifiedName.EMPTY, TEMPLATE_NAME));
	}

	@Test
	public void test_missing_axiom_source()
	{
        Scope globalScope = mock(Scope.class);
        ParserAssembler parserAssembler = mock(ParserAssembler.class);
        when(parserAssembler.getAxiomSource(Q_AXIOM_NAME)).thenReturn(null);
        when(globalScope.getParserAssembler()).thenReturn(parserAssembler);
        Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		try
		{
			scope.getAxiomSource(GLOBAL_Q_AXIOM_NAME);
		    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Axiom \"" + AXIOM_KEY + "\" does not exist");
		}
	}

	@Test
	public void test_missing_template()
	{
        Scope globalScope = mock(Scope.class);
        ParserAssembler parserAssembler = mock(ParserAssembler.class);
        when(parserAssembler.getTemplate(QualifiedName.parseTemplateName(TEMPLATE_NAME))).thenReturn(null);
        when(globalScope.getParserAssembler()).thenReturn(parserAssembler);
        Map<String, Scope> scopeMap = new HashMap<String, Scope>();
        scopeMap.put(QueryProgram.GLOBAL_SCOPE, globalScope);
        Scope scope = new Scope(scopeMap, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
        scopeMap.put(SCOPE_NAME, scope);
		try
		{
			scope.getTemplate(Q_TEMPLATE_NAME);
		    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Template \"" + SCOPE_NAME + "." + TEMPLATE_NAME + "\" does not exist");
		}
	}

	@Test
	public void test_get_null_AxiomListenerMap()
	{
        Scope globalScope = mock(Scope.class);
        ParserAssembler parserAssembler = mock(ParserAssembler.class);
        when(parserAssembler.getAxiomListenerMap()).thenReturn(EMPTY_AXIOM_LISTENER_MAP);
        when(globalScope.getParserAssembler()).thenReturn(parserAssembler);
        Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		assertThat(scope.getAxiomListenerMap()).isNull();
	}
	
	@Test
	public void test_get_global_only_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		AxiomListener axiomListener = mock(AxiomListener.class);
		axiomListenerMap.put(Q_AXIOM_NAME, Collections.singletonList(axiomListener));
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		assertThat(scope.getAxiomListenerMap().get(Q_AXIOM_NAME).get(0)).isEqualTo(axiomListener);
	}

	@Test
	public void test_merge_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		AxiomListener axiomListener = mock(AxiomListener.class);
		axiomListenerMap.put(Q_AXIOM_NAME, Collections.singletonList(axiomListener));
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		AxiomListener axiomListener2 = mock(AxiomListener.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomTermList.getAxiomListener()).thenReturn(axiomListener2);
		when(axiomTermList.getKey()).thenReturn(GLOBAL_Q_AXIOM_NAME);
		QualifiedName qname = QualifiedName.parseName(AXIOM_KEY + 1);
		when(axiomTermList.getQualifiedName()).thenReturn(qname);
		scope.getParserAssembler().registerAxiomTermList(axiomTermList);

		assertThat(scope.getAxiomListenerMap().get(Q_AXIOM_NAME).get(0)).isEqualTo(axiomListener);
		assertThat(scope.getAxiomListenerMap().get(GLOBAL_Q_AXIOM_NAME).get(0)).isEqualTo(axiomListener2);
	}

	@Test
	public void test_empty_global_map_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		AxiomListener axiomListener2 = mock(AxiomListener.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomTermList.getAxiomListener()).thenReturn(axiomListener2);
        QualifiedName qname = QualifiedName.parseName(AXIOM_KEY + 1);
		when(axiomTermList.getKey()).thenReturn(GLOBAL_Q_AXIOM_NAME);
        when(axiomTermList.getQualifiedName()).thenReturn(qname);
		scope.getParserAssembler().registerAxiomTermList(axiomTermList);
		assertThat(scope.getAxiomListenerMap().get(GLOBAL_Q_AXIOM_NAME).get(0)).isEqualTo(axiomListener2);
	}

	@Ignore // TODO - investigate JRE behaviour
	@Test
	public void test_add_scope_locale()
	{
		Scope globalScope = mock(Scope.class);
		// ja_JP_JP_#u-ca-japanese
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(QueryProgram.LANGUAGE, "ja");
		properties.put(QueryProgram.REGION, "JP");
		properties.put(QueryProgram.VARIANT, "JP");
		Scope scope = new Scope(Collections.singletonMap(QueryProgram.GLOBAL_SCOPE, globalScope), SCOPE_NAME, properties);
		assertThat(scope.getLocale().toString()).isEqualTo("ja_JP_JP_#u-ca-japanese");
        // Uncomment following if SE7 supported
		// sr_BA_#Latn
		//properties = new HashMap<String, Object>();
		//properties = new HashMap<String, Object>();
		//properties.put(QueryProgram.LANGUAGE, "sr");
		//properties.put(QueryProgram.REGION, "BA");
		//properties.put(QueryProgram.SCRIPT, "Latn");
		//scope = new Scope(globalScope, SCOPE_NAME, properties);
		//assertThat(scope.getLocale().toString()).isEqualTo("sr_BA_#Latn");

	}
}
