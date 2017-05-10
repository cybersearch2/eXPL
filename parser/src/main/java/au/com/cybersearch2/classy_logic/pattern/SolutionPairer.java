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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermPairList;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * SolutionVisitor
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class SolutionPairer implements OperandVisitor 
{
    /** Map of Axioms selectable by Axiom name */
	protected Solution solution;
    /** List of paired terms collected by this object */
    protected TermPairList termPairList;
    /** Qualified name for local context */
    protected QualifiedName localContext;
	
	/**
	 * Construct SolutionPairer object
     * @param solution Container to aggregate results
     * @param localContext Qualified name of enclosing context
	 */
	public SolutionPairer(Solution solution, QualifiedName localContext, TermPairList termPairList) 
	{
        this.solution = solution;
        this.termPairList = termPairList;
        this.localContext = localContext;
	}

	/**
	 * Update solution and reset pair list
	 * @param solution Container to aggregate results
	 */
	public void setSolution(Solution solution)
	{
		this.solution = solution;
	}

	/**
	 * Visit next term
	 * @see au.com.cybersearch2.classy_logic.pattern.AxiomPairer#next(au.com.cybersearch2.classy_logic.interfaces.Operand, int)
	 */
	@Override
	public boolean next(Operand operand, int depth) 
	{
	    QualifiedName qname = operand.getQualifiedName();
	    if (qname.getTemplate().isEmpty() || qname.getName().isEmpty())
	        return true;
	    String templateKey = solution.getCurrentKey();
	    if (!localContext.inSameSpace(qname)) 
	    {
 	        if (!localContext.getScope().isEmpty())
	        {
	            String localTemplateKey = new QualifiedTemplateName(localContext.getScope(), qname.getTemplate()).toString();
	            if (solution.keySet().contains(localTemplateKey))
	                return processKey(localTemplateKey, qname.getName(), operand);
	        }
            templateKey = new QualifiedTemplateName(qname.getScope(), qname.getTemplate()).toString();
	    }
		if (solution.keySet().contains(templateKey))
		    return processKey(templateKey, qname.getName(), operand);
		return true;
	}

	private boolean processKey(String templateKey, String termName, Operand operand)
    {   
        // Solution has Axiom with key name
        Term otherTerm = solution.getAxiom(templateKey).getTermByName(termName);
        if ((otherTerm != null) && !otherTerm.isEmpty())
        { 
            // Check for exit case: Axiom term contains different value to matching Solution term
            if (!operand.isEmpty() && localContext.inSameSpace(operand.getQualifiedName()) &&
                !operand.getValue().equals(otherTerm.getValue()))
                return false;
            else if (operand.isEmpty())
                // Unify with Solution term
                termPairList.add(operand, otherTerm);
        }
        return true;
    }
}
