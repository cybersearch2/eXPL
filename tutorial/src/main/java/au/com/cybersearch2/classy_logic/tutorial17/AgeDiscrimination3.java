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

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
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
    static final String STAR_PERSON = 
            "axiom person (name, sex, age, starsign)\n" +
            "             {\"John\", \"m\", 23, \"gemini\"}\n" + 
            "             {\"Sue\", \"f\", 19, \"cancer\"}\n" + 
            "             {\"Sam\", \"m\", 34, \"scorpio\"}\n" + 
            "             {\"Jenny\", \"f\", 28, \"gemini\"}\n" + 
            "             {\"Andrew\", \"m\", 26, \"virgo\"}\n" + 
            "             {\"Alice\", \"f\", 20, \"pices\"}\n" + 
            "             {\"Ingrid\", \"f\", 23, \"cancer\"}\n" + 
            "             {\"Jack\", \"m\", 32, \"pices\"}\n" + 
            "             {\"Sonia\", \"f\", 33, \"gemini\"}\n" + 
            "             {\"Alex\", \"m\", 22, \"aquarius\"}\n" + 
            "             {\"Jill\", \"f\", 33, \"cancer\"}\n" + 
            "             {\"Fiona\", \"f\", 29, \"gemini\"}\n" + 
            "             {\"melissa\", \"f\", 30, \"virgo\"}\n" + 
            "             {\"Tom\", \"m\", 22, \"cancer\"}\n" + 
            "             {\"Bill\", \"m\", 19, \"virgo\"};\n" + 
            "choice age_rating\n" +
            "  (age     , age_weight)\n" +
            "  {age > 29, 0.3}\n" +
            "  {age > 25, 0.6}\n" +
            "  {age > 20, 1.0}\n" +
            "  {age < 21, NaN};\n" +
            "axiom star_people = {}\n;" +
            "calc perfect_match(\n" +
            "  template age_rating(age_weight) << age_rating(age),\n" +
            "  rating = unknown,\n" +
            "  ? fact(age_weight) {rating = age_weight} ,\n" +
            "  axiom star_person = { name, sex, starsign, rating },\n" +
            "  star_people += star_person\n" +
            ");\n" +
           "query star_people(person : perfect_match);";
            
    /**
     * Compiles the STAR_PERSON script and runs the "star_people" query which gives each person 
     * over the age of 20 an age rating and excludes those aged 20 and under.<br/>
     * The expected result:<br/>
     * payable(duty = 3768.32)<br/>
     * @return Axiom iterator
     */
    public Iterator<Axiom> getAgeRating()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(STAR_PERSON);
        Result result = queryProgram.executeQuery("star_people");
        return result.getIterator(QualifiedName.parseGlobalName("star_people"));
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
