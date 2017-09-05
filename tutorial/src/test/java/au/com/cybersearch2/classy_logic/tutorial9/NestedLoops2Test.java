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
package au.com.cybersearch2.classy_logic.tutorial9;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * NestedLoops2Test
 * @author Andrew Bowley
 * 13Apr.,2017
 */
public class NestedLoops2Test
{

    @Test
    public void testNestedLoops() throws Exception
    {
        NestedLoops2 nestedLoops = new NestedLoops2();
        Axiom axiom = nestedLoops.displayAxiomSort();
        assertThat(axiom.toString()).isEqualTo("sorted(1, 3, 5, 8, 12)");
   }

}
