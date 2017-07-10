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
package au.com.cybersearch2.classy_logic.tutorial10;

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
 * ColorSwatchTest
 * @author Andrew Bowley
 * 14Apr.,2017
 */
public class ColorSwatchTest
{
    @Test
    public void testColorSwatch() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial10", "color-swatch.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        ColorSwatch colorSwatch = new ColorSwatch();
        checkSolution(reader, colorSwatch.getColorSwatch(0x00ffff).toString());
        checkSolution(reader, colorSwatch.getColorSwatch(0x0000ff).toString());
        checkSolution(reader, colorSwatch.getColorSwatch(0x77ffff).toString());
        ParserContext context = colorSwatch.getParserContext();
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query color (13,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("shade:swatch (13,19) (13,32)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom shade (11,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("shade(rgb):parameter (11,1) (11,29)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("choice swatch (3,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("choice swatch(rgb,color,red,green,blue) (3,1) (4,36)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{rgb==65535,aqua,0,255,255} (5,2) (5,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{rgb==0,black,0,0,0} (6,2) (6,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{rgb==255,blue,0,0,255} (7,2) (7,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{rgb==16777215,white,255,255,255} (8,2) (8,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{true,unknown,0,0,0} (9,2) (9,33)");
        assertThat(iterator.hasNext()).isFalse();
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
