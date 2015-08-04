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

import java.util.List;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;
import dagger.Module;
import dagger.Provides;

/**
 * CallOperandTest
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public class CallOperandTest
{
    @Module(injects=ParserAssembler.ExternalFunctionProvider.class)
    static class CallOperandTestModule implements ApplicationModule
    {
        @Provides @Singleton FunctionManager provideFunctionManager()
        {
            FunctionManager functionManager = new FunctionManager();
            MathFunctionProvider mathFunctionProvider = new MathFunctionProvider();
            functionManager.putFunctionProvider(mathFunctionProvider.getName(), mathFunctionProvider);
            EduFunctionProvider eduFunctionProvider = new EduFunctionProvider();
            functionManager.putFunctionProvider(eduFunctionProvider.getName(), eduFunctionProvider);
            return functionManager;
        }
    }
    
    static class MathFunctionProvider implements FunctionProvider
    {

        @Override
        public String getName()
        {
            return "math";
        }

        @Override
        public CallEvaluator getCallEvaluator(String identifier)
        {
            if (identifier.equals("add"))
                return new CallEvaluator(){
    
                    @Override
                    public String getName()
                    {
                        return "add";
                    }
    
                    @Override
                    public Object evaluate(List<Variable> argumentList)
                    {
                        if ((argumentList == null) || argumentList.isEmpty())
                            return Double.NaN;
                        long addendum = 0;
                        for (int i = 0; i < argumentList.size(); i++)
                        {
                            Long param = (Long)argumentList.get(i).getValue();
                            addendum += param.longValue();
                        }
                        return Long.valueOf(addendum);
                    }
                };
            if (identifier.equals("avg"))
                return new CallEvaluator(){

                    @Override
                    public String getName()
                    {
                        return "avg";
                    }

                    @Override
                    public Object evaluate(List<Variable> argumentList)
                    {
                        if ((argumentList == null) || argumentList.isEmpty())
                            return Double.NaN;
                        long avaerage = 0;
                        for (int i = 0; i < argumentList.size(); i++)
                        {
                            Long param = (Long)argumentList.get(i).getValue();
                            avaerage += param.longValue();
                        }
                        return Long.valueOf(avaerage / argumentList.size());
                    }};
             throw new ExpressionException("Unknown function identifier: " + identifier);
        }
    }

    static class EduFunctionProvider implements FunctionProvider
    {

        @Override
        public String getName()
        {
            return "edu";
        }

        @Override
        public CallEvaluator getCallEvaluator(String identifier)
        {
            return new CallEvaluator(){

                @Override
                public String getName()
                {
                    return "add";
                }

                @Override
                public Object evaluate(List<Variable> argumentList)
                {
                    long total = 0;
                    for (Object letterGrade: argumentList)
                    {
                        String text = letterGrade.toString();
                        char base = text.charAt(0);
                        if (base == 'f')
                            total += 2;
                        else if (base == 'e')
                            total += 5;
                        else if (base == 'd')
                            total += 8;
                        else if (base == 'c')
                            total += 11;
                        else if (base == 'b')
                            total += 14;
                        else if (base == 'a')
                            total += 17;
                        if (text.length() > 1)
                        {
                            char adjust = text.charAt(1);
                            total += adjust == '+' ? 1 : -1;
                        }
                    }
                    return Long.valueOf(total);
                }
            };
        }
        
    }

    static final String TWO_ARG_CALC =
        " calc test (integer x = math.add(1,2));\n" +
        " query no_arg_query calc(test);";
    static final String THREE_ARG_CALC =
            " calc test (integer x = math.add(1,2,3));\n" +
            " query no_arg_query calc(test);";
    static final String FOUR_ARG_CALC =
            " calc test (integer x = math.add(12,42,93,55));\n" +
            " query no_arg_query calc(test);";
    static final String GRADES = 
            "axiom grades (student, english, math, history):\n" +
            " (\"George\", 15, 13, 16),\n" +
            " (\"Sarah\", 12, 17, 15),\n" +
            " (\"Amy\", 14, 16, 6);\n";

    static final String GRADES_CALC = GRADES +
            " template score(student, integer total = math.add(english, math, history));\n" +
            " query marks(grades : score);";

    static final String[] GRADES_RESULTS = 
    {
        "score(student = George, total = 44)",
        "score(student = Sarah, total = 44)",
        "score(student = Amy, total = 36)"
    };
 
    static final String MARKS_CALC = GRADES +
    " axiom alpha_marks :\n" +
    "(\n" +
    " \"f-\", \"f\", \"f+\",\n" +
    " \"e-\", \"e\", \"e+\",\n" +
    " \"d-\", \"d\", \"d+\",\n" +
    " \"c-\", \"c\", \"c+\",\n" +
    " \"b-\", \"b\", \"b+\",\n" +
    " \"a-\", \"a\", \"a+\"\n" +
    ");\n" +
    " list<term> mark(alpha_marks);\n" +
    " template score(student, integer total = edu.add(mark[(english)-1], mark[(math)-1], mark[(history)-1]));\n" +
    " query marks(grades : score);";

    static final String CITY_EVELATIONS =
        "axiom city (name, altitude):\n" + 
            "    (\"bilene\", 1718),\n" +
            "    (\"addis ababa\", 8000),\n" +
            "    (\"denver\", 5280),\n" +
            "    (\"flagstaff\", 6970),\n" +
            "    (\"jacksonville\", 8),\n" +
            "    (\"leadville\", 10200),\n" +
            "    (\"madrid\", 1305),\n" +
            "    (\"richmond\",19),\n" +
            "    (\"spokane\", 1909),\n" +
            "    (\"wichita\", 1305);\n";
    
    static final String CITY_AVERAGE_HEIGHT_CALC = CITY_EVELATIONS +
            "list city_list(city);\n" +
            "calc average (integer average_height = math.avg(" +
            "  city_list[0][altitude],\n" +
            "  city_list[1][altitude],\n" +
            "  city_list[2][altitude],\n" +
            "  city_list[3][altitude],\n" +
            "  city_list[4][altitude],\n" +
            "  city_list[5][altitude],\n" +
            "  city_list[6][altitude],\n" +
            "  city_list[7][altitude],\n" +
            "  city_list[8][altitude],\n" +
            "  city_list[9][altitude]\n" +
            "));\n" +
            "query average_height calc(city : average);";
    
    static final String CITY_AVERAGE_HEIGHT_CALC2 = CITY_EVELATIONS +
            "list city_list(city);\n" +
            "scope city\n" +
            "{\n" +
            "  integer average;\n" +
            "  integer accum = 0;\n" +
            "  integer index = 0;\n" +
           "   calc average_height(\n" +
            "  {\n" +
            "    accum += city_list[index][altitude],\n" +
            "    ? ++index < length(city_list)\n" +
            "  },\n" +
            "  average = accum / index\n" +
            "  );\n" +
            "  query average_height calc(average_height);\n" +
            "}\n"  +
            "axiom calc_average(average_height, average) : parameter;\n" + 
            "calc call_average_height(\n" +
            "  calc_average = city.average_height(),\n" +
            "  average = calc_average[average]\n" +
            ");\n" +
            "query function_average_height calc(call_average_height);"
           ;

    @Before
    public void setUp()
    {
        new DI(new CallOperandTestModule());
    }
    
    @Test
    public void test_two_argument()
    {
        QueryProgram queryProgram = new QueryProgram(TWO_ARG_CALC);
        queryProgram.executeQuery("no_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                assertThat((Long)(solution.getValue("test", "x"))).isEqualTo(3);
                return false;
            }});
    }

    @Test
    public void test_three_argument()
    {
        QueryProgram queryProgram = new QueryProgram(THREE_ARG_CALC);
        queryProgram.executeQuery("no_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                assertThat((Long)(solution.getValue("test", "x"))).isEqualTo(6);
                return false;
            }});
    }

    @Test
    public void test_four_argument()
    {
        QueryProgram queryProgram = new QueryProgram(FOUR_ARG_CALC);
        queryProgram.executeQuery("no_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                assertThat((Long)(solution.getValue("test", "x"))).isEqualTo(12+42+93+55);
                return false;
            }});
    }

    @Test
    public void test_three_variables()
    {
        QueryProgram queryProgram = new QueryProgram(GRADES_CALC);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("score").toString());
                assertThat(solution.getAxiom("score").toString()).isEqualTo(GRADES_RESULTS[index++]);
                return true;
            }});
    }

    @Test
    public void test_term_variables()
    {
        QueryProgram queryProgram = new QueryProgram(MARKS_CALC);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("score").toString());
                assertThat(solution.getAxiom("score").toString()).isEqualTo(GRADES_RESULTS[index++]);
                return true;
            }});
    }

    @Test
    public void test_list_variables()
    {
        QueryProgram queryProgram = new QueryProgram(CITY_AVERAGE_HEIGHT_CALC);
        queryProgram.executeQuery("average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("average").toString());
                long averageHeight = (1718+8000+5280+6970+8+10200+1305+19+1909+1305)/10;
                assertThat((Long)(solution.getValue("average", "average_height"))).isEqualTo(averageHeight);
                return true;
            }});
    }

    @Test
    public void test_calculator()
    {
        final long averageHeight = (1718+8000+5280+6970+8+10200+1305+19+1909+1305)/10;
        QueryProgram queryProgram = new QueryProgram(CITY_AVERAGE_HEIGHT_CALC2);
        /*
        queryProgram.executeQuery("city.average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getValue("average_height", "average").toString());
                long averageHeight = (1718+8000+5280+6970+8+10200+1305+19+1909+1305)/10;
                assertThat((Long)(solution.getValue("average_height", "average"))).isEqualTo(averageHeight);
                return true;
            }}); */
        queryProgram.executeQuery("function_average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                System.out.println(solution.getAxiom("call_average_height").toString());
                Axiom result = solution.getAxiom("call_average_height");
                AxiomList calcAverageList = (AxiomList) result.getTermByIndex(0).getValue();
                Axiom calcAverage = ((AxiomTermList)(calcAverageList.getItem(0))).getAxiom();
                assertThat(calcAverage.toString()).isEqualTo("average_height(average_height1 = true, average = " + averageHeight + ")");
                Long average = (Long) result.getTermByIndex(1).getValue();
                assertThat(average).isEqualTo(averageHeight);
                return true;
            }});
    }

}
