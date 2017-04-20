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
package au.com.cybersearch2.classy_logic.tutorial17;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * AgeDiscrimination2
 * Demonstrates how to deal with calculator query short circuits using fact keyword
 * @author Andrew Bowley
 * 11 Sep 2015
 */
public class AgeDiscrimination3
{
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public AgeDiscrimination3()
    {
        File resourcePath = new File("src/main/resources/tutorial17");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the STAR_PERSON script and runs the "star_people" query which gives each person 
     * over the age of 20 an age rating and excludes those aged 20 and under.<br/>
     * The expected result:<br/>
     * @return Axiom iterator
     */
    public Iterator<Axiom> getAgeRating()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("age-discrimination3.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("star_people");
        return result.getIterator("star_people");
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
            AgeDiscrimination3 ageDiscrimination3 = new AgeDiscrimination3();
            Iterator<Axiom> iterator = ageDiscrimination3.getAgeRating();
            while(iterator.hasNext())
            {
                System.out.println(iterator.next().toString().substring(13));
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
