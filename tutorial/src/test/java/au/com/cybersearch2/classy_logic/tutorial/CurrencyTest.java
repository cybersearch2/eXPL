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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.tutorial12.SingleCurrency;
import au.com.cybersearch2.classy_logic.tutorial12.MultiCurrency;


/**
 * CurrencyTest
 * @author Andrew Bowley
 * 5 Jun 2015
 */
public class CurrencyTest
{

    @Test
    public void test_SingleCurrency() throws Exception
    {
        SingleCurrency singleCurrency = new SingleCurrency();
        assertThat(singleCurrency.getFormatedTotalAmount().toString()).isEqualTo("Total + gst: AUD1,358.02");
    }

    @Test
    public void test_MultiCurrency() throws Exception
    {
        MultiCurrency multiCurrency = new MultiCurrency();
        Iterator<Axiom> iterator = multiCurrency.getFormatedAmounts();
        File testFile = new File("src/main/resources", "multi-currency-list.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        while(iterator.hasNext())
        {
            String line = reader.readLine();
            assertThat(iterator.next().getTermByName("total_text").getValue().toString()).isEqualTo(line);
        }
        reader.close();
    }
}
