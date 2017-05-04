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
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * SolutionVisitor
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class SolutionPairer extends AxiomPairer
{
    /** Map of Axioms selectable by Axiom name */
	protected Solution solution;
	
	/**
	 * Construct SolutionPairer object
	 * @param owner Structure which is performing unification
     * @param solution Container to aggregate results
	 */
	public SolutionPairer(Axiom owner, Solution solution, QualifiedName localContext) 
	{
		super(owner, localContext);
		this.solution = solution;
	}

	/**
	 * Construct SolutionPairer object
     * @param solution Container to aggregate results
     * @param localContext Qualified name of enclosing context
	 */
	public SolutionPairer(Solution solution, QualifiedName localContext) 
	{
		this(null, solution, localContext);
	}

	/**
	 * Update solution and reset pair list
	 * @param solution Container to aggregate results
	 */
	public void setSolution(Solution solution)
	{
		this.solution = solution;
		reset();
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
            Term axiomTerm = null;
            if (owner == null) 
                axiomTerm = operand;
            else
                axiomTerm = owner.getTermByName(termName);
            if ((axiomTerm != null) && 
                !axiomTerm.isEmpty() && localContext.inSameSpace(operand.getQualifiedName()) &&
                !axiomTerm.getValue().equals(otherTerm.getValue()))
                return false;
            else if (operand.isEmpty())
                // Unify with Solution term
                pairList.add(new TermPair(operand, otherTerm));
        }
        return true;
    }

}
