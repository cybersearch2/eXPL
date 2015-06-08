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
package au.com.cybersearch2.classy_logic.helper;

import au.com.cybersearch2.classy_logic.QueryProgram;

/**
 * NameParser
 * Utility class to extract name components
 * @author Andrew Bowley
 * 7 Jun 2015
 */
public class NameParser
{
    final static char DOT = '.';
    final static String NULL_KEY_MESSAGE = "Null key passed to name parser";

    /**
     * Returns name part of key
     * @param key
     * @return String
     */
    public static String getNamePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.lastIndexOf(DOT);
        if (dot != -1)
            return key.substring(dot + 1);
        return key;
    }

    /**
     * Returns scope part of key or name of Global Scope if not in key
     * @param key
     * @return String
     */
    public static String getScopePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.indexOf(DOT);
        if (dot != -1)
            return key.substring(0, dot);
        return QueryProgram.GLOBAL_SCOPE;
    }
}
