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
     * @param solution Map of Axioms selectable by Axiom name
	 */
	public SolutionPairer(Structure owner, Solution solution) 
	{
		super(owner);
		this.solution = solution;
	}

	/**
	 * Construct SolutionPairer object
     * @param solution Map of Axioms selectable by Axiom name
	 */
	public SolutionPairer(Solution solution) 
	{
		this(null, solution);
	}

	/**
	 * Update solution and reset pair list
	 * @param solution
	 */
	public void setSolution(Solution solution)
	{
		this.solution = solution;
		reset();
	}

	/**
	 * Visit next term
	 * @see au.com.cybersearch2.classy_logic.pattern.AxiomPairer#next(au.com.cybersearch2.classy_logic.interfaces.Term, int)
	 */
	@Override
	public boolean next(Term term, int depth) 
	{
		// Test for compound name
		// Compound name has format "<key>.<name>"
		KeyName keyName = parseKeyName(term.getName());
		if (!keyName.getAxiomKey().isEmpty() && !keyName.getTemplateName().isEmpty() && solution.keySet().contains(keyName.getAxiomKey()))
		{   
			// Solution has Axiom with key name
			Term otherTerm = solution.getAxiom(keyName.getAxiomKey()).getTermByName(keyName.getTemplateName());
			if ((otherTerm != null) && !otherTerm.isEmpty())
			{ 
			    // Check for exit case: Axiom term contains different value to matching Solution term
				Term axiomTerm = null;
				if (owner == null) 
					axiomTerm = term;
				else
					axiomTerm = owner.getTermByName(keyName.getTemplateName());
                if ((axiomTerm != null) && 
                	!axiomTerm.isEmpty() && 
                	!axiomTerm.getValue().equals(otherTerm.getValue()))
                	return false;
                else if (term.isEmpty())
                	// Unify with Solution term
                	pairList.add(new TermPair(term, otherTerm));
			}
		}
		return true;
	}
	
}
