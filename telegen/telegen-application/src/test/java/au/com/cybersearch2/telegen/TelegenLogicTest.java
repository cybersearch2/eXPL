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
package au.com.cybersearch2.telegen;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * TelegenLogicTest
 * @author Andrew Bowley
 * 25 Jun 2015
 */
@RunWith(RobolectricTestRunner.class)
public class TelegenLogicTest
{
    @Before
    public void setUp() 
    {
        TestTelegenApplication testTelegenApplication = TestTelegenApplication.getTestInstance();
        testTelegenApplication.startup();// Initialize database
        testTelegenApplication.waitForApplicationSetup();
    }

    @Test
    public void test_NextCheck()
    {
        TelegenLogic telegenLogic = new TelegenLogic();
        String firstCheck = telegenLogic.getFirstCheck("Start");
        assertThat(firstCheck).isEqualTo("Make sure the AC power cord is securely plugged in to the wall outlet");
        assertThat(telegenLogic.currentCheck).isEqualTo("Power cord");
        String nextCheck = telegenLogic.getNextCheck();
        assertThat(nextCheck).isEqualTo("Make sure the wall outlet is working");
        assertThat(telegenLogic.currentCheck).isEqualTo("Wall outlet");
        nextCheck = telegenLogic.getNextCheck();
        assertThat(nextCheck).isEqualTo("Try pressing the POWER button on the TV to make sure the problem is not the remote.");
        assertThat(telegenLogic.currentCheck).isEqualTo("Remote");
        nextCheck = telegenLogic.getNextCheck();
        assertThat(nextCheck).isEqualTo("Contact Support");
        assertThat(telegenLogic.currentCheck).isEqualTo(TelegenLogic.CALL_SUPPORT);
    }
}
