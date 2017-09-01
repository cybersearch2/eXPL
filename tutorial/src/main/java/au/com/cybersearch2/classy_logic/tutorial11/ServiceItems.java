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
 * Demonstrates type assigned to cursor.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class ServiceItems
{
/* service-items.xpl
list<string> account_info = 
{
 "Invoice #00035", 
 "Service #83057 $60.00",
 "Service #93001       ",
 "Service #10800 $30.00",
 "Service #10661 $45.00",
 "Service #00200       ",
 "Service #78587 $15.00",
 "Service #99585 $10.00",
 "Service #99900  $5.00"
};

string serviceRegex = "#([0-9]+)";
string amountRegex = "(\\$[0-9]+\\.[0-9]+)";

calc scan_item
+ export list<axiom> charges {};
+ cursor item(account_info);
(
. string itemRegex = 
    "^Service "  + serviceRegex + 
    "\\s+" + amountRegex + "?$", 
. currency $ "US" amount,
  currency $ "US" total = 0.0,
  {
    ? item.fact,
    amount = 0.0,
    regex line = item++ ? itemRegex { service, amount }
    {
      total += amount,
      charges += axiom {
        Service = service,
        Amount = amount.format} 
    } 
  }
);

query<term> scan_service_items(scan_item);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ServiceItems()
    {
        File resourcePath = new File("src/main/resources/tutorial11");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the service-items.xpl script and runs the "scan_service_items" query.<br/>
     * The expected results:<br/>
        charges(Service=83057, Amount=USD60.00)<br/>
        charges(Service=93001, Amount=USD0.00)<br/>
        charges(Service=10800, Amount=USD30.00)<br/>
        charges(Service=10661, Amount=USD45.00)<br/>
        charges(Service=00200, Amount=USD0.00)<br/>
        charges(Service=78587, Amount=USD15.00)<br/>
        charges(Service=99585, Amount=USD10.00)<br/>
        charges(Service=99900, Amount=USD5.00)<br/>
        scan_service_items(total=165.0)<br/>  
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
