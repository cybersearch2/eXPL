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
package au.com.cybersearch2.classy_logic.tutorial8;

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
 * CalculateSquareMiles
 * Demonstrates using a calculator to convert surface area in square kilometres to square miles and
 * accumulating a total area value. 
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles 
{
    /* calculate-square-miles.xpl
include "surface-land.xpl";

axiom totals (total_area) { integer(0) };

// Calculator declaration:
calc km2_to_mi2 
+ export list<term> totals(totals);
(
  country, 
  double surface_area_mi2 = surface_area_Km2 *= 0.3861,
. totals->total_area += surface_area_mi2
);

// Calculator performing conversion on it's own:
query<axiom> convert(surface_area : km2_to_mi2);

template country_data(country, double surface_area_Km2);

// Chained query with calculator performing conversion:
query<axiom> surface_area_mi2(surface_area : country_data)
  -> (km2_to_mi2);

// Cascade query with calculator performing conversion:
query<axiom> cascade(surface_area : country_data, km2_to_mi2);
  
// Calculator using parameters for one country:
query<axiom> convert_afghan(km2_to_mi2)
(country = "Afghanistan", surface_area_Km2 = "652,230.00");
      
*/
    static final String[] QUERY_LIST =
    {
        "surface_area_mi2",
        "convert",
        "convert_afghan",
        "cascade"
    };
 
    static class SurfaceAreas
    {
        public Iterator<Axiom> countryIterator;
        public Long totalSquareMiles;
        
    }
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CalculateSquareMiles()
	{	
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/tutorial8"));
	}
	
    /**
     * Compiles the calculate-square-miles.xpl script and runs the "surface_area_mi2" query, displaying the solution on the console.<br/>
     * The expected first 3 results:<br/>
        km2_to_mi2(country=Antigua and Barbuda, surface_area_mi2=169.884)<br/>
        km2_to_mi2(country=Australia, surface_area_mi2=2988885.042)<br/>
        km2_to_mi2(country=Bahamas, surface_area_mi2=5359.068)<br/>
        ...<br/>
        Total area = 10754576 square miles<br/>
     */
	public SurfaceAreas getSurfaceAreas(String query)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("calculate-square-miles.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery(query);
        SurfaceAreas surfaceAreas = new SurfaceAreas();
        surfaceAreas.countryIterator = result.axiomIterator(query);
        surfaceAreas.totalSquareMiles = (Long) result.getAxiom("km2_to_mi2.totals").getTermByName("total_area").getValue();
        return surfaceAreas;
	}


    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
	    boolean doAll = false;
	    int index = 0;
	    if (args.length > 0)
	    {
	        if ("2".equals(args[0]))
	           index = 1;
	        else if ("3".equals(args[0]))
	           index = 2;
            else if ("4".equals(args[0]))
                index = 3;
            else if ("5".equals(args[0]))
                doAll = true;
	    }
		try 
		{
	        CalculateSquareMiles calculateSquareMiles = new CalculateSquareMiles();
	        if (doAll)
	        {
	            calculateSquareMiles.getSurfaceAreas(QUERY_LIST[0]);
                calculateSquareMiles.getSurfaceAreas(QUERY_LIST[1]);
                calculateSquareMiles.getSurfaceAreas(QUERY_LIST[2]);
                index = 3;
	        }
	        SurfaceAreas surfaceAreas = calculateSquareMiles.getSurfaceAreas(QUERY_LIST[index]);
	        while(surfaceAreas.countryIterator.hasNext())
	            System.out.println(surfaceAreas.countryIterator.next().toString());
	        System.out.println("Total area = " + surfaceAreas.totalSquareMiles + " square miles");
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
