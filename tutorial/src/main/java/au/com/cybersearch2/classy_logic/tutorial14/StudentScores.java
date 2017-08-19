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
package au.com.cybersearch2.classy_logic.tutorial14;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * PetNames
 * Demonstrates query call and accessing a single return value as a variable.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class StudentScores
{
/* student-scores.xpl
axiom grades (student, english, maths, history)
  {"George", 15, 13, 16}
  {"Sarah", 12, 17, 15}
  {"Amy", 14, 16, 6};
 
list<string> mark =
{
   "", // Index = 0 is out of range
   "f-", "f", "f+", "e-", "e", "e+", "d-", "d", "d+", 
   "c-", "c", "c+", "b-", "b", "b+", "a-", "a", "a+"
};

scope school
{
   calc subjects
   + list<axiom> marks_list {};
  (
    integer english,
    integer maths,
    integer history,
    marks_list =
       axiom { "English", mark[english] }
             { "Math",    mark[maths] }
             { "History", mark[history] }
  );
}

calc score
(
  <- school.subjects(english, maths, history) -> (marks_list),
  string report = student + ": " + 
    marks_list[0][0] + ":" + marks_list[0][1] + ", " + 
    marks_list[1][0] + ":" + marks_list[1][1] + ", " + 
    marks_list[2][0] + ":" + marks_list[2][1] 
);

query<axiom> marks(grades : score);
 
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StudentScores()
    {
        File resourcePath = new File("src/main/resources/tutorial14");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the student-scores.xpl script and runs the "marks" query.<br/>
     * The expected results:<br/>
        score(report=George: English:b+, Math:b-, History:a- )<br/>
        score(report=Sarah: English:c+, Math:a, History:b+ )<br/>
        score(report=Amy: English:b, Math:a-, History:e+ )<br/>
     * @return Axiom iterator
     */
    public Iterator<Axiom>  generateReport()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("student-scores.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("marks");
        return result.axiomIterator("marks");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * @param args
     */
    public static void main(String[] args)
    {
        try 
        {
            StudentScores schoolMarks = new StudentScores();
            Iterator<Axiom> iterator = schoolMarks.generateReport();
            while(iterator.hasNext())
            {
                System.out.println(iterator.next().toString());
            }
        } 
        catch (ExpressionException e) 
        { 
            e.printStackTrace();
            System.exit(1);
        }
        catch (QueryExecutionException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
