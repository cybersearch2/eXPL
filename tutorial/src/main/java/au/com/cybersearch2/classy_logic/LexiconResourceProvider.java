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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * LexiconResourceProvider
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class LexiconResourceProvider implements ResourceProvider 
{
    boolean isTestMode;
    List<Axiom> wordsList;
    
	@Override
	public String getName() 
	{
		return "lexicon";
	}

	/**
     * @param isTestMode the isTestMode to set
     */
    public void setTestMode(boolean isTestMode)
    {
        this.isTestMode = isTestMode;
        if (isTestMode && (wordsList == null))
            wordsList = new ArrayList<Axiom>();
    }

    /**
     * @return the wordsList
     */
    public List<Axiom> getWordsList()
    {
        return wordsList;
    }

    @Override
	public void open(Map<String, Object> properties) 
	{
        if (wordsList != null)
            wordsList.clear();
	}

	@Override
	public Iterator<Axiom> iterator(AxiomArchetype archetype) 
	{
		return new LexiconIterator(archetype);
	}

	@Override
	public boolean isEmpty() 
	{
		return false;
	}

	@Override
	public AxiomListener getAxiomListener(String name) 
	{   // Listener writes to console
		return new AxiomListener()
		{
			@Override
			public void onNextAxiom(QualifiedName qname, Axiom axiom) 
			{
			    if (!isTestMode)
			        System.out.println(axiom.toString());
			    else
			        wordsList.add(axiom);
			}
		};
	}

    @Override
    public void close()
    {
    }
}
