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
 * Demonstrates how to deal with calculator query short circuits using fact keyword
 * @author Andrew Bowley
 * 11 Sep 2015
 */
public class PerfectMatch
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
             {"Jack", "m", 32, "pisces"} 
             {"Sonia", "f", 33, "gemini"} 
             {"Alex", "m", 22, "aquarius"} 
             {"Jill", "f", 33, "cancer"} 
             {"Fiona", "f", 29, "gemini"} 
             {"Melissa", "f", 30, "virgo"} 
             {"Tom", "m", 22, "cancer"} 
             {"Bill", "m", 19, "virgo"};
              
choice age_rating
  (age,       age_weight)
  {age >  29, 0.3}
  {age >  25, 0.6}
  {age >= 20, 1.0}
  {age <= 19, NaN};

calc perfect_match
(
  Name = name, 
  Age = age,
  Starsign = starsign, 
. choice age_rating,
  Rating = age_weight.format
);

query<axiom> star_people(person : perfect_match);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public PerfectMatch()
    {
        File resourcePath = new File("src/main/resources/tutorial10");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the STAR_PERSON script and runs the "star_people" query which gives each person 
     * over the age of 20 an age rating and those not rated have an unknown value.<br/>
     * The expected result:<br/>
        (Name=John, Starsign=gemini, Rating=1)<br/>
        (Name=Sue, Starsign=cancer, Rating=unknown)<br/>
        (Name=Sam, Starsign=scorpio, Rating=0.3)<br/>
        (Name=Jenny, Starsign=gemini, Rating=0.6)<br/>
        (Name=Andrew, Starsign=virgo, Rating=0.6)<br/>
        (Name=Alice, Starsign=pices, Rating=1)<br/>
        (Name=Ingrid, Starsign=cancer, Rating=1)<br/>
        (Name=Jack, Starsign=pisces, Rating=0.3)<br/>
        (Name=Sonia, Starsign=gemini, Rating=0.3)<br/>
        (Name=Alex, Starsign=aquarius, Rating=1)<br/>
        (Name=Jill, Starsign=cancer, Rating=0.3)<br/>
        (Name=Fiona, Starsign=gemini, Rating=0.6)<br/>
        (Name=Melissa, Starsign=virgo, Rating=0.3)<br/>
        (Name=Tom, Starsign=cancer, Rating=1)<br/>
        (Name=Bill, Starsign=virgo, Rating=unknown)<br/>     
     * @return Axiom iterator
     */
    public Iterator<Axiom> getAgeRating()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("perfect-match.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("star_people");
        return result.axiomIterator("star_people");
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
            PerfectMatch perfectMatch = new PerfectMatch();
            Iterator<Axiom> iterator = perfectMatch.getAgeRating();
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
