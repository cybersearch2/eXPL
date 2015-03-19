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
package au.com.cybersearch2.classy_logic.tutorial6;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Gaming {

    static final String FRUITS_POKER=
    		"axiom spin (r1, r2, r3, r4) : (3,2,0,1);\n" +
    		"axiom fruit : (\"apple\", \"orange\", \"banana\", \"lemon\");\n" +
    	    
            "list<term> combo(fruit);\n" +
    		"template spin(combo[(r1)], combo[(r2)], combo[(r3)], combo[(r4)]);\n" +
    		"query spin(spin : spin);"
    		;

	/**
	 * Compiles the FRUITS_POKER script and runs the "spin" query, displaying the solution on the console.<br/>
	 * The sample demonstrates enclosing a variable in parentheses to avoid it being interpreted as a term name.<br/>
	 * Class StudentScores2 provides another example of parentheses.<\br>
	 * The expected result:<br/>
	 * spin(combo.r1 = lemon, combo.r2 = banana, combo.r3 = apple, combo.r4 = orange)<br/>
	 * @throws ParseException
	 */
	public void displayFruit() throws ParseException
	{
		QueryProgram queryProgram = compileScript(FRUITS_POKER);
		queryProgram.executeQuery("spin", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("spin").toString());
				return true;
			}});
	}

	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		return queryProgram;
	}
	
	public static void main(String[] args)
	{
		Gaming gaming = new Gaming();
		try 
		{
			gaming.displayFruit();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
