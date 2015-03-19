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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;

/**
 * ProviderManager
 * @author Andrew Bowley
 * 6 Mar 2015
 */
public class ProviderManager 
{
	protected Map<String, AxiomProvider> axiomProviderMap;
	
	/**
	 * 
	 */
	public ProviderManager() 
	{
		axiomProviderMap = new HashMap<String, AxiomProvider>();
	}

	public void setResourceProperties(String name, String axiomName,
			Map<String, Object> properties) 
	{
		AxiomProvider axiomProvider = getAxiomProvider(name);
		axiomProvider.setResourceProperties(axiomName, properties);
	}

	public AxiomSource getAxiomSource(String name, String axiomName,
			List<String> axiomTermNameList) 
	{
		AxiomProvider axiomProvider = getAxiomProvider(name);
		return axiomProvider.getAxiomSource(axiomName, axiomTermNameList);
	}

	public boolean isEmpty() 
	{
		return axiomProviderMap.size() == 0;
	}

	public void putAxiomProvider(AxiomProvider axiomProvider)
	{
		axiomProviderMap.put(axiomProvider.getName(), axiomProvider);
	}
	
	public AxiomProvider getAxiomProvider(String name)
	{
		AxiomProvider axiomProvider = axiomProviderMap.get(name);
		if (axiomProvider == null)
			throw new ExpressionException("Axiom provider \"" + name + "\" not found");
		return axiomProvider;
	}
}
