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
    /** Qualified name for local context */
    protected Template template;
	
	/**
	 * Construct SolutionPairer object
     * @param solution Container to aggregate results
     * @param localContext Qualified name of enclosing context
	 */
	public SolutionPairer(Solution solution, Template template) 
	{
        this.solution = solution;
        this.template = template;
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
	    QualifiedName localContext = template.getQualifiedName();
	    boolean inSameSpace = localContext.inSameSpace(qname);
	    if (!inSameSpace) 
	    {
 	        if (!localContext.getScope().isEmpty())
	        {
	            String localTemplateKey = QualifiedTemplateName.toString(localContext.getScope(), qname.getTemplate());
	            Axiom axiom = solution.getAxiom(localTemplateKey);
	            if (axiom != null)
	            {
	                Term otherTerm = axiom.getTermByName(qname.getName());
	                if (otherTerm != null)
	                    return processKey(operand, otherTerm, inSameSpace);
	                else
	                    return true;
	            }
	        }
            templateKey = QualifiedTemplateName.toString(qname.getScope(), qname.getTemplate());
	    }
	    Axiom axiom = solution.getAxiom(templateKey);
		if (axiom != null)
		{
            Term otherTerm = axiom.getTermByName(qname.getName());
            if (otherTerm != null)
                return processKey(operand, otherTerm, inSameSpace);
		}
		return true;
	}

	private boolean processKey(Operand operand, Term term, boolean inSameSpace)
    {   
        // Solution has Axiom with key name
        if (term != null)
        { 
            if (operand.isEmpty())
                // Unify with Solution term
                operand.unifyTerm(term, template.getId());
            else if (inSameSpace)
                return operand.getValue().equals(term.getValue());
        }
        return true;
    }
}
