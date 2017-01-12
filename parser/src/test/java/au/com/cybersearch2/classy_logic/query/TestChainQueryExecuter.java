/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.QueryParams;

/**
 * TestChainQueryExecuter
 * @author Andrew Bowley
 * 13Jan.,2017
 */
public class TestChainQueryExecuter extends ChainQueryExecuter
{

    public TestChainQueryExecuter(QueryParams queryParams)
    {
        super(queryParams);
    }

    /**
     * Set initial solution
     * @param solution Solution object - usually empty but can contain initial axioms
     */
    public void setSolution(Solution solution)
    {
        super.setSolution(solution);
    }

    /**
     * Returns solution
     * @return Collection of axioms referenced by name. The axioms reference the templates supplied to the query.
     */
    public Solution getSolution() 
    {
        return super.getSolution();
    }
}
