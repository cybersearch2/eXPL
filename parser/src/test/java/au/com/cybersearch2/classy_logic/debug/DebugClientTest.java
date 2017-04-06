/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.debug;

import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * DebugClientTest
 * @author Andrew Bowley
 * 5Apr.,2017
 */
public class DebugClientTest
{
    static final String PERSON_RATING = 
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
            "  (age     , age_weight, name)\n" +
            "  {age > 29, 0.3}\n" +
            "  {age > 25, 0.6}\n" +
            "  {age > 20, 1.0};\n" +
            "list rated(age_rating);\n" +
            "query rate_age (person : age_rating);";

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
        QueryProgram queryProgram = new QueryProgram(PERSON_RATING);
        Result result = queryProgram.executeQuery("rate_age");
        return result.getIterator(QualifiedName.parseGlobalName("rated"));
    }
    
    @Test
    public void testAgeRating()
    {
        Iterator<Axiom> iterator = getAgeRating();
        while(iterator.hasNext())
        {
            System.out.println(iterator.next().toString());
        }
    }
}
