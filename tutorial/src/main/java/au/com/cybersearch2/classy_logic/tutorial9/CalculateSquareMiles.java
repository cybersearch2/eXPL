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

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.DaggerTestComponent;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.TestComponent;
import au.com.cybersearch2.classy_logic.TestModule;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classyinject.DI;

/**
 * CalculateSquareMiles
 * Demonstrates using a calculator to convert surface area in square kilometres to square miles. 
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles 
{
	static final String COUNTRY_SURFACE_AREA = 
			"include \"surface-land.xpl\";\n" +
			"template surface_area(country, double surface_area_Km2);\n" +
			"// Calculator declaration:\n" +
			"calc km2_to_mi2 (country, double surface_area_mi2 = surface_area.surface_area_Km2 *= 0.3861);" +
			"// Result list receives calculator solution\n" +
			"list surface_area(km2_to_mi2);\n" +
            "// Chained query with calculator performing conversion:\n" +
		    "query surface_area_mi2(surface_area : surface_area)\n" + 
		    "  >> (km2_to_mi2);";

	public CalculateSquareMiles()
	{	
        TestComponent component = 
                DaggerTestComponent.builder()
                .testModule(new TestModule())
                .build();
        DI.getInstance(component);
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
		QueryProgram queryProgram = new QueryProgram(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("surface_area"));
                //System.out.println(solution.getAxiom("km2_to_mi2"));
                return true;
            }});
		return  result.getIterator(QualifiedName.parseGlobalName("surface_area"));
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
