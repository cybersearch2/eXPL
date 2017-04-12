/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.expression;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * ParameterOperandTest
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterOperandTest
{
    static final String NO_ARG_CALC =
        "calc test (axiom x = {} );\n" +
        "query no_arg_query (test);";

    static final String ALL_TYPES_ARG_CALC =
            "calc test (\n" +
            "axiom all_types = {\n" +
            "boolean bool = true,\n" +       
            "integer int = 123,\n" +       
            "double real = 1.456,\n" +       
            "decimal dec = 5.00,\n" +       
            "currency $\"AU\" amount = 19.76,\n" +       
            "string text = \"To be, or not to be\"\n" +       
            "} );\n" +
            "query all_types_arg_query (test);";

    static final String ALL_TYPES_VARS_ARG_CALC =
            "boolean bool = true;\n" +       
            "integer int = 123;\n" +       
            "double real = 1.456;\n" +       
            "decimal dec = 5.00;\n" +       
            "currency $ \"AU\" amount = 19.76;\n" +       
            "string text = \"To be, or not to be\";\n" +       
            "calc test (\n" +
            "axiom all_types = {\n" +
            "term a = bool,\n" +       
            "term b = int,\n" +       
            "term c = real,\n" +       
            "term d = dec,\n" +       
            "term e = amount,\n" +       
            "term f = text\n" +       
            "} );\n" +
            "query all_types_vars_arg_query (test);";

    @Test
    public void testNoArgs()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(NO_ARG_CALC);
        queryProgram.executeQuery("no_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiomList("test").toString());
                validateSolution((AxiomList)solution.getAxiom("test").getTermByIndex(0).getValue(), "x", null);
                return false;
            }});
    }
    
    @Test
    public void testAllTypesArgs()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(ALL_TYPES_ARG_CALC);
        queryProgram.executeQuery("all_types_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                AxiomList allTypesContainer = (AxiomList)solution.getAxiom("test").getTermByIndex(0).getValue();
                //System.out.println(solution.getAxiom("test").toString());
                validateSolution(allTypesContainer, "all_types", "bool, int, real, dec, amount, text", 
                        "all_types(bool = true, int = 123, real = 1.456, dec = 5.0, amount = 19.76, text = To be, or not to be)");
                return false;
            }});
    }
    
    @Test
    public void testAllTypesVarsArgs()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(ALL_TYPES_VARS_ARG_CALC);
        queryProgram.executeQuery("all_types_vars_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("test").toString());
                validateSolution((AxiomList)solution.getAxiom("test").getTermByIndex(0).getValue(), "all_types", "a, b, c, d, e, f", 
                        "all_types(a = true, b = 123, c = 1.456, d = 5.0, e = 19.76, f = To be, or not to be)");
                return false;
            }});
    }
    
    protected void validateSolution(AxiomList result, String axiomName, String termNames, String... params)
    {
        assertThat(result.getName()).isEqualTo(axiomName);
        Iterator<AxiomTermList> iterator = result.iterator();
        //while (iterator.hasNext())
        //    System.out.println(iterator.next().toString());
        for (String param: params)
        {
            Object item = iterator.next();
            assertThat(item.toString()).isEqualTo("list<term> " + param);
        }
        assertThat(iterator.hasNext()).isFalse();
        //System.out.println(result.getTermByName(Template.TERMNAMES).toString());
        if (termNames != null)
            assertThat(result.getAxiomTermNameList().toString()).isEqualTo("[" + termNames + "]");
        else
            assertThat(result.getAxiomTermNameList()).isNull();
    }
}
