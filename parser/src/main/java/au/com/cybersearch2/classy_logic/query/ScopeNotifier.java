/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.Scope;

/**
 * ScopeNotifier
 * @author Andrew Bowley
 * 30May,2017
 */
public class ScopeNotifier
{
    protected Scope templateScope;
    protected ChainQueryExecuter executer;
    
    public ScopeNotifier(ChainQueryExecuter executer, Scope templateScope)
    {
        this.templateScope = templateScope;
        this.executer = executer;
    }
    
    /**
     * Update scope listeners with locale details and
     * copy all axiom listeners in scope to this executer
     * @param executer Head query executer
     */
    public void notifyScopes()
    {
        executer.setAxiomListeners(templateScope); 
        executer.onScopeChange(templateScope);
        executer.bindAxiomListeners(templateScope);
    }

}
