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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * ProviderManager
 * Contains Axiom Providers and performs their persistence work.
 * @author Andrew Bowley
 * 6 Mar 2015
 */
public class ProviderManager 
{
	/** Map Axiom Providers to their names */
	protected Map<QualifiedName, AxiomProvider> axiomProviderMap;
    /** Map Axiom Providers to their names */
    protected Map<QualifiedName, ResourceProvider> resourceProviderMap;
	/** Resource path base */
	protected File resourceBase;
	
	/**
	 * Construct ProviderManager object
	 */
	public ProviderManager() 
	{
		this(null);
	}
	
	/**
	 * Construct ProviderManager object
	 */
	public ProviderManager(File resourceBase) 
	{
		this.resourceBase = resourceBase;
		axiomProviderMap = new HashMap<QualifiedName, AxiomProvider>();
		resourceProviderMap = new HashMap<QualifiedName, ResourceProvider>();
	}
	
	public File getResourceBase() 
	{
		return resourceBase;
	}

	public void setResourceBase(File resourceBase) 
	{
		this.resourceBase = resourceBase;
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
		AxiomProvider axiomProvider = getAxiomProvider(name);
		if (axiomProvider == null) 
            throw new ExpressionException("Axiom provider \"" + name + "\" not found");
		return  axiomProvider.getAxiomSource(axiomName, axiomTermNameList);
	}

	/** 
	 * Returns flag set true if no Axiom Providers are configured
	 * @return boolean
	 */
	public boolean isEmpty() 
	{
		return axiomProviderMap.size() == 0 && resourceProviderMap.size() == 0;
	}

	/**
	 * Add Axiom Provider
	 * @param axiomProvider AxiomProvider object
	 */
	public void putAxiomProvider(AxiomProvider axiomProvider)
	{
	    QualifiedName qualifiedAxiomName = QualifiedName.parseName(axiomProvider.getName());
		axiomProviderMap.put(qualifiedAxiomName, axiomProvider);
		QualifiedTemplateName qualifiedTemplateName = new QualifiedTemplateName(qualifiedAxiomName.getScope(), qualifiedAxiomName.getName());
        axiomProviderMap.put(qualifiedTemplateName, axiomProvider);
	}

	/**
	 * Returns Axiom Provider specified by qualified name
	 * @param name Axiom Provider qualified name
	 * @return AxiomProvider implementation or null if not found
	 */
	public AxiomProvider getAxiomProvider(QualifiedName name)
	{
		return axiomProviderMap.get(name);
	}

    /**
     * Add Resource Provider
     * @param resourceProvider ResourceProvider object
     */
    public void putResourceProvider(final ResourceProvider resourceProvider)
    {
        QualifiedName qualifiedAxiomName = QualifiedName.parseName(resourceProvider.getName());
        resourceProviderMap.put(qualifiedAxiomName, resourceProvider);
        QualifiedTemplateName qualifiedTemplateName = new QualifiedTemplateName(qualifiedAxiomName.getScope(), qualifiedAxiomName.getName());
        resourceProviderMap.put(qualifiedTemplateName, resourceProvider);
        AxiomProvider axiomProvider = new AxiomProvider(){
    
            @Override
            public String getName()
            {
                return resourceProvider.getName();
            }
    
            @Override
            public void open(Map<String, Object> properties)
                    throws ExpressionException
            {
                resourceProvider.open(properties);
            }
    
            @Override
            public void close()
            {
                resourceProvider.close();
            }
    
            @Override
            public AxiomSource getAxiomSource(String axiomName,
                    final List<String> axiomTermNameList)
            {
                final AxiomArchetype archetype = new AxiomArchetype(QualifiedName.parseGlobalName(axiomName));
                for (String termName: axiomTermNameList)
                    archetype.addTermName(termName);
                archetype.clearMutable();
                return new AxiomSource(){
    
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
                };
            }
    
            @Override
            public AxiomListener getAxiomListener(String axiomName)
            {
                return resourceProvider.getAxiomListener(axiomName);
            }
    
            @Override
            public boolean isEmpty()
            {
                return false;
            }};
        putAxiomProvider(axiomProvider);
    }

    /**
     * Returns Resource Provider specified by qualified name
     * @param name Resource Provider qualified name
     * @return ResourceProvider implementation or null if not found
     */
    public ResourceProvider getResourceProvider(QualifiedName name)
    {
        return resourceProviderMap.get(name);
    }
}
