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
package au.com.cybersearch2.classy_logic.tutorial7;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.pattern.Axiom;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * BirdsTest
 * @author Andrew Bowley
 * 26Jul.,2017
 */
public class BirdsTest
{
    @Test
    public void testBirds()
    {
        Birds birds = new Birds();
        Iterator<Axiom> iterator = birds.getBirds();
        assertThat(iterator.next().toString()).isEqualTo("waterfowl(bird=whistling swan, voice=muffled musical whistle, feet=webbed)");
        assertThat(iterator.next().toString()).isEqualTo("waterfowl(bird=trumpeter swan, voice=loud trumpeting, feet=webbed)");
        assertThat(iterator.next().toString()).isEqualTo("waterfowl(bird=snow goose, voice=honks, feet=webbed)");
        assertThat(iterator.next().toString()).isEqualTo("waterfowl(bird=pintail, voice=short whistle, feet=webbed)");
    }

}
