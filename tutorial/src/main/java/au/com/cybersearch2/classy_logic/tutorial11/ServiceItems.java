/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial11;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * ServiceItems
 * Demonstrates regular expression group default.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class ServiceItems
{
/* service-items.xpl

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ServiceItems()
    {
        File resourcePath = new File("src/main/resources/tutorial11");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the service-item script and runs the "scan_service_items" query.<br/>
     * The expected results:<br/>
     * @return Axiom iterator
     */
    public List<Axiom>  scanServiceItems()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("service-items.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("scan_service_items");
        List<Axiom> axiomList = new ArrayList<Axiom>();
        Iterator<Axiom> iterator = result.axiomIterator("charges.scan_item@");
        while(iterator.hasNext())
            axiomList.add(iterator.next());
        axiomList.add(result.getAxiom("scan_service_items"));
        return axiomList;
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * @param args
     */
    public static void main(String[] args)
    {
        try 
        {
            ServiceItems serviceItems = new ServiceItems();
            Iterator<Axiom> iterator = serviceItems.scanServiceItems().iterator();
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
