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
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * CallOperandTest
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public class CallOperandTest
{
    static class SystemFunctionProvider implements FunctionProvider
    {
        @Override
        public String getName()
        {
            return "system";
        }

        @Override
        public CallEvaluator<Axiom> getCallEvaluator(String identifier)
        {
            if (identifier.equals("print"))
                return new CallEvaluator<Axiom>(){
    
                    @Override
                    public String getName()
                    {
                        return "print";
                    }
    
                    @Override
                    public Axiom evaluate(List<Term> argumentList)
                    {
                        //for (Term term: argumentList)
                        //    System.out.print(term.getValue().toString());
                        //System.out.println();
                        Axiom axiom = new Axiom(getName());
                        axiom.addTerm(new Parameter(Term.ANONYMOUS, new Null()));
                        return null;
                    }

                    @Override
                    public void setExecutionContext(ExecutionContext context)
                    {
                    }
                    
            };
            throw new ExpressionException("Unknown function identifier: " + identifier);
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
        public CallEvaluator<Axiom> getCallEvaluator(String identifier)
        {
            if (identifier.equals("add"))
                return new CallEvaluator<Axiom>(){
    
                    @Override
                    public String getName()
                    {
                        return "add";
                    }
    
                    @Override
                    public Axiom evaluate(List<Term> argumentList)
                    {
                        if ((argumentList == null) || argumentList.isEmpty())
                        {
                            Axiom axiom = new Axiom("NaN");
                            axiom.addTerm(new Parameter(Term.ANONYMOUS, Double.NaN));
                            return axiom;
                        }
                        long addendum = 0;
                        for (int i = 0; i < argumentList.size(); i++)
                        {
                            Long param = (Long)argumentList.get(i).getValue();
                            addendum += param.longValue();
                        }
                        Axiom axiom = new Axiom(Long.toString(addendum));
                        axiom.addTerm(new Parameter(Term.ANONYMOUS, addendum));
                        return axiom;
                    }

                    @Override
                    public void setExecutionContext(ExecutionContext context)
                    {
                    }
                };
            if (identifier.equals("avg"))
                return new CallEvaluator<Axiom>(){

                    @Override
                    public String getName()
                    {
                        return "avg";
                    }

                    @Override
                    public Axiom evaluate(List<Term> argumentList)
                    {
                        if ((argumentList == null) || argumentList.isEmpty())
                        {
                            Axiom axiom = new Axiom("NaN");
                            axiom.addTerm(new Parameter(Term.ANONYMOUS, Double.NaN));
                            return axiom;
                        }
                        long average = 0;
                        for (int i = 0; i < argumentList.size(); i++)
                        {
                            Long param = (Long)argumentList.get(i).getValue();
                            average += param.longValue();
                        }
                        average /= argumentList.size();
                        Axiom axiom = new Axiom(Long.toString(average));
                        axiom.addTerm(new Parameter(Term.ANONYMOUS, average));
                        return axiom;
                   }

                    @Override
                    public void setExecutionContext(ExecutionContext context)
                    {
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
        public CallEvaluator<Axiom> getCallEvaluator(String identifier)
        {
            return new CallEvaluator<Axiom>(){

                @Override
                public String getName()
                {
                    return "add";
                }

                @Override
                public Axiom evaluate(List<Term> argumentList)
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
                    Axiom axiom = new Axiom(Long.toString(total));
                    axiom.addTerm(new Parameter(Term.ANONYMOUS, total));
                    return axiom;
                }

                @Override
                public void setExecutionContext(ExecutionContext context)
                {
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
        "axiom grades (student, english, math, history)\n" +
            " {\"Amy\", 14, 16, 6}\n" +
            " {\"George\", 15, 13, 16}\n" +
            " {\"Sarah\", 12, 17, 15};\n";

    static final String GRADES_CALC = GRADES +
        " template score(student, integer total = math.add(english, math, history));\n" +
        " query marks(grades : score);";

    static final String[] GRADES_RESULTS = 
    {
        "score(student=Amy, english=14, math=16, history=6, total=36)",
        "score(student=George, english=15, math=13, history=16, total=44)",
        "score(student=Sarah, english=12, math=17, history=15, total=44)"
    };

    static final String[] GRADES_RESULTS2 = 
    {
        "score(student=Amy, total=36)",
        "score(student=George, total=44)",
        "score(student=Sarah, total=44)"
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
 
    static final String[] MARKS_GRADES_RESULTS2 = 
    {
        "marks_total=36",
        "marks_total=44",
        "marks_total=44"
    };
    
    static final String[] MATH_SCORES =
    {
        "marks_list(Math, mark=a-)",
        "marks_list(Math, mark=b-)",
        "marks_list(Math, mark=a)"
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
    " axiom alpha_marks()\n" +
    "{\n" +
    " \"\",\n" +
    " \"f-\", \"f\", \"f+\",\n" +
    " \"e-\", \"e\", \"e+\",\n" +
    " \"d-\", \"d\", \"d+\",\n" +
    " \"c-\", \"c\", \"c+\",\n" +
    " \"b-\", \"b\", \"b+\",\n" +
    " \"a-\", \"a\", \"a+\"\n" +
    "};\n" +
    "list<term> mark(alpha_marks);\n";
    
    static final String MARKS_CALC = GRADES + ALPHA_MARKS +
    "template score(\n" +
    "  student, english, math, history,\n" +
    "integer total = edu.add(mark[english], mark[math], mark[history]));\n" +
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
        "    <- school.total_score(english,math,history) -> (label, value),\n" +
        "    string total_text = label + \": \" + value\n" +
        ");\n" +
        "query marks(grades : score);";

    static final String SCHOOL_REPORT_OUT_SCOPE = GRADES + ALPHA_MARKS +
        "scope school\n" +
        "{\n" +
        "   calc subjects\n" +
        "   + list<axiom> marks_list {};\n" +
        "(\n" +
        "    integer english,\n" +
        "    integer math,\n" +
        "    integer history,\n" +
        "    marks_list =\n" +
        "       axiom { \"English\", mark[english] } \n" +
        "             { \"Math\",    mark[math] }\n" +
        "             { \"History\", mark[history] }\n" +
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
        "  <- school.subjects(english, math, history) -> (marks_list),\n" +
        "  <- school.total_score(english, math, history) -> (label, value),\n" +
        "  marks = marks_list,\n" +
        "  string total = label + \": \" + value\n" +
        ");\n" +
        "query marks(grades : score);";

    static final String SCHOOL_REPORT_IN_SCOPE = GRADES + ALPHA_MARKS +
            "calc subjects\n" +
            "+ list<axiom> marks_list {};\n" +
            "(\n" +
            "    integer english,\n" +
            "    integer math,\n" +
            "    integer history,\n" +
            "    marks_list =\n" +
            "        axiom { \"English\", mark[english] } \n" +
            "              { \"Math\",    mark[math] }\n" +
            "              { \"History\", mark[history] }\n" +
            "  );\n" +
            "  calc total_score(\n" +
            "    integer english,\n" +
            "    integer math,\n" +
            "    integer history,\n" +
            "    string label = \"Total score\",\n" +
            "    integer value = english + math + history\n" +
            "  );\n" +
            "\n" +
            "calc score(\n" +
            "  <- subjects(english, math, history) -> (marks_list),\n" +
            "  math_score = score.subjects[1],\n" +
            "  <- total_score(english, math, history) -> (label, value),\n" +
            "  marks_total = score.total_score->value,\n" +
            "  marks = marks_list,\n" +
            "  string total = label + \": \" + value\n" +
            ");\n" +
            "query<axiom> marks(grades : score);";

    static final String CITY_EVELATIONS =
        "axiom city (name, altitude)\n" + 
            "    {\"bilene\", 1718}\n" +
            "    {\"addis ababa\", 8000}\n" +
            "    {\"denver\", 5280}\n" +
            "    {\"flagstaff\", 6970}\n" +
            "    {\"jacksonville\", 8}\n" +
            "    {\"leadville\", 10200}\n" +
            "    {\"madrid\", 1305}\n" +
            "    {\"richmond\",19}\n" +
            "    {\"spokane\", 1909}\n" +
            "    {\"wichita\", 1305};\n";
    
    static final String CITY_AVERAGE_HEIGHT_CALC = CITY_EVELATIONS +
            "list city_list(city);\n" +
            "calc average (integer average_height=math.avg(" +
            "  city_list[0].altitude,\n" +
            "  city_list[1].altitude,\n" +
            "  city_list[2].altitude,\n" +
            "  city_list[3].altitude,\n" +
            "  city_list[4].altitude,\n" +
            "  city_list[5].altitude,\n" +
            "  city_list[6].altitude,\n" +
            "  city_list[7].altitude,\n" +
            "  city_list[8].altitude,\n" +
            "  city_list[9].altitude\n" +
            "));\n" +
            "query average_height (average);";
    
    static final String CITY_AVERAGE_HEIGHT_CALC2 = CITY_EVELATIONS +
            "list city_list(city);\n" +
            "scope city\n" +
            "{\n" +
            "  integer accum=0;\n" +
            "  integer index=0;\n" +
           "   calc average_height(\n" +
            "  {\n" +
            "    accum += city_list[index].altitude,\n" +
            "    ? ++index < city_list.length\n" +
            "  },\n" +
            "  average=accum / index\n" +
            "  );\n" +
            "}\n"  +
            "calc average_height(\n" +
            "  <- city.average_height() -> (average),\n" +
            "  average\n" +
            ");\n" +
            "query average_height (average_height);"
           ;
    static final String GERMAN_COLORS =
            "calc german_colors\n" +
            "+ export list<axiom> colors {};\n" +
            "(\n" +
            "  <- german.swatch(shade=\"Wasser\") -> (red, green, blue),\n" + 
            "  colors += axiom aqua { red, green, blue },\n" +
            "  <- german.swatch(shade=\"blau\") -> (red, green, blue),\n" + 
            "  colors += axiom blue { red, green, blue }\n" +
            "  \n" + 
            ");\n" +
            "axiom german.lexicon (aqua, black, blue, white)\n" +
            "  {\"Wasser\", \"schwarz\", \"blau\", \"weiß\"};\n" +
            "choice swatch\n" +
            "+ list<term> lexicon@scope;\n" +
            "(shade, red, green, blue)\n" +
            "{lexicon->aqua, 0, 255, 255}\n" +
            "{lexicon->black, 0, 0, 0}\n" +
            "{lexicon->blue, 0, 0, 255}\n" +
            "{lexicon->white, 255, 255, 255};\n" +
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "}\n" +
            "query colors (german_colors);\n" +
            "calc german_orange\n" +
            "(\n" +
            "  <- german.swatch(shade=\"Orange\") -> (red, green, blue),\n" + 
            "  boolean hasRed=red.fact,\n" +
            "  boolean hasGreen=green.fact,\n" +
            "  boolean hasBlue=blue.fact\n" +
            ");\n" +
            "query orange (german_orange);"
            ;
    static final String SORTED_CITIES = CITY_EVELATIONS +
            "// Calculator to perform insert sort on any list\n" +
             "calc list_sort (\n" +
            "  // This calculator takes 2 parameters, an axiom list\n" +
            "  // and the name of the column to sort on\n " +
            "  sort_list,\n" +
            "  string column,\n" +
            "  // i is index to last item appended to the list\n" +
            "  integer i= sort_list.length - 1,\n" +
            "  // Skip first time when only one item in list\n" +
            "  : i < 1,\n" +
            "  // j is the swap index\n" + 
            "  integer j=i - 1,\n" +
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
            "export list<axiom> city_list {};\n" +
            "calc sort_cities(\n" +
            "  city_list += axiom sort_city { name, altitude },\n" +
            "  <- list_sort(city_list, \"altitude\")\n" +
            ");\n" +
            "query sort_cities (city : sort_cities);\n"; 

    static String[] SORTED_CITIES_LIST =
    {
        "sort_city(name=jacksonville, altitude=8)",
        "sort_city(name=richmond, altitude=19)",
        "sort_city(name=madrid, altitude=1305)",
        "sort_city(name=wichita, altitude=1305)",
        "sort_city(name=bilene, altitude=1718)",
        "sort_city(name=spokane, altitude=1909)",
        "sort_city(name=denver, altitude=5280)",
        "sort_city(name=flagstaff, altitude=6970)",
        "sort_city(name=addis ababa, altitude=8000)",
        "sort_city(name=leadville, altitude=10200)"
    };
    
    static final String PERFECT_MATCH = 
            " axiom person (name, sex, age, starsign)\n" +
            "              {\"John\", \"m\", 23, \"gemini\"}\n" + 
            "              {\"Sue\", \"f\", 19, \"cancer\"}\n" + 
            "              {\"Sam\", \"m\", 24, \"scorpio\"}\n" + 
            "              {\"Jenny\", \"f\", 21, \"gemini\"}\n" + 
            "              {\"Andrew\", \"m\", 26, \"virgo\"}\n" + 
            "              {\"Alice\", \"f\", 20, \"pices\"}\n" + 
            "              {\"Ingrid\", \"f\", 23, \"cancer\"}\n" + 
            "              {\"Jack\", \"m\", 32, \"pices\"}\n" + 
            "              {\"Sonia\", \"f\", 33, \"gemini\"}\n" + 
            "              {\"Alex\", \"m\", 22, \"aquarius\"}\n" + 
            "              {\"Jill\", \"f\", 33, \"cancer\"}\n" + 
            "              {\"Fiona\", \"f\", 29, \"gemini\"}\n" + 
            "              {\"melissa\", \"f\", 30, \"virgo\"}\n" + 
            "              {\"Tom\", \"m\", 22, \"cancer\"}\n" + 
            "              {\"Bill\", \"m\", 19, \"virgo\"};\n" + 
            "list person_list(person);\n" +
            "calc people_by_starsign\n" +
            "+ list<axiom> candidates {};\n" +
            "(\n" +
            "  string starsign,\n" +
            "  integer i = 0,\n" +
            "  candidates,\n" +
            "  {\n" +
            "    ? i < person_list.length,\n" +
            "    ? person_list[i].starsign == starsign\n" +
            "    {\n" +
            "       candidates += person_list[i]\n" +
            "    },\n" +
            "    (++i)\n" +
            "  }\n" +
            ");\n" +
            "calc match\n" +
            "+ export list<axiom> geminis {};\n" +
            "(\n" +
            "  <- people_by_starsign(\"gemini\") -> (candidates),\n" +
            " candidate_list = match.people_by_starsign,\n" +
            ". integer i = 0,\n" +
            "  {\n" +
            "    ? i < candidates.length,\n" +
            "    geminis += candidates[i++]\n" +
            "  }\n" +
            " );\n" +
            "query<term> match(match);";
    
    static final String FACTUAL_MATCH = 
            " axiom person (name, sex, age, starsign)\n" +
            "              {\"John\", \"m\", 23, \"gemini\"}\n" + 
            "              {\"Sue\", \"f\", 19, \"cancer\"}\n" + 
            "              {\"Sam\", \"m\", 24, \"scorpio\"}\n" + 
            "              {\"Jenny\", \"f\", 21, \"gemini\"}\n" + 
            "              {\"Andrew\", \"m\", 26, \"virgo\"}\n" + 
            "              {\"Alice\", \"f\", 20, \"pices\"}\n" + 
            "              {\"Ingrid\", \"f\", 23, \"cancer\"}\n" + 
            "              {\"Jack\", \"m\", 32, \"pices\"}\n" + 
            "              {\"Sonia\", \"f\", 0, \"gemini\"}\n" + 
            "              {\"Alex\", \"m\", 22, \"aquarius\"}\n" + 
            "              {\"Jill\", \"f\", 33, \"cancer\"}\n" + 
            "              {\"Fiona\", \"f\", 29, \"gemini\"}\n" + 
            "              {\"Melissa\", \"f\", 30, \"virgo\"}\n" + 
            "              {\"Tom\", \"m\", 22, \"cancer\"}\n" + 
            "              {\"Bill\", \"m\", 19, \"virgo\"};\n" + 
            "list person_list(person);\n" +
            "calc people_by_starsign\n" +
            "+ list<axiom> candidates {};\n" +
            "(\n" +
            "  string starsign,\n" +
            "  integer i = 0,\n" +
            "  candidates,\n" +
            "  {\n" +
            "    ? i < person_list.length,\n" +
            "    ? person_list[i].starsign == starsign\n" +
            "    {\n" +
            "       age = person_list[i].age,\n" +
            "       ? age < 18\n" +
            "       { age = unknown },\n" +
            "       candidates += axiom person\n" +
            "       {\n" +
            "         string name = person_list[i].name,\n" +
            "         string sex = person_list[i].sex,\n" +
            "         age,\n" +
            "         string starsign = person_list[i].starsign\n" +
            "       }\n" +
            "    },\n" +
            "    ++i\n" +
            "  }\n" +
            ");\n" +
            "calc match\n" +
            "+ export list<axiom> eligible {};\n" +
            "(\n" +
            "  <- people_by_starsign(\"gemini\") -> (candidates),\n" +
            "  integer i = 0,\n" +
            "  {\n" +
            "    ? i < candidates.length,\n" +
            "    gemini = candidates[i++],\n" +
            "    ? gemini.fact\n" +
            "    {\n" +
            "      eligible += axiom person\n" +
            "      {\n" + 
            "       string name = gemini->name,\n" +
            "       string sex = gemini->sex,\n" +
            "       integer age = gemini->age,\n" +
            "       string starsign = gemini->starsign\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            " );\n" +
            " query match(match);";

    static String[] PERFECT_GEMINIS = 
    {
        "person(name=John, sex=m, age=23, starsign=gemini)",
        "person(name=Jenny, sex=f, age=21, starsign=gemini)",
        "person(name=Sonia, sex=f, age=33, starsign=gemini)",
        "person(name=Fiona, sex=f, age=29, starsign=gemini)"
    };
    
    static String[] FACTUAL_GEMINIS = 
    {
        "person(name=John, sex=m, age=23, starsign=gemini)",
        "person(name=Jenny, sex=f, age=21, starsign=gemini)",
        "person(name=Fiona, sex=f, age=29, starsign=gemini)"
    };

    QueryProgram queryProgram;
    
    @Before
    public void setUp()
    {
    	queryProgram = new QueryProgram(provideFunctionManager());
    }

    

    @Test
    public void test_gemini_people()
    {
        queryProgram.parseScript(PERFECT_MATCH);
        //queryProgram.setExecutionContext(new ExecutionContext());
        Result result = queryProgram.executeQuery("match");
        Iterator<Axiom> geminiIterator = result.axiomIterator("match.geminis");
        int index = 0;
        while(geminiIterator.hasNext())
            //System.out.println(geminiIterator.next().toString());
            assertThat(geminiIterator.next().toString()).isEqualTo(PERFECT_GEMINIS[index++]);
        assertThat(index).isEqualTo(4);
        Axiom match = result.getAxiom("match");
        //System.out.println(axiom); //iterator.next().toString()); 
        AxiomList axiomList = (AxiomList) (match.getTermByName("candidate_list").getValue());
        for (index = 0; index < axiomList.getLength(); ++index)
            //System.out.println(axiomList.getItem(index).getAxiom().toString());
            assertThat(axiomList.getItem(index).getAxiom().toString()).isEqualTo(PERFECT_GEMINIS[index++]);
        assertThat(index).isEqualTo(4);
    }
    
    @Test
    public void test_factual_people()
    {
        queryProgram.parseScript(FACTUAL_MATCH);
        Result result = queryProgram.executeQuery("match");
        QualifiedName qname = QualifiedName.parseGlobalName("match.eligible");
        Iterator<Axiom> iterator = result.axiomIterator(qname);
        int index = 0;
        while(iterator.hasNext())
            //System.out.println(iterator.next().toString());
            assertThat(iterator.next().toString()).isEqualTo(FACTUAL_GEMINIS[index++]);
        assertThat(index).isEqualTo(3);
    }
    
    @Test
    public void test_sort_cities()
    {
        queryProgram.parseScript(SORTED_CITIES);
        //queryProgram.setExecutionContext(new ExecutionContext());
        Result result = queryProgram.executeQuery("sort_cities");
        Iterator<Axiom> iterator = result.axiomIterator(QualifiedName.parseGlobalName("city_list"));
        int index = 0;
        while(iterator.hasNext())
            //System.out.println(iterator.next().toString());
            assertThat(iterator.next().toString()).isEqualTo(SORTED_CITIES_LIST[index++]);
        assertThat(index).isEqualTo(SORTED_CITIES_LIST.length);
    }
    
    @Test
    public void test_two_argument()
    {
    	queryProgram.parseScript(TWO_ARG_CALC);
        queryProgram.executeQuery("two_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                Long result = (Long)solution.getValue("test", "x");
                assertThat(result).isEqualTo(3);
                return false;
            }});
    }

    @Test
    public void test_three_argument()
    {
    	queryProgram.parseScript(THREE_ARG_CALC);
        queryProgram.executeQuery("three_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                Long result = (Long)solution.getValue("test", "x");
                assertThat(result).isEqualTo(6);
                return false;
            }});
    }

    @Test
    public void test_four_argument()
    {
    	queryProgram.parseScript(FOUR_ARG_CALC);
        queryProgram.executeQuery("four_arg_query", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                Long result = (Long) solution.getValue("test", "x");
                assertThat(result).isEqualTo(12+42+93+55);
                return false;
            }});
    }

    @Test
    public void test_three_variables()
    {
    	queryProgram.parseScript(GRADES_CALC);
        queryProgram.executeQuery("marks", new SolutionHandler(){
            int index = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("score").toString());
                assertThat(solution.getAxiom("score").toString()).isEqualTo(GRADES_RESULTS2[index++]);
                return true;
            }});
    }

    @Test
    public void test_term_variables()
    {
        queryProgram.parseScript(MARKS_CALC);
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
    public void test_school_grades()
    {
        queryProgram.parseScript(MARKS_GRADES_CALC);
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
        Result result = test_school_report(SCHOOL_REPORT_IN_SCOPE);
        Iterator<Axiom> iterator = result.axiomIterator("marks");
        int index = 0;
        while (iterator.hasNext())
        {
            Axiom score = iterator.next();
            AxiomTermList marksList = (AxiomTermList)(score.getTermByName("math_score").getValue());
            //System.out.println(marksList.getAxiom().toString());
            assertThat(marksList.getAxiom().toString()).isEqualTo(MATH_SCORES[index]);
            //System.out.println(score.getTermByName("total"));
            assertThat(score.getTermByName("marks_total").toString()).isEqualTo(MARKS_GRADES_RESULTS2[index++]);
        }
    }
    
    protected Result test_school_report(String xpl)
    {
        queryProgram.parseScript(xpl);
        Result result = queryProgram.executeQuery("marks", new SolutionHandler(){
            int index1 = 0;  
            int index2 = 0;  
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom score = solution.getAxiom("score");
                assertThat(solution.getString("grades", "student")).isEqualTo(STUDENTS[index1++]);
                AxiomList marksList = (AxiomList)score.getTermByName("marks").getValue();
                Iterator<AxiomTermList> subjects = marksList.iterator();
                while (subjects.hasNext())
                {
                    Axiom subject = subjects.next().getAxiom();
                    //System.out.println(subject.getTermByIndex(0).getValue().toString() + " " + subject.getTermByIndex(1).getValue().toString());
                    assertThat(subject.getTermByIndex(0).getValue().toString() + " " + subject.getTermByIndex(1).getValue().toString()).isEqualTo(SCHOOL_REPORT[index2++]);
                }
                //System.out.println(item.getAxiom().getTermByIndex(1).getValue());
                assertThat(score.getTermByName("total").getValue().toString()).isEqualTo(SCHOOL_REPORT[index2++]);
                return true;
            }});
        return result;
    }

    @Test
    public void test_list_variables()
    {
        queryProgram.parseScript(CITY_AVERAGE_HEIGHT_CALC);
        queryProgram.executeQuery("average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("average").toString());
                long averageHeight = (1718+8000+5280+6970+8+10200+1305+19+1909+1305)/10;
                Long result = (Long)solution.getValue("average", "average_height");
                assertThat(result).isEqualTo(averageHeight);
                return true;
            }});
    }

    @Test
    public void test_calculator()
    {
        final long averageHeight = (1718+8000+5280+6970+8+10200+1305+19+1909+1305)/10;
        queryProgram.parseScript(CITY_AVERAGE_HEIGHT_CALC2);
        queryProgram.executeQuery("average_height", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                System.out.println(solution.getAxiom("average_height").toString());
                //Axiom result = solution.getAxiom("average_height");
                //assertThat(result.toString()).isEqualTo("average_height(average=" + averageHeight + ")");
                return true;
            }});
    }

    @Test
    public void test_choice_german_colors()
    {
        queryProgram.parseScript(GERMAN_COLORS);
        Result result = queryProgram.executeQuery("colors");
        Iterator<Axiom> iterator = result.axiomIterator("colors.german_colors@");
        //System.out.println(iterator.next().toString());
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next().toString()).isEqualTo("aqua(red=0, green=255, blue=255)");
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next().toString()).isEqualTo("blue(red=0, green=0, blue=255)");
    }
    
    @Test
    public void test_choice_german_missing_color()
    {
        queryProgram.parseScript(GERMAN_COLORS);
        queryProgram.executeQuery("orange", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom germanOrange = solution.getAxiom("german_orange");
                //System.out.println(germanOrange.toString());
                assertThat(germanOrange.toString()).isEqualTo("german_orange(hasRed=false, hasGreen=false, hasBlue=false)");
                return true;
            }});
    }
    
    FunctionManager provideFunctionManager()
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
