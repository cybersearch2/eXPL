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
package au.com.cybersearch2.classy_logic.tutorial3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.compile.TemplateAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Lists
 * Demonstrates list types applied to template terms. 
 * @author Andrew Bowley
 * 4 Jul 2017
 */
public class Lists 
{
/* lists.xpl
   
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public Lists()
    {
        File resourcePath = new File("src/main/resources/tutorial3");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the lists.xpl script and runs the "lists" query
     */
    public List<Axiom> checkTypes() 
    {
        List<Axiom> result = new ArrayList<Axiom>();
        QueryProgram queryProgram = queryProgramParser.loadScript("lists.xpl");
        parserContext = queryProgramParser.getContext();
        TemplateAssembler templateAssembler = parserContext.getParserAssembler().getTemplateAssembler();
        Template template = templateAssembler.getTemplate("fruit");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("dice");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("dimensions");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("huges");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("flags");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("stars");
        template.evaluate(null);
        result.add(template.toAxiom());
        template = templateAssembler.getTemplate("movies");
        template.evaluate(null);
        result.add(template.toAxiom());
        return result;
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        TimestampProvider timestampProvider = new TimestampProvider();
        functionManager.putFunctionProvider(timestampProvider.getName(), timestampProvider);
        return functionManager;
    }

    /**
     * Displays types solution on the console. Note timestamp details will vary.
     * <br/>
     * The expected result:<br/>
      */
    public static void main(String[] args)
    {
        try 
        {
            Lists types = new Lists();
            List<Axiom> axiomList = types.checkTypes();
            for (Axiom axiom: axiomList)
            {
                System.out.println("\nList: " + axiom.getName());
                for (int i = 0; i < axiom.getTermCount(); ++i)
                {
                    Term term = axiom.getTermByIndex(i);
                    System.out.println(term.toString());
                }
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
