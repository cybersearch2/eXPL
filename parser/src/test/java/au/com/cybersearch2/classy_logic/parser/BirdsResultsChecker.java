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
package au.com.cybersearch2.classy_logic.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * BirdsResultsChecker
 * @author Andrew Bowley
 * 7 Jan 2015
 */
public class BirdsResultsChecker 
{

	LineNumberReader reader;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public BirdsResultsChecker(String filename) throws IOException 
	{
    	File americanCityList = new File("src/test/resources", filename);
     	reader = new LineNumberReader(new FileReader(americanCityList));
	}

	public void checkNextResult(String result)
	{
		try 
		{
			assertThat(result).isEqualTo(reader.readLine());
			//System.out.println(result);
			//reader.readLine();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException
	{
		reader.close();
	}
}
