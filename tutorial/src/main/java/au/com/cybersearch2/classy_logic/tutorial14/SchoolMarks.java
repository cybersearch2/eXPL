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
 * Demonstrates library function call
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class SchoolMarks
{
/* school-marks.xpl
axiom grades 
  (student, english, math, history)
  {"Amy",    14, 16, 6}
  {"George", 15, 13, 16}
  {"Sarah",  12, 17, 14};
  
template score(student, integer total = math.add(english, math, history));

query<axiom> marks(grades : score); 
 
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public SchoolMarks()
    {
        File resourcePath = new File("src/main/resources/tutorial14");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the school-marks.xpl script and runs the "marks" query.<br/>
     * The expected results:<br/>
        score(student = Amy, total = 36)<br/>
        score(student = George, total = 44)<br/>
        score(student = Sarah, total = 43)<br/>
     * @return Axiom iterator
     */
    public Iterator<Axiom>  generateReport()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("school-marks.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("marks");
        return result.axiomIterator("marks");
    }

    FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        SchoolFunctionProvider schoolFunctionProvider = new SchoolFunctionProvider();
        functionManager.putFunctionProvider(schoolFunctionProvider.getName(), schoolFunctionProvider);
        return functionManager;
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
            SchoolMarks schoolMarks = new SchoolMarks();
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
