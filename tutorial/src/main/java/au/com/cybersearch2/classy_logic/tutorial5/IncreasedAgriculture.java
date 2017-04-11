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
package au.com.cybersearch2.classy_logic.tutorial5;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * IncreasedAgriculture demonstrates evaluation for the purpose of numerical analysis.
 * The 'more_agriculture' query produces a list of countries which have increased the area
 * under agriculture by more than 1% over the twenty years between 1990 and 2010.  
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class IncreasedAgriculture 
{
/* more_agriculture.xpl
include "agriculture-land.xpl";
include "surface-land.xpl";
list nation_list(surface_area_increase);
template agri_10y (country ? Y2010 - Y1990 > 1.0, double Y1990, double Y2010);
template surface_area_increase (
  country? country == agri_10y.country,
  double surface_area = (agri_10y.Y2010 - agri_10y.Y1990)/100
    * surface_area_Km2);
query more_agriculture(Data : agri_10y, surface_area : surface_area_increase); 
*/
    protected QueryProgramParser queryProgramParser;
    
    public IncreasedAgriculture()
    {
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/"));
     }

    /**
     * Compiles the more_agriculture.xpl script and runs the "more_agriculture" query
     * @return Axiom iterator
     */
    public ParserContext  findIncreasedAgriculture(SolutionHandler solutionHandler) 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("tutorial5/more_agriculture.xpl");
        queryProgram.executeQuery("more_agriculture", solutionHandler);
        return queryProgramParser.getContext();
    }


	/**
     * Displays the solution on the console.<br/>
	 * The expected result first 3 lines:<br/>
        increased(country = Albania, surface_area = 986.1249999999999, id = 0)<br/>
        increased(country = Algeria, surface_area = 25722.79200000004, id = 1)<br/>
        increased(country = American Samoa, surface_area = 10.0, id = 2)<br/><br/>
     * The full result can be viewed in file src/main/resources/tutorial5/more_agriculture.txt
 	 */
    public static void main(String[] args)
    {
        SolutionHandler solutionHandler = new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                System.out.println(solution.getAxiom("surface_area_increase").toString());
                return true;
            }};
        try 
        {
            IncreasedAgriculture increasedAgriculture = new IncreasedAgriculture();
            increasedAgriculture.findIncreasedAgriculture(solutionHandler);
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
