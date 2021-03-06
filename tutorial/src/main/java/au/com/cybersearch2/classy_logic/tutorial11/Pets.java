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
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * PetNames
 * Demonstrates regular expression case-insensitive flag. Also shows how to build
 * the regular expresion from segments using concatenation.
 * Note that expression "pets_info[i++]" cannot be used as the regex input and must
 * be assigned to a simple term first.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class Pets
{
/* pets.xpl
list<string> pets_info = 
{
  "<pet><species>dog</species><name>Lassie</name><color>blonde</color></pet>",
  "<pet><species>cat</species><name>Cuddles</name><color>tortoise</color></pet>",
  "<pet><species>Dog</species><name>Bruiser</name><color>brindle</color></pet>",
  "<pet><species>Dog</species><name>Rex</name><color>black and tan</color></pet>",
  "<pet><species>Cat</species><name>Pixie</name><color>black</color></pet>",
  "<pet><species>dog</species><name>Axel</name><color>white</color></pet>",
  "<pet><species>Cat</species><name>Amiele</name><color>ginger</color></pet>",
  "<pet><species>dog</species><name>Fido</name><color>brown</color></pet>"
};

string speciesRegex = "<species>dog";
string nameRegex = "<name>([a-zA-z']*)[^a-zA-z']";
string colorRegex = "<color>([a-zA-z' ]*)[^a-zA-z' ]";

calc dogs_only
+ export list<string> dogs;
+ cursor pet(pets_info);
(
  string petRegex = 
    "^.*" + speciesRegex + 
    ".*" + nameRegex + 
    ".*" + colorRegex +".*", 
  {
    ? pet.fact,
    regex(case_insensitive) dog = pet++ ? petRegex { name, color }
      { dogs += name + " is a " + color + " dog." }
  }
);

query pet_query (dogs_only);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Pets()
    {
        File resourcePath = new File("src/main/resources/tutorial11");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the pets.xpl script and runs the "pet_query" query.<br/>
     * The expected results:<br/>
        Lassie is a blonde dog.<br/>
        Bruiser is a brindle dog.<br/>
        Rex is a black and tan dog.<br/>
        Axel is a white dog.<br/>
        Fido is a brown dog.<br/>    
     * @return Axiom iterator
     */
    public Iterator<String>  dogs()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("pets.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("pet_query");
        return result.stringIterator("dogs.dogs_only@");
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
            Pets pets = new Pets();
            Iterator<String> iterator = pets.dogs();
            while (iterator.hasNext())
                System.out.println(iterator.next());
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
