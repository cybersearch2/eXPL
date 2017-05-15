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

import java.util.List;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;

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
     * @return AxiomProvider implementation or null if not found
     */
    public AxiomProvider getAxiomProvider(QualifiedName name)
    {
        return providerManager.getAxiomProvider(name);
    }
    
    /**
     * Returns Axiom Source of specified Axiom Provider and Axiom names
     * @param name Axiom Provider qualified name
     * @param axiomName Axiom name
     * @param axiomTermNameList List of term names constrains which terms are included and their order
     * @return AxiomSource implementation or null if axiom provider not found
     * @throws ExpressionException if axiom provider not found
     */
    public AxiomSource getAxiomSource(QualifiedName name, String axiomName,
            List<String> axiomTermNameList) 
    {
        return providerManager.getAxiomSource(name, axiomName, axiomTermNameList);
    }

}
