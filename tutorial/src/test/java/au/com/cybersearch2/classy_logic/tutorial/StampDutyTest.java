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
package au.com.cybersearch2.classy_logic.tutorial;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.tutorial10.StampDuty;

/**
 * StampDutyTest
 * @author Andrew Bowley
 * 5 Jun 2015
 */
public class StampDutyTest
{

    @Test
    public void test_StampDuty() throws Exception
    {
        StampDuty stampDuty = new StampDuty();
        assertThat(stampDuty.getStampDuty().toString()).isEqualTo("stamp_duty(duty = 3768.32)");
    }

}
