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

import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;

/**
 * StampDutyTest
 * @author Andrew Bowley
 * 14Apr.,2017
 */
public class StampDutyTest
{
    static String[] choices =
    {
        "{amount<12000,0,0.0,1.0} (3,4) (3,42)",
        "{amount<30000,12000,120.0,2.0} (4,4) (4,42)",
        "{amount<50000,30000,480.0,3.0} (5,4) (5,42)",
        "{amount<100000,50000,1080.0,3.5} (6,4) (6,42)",
        "{amount<200000,100000,2830.0,4.0} (7,4) (7,42)",
        "{amount<250000,200000,6830.0,4.25} (8,4) (8,42)",
        "{amount<300000,250000,8955.0,4.75} (9,4) (9,42)",
        "{amount<500000,300000,11330.0,5.0} (10,4) (10,42)",
        "{amount>500000,500000,21330.0,5.5} (11,4) (11,42)"
    };
    
    @Test
    public void testStampDuty() throws Exception
    {
        StampDuty stampDuty = new StampDuty();
        assertThat(stampDuty.getStampDuty()).isEqualTo("stamp_duty(id=100078, duty=3768.32)");
        Iterator<SourceMarker> iterator = stampDuty.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("choice bracket (1,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("choice bracket(amount,threshold,base,percent,id) (1,1) (2,49)");
        for (int i = 0; i < choices.length; ++i)
        {
            sourceItem = sourceItem.getNext();
            assertThat(sourceItem).isNotNull();
            assertThat(sourceItem.toString()).isEqualTo(choices[i]);
        }
    }
}
