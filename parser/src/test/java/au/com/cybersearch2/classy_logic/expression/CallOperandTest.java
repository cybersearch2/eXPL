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

import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
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
            SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
            functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
            return functionManager;
        }
    }
 
    static class SystemFunctionProvider implements FunctionProvider<Void>
    {
        @Override
        public String getName()
        {
            return "system";
        }

        @Override
        public CallEvaluator<Void> getCallEvaluator(String identifier)
        {
            if (identifier.equals("print"))
                return new CallEvaluator<Void>(){
    
                    @Override
                    public String getName()
                    {
                        return "print";
                    }
    
                    @Override
                    public Void evaluate(List<Term> argumentList)
                    {
                        for (Term term: argumentList)
                            System.out.println(term.toString());
                        return null;
                    }
                    
            };
            throw new ExpressionException("Unknown function identifier: " + identifier);
        }
    }
    
    static class MathFunctionProvider implements FunctionProvider<Number>
    {

        @Override
        public String getName()
        {
            return "math";
        }

        @Override
        public CallEvaluator<Number> getCallEvaluator(String identifier)
        {
            if (identifier.equals("add"))
                return new CallEvaluator<Number>(){
    
                    @Override
                    public String getName()
                    {
                        return "add";
                    }
    
                    @Override
                    public Number evaluate(List<Term> argumentList)
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
                return new CallEvaluator<Number>(){

                    @Override
                    public String getName()
                    {
                        return "avg";
                    }

                    @Override
                    public Number evaluate(List<Term> argumentList)
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

    static class EduFunctionProvider implements FunctionProvider<Long>
    {

        @Override
        public String getName()
        {
            return "edu";
        }

        @Override
        public CallEvaluator<Long> getCallEvaluator(String identifier)
        {
            return new CallEvaluator<Long>(){

                @Override
                public String getName()
                {
                    return "add";
                }

                @Override
                public Long evaluate(List<Term> argumentList)
                {
                    long total = 0;
                    for (Object letterGrade: argumentList)
                    {
                        String text = ((Term)letterGrade).getValue().toString();
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
        " query two_arg_query (test);";
    static final String THREE_ARG_CALC =
        " calc test (integer x = math.add(1,2,3));\n" +
        " query three_arg_query (test);";
    static final String FOUR_ARG_CALC =
        " calc test (integer x = math.add(12,42,93,55));\n" +
        " query four_arg_query (test);";
    static final String GRADES = 
        "axiom grades (student, english, math, history):\n" +
            " (\"Amy\", 14, 16, 6),\n" +
            " (\"George\", 15, 13, 16),\n" +
            " (\"Sarah\", 12, 17, 15);\n";

    static final String GRADES_CALC = GRADES +
        " template score(student, integer total = math.add(english, math, history));\n" +
        " query marks(grades : score);";

    static final String[] GRADES_RESULTS = 
    {
        "score(student = Amy, total = 36)",
        "score(student = George, total = 44)",
        "score(student = Sarah, total = 44)"
    };

    static final String[] STUDENTS =
    {
         "Amy",
         "George",
         "Sarah"
    };
    
    static final String[] MARKS_GRADES_RESULTS = 
    {
        "Total score: 36",
        "Total score: 44",
        "Total score: 44"
    };
 
    static final String[] SCHOOL_REPORT = 
    {
        "English b",
        "Math a-",
        "History e+",
        "Total score 36",
        "English b+",
        "Math b-",
        "History a-",
        "Total score 44",
        "English c+",
        "Math a",
        "History b+",
        "Total score 44"
    };
    
   static final String ALPHA_MARKS = 
    " axiom alpha_marks :\n" +
    "(\n" +
    " \"\",\n" +
    " \"f-\", \"f\", \"f+\",\n" +
    " \"e-\", \"e\", \"e+\",\n" +
    " \"d-\", \"d\", \"d+\",\n" +
    " \"c-\", \"c\", \"c+\",\n" +
    " \"b-\", \"b\", \"b+\",\n" +
    " \"a-\", \"a\", \"a+\"\n" +
    ");\n" +
    "list<term> mark(alpha_marks);\n";
    
    static final String MARKS_CALC = GRADES + ALPHA_MARKS +
    "template score(student, integer total = edu.add(mark[(english)], mark[(math)], mark[(history)]));\n" +
    "query marks(grades : score);";
    
    static final String MARKS_GRADES_CALC = GRADES + ALPHA_MARKS +
        "scope school\n" +
        "{\n" +
        "  calc calc_total_score(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    solution total_score = { label=\"Total score\", value=english+math+history }\n" +
        "  );\n" +
        "  query calc_total_score (calc_total_score);\n" +
        "}\n"  +
        "calc score(\n" +
        "    school.total_score = school.calc_total_score(english,math,history),\n" +
        "    string total_text = school.total_score[label] + \": \" + school.total_score[value]\n" +
        ");\n" +
        "query marks(grades : score);";

    static final String ITEM_MARKS_GRADES_CALC = GRADES + ALPHA_MARKS +
        "scope school\n" +
        "{\n" +
        "  calc subjects(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    solution marks =\n" +
        "                { \"English\", mark[(english)] } \n" +
        "                { \"Math\",    mark[(math)] }\n" +
        "                { \"History\", mark[(history)] }\n" +
        "  );\n" +
        "  calc total_score(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    solution total_score = { \"Total score\", english + math + history }\n" +
        "  );\n" +
        "  query calc_marks (subjects);\n" +
        "  query calc_total_score (total_score);\n" +
        "}\n"  +
        "calc score(\n" +
        "    school.marks = school.calc_marks(english,math,history),\n" +
        "    school.total = school.calc_total_score(english,math,history),\n" +
        "    solution report = school.marks + school.total\n" +
        ");\n" +
        "query marks(grades : score);";

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
            "query average_height (city : average);";
    
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
            "  query average_height (average_height);\n" +
            "}\n"  +
            "calc call_average_height(\n" +
            "  axiom calc_average = city.average_height(),\n" +
            "  average = calc_average[average]\n" +
            ");\n" +
            "query function_average_height (call_average_height);"
           ;
    static final String GERMAN_COLORS =
            "axiom lexicon (language, aqua, black, blue, white):\n" +
            "  (\"english\", \"aqua\", \"black\", \"blue\", \"white\"),\n" +
            "  (\"german\", \"Wasser\", \"schwarz\", \"blau\", \"weiß\");\n" +
            "local colors(lexicon);" +
            "choice swatch (shade, red, green, blue) :\n" +
            "(colors[aqua], 0, 255, 255),\n" +
            "(colors[black], 0, 0, 0),\n" +
            "(colors[blue], 0, 0, 255),\n" +
            "(colors[white], 255, 255, 255);\n" +
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "  query color_query (swatch);\n" +
            "}\n" +
            "calc calc_german_colors\n" +
            "(\n" +
            "  solution color_list = german.color_query(shade=\"Wasser\"),\n" + 
            "  color_list += german.color_query(shade=\"blau\")\n" + 
            ");\n" +
            "query german_colors (calc_german_colors);\n" +
            "calc calc_german_orange\n" +
            "(\n" +
            "  solution german_orange = german.color_query(shade=\"Orange\")\n" + 
            ");\n" +
            "query german_orange (calc_german_orange);"
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
        queryProgram.executeQuery("two_arg_query", new SolutionHandler(){

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
        queryProgram.executeQuery("three_arg_query", new SolutionHandler(){

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
        queryProgram.executeQuery("four_arg_query", new SolutionHandler(){

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
            @Override
            public boolean onSolution(Solution solution)
            {
                System.out.println(solution.getAxiom("score").toString());
                //assertThat(solution.getAxiom("score").toString()).isEqualTo(GRADES_RESULTS[index++]);
                return true;
            }});
    }

    @Test
    public void test_school_grades()
    {
        QueryProgram queryProgram = new QueryProgram(MARKS_GRADES_CALC);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                 //System.out.println(solution.getString("score", "total_text"));
                assertThat(solution.getString("grades", "student")).isEqualTo(STUDENTS[index]);
                assertThat(solution.getString("score", "total_text")).isEqualTo(MARKS_GRADES_RESULTS[index++]);
                return true;
            }});
    }
//
    @Test
    public void test_school_report()
    {
        QueryProgram queryProgram = new QueryProgram(ITEM_MARKS_GRADES_CALC);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index1 = 0;  
            int index2 = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                Iterator<AxiomTermList> iterator = solution.getAxiomList("score").iterator();
                assertThat(solution.getString("grades", "student")).isEqualTo(STUDENTS[index1++]);
                while (iterator.hasNext())
                {
                    AxiomTermList termList = iterator.next();
                    Axiom item = termList.getAxiom();
                    String subject = item.getTermByIndex(0).toString();
                    String mark = item.getTermByIndex(1).getValue().toString();
                    //System.out.println(subject + " " + mark);
                    assertThat(subject + " " + mark).isEqualTo(SCHOOL_REPORT[index2++]);
                }
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
                //System.out.println(solution.getAxiom("call_average_height").toString());
                Axiom result = solution.getAxiom("call_average_height");
                AxiomList calcAverageList = (AxiomList) result.getTermByIndex(0).getValue();
                Axiom calcAverage = ((AxiomTermList)(calcAverageList.getItem(0))).getAxiom();
                assertThat(calcAverage.toString()).isEqualTo("average_height(average_height1 = true, average = " + averageHeight + ")");
                Long average = (Long) result.getTermByIndex(1).getValue();
                assertThat(average).isEqualTo(averageHeight);
                return true;
            }});
    }

    @Test
    public void test_choice_string_colors()
    {
        QueryProgram queryProgram = new QueryProgram(GERMAN_COLORS);
        queryProgram.executeQuery("german_colors", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("calc_german_colors").toString());
                AxiomList calcGermanColors = solution.getAxiomList("calc_german_colors");
                assertThat(calcGermanColors.getName()).isEqualTo("swatch");
                 Iterator<AxiomTermList> iterator = calcGermanColors.iterator();
                assertThat(iterator.next().getAxiom().toString()).isEqualTo("swatch(shade = Wasser, red = 0, green = 255, blue = 255)");
                assertThat(iterator.next().getAxiom().toString()).isEqualTo("swatch(shade = blau, red = 0, green = 0, blue = 255)");
                assertThat(iterator.hasNext()).isFalse();
                assertThat(calcGermanColors.getAxiomTermNameList().toString()).isEqualTo("[shade, red, green, blue]");
                return true;
            }});
        queryProgram.executeQuery("german_orange", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("calc_german_orange").toString());
                AxiomList calcGermanOrange = solution.getAxiomList("calc_german_orange");
                assertThat(calcGermanOrange.getName()).isEqualTo("swatch");
                 Iterator<AxiomTermList> iterator = calcGermanOrange.iterator();
                assertThat(iterator.hasNext()).isFalse();
                assertThat(calcGermanOrange.getAxiomTermNameList().toString()).isEqualTo("[shade, red, green, blue]");
                return true;
            }});
    }
}
