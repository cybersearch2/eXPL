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
package au.com.cybersearch2.classy_logic.tutorial18;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.parser.FileAxiomProvider;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ForeignColors
 * Demonstrates Choice selection terms consisting of local axiom terms for locale-sensitive matching of values.
 * The Choice selects a color swatch by name in the language of the locale.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class ForeignColors 
{
/* foreign-colors.xpl
axiom german.colors (aqua, black, blue, white) : resource;
axiom french.colors (aqua, black, blue, white) : resource;

local select(colors);

choice swatch (name, red, green, blue)
  {select^aqua, 0, 255, 255}
  {select^black, 0, 0, 0}
  {select^blue, 0, 0, 255}
  {select^white, 255, 255, 255};
  
axiom shade (name) : parameter;

scope french (language="fr", region="FR")
{
  query<term> color_query (shade : swatch);
}

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}

*/
    protected QueryProgramParser queryProgramParser;
    protected static FileAxiomProvider[] fileAxiomProviders;
    ParserContext parserContext;

    public ForeignColors()
    {
        File testPath = new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION);
        if (!testPath.exists())
            testPath.mkdir();
        File resourcePath = new File("src/main/resources/tutorial18");
        fileAxiomProviders = new FileAxiomProvider[2];
        fileAxiomProviders[0] = new FileAxiomProvider("german.colors", testPath);
        fileAxiomProviders[1] = new FileAxiomProvider("french.colors", testPath);
        queryProgramParser = new QueryProgramParser(resourcePath, fileAxiomProviders[0], fileAxiomProviders[1]);
    }

    public List<Axiom> createForeignLexicon()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("foreign-lexicon.xpl");
        parserContext = queryProgramParser.getContext();
        try
        {
            Result result = queryProgram.executeQuery("color_query"); 
            List<Axiom> axiomList = new ArrayList<Axiom>();
            axiomList.add(result.getAxiom("german_list"));
            axiomList.add(result.getAxiom("french_list"));
            return axiomList;
        }
        finally
        {
            fileAxiomProviders[0].close();
            fileAxiomProviders[1].close();
        }
    }
    
    /**
	 * Compiles the GERMAN_COLORS script and runs the "color_query" query, displaying the solution on the console.
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public String getColorSwatch(String language, String name)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("foreign-colors.xpl");
        parserContext = queryProgramParser.getContext();
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(language, "color_query");
        // Add a shade Axiom with a specified color term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", name)));
        Result result = queryProgram.executeQuery(queryParams);
        return result.getAxiom(language, "color_query").toString();
	}
	
    /**
     * Run tutorial
     * The expected result:<br/>
        german_list(aqua=Wasser, black=schwarz, blue=blau, white=weiß)<br/>
        french_list(aqua=bleu vert, black=noir, blue=bleu, white=blanc)<br/>
        color_query(name=bleu vert, red=0, green=255, blue=255, swatch=0)<br/>
        color_query(name=noir, red=0, green=0, blue=0, swatch=1)<br/>
        color_query(name=blanc, red=255, green=255, blue=255, swatch=3)<br/>
        color_query(name=bleu, red=0, green=0, blue=255, swatch=2)<br/>
        color_query(name=Wasser, red=0, green=255, blue=255, swatch=0)<br/>
        color_query(name=schwarz, red=0, green=0, blue=0, swatch=1)<br/>
        color_query(name=weiß, red=255, green=255, blue=255, swatch=3)<br/>
        color_query(name=blau, red=0, green=0, blue=255, swatch=2) <br/>    
        * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        ForeignColors foreignColors = new ForeignColors();
	        Iterator<Axiom> colors = foreignColors.createForeignLexicon().iterator();
	        while (colors.hasNext())
	            System.out.println(colors.next());
            System.out.println(foreignColors.getColorSwatch("french", "bleu vert"));
            System.out.println(foreignColors.getColorSwatch("french", "noir"));
            System.out.println(foreignColors.getColorSwatch("french", "blanc"));
            System.out.println(foreignColors.getColorSwatch("french", "bleu"));
            System.out.println(foreignColors.getColorSwatch("german", "Wasser"));
            System.out.println(foreignColors.getColorSwatch("german", "schwarz"));
            System.out.println(foreignColors.getColorSwatch("german", "weiß"));
            System.out.println(foreignColors.getColorSwatch("german", "blau"));
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
		finally
		{
		    fileAxiomProviders[0].close();
            fileAxiomProviders[1].close();
		}
		System.exit(0);
	}
}
