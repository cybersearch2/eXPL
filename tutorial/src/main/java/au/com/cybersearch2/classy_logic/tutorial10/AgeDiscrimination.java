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
package au.com.cybersearch2.classy_logic.tutorial10;

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
 * @author Andrew Bowley
 * 11 Sep 2015
 */
public class AgeDiscrimination
{
 /* age-discrimination.xpl
 axiom person (name, sex, age, starsign)
              {"John", "m", 23, "gemini"} 
              {"Sue", "f", 19, "cancer"} 
              {"Sam", "m", 34, "scorpio"} 
              {"Jenny", "f", 28, "gemini"} 
              {"Andrew", "m", 26, "virgo"} 
              {"Alice", "f", 20, "pices"} 
              {"Ingrid", "f", 23, "cancer"} 
              {"Jack", "m", 32, "pices"} 
              {"Sonia", "f", 33, "gemini"} 
              {"Alex", "m", 22, "aquarius"} 
              {"Jill", "f", 33, "cancer"} 
              {"Fiona", "f", 29, "gemini"} 
              {"melissa", "f", 30, "virgo"} 
              {"Tom", "m", 22, "cancer"} 
              {"Bill", "m", 19, "virgo"}; 
 choice age_rating
   (age     , age_weight, name)
   {age > 29, 0.3}
   {age > 25, 0.6}
   {age > 20, 1.0};
 
 query<axiom> rate_age (person : age_rating);
 
 */
            
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public AgeDiscrimination()
    {
        File resourcePath = new File("src/main/resources/tutorial10");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the PERSON_RATING script and runs the "rate_age" query which gives each person 
     * over the age of 20 an age rating and excludes those aged 20 and under.<br/>
     * The first 3 expected results:<br/>
    age_rating(age = 23, age_weight = 1.0, name = John)<br/>
    age_rating(age = 34, age_weight = 0.3, name = Sam)<br/>
    age_rating(age = 28, age_weight = 0.6, name = Jenny)<br/>
     * @return Axiom iterator
     */
    public Iterator<Axiom> getAgeRating()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("age-discrimination.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("rate_age");
        return result.getIterator("rate_age");
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
            AgeDiscrimination ageDiscrimination = new AgeDiscrimination();
            Iterator<Axiom> iterator = ageDiscrimination.getAgeRating();
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
