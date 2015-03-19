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
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * QueryExecuterAdapter
 * @author Andrew Bowley
 * 17 Feb 2015
 */
public class QueryExecuterAdapter 
{
	protected static final String QUERY_NAME = "Test";
	protected QuerySpec querySpec;
	protected Scope scope;
	protected QueryProgram queryProgram;
	
	public QueryExecuterAdapter(AxiomCollection axiomCollection, List<Template> templateList) 
	{
		queryProgram = new QueryProgram();
		scope = queryProgram.getGlobalScope();
		querySpec = new QuerySpec(QUERY_NAME);
		ParserAssembler parserAssembler = scope.getParserAssembler();
		List<String> keyList = new ArrayList<String>();
		for (Template template: templateList)
		{
			String key = template.getKey();
			keyList.add(key);
			KeyName keyName = new KeyName(key, template.getName());
			querySpec.addKeyName(keyName);
		}
		parserAssembler.addAxiomCollection(keyList, axiomCollection);
		parserAssembler.addTemplateList(templateList);
	}

	public QueryExecuterAdapter(AxiomSource axiomSource, List<Template> templateList) 
	{
		this(ensembleFromSource(axiomSource), templateList);
	}
	
	public QueryParams getQueryParams()
	{
		QueryParams queryParams = new QueryParams(scope, querySpec);
		queryParams.initialize();
		return queryParams;
	}
	
	public QueryProgram getQueryProgram()
	{
		return queryProgram;
	}
	
	public QuerySpec getQuerySpec() 
	{
		return querySpec;
	}

	public Scope getScope() 
	{
		return scope;
	}

	/**
	 * Adapt an axiom source to an ensemble
	 * @param axiomSource The AxiomSource object
	 * @return AxiomCollection
	 */
	public static AxiomCollection ensembleFromSource(final AxiomSource axiomSource)
	{
		return new AxiomCollection(){

			@Override
			public AxiomSource getAxiomSource(String name) {
				return axiomSource;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
	}
}
