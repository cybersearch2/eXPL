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
    /*
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
  (age     , age_weight)
  {age > 29, 0.3}
  {age > 25, 0.6}
  {age > 20, 1.0}
  {age < 21, NaN};

calc perfect_match
(
. rating = unknown,
. <- age_rating(age) -> (age_weight),
  ? age_weight.fact {rating = age_weight},
  name, sex, starsign, age_rating = rating
);

query<axiom> star_people(person : perfect_match);

     */
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public AgeDiscrimination3()
    {
        File resourcePath = new File("src/main/resources/tutorial17");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the STAR_PERSON script and runs the "star_people" query which gives each person 
     * over the age of 20 an age rating and those not rated have an unknown value.<br/>
     * The expected result:<br/>
        (name=John, sex=m, starsign=gemini, age_rating=1.0)<br/>
        (name=Sue, sex=f, starsign=cancer, age_rating=unknown)<br/>
        (name=Sam, sex=m, starsign=scorpio, age_rating=0.3)<br/>
        (name=Jenny, sex=f, starsign=gemini, age_rating=0.6)<br/>
        (name=Andrew, sex=m, starsign=virgo, age_rating=0.6)<br/>
        (name=Alice, sex=f, starsign=pices, age_rating=unknown)<br/>
        (name=Ingrid, sex=f, starsign=cancer, age_rating=1.0)<br/>
        (name=Jack, sex=m, starsign=pices, age_rating=0.3)<br/>
        (name=Sonia, sex=f, starsign=gemini, age_rating=0.3)<br/>
        (name=Alex, sex=m, starsign=aquarius, age_rating=1.0)<br/>
        (name=Jill, sex=f, starsign=cancer, age_rating=0.3)<br/>
        (name=Fiona, sex=f, starsign=gemini, age_rating=0.6)<br/>
        (name=melissa, sex=f, starsign=virgo, age_rating=0.3)<br/>
        (name=Tom, sex=m, starsign=cancer, age_rating=1.0)<br/>
        (name=Bill, sex=m, starsign=virgo, age_rating=unknown)<br/>     
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
