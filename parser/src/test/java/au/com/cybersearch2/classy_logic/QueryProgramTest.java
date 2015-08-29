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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import dagger.Module;
import dagger.Provides;
import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.QueryType;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;

/**
 * QueryProgramTest
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class QueryProgramTest 
{
	@Module(injects = ParserAssembler.ExternalAxiomSource.class)
	static class QueryProgramModule implements ApplicationModule
	{
	    @Provides @Singleton ProviderManager provideProviderManager()
	    {
	    	return new ProviderManager();
	    }
	}
	
	private static final String AXIOM_KEY = "AxiomKey";
	protected QualifiedName Q_AXIOM_NAME = new QualifiedName(SCOPE_NAME, QualifiedName.EMPTY, AXIOM_KEY);
	private static final String AXIOM_KEY2 = "AxiomKey2";
    protected QualifiedName Q_AXIOM_NAME2 = new QualifiedName(SCOPE_NAME, QualifiedName.EMPTY, AXIOM_KEY2);
	private static final String TEMPLATE_NAME = "TemplateName";
	protected QualifiedName Q_TEMPLATE_NAME = new QualifiedName(SCOPE_NAME ,TEMPLATE_NAME, QualifiedName.EMPTY);
	private static final String TEMPLATE_NAME2 = "TemplateName2";
    protected QualifiedName Q_TEMPLATE_NAME2 = new QualifiedName(SCOPE_NAME ,TEMPLATE_NAME2, QualifiedName.EMPTY);;
	private static final String OPERAND_NAME = "OperandName";
	private static final String SCOPE_NAME = "ScopeName";
	private static final String QUERY_SPEC_NAME = "QuerySpec";
	private static final String VARIABLE_NAME = "VariableName";
    protected QualifiedName Q_VARIABLE_NAME = new QualifiedName(SCOPE_NAME, QualifiedName.EMPTY, VARIABLE_NAME);

	@Before
	public void setUp()
	{
		new DI(new QueryProgramModule());
	}
	
	@Test
	public void test_constructor()
	{
		QueryProgram queryProgram = new QueryProgram();
		assertThat(queryProgram.scopes).isNotNull();
		assertThat(queryProgram.getGlobalScope()).isNotNull();
	}

	@Test
	public void test_globalScope()
	{
	    QualifiedName GLOBAL_Q_AXIOM_NAME = QualifiedName.parseGlobalName(AXIOM_KEY);
	    QualifiedName GLOBAL_Q_TEMPLATE_NAME = QualifiedName.parseTemplateName(TEMPLATE_NAME);
		QueryProgram queryProgram = new QueryProgram();
		ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
		parserAssembler.createAxiom(GLOBAL_Q_AXIOM_NAME);
		parserAssembler.addAxiom(GLOBAL_Q_AXIOM_NAME, new Parameter("x", Integer.valueOf(3)));
		parserAssembler.saveAxiom(GLOBAL_Q_AXIOM_NAME);
        OperandMap operandMap = parserAssembler.getOperandMap();
		parserAssembler.createTemplate(GLOBAL_Q_TEMPLATE_NAME, false);
        operandMap.setQualifiedContextname(GLOBAL_Q_TEMPLATE_NAME);
		Template template = parserAssembler.getTemplate(GLOBAL_Q_TEMPLATE_NAME);
		operandMap.addOperand(OPERAND_NAME, null);
		assertThat(parserAssembler.getAxiomSource(GLOBAL_Q_AXIOM_NAME)).isNotNull();
		assertThat(parserAssembler.getTemplate(GLOBAL_Q_TEMPLATE_NAME)).isEqualTo(template);
		assertThat(parserAssembler.getOperandMap().get(QualifiedName.parseGlobalName(TEMPLATE_NAME + "." + OPERAND_NAME))).isNotNull();
	}
	
	@Test
	public void test_new_scope()
	{
		QueryProgram queryProgram = new QueryProgram();
		Map<String, Object> properties = new HashMap<String, Object>();
		Scope scope = queryProgram.scopeInstance(SCOPE_NAME, properties);
		assertThat(queryProgram.getScope(SCOPE_NAME)).isEqualTo(scope);
		try
		{
	        Map<String, Object> properties2 = new HashMap<String, Object>();
	        queryProgram.scopeInstance(SCOPE_NAME, properties2);
			failBecauseExceptionWasNotThrown(ExpressionException.class);
		}
		catch(ExpressionException e)
		{
			assertThat(e.getMessage()).isEqualTo("Scope named \"" + SCOPE_NAME + "\" already exists");
		}
        try
        {
            Map<String, Object> properties2 = new HashMap<String, Object>();
            queryProgram.scopeInstance(QueryProgram.GLOBAL_SCOPE, properties2);
            failBecauseExceptionWasNotThrown(ExpressionException.class);
        }
        catch(ExpressionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Scope name \"" + QueryProgram.GLOBAL_SCOPE + "\" is reserved");
        }
	}
	
	@Test
	public void test_execute_query()
	{
		QueryProgram queryProgram = new QueryProgram();
        Scope scope = queryProgram.scopeInstance(SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler parserAssembler = scope.getParserAssembler();
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		when(keyname.getAxiomKey()).thenReturn(AXIOM_KEY);
		when(keyname.getTemplateName()).thenReturn(TEMPLATE_NAME);
		parserAssembler.createAxiom(Q_AXIOM_NAME);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME, false);
		Variable variable = new Variable(Q_VARIABLE_NAME);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME, variable);
		Parameter param = new Parameter(VARIABLE_NAME, "eureka!");
		parserAssembler.addAxiom(Q_AXIOM_NAME, param);
		parserAssembler.saveAxiom(Q_AXIOM_NAME);
        OperandMap operandMap = parserAssembler.getOperandMap();
        operandMap.setQualifiedContextname(Q_TEMPLATE_NAME);
		operandMap.addOperand(OPERAND_NAME, null);
		querySpec.addKeyName(keyname);
		scope.addQuerySpec(querySpec);
		SolutionHandler solutionHandler = new SolutionHandler(){

			@Override
			public boolean onSolution(Solution solution) 
			{
				assertThat(solution.getString(Q_TEMPLATE_NAME.toString(), VARIABLE_NAME)).isEqualTo("eureka!");
				return true;
			}};
		queryProgram.executeQuery(SCOPE_NAME, QUERY_SPEC_NAME, solutionHandler);
		assertThat(variable.isEmpty()).isTrue();
		
	}
	
	@Test
	public void test_execute_logic_chainquery()
	{
		QueryProgram queryProgram = new QueryProgram();
        Scope scope = queryProgram.scopeInstance(SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler parserAssembler = scope.getParserAssembler();
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		when(keyname.getAxiomKey()).thenReturn(AXIOM_KEY);
		when(keyname.getTemplateName()).thenReturn(TEMPLATE_NAME);
		parserAssembler.createAxiom(Q_AXIOM_NAME);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME, false);
		Variable variable = new Variable(Q_VARIABLE_NAME);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME, variable);
		Parameter param = new Parameter(VARIABLE_NAME, "eureka!");
		parserAssembler.addAxiom(Q_AXIOM_NAME, param);
		parserAssembler.saveAxiom(Q_AXIOM_NAME);
		querySpec.addKeyName(keyname);
		QuerySpec querySpec2 = querySpec.chain();
		KeyName keyname2 = mock(KeyName.class);
		when(keyname2.getAxiomKey()).thenReturn(AXIOM_KEY2);
		when(keyname2.getTemplateName()).thenReturn(TEMPLATE_NAME2);
		parserAssembler.createAxiom(Q_AXIOM_NAME2);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME2, false);
		Variable variable2 = new Variable(Q_VARIABLE_NAME);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME2, variable2);
		Parameter param2 = new Parameter(VARIABLE_NAME, "eureka2!");
		parserAssembler.addAxiom(Q_AXIOM_NAME2, param2);
		parserAssembler.saveAxiom(Q_AXIOM_NAME2);
		querySpec2.addKeyName(keyname2);
		scope.addQuerySpec(querySpec);
		SolutionHandler solutionHandler = new SolutionHandler(){

			@Override
			public boolean onSolution(Solution solution) 
			{
				assertThat(solution.getString(Q_TEMPLATE_NAME.toString(), VARIABLE_NAME)).isEqualTo("eureka!");
				assertThat(solution.getString(Q_TEMPLATE_NAME2.toString(), VARIABLE_NAME)).isEqualTo("eureka2!");
				return true;
			}};
		queryProgram.executeQuery(SCOPE_NAME, QUERY_SPEC_NAME, solutionHandler);
		assertThat(variable.isEmpty()).isTrue();
		
	}
	
	@Test
	public void test_execute_calculate_chainquery()
	{
		QueryProgram queryProgram = new QueryProgram();
		Scope scope = queryProgram.scopeInstance(SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler parserAssembler = scope.getParserAssembler();
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		when(keyname.getAxiomKey()).thenReturn(AXIOM_KEY);
		when(keyname.getTemplateName()).thenReturn(TEMPLATE_NAME);
		parserAssembler.createAxiom(Q_AXIOM_NAME);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME, false);
		Variable variable = new Variable(Q_VARIABLE_NAME);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME, variable);
		Parameter param = new Parameter(VARIABLE_NAME, "eureka!");
		parserAssembler.addAxiom(Q_AXIOM_NAME, param);
		parserAssembler.saveAxiom(Q_AXIOM_NAME);
		querySpec.addKeyName(keyname);
		// Chain to calculatorTEMPLATE_NAME2
		QuerySpec querySpec2 = querySpec.chain();
		KeyName keyname2 = mock(KeyName.class);
		when(keyname2.getAxiomKey()).thenReturn("");
		when(keyname2.getTemplateName()).thenReturn(TEMPLATE_NAME2);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME2, true);
		Variable variable2 = new Variable(Q_VARIABLE_NAME);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME2, variable2);
		Template template2 = parserAssembler.getTemplate(Q_TEMPLATE_NAME2);
		template2.putInitData(VARIABLE_NAME, "eureka2!");
		querySpec2.addKeyName(keyname2);
		querySpec2.setQueryType(QueryType.calculator);
		scope.addQuerySpec(querySpec);
		SolutionHandler solutionHandler = new SolutionHandler(){

			@Override
			public boolean onSolution(Solution solution) 
			{
				assertThat(solution.getString(Q_TEMPLATE_NAME.toString(), VARIABLE_NAME)).isEqualTo("eureka!");
				assertThat(solution.getString(Q_TEMPLATE_NAME2.toString(), VARIABLE_NAME)).isEqualTo("eureka2!");
				return true;
			}};
		queryProgram.executeQuery(SCOPE_NAME, QUERY_SPEC_NAME, solutionHandler);
		assertThat(variable.isEmpty()).isTrue();
		
	}

	@Test
	public void test_execute_calculate_no_axiom_chainquery()
	{
		QueryProgram queryProgram = new QueryProgram();
        Scope scope = queryProgram.scopeInstance(SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		ParserAssembler parserAssembler = scope.getParserAssembler();
		QuerySpec querySpec = new QuerySpec(QUERY_SPEC_NAME);
		KeyName keyname = mock(KeyName.class);
		when(keyname.getAxiomKey()).thenReturn(AXIOM_KEY);
		when(keyname.getTemplateName()).thenReturn(TEMPLATE_NAME);
		parserAssembler.createAxiom(Q_AXIOM_NAME);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME, false);
		Variable variable = new Variable(Q_VARIABLE_NAME);
		variable.assign(Integer.valueOf(0));
		parserAssembler.addTemplate(Q_TEMPLATE_NAME, variable);
		Parameter param = new Parameter(VARIABLE_NAME, "eureka!");
		parserAssembler.addAxiom(Q_AXIOM_NAME, param);
		parserAssembler.saveAxiom(Q_AXIOM_NAME);
		parserAssembler.getOperandMap().addOperand(OPERAND_NAME, null);
		querySpec.addKeyName(keyname);
		QuerySpec querySpec2 = querySpec.chain();
		KeyName keyname2 = mock(KeyName.class);
		when(keyname2.getAxiomKey()).thenReturn("");
		when(keyname2.getTemplateName()).thenReturn(TEMPLATE_NAME2);
		Variable variable2 = new Variable(QualifiedName.parseGlobalName(TEMPLATE_NAME + "." + VARIABLE_NAME));
        Variable variable3 = new Variable(QualifiedName.parseName(VARIABLE_NAME + "3"), variable2);
		parserAssembler.createTemplate(Q_TEMPLATE_NAME2, true);
		parserAssembler.addTemplate(Q_TEMPLATE_NAME2, variable3);
		querySpec2.addKeyName(keyname2);
		querySpec2.setQueryType(QueryType.calculator);
		scope.addQuerySpec(querySpec);
		SolutionHandler solutionHandler = new SolutionHandler(){

			@Override
			public boolean onSolution(Solution solution) 
			{
				assertThat(solution.getString(Q_TEMPLATE_NAME.toString(), VARIABLE_NAME)).isEqualTo("eureka!");
				assertThat(solution.getString(Q_TEMPLATE_NAME2.toString(), VARIABLE_NAME + "3")).isEqualTo("eureka!");
				return true;
			}};
		queryProgram.executeQuery(SCOPE_NAME, QUERY_SPEC_NAME, solutionHandler);
		
	}

	@Test
	public void test_execute_query_missing_scope()
	{
		SolutionHandler solutionHandler = mock(SolutionHandler.class);
		QueryProgram queryProgram = new QueryProgram();
		try
		{
			queryProgram.executeQuery("x", QUERY_SPEC_NAME, solutionHandler);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Scope \"x\" does not exist");
		}
	}

	@Test
	public void test_execute_query_missing_querySpec()
	{
		SolutionHandler solutionHandler = mock(SolutionHandler.class);
		QueryProgram queryProgram = new QueryProgram();
        queryProgram.scopeInstance(SCOPE_NAME, Scope.EMPTY_PROPERTIES);
		try
		{
			queryProgram.executeQuery(SCOPE_NAME, "x", solutionHandler);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Query \"x\" does not exist");
		}
	}

}
