/**
    Copyright (C) 2014  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial7;

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
 * HighCities
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * This example features using an Axiom variable to grow the result list by concatenation.
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * A template cannot perform Axiom variable operations, so a calculator is used for this example.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class Birds
{
/* birds.xpl
axiom order (order, nostrils, live, bill, feet, eats)
  {"tubenose", "external tubular", "at sea", "hooked", "webbed", ""}
  {"waterfowl", "", "", "flat", "webbed", ""}
  {"falconiforms", "", "", "sharp hooked", "curved talons", "meat"}
  {"passerformes", "", "", "", "one long backward toe", ""};

axiom family (family, order, size, wings, neck, color, flight, feed, head, tail, bill, eats)
  {"albatross", "tubenose", "large", "long narrow", "", "", "", "", "", "", "", ""}
  {"procellariidae", "tubenose", "", "long narrow", "", "", "", "", "", "", "", ""}
  {"swan", "waterfowl", "", "", "long", "white", "ponderous", "", "", "", "", ""}
  {"goose", "waterfowl", "plump", "", "", "", "powerful", "", "", "", "", ""}
  {"duck", "waterfowl", "", "", "", "", "agile", "on water surface", "", "", "", ""} 
  {"vulture", "falconiforms", "", "broad", "", "", "", "scavange", "", "", "", ""}
  {"falcon", "falconiforms", "", "long pointed", "", "", "", "", "large", "narrow at tip", "", ""}
  {"flycatcher", "passerformes", "", "", "", "", "",  "", "", "", "flat", "flying insects"}
  {"swallow", "passerformes", "", "long pointed", "", "", "", "", "", "forked", "short", ""};

axiom bird (bird, family, color, size, flight, throat, voice, eats, tail)
  {"laysan_albatross", "albatross", "white", "", "", "", "", "", ""}
  {"black footed albatross",  "albatross", "dark", "", "", "", "", "", ""}
  {"fulmar", "procellariidae", "", "medium", "flap glide", "", "", "", ""}
  {"whistling swan", "swan", "", "", "", "", "muffled musical whistle", "", ""}
  {"trumpeter swan", "swan", "", "", "", "", "loud trumpeting", "", ""}
  {"snow goose", "goose", "white", "", "", "", "honks", "", ""}
  {"pintail", "duck", "", "", "", "", "short whistle", "", ""}
  {"turkey vulture", "vulture", "", "", "v shaped", "", "", "", ""}
  {"california condor", "vulture", "", "", "flat", "", "", "", ""}
  {"sparrow hawk", "falcon", "", "", "", "", "", "insects", ""}
  {"peregrine falcon", "falcon", "", "", "", "", "", "birds", ""}
  {"great crested flycatcher", "flycatcher", "", "", "", "", "", "", "long rusty"}
  {"ash throated flycatcher", "flycatcher", "", "", "", "white", "", "", ""}
  {"barn swallow", "swallow", "", "", "", "", "", "", "forked"}
  {"cliff swallow", "swallow", "", "", "", "", "", "", "square"}
  {"purple martin", "swallow", "dark", "", "", "", "", "", ""};

template order
(
  order, nostrils, live, bill, feet, eats
);

template family
(
  family, order ? order == order.order, size, wings, neck, color, flight, feed, head, feet = order.feet, tail, bill, eats
);

template species
+ export list<axiom> waterfowl {};
(
  family ? family == family.family,
  waterfowl += axiom waterfowl { bird , voice, feet = order.feet } ? family.order == "waterfowl"
);

query birds (order:order, family:family, bird:species);  

*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Birds()
    {
        File resourcePath = new File("src/main/resources/tutorial7");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the birds.xplS script and runs the "birds" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
        waterfowl(bird=whistling swan, voice=muffled musical whistle, feet=webbed)<br/>
        waterfowl(bird=trumpeter swan, voice=loud trumpeting, feet=webbed)<br/>
        waterfowl(bird=snow goose, voice=honks, feet=webbed)<br/>
        waterfowl(bird=pintail, voice=short whistle, feet=webbed)<br/>
        @return Axiom iterator
  	 */
	public Iterator<Axiom> getBirds()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("birds.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("birds"); 
		return result.axiomIterator("species.waterfowl");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }

    public static void main(String[] args)
	{
		try 
		{
		    Birds birds = new Birds();
	        Iterator<Axiom> iterator = birds.getBirds();
	        while(iterator.hasNext())
	            System.out.println(iterator.next().toString());
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
