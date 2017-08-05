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
package au.com.cybersearch2.classy_logic.tutorial7;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.axiom.ResourceAxiomProvider;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * EuropeanMegaCities
 * Demonstrates dynamic axiom list declared in one template accessed by another
 * @author Andrew Bowley
 * 24 Feb 2015
 */
public class DynamicMegaCities 
{
/* mega_city.xpl
axiom mega_city (Rank,Megacity,Country,Continent,Population)
{1,"Tokyo","Japan","Asia",37900000}
{2,"Delhi","India","Asia",26580000}
{3,"Seoul","South,Korea","Asia",26100000}
{4,"Shanghai","China","Asia",25400000}
{5,"Mumbai","India","Asia",23920000}
{6,"Mexico City","Mexico","North America",22200000}
{7,"Beijing","China","Asia",21650000}
{8,"Sao Paulo","Brazil","South,America",21390000}
{9,"Jakarta","Indonesia","Asia",20500000}
{10,"New York City","United,States","North America",20300000}
{11,"Karachi","Pakistan","Asia",20290000}
{12,"Osaka","Japan","Asia",20260000}
{13,"Manila","Philippines","Asia",20040000}
{14,"Cairo","Egypt","Africa",18810000}
{15,"Dhaka","Bangladesh","Asia",18250000}
{16,"Los Angeles","United,States","North America",17900000}
{17,"Moscow","Russia","Europe",16900000}
{18,"Buenos Aires","Argentina","South America",16500000}
{19,"Kolkata","India","Asia",16240000}
{20,"London","United,Kingdom","Europe",15800000}
{21,"Bangkok","Thailand","Asia",15350000}
{22,"Lagos","Nigeria","Africa",15210000}
{23,"Istanbul","Turkey","Europe",14800000}
{24,"Rio de Janeiro","Brazil","South America",14500000}
{25,"Tehran","Iran","Asia",13700000}
{26,"Guangzhou","China","Asia",12700000}
{27,"Kinshasa","Democratic Republic of Congo","Africa",12500000}
{28,"Shenzhen","China","Asia",12250000}
{29,"Lahore","Pakistan","Asia",11580000}
{30,"Rhine-Ruhr","Germany","Europe",11350000}
{31,"Tianjin","China","Asia",11000000}
{32,"Bengaluru","India","Asia",10820000}
{33,"Paris","France","Europe",10770000}
{34,"Chennai","India","Asia",10350000}
{35,"Hyderabad","India","Asia",10100000};
 */
/* grouping.xpl
axiom mega_city (Rank,Megacity,Country,Continent,Population): resource;

axiom continents(continent)
  { "Asia" }
  { "Africa"}
  { "Europe" }
  { "South America" }
  { "North America" };

template continent 
(
  continent
); 

template continent_group 
(
  continent ? continent == Continent, city = Megacity, country = Country, rank = Rank, population = Population.format
); 

query<axiom> mega_cities_by_continent (continents : continent, mega_city : continent_group); */
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public DynamicMegaCities()
    {
        ResourceAxiomProvider resourceAxiomProvider = new ResourceAxiomProvider("mega_city", "mega_city.xpl", 7);
        queryProgramParser = new QueryProgramParser(resourceAxiomProvider);
     }

    /**
     * Compiles the asia_top_ten.xpl script and runs the "asia_top_ten" query
     */
    public Iterator<Axiom> findMegaCities() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("dynamic-grouping.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("mega_cities_by_continent");
        List<Axiom> axiomList = new ArrayList<Axiom>();
        Iterator<Axiom> iterator = result.axiomIterator("asia.city_info@");
        while (iterator.hasNext())
            axiomList.add(iterator.next());
        iterator = result.axiomIterator("africa.city_info@");
        while (iterator.hasNext())
            axiomList.add(iterator.next());
        iterator = result.axiomIterator("europe.city_infop@");
        while (iterator.hasNext())
            axiomList.add(iterator.next());
        iterator = result.axiomIterator("south_america.city_info@");
        while (iterator.hasNext())
            axiomList.add(iterator.next());
        iterator = result.axiomIterator("north_america.city_info@");
        while (iterator.hasNext())
            axiomList.add(iterator.next());
        return axiomList.iterator();
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	/**
	 * Displays the asia_top_ten solution on the console.<br/>
	 * The expected result:<br/>
	 */
    public static void main(String[] args)
    {
        try 
        {
            DynamicMegaCities megaCities = new DynamicMegaCities();
            Iterator<Axiom> iterator = megaCities.findMegaCities();
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
