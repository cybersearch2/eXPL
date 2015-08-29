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
package au.com.cybersearch2.classy_logic.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * Solution
 * Container to hold axioms produced processing a query chain
 * @author Andrew Bowley
 * 8 Jan 2015
 */
public class Solution 
{
	/** Axioms referenced by key */
	protected Map<String, Axiom> axiomMap;
	/** Optional axiom listeners referenced by key */
	protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;

	/**
	 * Construct a Solution object
	 */
	public Solution() 
	{
		axiomMap = new HashMap<String, Axiom>();
	}

	/**
	 * Returns count of axioms
	 * @return int
	 */
	public int size() 
	{
		return axiomMap.size();
	}

	/**
	 * Remove axiom referenced by key
	 * @param key
	 */
	public void remove(String key) 
	{
		axiomMap.remove(key);
	}

    /**
     * Add axiom to this object and notify listener if present
     * @param key Name of axiom
     * @param axiom Axiom
     */
    public void put(String key, Axiom axiom) 
    {
        axiomMap.put(key.toString(), axiom);
        QualifiedName qname = QualifiedName.parseTemplateName(key);
        if ((axiomListenerMap != null) && axiomListenerMap.containsKey(qname))
            for (AxiomListener axiomListener: axiomListenerMap.get(qname))
                axiomListener.onNextAxiom(axiom);
    }

	/**
	 * Returns set of axiom keys
	 * @return Set of generic type String
	 */
	public Set<String> keySet() 
	{
		return axiomMap.keySet();
	}

	/**
	 * Returns axiom referenced by key
	 * @param key
	 * @return Axiom
	 */
	public Axiom getAxiom(String key)
	{
		return axiomMap.get(key);
	}

	/**
	 * Clear axiom container. Has no impact on axiom listeners.
	 */
	public void reset() 
	{
		axiomMap.clear();
	}

	/**
	 * Returns term value as Object referenced by axiom key and term name
	 * @param key Axiom key
	 * @param name Term name
	 * @return Object or null if axiom or term not found
	 */
	public Object getValue(String key, String name)
	{
		Axiom axiom = axiomMap.get(key);
		if (axiom != null)
		{
			Term term = axiom.getTermByName(name);
			if (term != null)
				return term.getValue();
		}
		return null;
	}

	/**
	 * Returns term value as String referenced by axiom key and term name
	 * @param key Axiom key
	 * @param name Term name
	 * @return String or null if axiom or term not found
	 */
	public String getString(String key, String name)
	{
		Object object = getValue(key, name);
		return object == null ? null : object.toString();
	}

	/**
	 * Set axiom listener for specified axiom key
	 * @param key
	 * @param axiomListener AxiomListener object
	 */
	void setAxiomListener(QualifiedName key, AxiomListener axiomListener) 
	{
		List<AxiomListener> axiomListenerList = null;
		if (axiomListenerMap == null)
			axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		else
			axiomListenerList = axiomListenerMap.get(key);
		if (axiomListenerList == null)
		{
			axiomListenerList = new ArrayList<AxiomListener>();
			axiomListenerMap.put(key, axiomListenerList);
		}
		axiomListenerList.add(axiomListener);
	}
	
	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return axiomMap.toString();
	}

}
