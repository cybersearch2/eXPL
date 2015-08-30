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
        "Total score: 36",
        "English b+",
        "Math b-",
        "History a-",
        "Total score: 44",
        "English c+",
        "Math a",
        "History b+",
        "Total score: 44"
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
        "  calc total_score(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    string label =\"Total score\",\n" +
        "    integer value = english+math+history\n" +
        "  );\n" +
         "}\n"  +
        "calc score(\n" +
        "    template total(label, value) << school.total_score(english,math,history),\n" +
        "    string total_text = total[label] + \": \" + total[value]\n" +
        ");\n" +
        "query marks(grades : score);";

    static final String SCHOOL_REPORT_OUT_SCOPE = GRADES + ALPHA_MARKS +
        "scope school\n" +
        "{\n" +
        "  calc subjects(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    axiom marks_list =\n" +
        "              { \"English\", mark[(english)] } \n" +
        "              { \"Math\",    mark[(math)] }\n" +
        "              { \"History\", mark[(history)] }\n" +
        "  );\n" +
        "  calc total_score(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    string label = \"Total score\",\n" +
        "    integer value = english + math + history\n" +
        "  );\n" +
        "}\n"  +
        "calc score(\n" +
        "    template marks(marks_list) << school.subjects(english, math, history),\n" +
        "    template total(label, value) << school.total_score(english, math, history),\n" +
        "    axiom report = { score.marks, score.total[label] + \": \" + score.total[value] }\n" +
        ");\n" +
        "query marks(grades : score);";

    static final String SCHOOL_REPORT_IN_SCOPE= GRADES + ALPHA_MARKS +
            "  calc subjects(\n" +
            "    integer english,\n" +
            "    integer math,\n" +
            "    integer history,\n" +
            "    axiom marks_list =\n" +
            "                { \"English\", mark[(english)] } \n" +
            "                { \"Math\",    mark[(math)] }\n" +
            "                { \"History\", mark[(history)] }\n" +
            "  );\n" +
            "  calc total_score(\n" +
            "    integer english,\n" +
            "    integer math,\n" +
            "    integer history,\n" +
            "    string label = \"Total score\",\n" +
            "    integer value = english + math + history\n" +
            "  );\n" +
            "calc score(\n" +
            "    template marks(marks_list) << subjects(english, math, history),\n" +
            "    template total(label, value) << total_score(english, math, history),\n" +
            "    axiom report = { score.marks, score.total[label] + \": \" + score.total[value] }\n" +
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
            "  integer accum = 0;\n" +
            "  integer index = 0;\n" +
           "   calc average_height(\n" +
            "  {\n" +
            "    accum += city_list[index][altitude],\n" +
            "    ? ++index < length(city_list)\n" +
            "  },\n" +
            "  average = accum / index\n" +
            "  );\n" +
            "}\n"  +
            "calc average_height(\n" +
            "  template city_info(average) << city.average_height(),\n" +
            "  average = city_info[average]\n" +
            ");\n" +
            "query average_height (average_height);"
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
            "}\n" +
            "calc german_colors\n" +
            "(\n" +
            "  template aqua(red, green, blue) << german.swatch(shade=\"Wasser\"),\n" + 
            "  template blue(red, green, blue) << german.swatch(shade=\"blau\"),\n" + 
            "  axiom colors =\n" +
            "     { \"Aqua\", aqua }\n" +
            "     { \"Blue\", blue }\n" +
            ");\n" +
            "query german_colors (german_colors);\n" +
            "calc german_orange\n" +
            "(\n" +
            "  template orange(red, green, blue) << german.swatch(shade=\"Orange\"),\n" + 
            "  axiom orange_color =\n" +
            "     { \"Orange\", german_orange.orange }\n" +
             ");\n" +
            "query german_orange (german_orange);"
            ;
    static final String SORTED_CITIES = CITY_EVELATIONS +
            "// Calculator to perform insert sort on any list\n" +
             "calc list_sort (\n" +
            "  // This calculator takes 2 parameters, an axiom list\n" +
            "  // and the name of the column to sort on\n " +
            "  sort_list,\n" +
            "  string column,\n" +
            "  // i is index to last item appended to the list\n" +
            "  integer i = length(sort_list) - 1,\n" +
            "  // Skip first time when only one item in list\n" +
            "  : i < 1,\n" +
            "  // j is the swap index\n" + 
            "  integer j = i - 1,\n" +
            "  // Get last altitude for sort comparison\n" + 
            "  integer altitude = sort_list[i][column],\n" +
            "  // Save axiom to swap\n" +
            "  temp = sort_list[i],\n" +
            "  // Shuffle list until sort order restored\n" + 
            "  {\n" +
            "    ? altitude < sort_list[j][column],\n" +
            "    sort_list[j + 1] = sort_list[j],\n" +
            "    ? --j >= 0\n" +
            "  },\n" +
            "  // Insert saved axiom in correct position\n" +
            "  sort_list[j + 1] = temp\n" +
            ");\n" +
            "axiom city_list = {};\n" +
            "calc sort_cities(\n" +
            "  axiom sort_city = { name, altitude },\n" +
            "  system.print(\"sort_city = \" + sort_city),\n" +
            "  city_list += sort_city,\n" +
            "  << list_sort(city_list, \"altitude\"),\n" +
             " system.print(\"Sorted cities\"),\n" +
            "  integer i = 0,\n" +
            "  {\n" +
            "      system.print(city_list[i++]),\n" +
            "      ? i < length(city_list)\n" +
            "  },\n" +
            "  i = 0\n" +
            ");\n" +
            "query sort_cities (city : sort_cities);\n"; 

    static final String PERFECT_MATCH = 
            " axiom person (name, sex, age, starsign):\n" +
            "              (\"John\", \"m\", 23, \"gemini\"),\n" + 
            "              (\"Sue\", \"f\", 19, \"cancer\"),\n" + 
            "              (\"Sam\", \"m\", 24, \"scorpio\"),\n" + 
            "              (\"Jenny\", \"f\", 21, \"gemini\"),\n" + 
            "              (\"Andrew\", \"m\", 26, \"virgo\"),\n" + 
            "              (\"Alice\", \"f\", 20, \"pices\"),\n" + 
            "              (\"Ingrid\", \"f\", 23, \"cancer\"),\n" + 
            "              (\"Jack\", \"m\", 32, \"pices\"),\n" + 
            "              (\"Sonia\", \"f\", 33, \"gemini\"),\n" + 
            "              (\"Alex\", \"m\", 22, \"aquarius\"),\n" + 
            "              (\"Jill\", \"f\", 33, \"cancer\"),\n" + 
            "              (\"Fiona\", \"f\", 29, \"gemini\"),\n" + 
            "              (\"melissa\", \"f\", 30, \"virgo\"),\n" + 
            "              (\"Tom\", \"m\", 22, \"cancer\"),\n" + 
            "              (\"Bill\", \"m\", 19, \"virgo\");\n" + 
            "list person_list(person);\n" +
            "calc people_by_starsign(\n" +
            "  string starsign,\n" +
            "  axiom candidate = {},\n" +
            "  integer i = 0,\n" +
            "  {\n" +
            "    ? i < length(person_list),\n" +
            "    ? person_list[i][starsign] == starsign\n" +
            "    {\n" +
            "       candidate += person_list[i]\n" +
            "    },\n" +
            "    ++i\n" +
            "  }\n" +
            ");\n" +
            "calc match(\n" +
            "  template perfect(candidate) << people_by_starsign(\"gemini\"),\n" +
            "  candidate_list = match.perfect[candidate],\n" +
            "  //system.print(candidate_list),\n" +
            "  integer i = 0,\n" +
            "  {\n" +
            "    ? i < length(candidate_list),\n" +
            "    gemini = candidate_list[i++],\n" +
            "    system.print(gemini[name] + \", \" + gemini[sex] + \", \" + gemini[age] + \", \" + gemini[starsign])\n" +
            "  }\n" +
            " );\n" +
            " query match(match);";



    @Before
    public void setUp()
    {
        new DI(new CallOperandTestModule());
    }

    @Test
    public void test_gemini_people()
    {
        QueryProgram queryProgram = new QueryProgram(PERFECT_MATCH);
        Result result = queryProgram.executeQuery("match");
        //System.out.println(result.getIterator("geminis").next());
    }
    
    @Test
    public void test_sort_cities()
    {
        QueryProgram queryProgram = new QueryProgram(SORTED_CITIES);
        Result result = queryProgram.executeQuery("sort_cities");
        Iterator<Axiom> iterator = result.getIterator("city_list");
        while(iterator.hasNext())
            System.out.println(iterator.next().toString());
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

    @Test
    public void test_school_report_out_scope()
    {
        test_school_report(SCHOOL_REPORT_OUT_SCOPE);
    }

    @Test
    public void test_school_report_in_scope()
    {
        test_school_report(SCHOOL_REPORT_IN_SCOPE);
    }
    
    protected void test_school_report(String xpl)
    {
        QueryProgram queryProgram = new QueryProgram(xpl);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index1 = 0;  
            int index2 = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom score = solution.getAxiom("score");
                AxiomList report = (AxiomList) score.getTermByName("report").getValue();
                assertThat(solution.getString("grades", "student")).isEqualTo(STUDENTS[index1++]);
                Iterator<AxiomTermList> iterator = report.iterator();
                AxiomTermList item = iterator.next();
                AxiomTermList marks = (AxiomTermList) item.getAxiom().getTermByName("marks").getValue();
                //System.out.println(marks);
                AxiomList marksList = (AxiomList) marks.getAxiom().getTermByName("marks_list").getValue();
                Iterator<AxiomTermList> subjects = marksList.iterator();
                while (subjects.hasNext())
                {
                    Axiom subject = subjects.next().getAxiom();
                    System.out.println(subject.toString());
                    //assertThat(subject.getTermByIndex(0).toString() + " " + subject.getTermByIndex(1).getValue().toString()).isEqualTo(SCHOOL_REPORT[index2++]);
                }
                System.out.println(item.getAxiom().getTermByIndex(1).getValue());
                //assertThat(item.getAxiom().getTermByIndex(1).getValue().toString()).isEqualTo(SCHOOL_REPORT[index2++]);
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
        queryProgram.executeQuery("average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                System.out.println(solution.getAxiom("average_height").toString());
                Axiom result = solution.getAxiom("average_height");
                assertThat(result.toString()).isEqualTo("average_height(average = " + averageHeight + ")");
                Long average = (Long) result.getTermByIndex(0).getValue();
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
                //System.out.println(solution.getAxiom("german_colors").toString());
                Axiom germanColors = solution.getAxiom("german_colors");
                AxiomList colorsList = (AxiomList)germanColors.getTermByName("colors").getValue();
                Iterator<AxiomTermList> iterator = colorsList.iterator();
                assertThat(iterator.next().getAxiom().toString()).isEqualTo("colors_list0(Aqua, aqua = aqua(red = 0, green = 255, blue = 255))");
                assertThat(iterator.next().getAxiom().toString()).isEqualTo("colors_list1(Blue, blue = blue(red = 0, green = 0, blue = 255))");
                assertThat(iterator.hasNext()).isFalse();
                assertThat(colorsList.getAxiomTermNameList()).isEmpty();
                return true;
            }});
            
        queryProgram.executeQuery("german_orange", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("calc_german_orange").toString());
                Axiom germanOrange = solution.getAxiom("german_orange");
                AxiomList colorsList = (AxiomList)germanOrange.getTermByName("orange_color").getValue();
                Iterator<AxiomTermList> iterator = colorsList.iterator();
                assertThat(iterator.next().getAxiom().toString()).isEqualTo("orange_color_list0(Orange, orange = *())");
                assertThat(iterator.hasNext()).isFalse();
                assertThat(colorsList.getAxiomTermNameList()).isEmpty();
                return true;
            }}); 
    }
}
