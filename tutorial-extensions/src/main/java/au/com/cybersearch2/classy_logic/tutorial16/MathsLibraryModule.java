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
package au.com.cybersearch2.classy_logic.tutorial16;

import javax.inject.Singleton;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classyinject.ApplicationModule;
import dagger.Module;
import dagger.Provides;

/**
 * MathsLibraryModule
 * @author Andrew Bowley
 * 14 Sep 2015
 */
@Module(/*injects=ParserAssembler.ExternalFunctionProvider.class*/)
public class MathsLibraryModule implements ApplicationModule
{
    @Provides @Singleton FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        MathFunctionProvider mathFunctionProvider = new MathFunctionProvider();
        functionManager.putFunctionProvider(mathFunctionProvider.getName(), mathFunctionProvider);
        return functionManager;
    }
}
