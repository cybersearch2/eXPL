/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial9;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * HighCitiesSorted2Test
 * @author Andrew Bowley
 * 12Apr.,2017
 */
public class HighCitiesSorted3Test
{

    @Test
    public void testHighCities() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial9", "high-cities-sorted2.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        HighCitiesSorted3 highCities = new HighCitiesSorted3();
        Iterator<Axiom> cityIterator = highCities.displayHighCities();
        while (cityIterator.hasNext())
            checkSolution(reader, cityIterator.next().toString());
        reader.close();
   }

    protected void checkSolution(BufferedReader reader, String city)
    {
        try
        {
            String line = reader.readLine();
            assertThat(city).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
