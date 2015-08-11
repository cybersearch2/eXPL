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
package au.com.cybersearch2.classy_logic.interfaces;

/**
 * FunctionProvider
 * Sources functions from external libraries
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public interface FunctionProvider<R>
{
    /**
     * Name of function provider - must be unique
     * @return String
     */
    String getName();
    /**
     * Returns function object specified by name
     * @param identifier Name of function
     * @return Function object implementing CallEvaluator interface
     */
    CallEvaluator<R> getCallEvaluator(String identifier);
}
