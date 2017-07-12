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

import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * DictionaryAxiomProvider
 * @author Andrew Bowley
 * 10 Jul 2017
 */
public class DictionaryAxiomProvider implements AxiomProvider 
{

    @Override
    public String getName() 
    {
        return "dictionary";
    }

    @Override
    public void open(Map<String, Object> properties) 
    {
    }

    @Override
    public AxiomSource getAxiomSource(String axiomName,
            List<String> axiomTermNameList) 
    {
        return new DictionarySource(axiomName, axiomTermNameList);
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
    public void close()
    {
    }

}
