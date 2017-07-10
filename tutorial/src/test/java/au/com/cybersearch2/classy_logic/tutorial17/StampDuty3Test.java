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
package au.com.cybersearch2.classy_logic.tutorial17;

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
 * StampDuty3Test
 * @author Andrew Bowley
 * 23Apr.,2017
 */
public class StampDuty3Test
{
    static String[] BRACKETS =
    {
        "{amount<12000,0,0.0,1.0} (3,6) (3,44)",
        "{amount<30000,12000,120.0,2.0} (4,6) (4,44)",
        "{amount<50000,30000,480.0,3.0} (5,6) (5,44)",
        "{amount<100000,50000,1080.0,3.5} (6,6) (6,44)",
        "{amount<200000,100000,2830.0,4.0} (7,6) (7,44)",
        "{amount<250000,200000,6830.0,4.25} (8,6) (8,44)",
        "{amount<300000,250000,8955.0,4.75} (9,6) (9,44)",
        "{amount<500000,300000,11330.0,5.0} (10,6) (10,44)",
        "{amount>500000,500000,21330.0,5.5} (11,6) (11,44)"
    };
 
    static String[] STATEMENTS =
    {
        "bracket(amount) (21,3) (21,59)",
        "currency duty=base+amount-threshold*percent/100 (22,3) (22,63)",
        "bracket (23,3) (23,9)",
        "string property_value=amount_format=amount (24,3) (24,39)",
        "string payable=duty_format=duty=base+amount-threshold*percent/100 (25,3) (26,0)"
    };
    
    @Test
    public void testStampDuty3() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial17", "stamp-duty3.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        StampDuty3 stampDuty3 = new StampDuty3();
        Iterator<Axiom> dutyIterator = stampDuty3.getStampDuty();
        while(dutyIterator.hasNext())
        {
            checkSolution(reader, dutyIterator.next().toString());
        }
        ParserContext context = stampDuty3.getParserContext();
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("choice bracket (1,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("choice bracket(amount,threshold,base,percent) (1,1) (2,47)");
        for (int i = 0; i < 9; ++i)
        {
            sourceItem = sourceItem.getNext();
            assertThat(sourceItem).isNotNull();
            //System.out.println(sourceItem.toString());
            assertThat(sourceItem.toString()).isEqualTo(BRACKETS[i]);
        }
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query stamp_duty (28,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("transacton_amount:stamp_duty_payable (28,25) (28,62)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom transacton_amount (13,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("transacton_amount(amount)[3] (13,1) (17,14)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("calc stamp_duty_payable (19,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("currency amount (20,3) (20,17)");
        for (int i = 0; i < 5; ++i)
        {
            sourceItem = sourceItem.getNext();
            assertThat(sourceItem).isNotNull();
            //System.out.println(sourceItem.toString());
            assertThat(sourceItem.toString()).isEqualTo(STATEMENTS[i]);
        }
    }

    protected void checkSolution(BufferedReader reader, String duty)
    {
        try
        {
            String line = reader.readLine();
            assertThat(duty).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
