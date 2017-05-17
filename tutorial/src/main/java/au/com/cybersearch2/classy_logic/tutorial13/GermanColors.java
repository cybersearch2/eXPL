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
package au.com.cybersearch2.classy_logic.tutorial13;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * GermanColors
 * Demonstrates Choice selection terms consisting of local axiom terms for locale-sensitive matching of values.
 * The Choice selects a color swatch by name in the language of the locale.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class GermanColors 
{
/* german-colors.xpl
// Define lexicon term names
axiom lexicon (aqua, black, blue, white);

axiom german.lexicon 
  (    aqua,    black,    blue,  white)
  {"Wasser", "schwarz", "blau", "weiß"};
 
choice swatch 
  (   name,      red, green, blue)
  {colors[aqua],    0, 255, 255}
  {colors[black],   0,   0,   0}
  {colors[blue],    0  , 0, 255}
  {colors[white], 255, 255, 255};
  
axiom shade (name) : parameter;

local colors(lexicon);

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}
  
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public GermanColors()
    {
        File resourcePath = new File("src/main/resources/tutorial13");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the GERMAN_COLORS script and runs the "color_query" query, displaying the solution on the console.
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public String getColorSwatch(String name)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("german-colors.xpl");
        parserContext = queryProgramParser.getContext();
         
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams("german", "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", name)));
        Result result = queryProgram.executeQuery(queryParams);
        return result.getAxiom("german", "color_query").toString();
	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * The expected result:<br/>
        color_query(name=Wasser, red=0, green=255, blue=255, swatch=0)<br/>
        color_query(name=schwarz, red=0, green=0, blue=0, swatch=1)<br/>
        color_query(name=weiß, red=255, green=255, blue=255, swatch=3)<br/>
        color_query(name=blau, red=0, green=0, blue=255, swatch=2)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        GermanColors germanColors = new GermanColors();
            System.out.println(germanColors.getColorSwatch("Wasser"));
            System.out.println(germanColors.getColorSwatch("schwarz"));
            System.out.println(germanColors.getColorSwatch("weiß"));
            System.out.println(germanColors.getColorSwatch("blau"));
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
