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

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import dagger.Module;
import dagger.Provides;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;

/**
 * ScopeTest
 * @author Andrew Bowley
 * 16 Feb 2015
 */
public class ScopeTest 
{
	@Module(injects = ParserAssembler.ExternalAxiomSource.class)
	static class ScopeModule implements ApplicationModule
	{
	    @Provides @Singleton ProviderManager provideProviderManager()
	    {
	    	return new ProviderManager();
	    }
	}

	private static final String AXIOM_KEY = "AxiomKey";
	private static final String TEMPLATE_NAME = "TemplateName";
	private static final String SCOPE_NAME = "ScopeName";

	@Before
	public void setUp()
	{
		new DI(new ScopeModule());
	}
	

	@Test
	public void test_internal_global_scope()
	{
		Scope globalScope = mock(Scope.class);
		Scope scope = new Scope(globalScope, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		AxiomSource axiomSource = mock(AxiomSource.class);
		when(globalParserAssembler.getAxiomSource(AXIOM_KEY)).thenReturn(axiomSource);
		Template template = mock(Template.class);
		when(globalParserAssembler.getTemplate(TEMPLATE_NAME)).thenReturn(template);
		assertThat(scope.getAxiomSource(AXIOM_KEY)).isEqualTo(axiomSource);
		assertThat(scope.getTemplate(TEMPLATE_NAME)).isEqualTo(template);
		scope.getParserAssembler().createAxiom(AXIOM_KEY);
		scope.getParserAssembler().createTemplate(TEMPLATE_NAME);
	}
	
	@Test
	public void test_global_scope_precidence()
	{
		Scope globalScope = mock(Scope.class);
		Scope scope = new Scope(globalScope, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		scope.getParserAssembler().createAxiom(AXIOM_KEY);
		scope.getParserAssembler().addAxiom(AXIOM_KEY, new Parameter("x"));
		scope.getParserAssembler().saveAxiom(AXIOM_KEY);
		scope.getParserAssembler().createTemplate(TEMPLATE_NAME);
		assertThat(scope.getAxiomSource(AXIOM_KEY).iterator().next()).isNotNull();
		assertThat(scope.getTemplate(TEMPLATE_NAME)).isNotNull();
		verify(globalParserAssembler, times(0)).getAxiomSource(AXIOM_KEY);
		verify(globalParserAssembler, times(0)).getTemplate(TEMPLATE_NAME);
	}

	@Test
	public void test_missing_axiom_source()
	{
		Scope scope = new Scope(SCOPE_NAME);
		try
		{
			scope.getAxiomSource(AXIOM_KEY);
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
		Scope scope = new Scope(SCOPE_NAME);
		try
		{
			scope.getTemplate(TEMPLATE_NAME);
		    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Template \"" + TEMPLATE_NAME + "\" does not exist");
		}
	}

	@Test
	public void test_get_null_AxiomListenerMap()
	{
		Scope scope = new Scope(SCOPE_NAME);
		assertThat(scope.getAxiomListenerMap()).isNull();
	}
	
	@Test
	public void test_get_global_only_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<String, List<AxiomListener>> axiomListenerMap = new HashMap<String, List<AxiomListener>>();
		AxiomListener axiomListener = mock(AxiomListener.class);
		axiomListenerMap.put(AXIOM_KEY, Collections.singletonList(axiomListener));
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(globalScope, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		assertThat(scope.getAxiomListenerMap().get(AXIOM_KEY).get(0)).isEqualTo(axiomListener);
	}

	@Test
	public void test_merge_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<String, List<AxiomListener>> axiomListenerMap = new HashMap<String, List<AxiomListener>>();
		AxiomListener axiomListener = mock(AxiomListener.class);
		axiomListenerMap.put(AXIOM_KEY, Collections.singletonList(axiomListener));
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(globalScope, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		AxiomListener axiomListener2 = mock(AxiomListener.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomTermList.getAxiomListener()).thenReturn(axiomListener2);
		when(axiomTermList.getKey()).thenReturn(AXIOM_KEY + 1);
		scope.getParserAssembler().registerAxiomTermList(axiomTermList);

		assertThat(scope.getAxiomListenerMap().get(AXIOM_KEY).get(0)).isEqualTo(axiomListener);
		assertThat(scope.getAxiomListenerMap().get(AXIOM_KEY + 1).get(0)).isEqualTo(axiomListener2);
	}

	@Test
	public void test_empty_global_map_AxiomListenerMap()
	{
		Scope globalScope = mock(Scope.class);
		ParserAssembler globalParserAssembler = mock(ParserAssembler.class);
		when(globalScope.getParserAssembler()).thenReturn(globalParserAssembler);
		Map<String, List<AxiomListener>> axiomListenerMap = new HashMap<String, List<AxiomListener>>();
		when(globalParserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		Scope scope = new Scope(globalScope, SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		AxiomListener axiomListener2 = mock(AxiomListener.class);
		AxiomTermList axiomTermList = mock(AxiomTermList.class);
		when(axiomTermList.getAxiomListener()).thenReturn(axiomListener2);
		when(axiomTermList.getKey()).thenReturn(AXIOM_KEY + 1);
		scope.getParserAssembler().registerAxiomTermList(axiomTermList);
		assertThat(scope.getAxiomListenerMap().get(AXIOM_KEY + 1).get(0)).isEqualTo(axiomListener2);
	}

	@Test
	public void test_add_scope_locale()
	{
		Scope globalScope = mock(Scope.class);
		// ja_JP_JP_#u-ca-japanese
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(QueryProgram.LANGUAGE, "ja");
		properties.put(QueryProgram.REGION, "JP");
		properties.put(QueryProgram.VARIANT, "JP");
		Scope scope = new Scope(globalScope, SCOPE_NAME, properties);
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
