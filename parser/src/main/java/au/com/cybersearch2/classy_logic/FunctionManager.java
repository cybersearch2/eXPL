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
package au.com.cybersearch2.classy_logic;

import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;

/**
 * FunctionManager
 * Contains function libraries
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public class FunctionManager
{
    /** Map Function Providers to their names */
    protected Map<String, FunctionProvider> functionProviderMap;

    /**
     * Construct FunctionManager object
     */
    public FunctionManager()
    {
        functionProviderMap = new HashMap<String, FunctionProvider>();
    }

    /**
     * Put function library io container
     * @param name Name of library
     * @param functionProvider The library object which implements FunctionProvider interface
     * @see au.com.cybersearch2.classy_logic.interfaces.FunctionProvider
     */
    public void putFunctionProvider(String name, FunctionProvider functionProvider)
    {
        functionProviderMap.put(name, functionProvider);
    }

    /**
     * Returns function library specified by name
     * @param name The library name
     * @return FunctionProvider implementation or null if not found
     */
    public FunctionProvider findFunctionProvider(String name)
    {
       return functionProviderMap.get(name);
    }
    /**
     * Returns function library specified by name
     * @param name The library name
     * @return FunctionProvider implementation
     * @throws ExpressionException if provider not found
     */
    public FunctionProvider getFunctionProvider(String name)
    {
        FunctionProvider functionProvider = functionProviderMap.get(name);
        if (functionProvider == null)
            throw new ExpressionException("FunctionProvider \"" + name + "\" not found");
        return functionProvider;
    }
}
