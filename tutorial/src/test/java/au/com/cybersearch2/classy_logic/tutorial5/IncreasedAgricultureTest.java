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
package au.com.cybersearch2.classy_logic.tutorial5;

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
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * IncreasedAgricultureTest
 * @author Andrew Bowley
 * 6Feb.,2017
 */
public class IncreasedAgricultureTest
{

    @Test
    public void testIncreasedAgriculture() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial5", "more_agriculture.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        IncreasedAgriculture increasedAgriculture = new IncreasedAgriculture();
        Iterator<Axiom> countryIterator = increasedAgriculture.findIncreasedAgriculture();
        while (countryIterator.hasNext()) 
                checkSolution(reader, countryIterator.next().toString());
        reader.close();
        ParserContext context = increasedAgriculture.getParserContext();
        assertThat(context.getSourceDocumentList()).isNotNull();
        assertThat(context.getSourceDocumentList().size()).isEqualTo(3);
        assertThat(context.getSourceDocumentList().get(0).replace('\\', '/')).isEqualTo("src/main/resources/tutorial5/more_agriculture.xpl");
        assertThat(context.getSourceDocumentList().get(1).replace('\\', '/')).isEqualTo("src/main/resources/agriculture-land.xpl");
        assertThat(context.getSourceDocumentList().get(2).replace('\\', '/')).isEqualTo("src/main/resources/surface-land.xpl");
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom Data (1,1)");
        assertThat(sourceMarker.getSourceDocumentId()).isEqualTo(1);
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo(
            "Data(country,Y1962,Y1963,Y1964,Y1965,Y1966,Y1967,Y1968,Y1969,Y1970,Y1971,Y1972,Y1973,Y1974,Y1975,Y1976,Y1977,Y1978," +
            "Y1979,Y1980,Y1981,Y1982,Y1983,Y1984,Y1985,Y1986,Y1987,Y1988,Y1989,Y1990,Y1991,Y1992,Y1993,Y1994,Y1995,Y1996,Y1997," +
            "Y1998,Y1999,Y2000,Y2001,Y2002,Y2003,Y2004,Y2005,Y2006,Y2007,Y2008,Y2009,Y2010,Y2011)[208] (1,1) (210,310)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query more_agriculture (10,1)");
        assertThat(sourceMarker.getSourceDocumentId()).isEqualTo(0);
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("Data:agri_10y (10,31) (10,45)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("surface_area:surface_area_increase (10,48) (10,83)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom surface_area (1,1)");
        assertThat(sourceMarker.getSourceDocumentId()).isEqualTo(2);
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("surface_area(country,surface_area_Km2)[213] (1,1) (215,29)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template agri_10y (4,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("double Y1990 (4,20) (4,31)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("calc surface_area_increase (5,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("country?country==country (6,3) (6,38)");
        sourceItem = sourceItem.getNext();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("double surface_area_Km2 (7,3) (7,25)");
    }
    
    protected void checkSolution(BufferedReader reader, String country)
    {
        try
        {
            String line = reader.readLine();
            assertThat(country).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
