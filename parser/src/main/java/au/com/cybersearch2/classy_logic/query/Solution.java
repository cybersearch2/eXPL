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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
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
    protected static String EMPTY_KEY = "";
    
	/** Axioms referenced by key */
	protected Map<String, Axiom> axiomMap;
	/** Optional axiom listeners referenced by key */
	protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;
    /** Key used for last put() */
	protected String[] keyStack;
	/** Solution handler for self-evaluation */
	protected SolutionHandler solutionHandler;
	
	/**
	 * Construct a Solution object
	 */
	public Solution() 
	{
		axiomMap = new HashMap<String, Axiom>();
		keyStack = new String[2];
		keyStack[0] = EMPTY_KEY;
		keyStack[1] = EMPTY_KEY;
	}

	/**
	 * Set solution handler for self-evaluation
	 * @param solutionHandler
	 */
	public void setSolutionHandler(SolutionHandler solutionHandler)
    {
        this.solutionHandler = solutionHandler;
    }

	/**
	 * Returns key used for last put()
	 * @return key or null if not available
	 */
    public String getCurrentKey()
    {
        return keyStack[0];
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
     * Add axiom to this object and notify listener if present
     * @param key Name of axiom
     * @param axiom Axiom
     */
    public void put(String key, Axiom axiom) 
    {
        axiomMap.put(key, axiom);
        QualifiedName qname = QualifiedName.parseTemplateName(key);
        if ((axiomListenerMap != null) && axiomListenerMap.containsKey(qname) && (axiom.getTermCount() > 0))
            for (AxiomListener axiomListener: axiomListenerMap.get(qname))
                axiomListener.onNextAxiom(new QualifiedName(key), axiom);
        keyStack[1] = keyStack[0];
        keyStack[0] = key;
    }

    /**
     * Remove axiom referenced by key
     * @param key
     */
    public void remove(String key) 
    {
        axiomMap.remove(key);
        if (key.equals(keyStack[0]))
        {
            keyStack[0] = keyStack[1];
            keyStack[1] = EMPTY_KEY;
        }
        else if (key.equals(keyStack[1]))
            keyStack[1] = EMPTY_KEY;
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
	    Axiom axiom = axiomMap.get(key);
		return axiom == null ? new Axiom(key) : axiom;
	}

	/**
	 * Clear axiom container. Has no impact on axiom listeners.
	 */
	public void reset() 
	{
		axiomMap.clear();
        keyStack[0] = EMPTY_KEY;
        keyStack[1] = EMPTY_KEY;
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
	 * Self evaluate using external solution handler
	 * @return EvaluationStatus enum COMPLETE or SHORT_CIRCUIT
	 */
	public EvaluationStatus evaluate()
	{
        if ((solutionHandler != null) &&
             !solutionHandler.onSolution(this))
            return EvaluationStatus.SHORT_CIRCUIT;
        return EvaluationStatus.COMPLETE;
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
