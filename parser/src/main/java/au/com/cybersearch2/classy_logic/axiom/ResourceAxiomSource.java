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
package au.com.cybersearch2.classy_logic.axiom;

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * ResourceAxiomSource
 * @author Andrew Bowley
 * 6Sep.,2017
 */
public class ResourceAxiomSource implements AxiomSource
{
    protected AxiomArchetype archetype;
    protected ResourceProvider resourceProvider;
    
    /**
     * 
     */
    public ResourceAxiomSource(ResourceProvider resourceProvider, AxiomArchetype archetype)
    {
        this.resourceProvider = resourceProvider;
        this.archetype = archetype;
    }

    @Override
    public Iterator<Axiom> iterator()
    {
        return resourceProvider.iterator(archetype);
    }

    @Override
    public Archetype<Axiom, Term> getArchetype()
    {
        return archetype;
    }
}
