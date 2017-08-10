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
package au.com.cybersearch2.classy_logic.helper;

/**
 * RegexGroup
 * Group name and private flag
 * @author Andrew Bowley
 * 11Aug.,2017
 */
public class RegexGroup
{
    private String name;
    private boolean isPrivate;
    
    /**
     * Construct RegexGroup object
     */
    public RegexGroup(String name)
    {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the isPrivate
     */
    public boolean isPrivate()
    {
        return isPrivate;
    }

    /**
     * @param isPrivate the isPrivate to set
     */
    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }

}
