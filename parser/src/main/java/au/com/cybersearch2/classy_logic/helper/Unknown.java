/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.helper;

/**
 * Unknown
 * Class to represent no information available
 * @author Andrew Bowley
 * 1 Sep 2015
 */
public class Unknown implements Comparable<Unknown>
{
    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Unknown o)
    {
        // All empty objects are equal
        return 0;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
            return true;
        if (other instanceof Unknown)
            return true;
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() 
    {
        return "unknown";
    }

}
