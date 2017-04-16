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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * Result
 * Contains result lists and axioms generated by a query
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class Result 
{
    /** Empty collection */
    static Map<QualifiedName, Iterable<Axiom>> EMPTY_LIST_MAP;
    /** Container of result lists accessible by Iterable interface */
	protected Map<QualifiedName, Iterable<Axiom>> listMap;
    /** Container of result axioms */
    protected Map<QualifiedName, Axiom> axiomMap;

	static
	{
		EMPTY_LIST_MAP = Collections.emptyMap();
	}
	
	/**
	 * Create Result object
	 * @param listMap Container of result axiom lists accessible by Iterable interface or null if none available
	 * @param axiomMap Container of result axioms
	 */
	public Result(Map<QualifiedName, Iterable<Axiom>> listMap, Map<QualifiedName, Axiom> axiomMap) 
	{
		this.listMap = listMap == null ? EMPTY_LIST_MAP : listMap;
		this.axiomMap = axiomMap;
	}

	/**
	 * Returns iterator for result list specified by key
	 * @param qname Qualified name of list
	 * @return Iterator of generic type Axiom
	 */
    public Iterator<Axiom> getIterator(QualifiedName qname)
	{
	    return getList(qname).iterator();
	}

    /**
     * Returns axiom result specified by global namespace key
     * @param name Name of axiom
     * @return Axiom object
     */
    public Axiom getAxiom(String name)
    {
        return axiomMap.get(QualifiedName.parseGlobalName(name));
    }

    /**
     * Returns iterator for result list specified by global namespace key
     * @param name Name of list
     * @return Axiom Iterator
     */
    public Iterator<Axiom> getIterator(String name)
    {
        return getList(QualifiedName.parseGlobalName(name)).iterator();
    }

    /**
     * Returns axiom result specified by key
     * @param qname Qualified name of axiom
     * @return Axiom object
     */
    public Axiom getAxiom(QualifiedName qname)
    {
        return axiomMap.get(qname);
    }

    /**
     * Returns the result list for specified key
     * @param qname Qualiied name of list
     * @return Iterable object
     */
    protected Iterable<Axiom> getList(QualifiedName qname) 
    {
        Iterable<Axiom> list = listMap.get(qname);
        if (list == null)
            return Collections.emptyList();
        return list;
    }
}
