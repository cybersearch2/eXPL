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
package au.com.cybersearch2.classy_logic.compile;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;

/**
 * ExternalFunctionProvider
 * Binds client-supplied FunctionManager object. 
 * Allows dependency injection to be avoided if external functions are not used. 
 * @author Andrew Bowley
 * 4 Aug 2015
 */
public class ExternalFunctionProvider
{
    protected FunctionManager functionManager;
    
    public ExternalFunctionProvider(FunctionManager functionManager)
    {
        this.functionManager = functionManager;
    }

    public FunctionProvider<?> getFunctionProvider(String name)
    {
        return functionManager.getFunctionProvider(name);
    }
}
