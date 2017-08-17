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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.KeyName;

/**
 * QuerySpec
 * Query creation parameters
 * @author Andrew Bowley
 * 29 Dec 2014
 */
public class QuerySpec 
{
    /** Query name */
	protected String name;
	/** Query type: logic, calculator, unknown (ie. unassigned) */
	protected QueryType queryType;
	/** List of axiom key / template name pairs */
	protected ArrayList<KeyName> keyNameList;
	/** Specificatiion list for chained queries */
	protected List<QuerySpec> queryChainList;
	/** Properties for calculations referenced by template name */
	protected Map<String, List<Term>> propertiesMap;
	/** Flag set true if head query specification */
    protected boolean isHeadQuery;

	/**
	 * Construct a QuerySpec object 
	 * @param name
	 */
	public QuerySpec(String name, boolean isHeadQuery) 
	{
		this.name = name;
		this.isHeadQuery = isHeadQuery;
		queryType = QueryType.logic;
		keyNameList = new ArrayList<KeyName>();
		propertiesMap = new HashMap<String, List<Term>>();
	}

	/**
	 * Add axiom key / template name pair for default logic query
	 * @param keyName KeyName object
	 */
	public void addKeyName(KeyName keyName)
	{
	    if ((!isHeadQuery) && (keyNameList.size() >= 1))
	        throw new ExpressionException("Limit of 1 part for query chain exceeded for " + keyNameList.get(0).toString());
        keyNameList.add(keyName);
	}

	/**
	 * Returns qualified name of final template in query chain
	 * returns qualified name 
	 */
	public QualifiedName getKey()
	{
	    QuerySpec querySpec = 
	        queryChainList != null ? 
	        queryChainList.get(queryChainList.size() - 1) : 
	        this;
	    List<KeyName> tailKeyNameList = querySpec.getKeyNameList();
	    if (tailKeyNameList.size() == 0)
	        // Key is qualified name of query until first keyname is added
	        return QualifiedName.parseName(name);
	    return tailKeyNameList.get(tailKeyNameList.size() - 1).getTemplateName();
	}
	
	/**
	 * Add axiom key / template name pair for specified query type
	 * @param queryType
	 */
	public void setQueryType(QueryType queryType)
	{
		this.queryType = queryType;
	}

	/**
	 * Add axiom key / template name pair for calculator query, along with optional properties
	 * @param keyName KeyName object, axiomKey may be empty
	 * @param properties Calculator properties - may be empty
	 */
	public void putProperties(KeyName keyName, List<Term> properties) 
	{
		if ((properties != null) && properties.size() > 0)
			propertiesMap.put(keyName.getTemplateName().getTemplate(), properties);
	}

	/**
	 * Returns true if this query specification has no axiom key.
	 * This case is legal for a calculator type query.
	 * @return true
	 */
	public boolean hasNoAxiom()
	{
		return (keyNameList.size() == 1) && keyNameList.get(0).getAxiomKey().getName().isEmpty();
	}

	/**
	 * Returns query name
	 * @return String
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Returns query type
	 * @return QueryType enum
	 */
	public QueryType getQueryType()
	{
		return queryType;
	}

	/**
	 * Returns List of axiom key / template name pairs
	 * @return List of KeyName objects
	 */
	public List<KeyName> getKeyNameList() 
	{
		return keyNameList;
	}

	/**
	 * Returns query chain list
	 * @return List of QuerySpec objects
	 */
	public List<QuerySpec> getQueryChainList()
	{
		return queryChainList;
	}

	/**
	 * Returns properties referenced by template name or null if no properties found
	 * @param tempateName Template name of calculator
	 * @return Term list
	 */
	public List<Term> getProperties(String tempateName) 
	{
		return propertiesMap.get(tempateName);
	}

	/**
     * @return the isHeadQuery
     */
    public boolean isHeadQuery()
    {
        return isHeadQuery;
    }

    /**
	 * Returns new QuerySpec object chained to this query specification
	 * @return QuerySpec object
	 */
	public QuerySpec chain() 
	{
		if (queryChainList == null)
			queryChainList = new ArrayList<QuerySpec>();

		QuerySpec queryChain = new QuerySpec(name + queryChainList.size(), false);
		queryChainList.add(queryChain);
		return queryChain;
	}
    /**
     * Returns new QuerySpec object chained to this query specification
     * @return QuerySpec object
     */
    public QuerySpec prependChain() 
    {
        if (queryChainList == null)
            queryChainList = new ArrayList<QuerySpec>();
        QuerySpec queryChain = new QuerySpec(name + queryChainList.size(), false);
        queryChainList.add(0, queryChain);
        return queryChain;
    }
}
