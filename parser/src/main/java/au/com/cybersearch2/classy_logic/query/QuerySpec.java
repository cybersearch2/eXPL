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
	protected Map<String, Map<String, Object>> propertiesMap;

	/**
	 * Construct a QuerySpec object 
	 * @param name
	 */
	public QuerySpec(String name) 
	{
		this.name = name;
		queryType = QueryType.logic;
		keyNameList = new ArrayList<KeyName>();
		propertiesMap = new HashMap<String, Map<String, Object>>();
	}

	/**
	 * Add axiom key / template name pair for default logic query
	 * @param keyName KeyName object
	 */
	public void addKeyName(KeyName keyName)
	{
        keyNameList.add(keyName);
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
	public void putProperties(KeyName keyName, Map<String, Object> properties) 
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
	 * @return Properties object
	 */
	public Map<String, Object> getProperties(String tempateName) 
	{
		return propertiesMap.get(tempateName);
	}

	public boolean isHeadQuery()
	{
	    return queryChainList == null;
	}
	
	/**
	 * Returns new QuerySpec object chained to this query specification
	 * @return QuerySpec object
	 */
	public QuerySpec chain() 
	{
		if (queryChainList == null)
			queryChainList = new ArrayList<QuerySpec>();

		QuerySpec queryChain = new QuerySpec(name + queryChainList.size());
		queryChainList.add(queryChain);
		return queryChain;
	}


}
