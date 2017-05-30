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
package au.com.cybersearch2.classy_logic.tutorial13;

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

/**
 * GermanColorsTest
 * @author Andrew Bowley
 * 17Apr.,2017
 */
public class GermanColorsTest
{
    @Test
    public void testGermanColors() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial13", "german-colors.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        GermanColors germanColors = new GermanColors();
        checkSolution(reader, germanColors.getColorSwatch("Wasser"));
        checkSolution(reader,germanColors.getColorSwatch("schwarz"));
        checkSolution(reader,germanColors.getColorSwatch("weiß"));
        checkSolution(reader,germanColors.getColorSwatch("blau"));
        ParserContext context = germanColors.getParserContext();
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("local colors (14,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("list<term> colors(lexicon)=lexicon(aqua=Wasser, black=schwarz, blue=blau, white=weiß) (14,1) (14,21)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("scope german (16,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("language=de (16,15) (16,27)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("region=DE (16,30) (16,40)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom shade (12,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("shade(name):parameter (12,1) (12,30)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("choice swatch (5,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("choice swatch(name,red,green,blue) (5,1) (6,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{name=colors.aqua,0,255,255} (7,4) (7,30)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{name=colors.black,0,0,0} (8,4) (8,30)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{name=colors.blue,0,0,255} (9,4) (9,30)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{name=colors.white,255,255,255} (10,4) (10,30)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        ///System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query german.color_query (18,3)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("shade:swatch (18,28) (18,41)");
   }

    protected void checkSolution(BufferedReader reader, String color)
    {
        try
        {
            String line = reader.readLine();
            assertThat(color).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
