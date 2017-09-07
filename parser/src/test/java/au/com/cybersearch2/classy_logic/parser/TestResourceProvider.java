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
package au.com.cybersearch2.classy_logic.parser;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * TestResourceProvider
 * @author Andrew Bowley
 * 11 Feb 2015
 */
public class TestResourceProvider extends ProviderManager implements ResourceProvider 
{
	public TestResourceProvider()
	{
		super(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
	}
	
	@Override
	public void open(Map<String, Object> properties) 
	{
	}


	@Override
	public boolean isEmpty() 
	{
		return false;
	}

	@Override
	public AxiomListener getAxiomListener(String name) 
	{   // Do-nothing listener for read-only provider
		return new AxiomListener()
		{
			@Override
			public void onNextAxiom(QualifiedName qname, Axiom axiom) 
			{
			}
		};
	}
	
	@Override
	public ResourceProvider getResourceProvider(QualifiedName name)
	{
		return this;
	}

	@Override
	public String getName() 
	{
		return "test";
	}

    @Override
    public void close()
    {
    }

    @Override
    public Iterator<Axiom> iterator(AxiomArchetype archetype)
    {
        return null;
    }
}
