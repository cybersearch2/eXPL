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
package au.com.cybersearch2.classy_logic;

import au.com.cybersearch2.classy_logic.tutorial11.RegexGroups;
import au.com.cybersearch2.classy_logic.tutorial4.InWords;
import au.com.cybersearch2.classy_logic.tutorial9.CalculateSquareMiles2;
import au.com.cybersearch2.classyinject.ApplicationModule;
import dagger.Module;

/**
 * QueryParserModule
 * @author Andrew Bowley
 * 9 Dec 2014
 */
@Module(injects= 
{
		InWords.class, 
		RegexGroups.class,
		CalculateSquareMiles2.class
},
includes=TestModule.class)
public class QueryParserModule implements ApplicationModule 
{
}
