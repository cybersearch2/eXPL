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

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;

/**
 * ExternalAxiomSource
     * Binds client-supplied ProviderManager object. 
     * Allows dependency injection to be avoided if external axiom sources are not used. 
     * @author Andrew Bowley
     * 4 Aug 2015
 */
public class ExternalAxiomSource
{
    protected ProviderManager providerManager;
    
    public ExternalAxiomSource(ProviderManager providerManager)
    {
        this.providerManager = providerManager;
    }

    /**
     * Returns Axiom Provider specified by name
     * @param name Axiom Provider qualified name
     * @return ResourceProvider implementation or null if not found
     */
    public ResourceProvider getResourceProvider(QualifiedName name)
    {
        return providerManager.getResourceProvider(name);
    }
    

}
