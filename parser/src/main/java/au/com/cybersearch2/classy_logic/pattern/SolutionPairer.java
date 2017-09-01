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
package au.com.cybersearch2.classy_logic.pattern;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.operator.OperatorTerm;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * SolutionPairer
 * Attempts to pair an operand to a solution term, and if successful, unifies them
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class SolutionPairer implements OperandVisitor 
{
    /** Map of Axioms selectable by Axiom name */
	protected Solution solution;
    /** Template id for unification */
    protected int id;
    /** Template context name or names. The first name must be the template qualified name */
    protected QualifiedName[] contextNames;
	
	/**
	 * Construct SolutionPairer object
     * @param solution Contains query solution up to current stage
     * @param id Template id for unification
     * @param contextNames Template context name or names. The first name must be the template qualified name.
	 */
	public SolutionPairer(Solution solution, int id, QualifiedName... contextNames) 
	{
        this.solution = solution;
        this.id = id;
        this.contextNames = contextNames;
	}

	/**
	 * Set solution
	 * @param solution Contains query solution up to current stage
	 */
	public void setSolution(Solution solution)
	{
		this.solution = solution;
	}

	/**
	 * Visit next operand to pair and unify with a corresponding solution term
	 * @see au.com.cybersearch2.classy_logic.pattern.AxiomPairer#next(au.com.cybersearch2.classy_logic.interfaces.Operand, int)
	 */
	@Override
	public boolean next(Operand operand, int depth) 
	{
	    // Solution pairing only applies to operands with two or three part names
	    QualifiedName qname = operand.getQualifiedName();
	    if (qname.getTemplate().isEmpty() || qname.getName().isEmpty())
	        return false;
	    // Determine if operand in same name space as template context. Inner templates and replcates have 2 context names.
	    boolean inSameSpace = inSameSpace(operand);
	    // Prepare to hold up to 2 keys for solution axiom search
	    String[] keys = new String[2];
	    if (inSameSpace) 
	    {
	        // An operand in the template context can be paired with a solution from the previous query step, obtained using the "current key" of the soution
	        keys[0] = solution.getCurrentKey();
	        // A 2-part key indicates the solution template is in a non-global scope, so namespace match is required
	        if ((keys[0].indexOf(".") != -1) && !inSameSpace(keys[0]))
	            return false;
	    }
	    else
	    {   // An operand belonging to another template can pair by template name and scope, which can be the operand's scope or the context scope, if not global.
	        String contextScope = contextNames[0].getScope();
            String operandKey = QualifiedTemplateName.toString(qname.getScope(), qname.getTemplate());
 	        if (contextScope.isEmpty())
 	            keys[0] = operandKey;
 	        else
	        {
	            keys[0] = QualifiedTemplateName.toString(contextScope, qname.getTemplate());
	            keys[1] = operandKey;
	        }
	    }
	    for (String key: keys)
	    {
	        if ((key == null) || key.isEmpty())
	            break;
    	    Axiom axiom = solution.getAxiom(key);
    		if (axiom != null)
    		{
                OperatorTerm otherTerm = (OperatorTerm) axiom.getTermByName(qname.getName());
                if (otherTerm != null)
                    return processKey(operand, otherTerm, inSameSpace);
                return false;
    		}
	    }
		return false;
	}

	/**
	 * Process operand paired to solution term
	 * @param operand The operand to unify
	 * @param term The solution term to unify, which unlike a normal parameter, has the operator too
	 * @param inSameSpace Flag set true if operand belongs to same name space as template context
	 * @return boolean
	 */
	private boolean processKey(Operand operand, OperatorTerm term, boolean inSameSpace)
    {   
        if (operand.isEmpty())
        {
            // Unify with Solution term
            operand.unifyTerm(term, id);
            Trait trait = term.getOperator().getTrait();
            if (trait.getOperandType() != OperandType.UNKNOWN)
                operand.getOperator().setTrait(trait);
            return true;
        }
        else if (inSameSpace)
            // Terminate this iteration if operand and term have mis-matched values
            return operand.getValue().equals(term.getValue());
        return false;
    }

	/**
	 * Returns flag set true if operand is in the same name space as template which contains it
	 * @param operand The operand to check
	 * @return boolean
	 */
    private boolean inSameSpace(Operand operand)
    {
        for (QualifiedName contextName: contextNames)
            if (contextName.inSameSpace(operand.getQualifiedName())) 
                return true;
        return false;
    }

    /**
     * Returns flag set true if operand is in the same name space as template which contains it
     * @param operand The operand to check
     * @return boolean
     */
    private boolean inSameSpace(String key)
    {
        for (QualifiedName contextName: contextNames)
            if (contextName.toString().equals(key)) 
                return true;
        return false;
    }
}
