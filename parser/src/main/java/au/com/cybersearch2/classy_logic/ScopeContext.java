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

import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ScopeContext
 * Information to restore a scope to it's original state plus axiom lists of the
 * QueryProgram which owns the context.
 * @author Andrew Bowley
 * 2 Mar 2015
 */
public class ScopeContext 
{
	protected Scope scope;
	/** Map of operands and values used to save and restore initial state */
    protected Map<Operand, Object> operandValueMap;
	/** Map of global operands and values used to save and restore initial state */
    protected Map<Operand, Object> globalOperandValueMap;
    /** Names of lists which are empty at time of object construction */
    protected List<QualifiedName> emptyListNames;
    /** Names of global lists which are empty at time of object construction */
    protected List<QualifiedName> emptyGlobalListNames;
    /** Flag to indicate function scope */
    protected boolean isFunctionScope;

	/**
	 * Construct ScopeContext object
	 * @param scope Scope
	 */
	public ScopeContext(Scope scope, boolean isFunctionScope) 
	{
		this.scope = scope;
		this.isFunctionScope = isFunctionScope;
		OperandMap operandMap = scope.getParserAssembler().getOperandMap();
		operandValueMap = operandMap.getOperandValues();
		emptyListNames = operandMap.getEmptyListNames();
		if (!isFunctionScope && !QueryProgram.GLOBAL_SCOPE.equals(scope.getName()))
		{
			OperandMap globalOperandMap = scope.getGlobalScope().getParserAssembler().getOperandMap();
			globalOperandValueMap = globalOperandMap.getOperandValues();
	        emptyGlobalListNames = globalOperandMap.getEmptyListNames();
		}
	}

	/**
	 * Reset scope to initial state by clearing all operands and then assigning default values
	 */
	public void resetScope()
	{
		OperandMap operandMap = scope.getParserAssembler().getOperandMap();
		operandMap.setOperandValues(operandValueMap);
		operandMap.clearLists(emptyListNames);
		scope.getParserAssembler().clearScopeAxioms();
		if (globalOperandValueMap != null)
		{
			OperandMap globalOperandMap = scope.getGlobalScope().getParserAssembler().getOperandMap();
			globalOperandMap.setOperandValues(operandValueMap);
			globalOperandMap.clearLists(emptyGlobalListNames);
			scope.getGlobalParserAssembler().clearScopeAxioms();
		}
		// Restore Global scope locale which may be changed when used within another scope 
		if (QueryProgram.GLOBAL_SCOPE.equals(scope.getName()))
			scope.getParserAssembler().onScopeChange(scope);
	}
}
