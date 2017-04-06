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

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.ResourceAxiomProvider;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * EuropeanMegaCities
 * @author Andrew Bowley
 * 24 Feb 2015
 */
public class MegaCities 
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
/* asia_top_ten.xpl
axiom mega_city (Rank,Megacity,Country,Continent,Population): resource;
integer count = 0;
template asia_top_ten (Megacity ? Continent == "Asia" && count++ < 10, Country, Population); 
query asia_top_ten (mega_city : asia_top_ten); 
*/
    protected QueryProgramParser queryProgramParser;
    
    public MegaCities()
    {
        ResourceAxiomProvider resourceAxiomProvider = new ResourceAxiomProvider("mega_city", "mega_city.xpl", 5);
        queryProgramParser = new QueryProgramParser(resourceAxiomProvider);
     }

    /**
     * Compiles the asia_top_ten.xpl script and runs the "asia_top_ten" query
     */
    public void findMegaCities(SolutionHandler solutionHandler) 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("asia_top_ten.xpl");
        queryProgram.executeQuery("asia_top_ten", solutionHandler);
    }

	/**
	 * Displays the asia_top_ten solution on the console.<br/>
	 * The expected result:<br/>
		asia_top_ten(Megacity = Tokyo, Country = Japan, Population = 37900000)<br/>
		asia_top_ten(Megacity = Delhi, Country = India, Population = 26580000)<br/>
		asia_top_ten(Megacity = Seoul, Country = South,Korea, Population = 26100000)<br/>
		asia_top_ten(Megacity = Shanghai, Country = China, Population = 25400000)<br/>
		asia_top_ten(Megacity = Mumbai, Country = India, Population = 23920000)<br/>
		asia_top_ten(Megacity = Beijing, Country = China, Population = 21650000)<br/>
		asia_top_ten(Megacity = Jakarta, Country = Indonesia, Population = 20500000)<br/>
		asia_top_ten(Megacity = Karachi, Country = Pakistan, Population = 20290000)<br/>
		asia_top_ten(Megacity = Osaka, Country = Japan, Population = 20260000)<br/>
		asia_top_ten(Megacity = Manila, Country = Philippines, Population = 20040000)<br/>
	 */
    public static void main(String[] args)
    {
        SolutionHandler solutionHandler = new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                System.out.println(solution.getAxiom("asia_top_ten").toString());
                return true;
            }};
        try 
        {
            MegaCities megaCities = new MegaCities();
            megaCities.findMegaCities(solutionHandler);
            /* Uncomment to run query a second time to check the count variable 
             * is reset back to initial value of 0
            queryProgram.executeQuery("asia_top_ten", solutionHandler));
             */
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
