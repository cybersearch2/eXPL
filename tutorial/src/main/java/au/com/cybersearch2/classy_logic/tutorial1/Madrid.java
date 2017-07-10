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
package au.com.cybersearch2.classy_logic.tutorial1;

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.axiom.ResourceAxiomProvider;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * HighCities
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * The terms are anonymous, so unification term pairing is performed by position.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class Madrid
{
/* cities.xpl
axiom city_altitude() 
    {"bilene", 1718}
    {"addis ababa", 8000}
    {"denver", 5280}
    {"flagstaff", 6970}
    {"jacksonville", 8}
    {"leadville", 10200}
    {"madrid", 1305}
    {"richmond",19}
    {"spokane", 1909}
    {"wichita", 1305};  
*/   
/* madrid.xpl
axiom city_altitude() : resource;
template high_city(city { "madrid" }, altitude);
query<axiom> madrid (city_altitude : madrid); 
*/

    protected QueryProgramParser queryProgramParser;
 
    public Madrid()
    {
        ResourceAxiomProvider resourceAxiomProvider = new ResourceAxiomProvider("city_altitude", "cities.xpl", 1);
        queryProgramParser = new QueryProgramParser(resourceAxiomProvider);
     }

    /**
     * Compiles the madrid.xpl script and runs the "madrid" query
     */
    public Iterator<Axiom> findMadrid() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("madrid.xpl");
        Result result = queryProgram.executeQuery("madrid");
        return result.getIterator("madrid");
    }

	/**
     * Displays the solution to the madrid query on the console.<br/>
     * The expected result:<br/>
     * madrid(name=madrid, altitude=1305)<br/>
     */
	public static void main(String[] args)
	{
		try 
		{
	        Madrid madrid = new Madrid();
	        Iterator<Axiom> iterator = madrid.findMadrid();
	        while (iterator.hasNext())
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
