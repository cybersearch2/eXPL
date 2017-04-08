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
package au.com.cybersearch2.classy_logic.tutorial1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * HighCitiesTest
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class HighCitiesTest
{
    @Test
    public void testHighCities() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial1", "high_cities.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        HighCities highCities = new HighCities();
        highCities.findHighCities(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                checkSolution(reader, solution.getAxiom("high_city").toString());
                return true;
            }});
        reader.close();
    }
    
    @Test
    public void testHighCities2() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial1", "high_cities.txt");
        final BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        HighCities2 highCities2 = new HighCities2();
        ParserContext context = highCities2.findHighCities(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                checkSolution(reader2, solution.getAxiom("high_city").toString());
                return true;
            }});
        reader2.close();
        assertThat(context.getSourceDocumentList()).isNotNull();
        assertThat(context.getSourceDocumentList().size()).isEqualTo(1);
        assertThat(context.getSourceDocumentList().get(0).replace('\\', '/')).isEqualTo("src/main/resources/tutorial1/high_cities.xpl");
         Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query high_cities (3,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("city:high_city (3,20) (3,35)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template high_city (2,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("name?altitude>5000 (2,20) (2,41)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("altitude (2,44) (2,51)");
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
