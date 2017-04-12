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
package au.com.cybersearch2.classy_logic.tutorial9;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * CalculateSquareMiles
 * Demonstrates using a calculator to convert surface area in square kilometres to square miles. 
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles 
{
    /* calculate-square-miles.xpl
    include "surface-land.xpl";
    template surface_area(country, double surface_area_Km2);
    // Calculator declaration:
    calc km2_to_mi2 (country, double surface_area_mi2 = surface_area.surface_area_Km2 *= 0.3861);
    // Result list receives calculator solution
    list surface_area(km2_to_mi2);
    // Chained query with calculator performing conversion:
    query surface_area_mi2(surface_area : surface_area)
      >> (km2_to_mi2);
      
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CalculateSquareMiles()
	{	
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/"));
	}
	
    /**
     * Compiles the COUNTRY_SURFACE_AREA script and runs the "surface_area_mi2" query, displaying the solution on the console.<br/>
     * The expected first 3 results:<br/>
        km2_to_mi2(country = Afghanistan, surface_area_mi2 = 251826.003)<br/>
        km2_to_mi2(country = Albania, surface_area_mi2 = 11100.375)<br/>
        km2_to_mi2(country = Algeria, surface_area_mi2 = 919589.814)<br/>
     */
	public Iterator<Axiom> getSurfaceAreas()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("tutorial9/calculate-square-miles.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("surface_area_mi2", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {   // Uncomment to see intermediate results
                //System.out.println(solution.getAxiom("surface_area"));
                //System.out.println(solution.getAxiom("km2_to_mi2"));
                return true;
            }});
		return  result.getIterator(QualifiedName.parseGlobalName("surface_area"));
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		try 
		{
	        CalculateSquareMiles calculateSquareMiles = new CalculateSquareMiles();
	        Iterator<Axiom> iterator = calculateSquareMiles.getSurfaceAreas();
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
