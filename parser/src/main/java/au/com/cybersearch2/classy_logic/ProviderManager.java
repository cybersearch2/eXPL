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
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;

/**
 * ProviderManager
 * Contains Axiom Providers and performs their persistence work.
 * @author Andrew Bowley
 * 6 Mar 2015
 */
public class ProviderManager 
{
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
	 * Returns flag set true if no Axiom Providers are configured
	 * @return boolean
	 */
	public boolean isEmpty() 
	{
		return resourceProviderMap.size() == 0;
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
