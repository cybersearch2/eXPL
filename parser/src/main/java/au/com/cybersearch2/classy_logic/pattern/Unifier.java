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
package au.com.cybersearch2.classy_logic.pattern;

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Unifier
 * Attempts to unify one operand with a term.
 * For an operand in the template context, the term is selected from a supplied axiom,
 * otherwise the operand is unified with a term selected from a solution axiom, if available.
 * @author Andrew Bowley
 * 9May,2017
 */
public class Unifier implements OperandVisitor
{
    /** The template containing operands to be unified */
    private Template template;
    /** Int array mapping operand indexes to name=matched axiom indexes */
    private int[] termMapping;
    /** Axiom reduced to a TermList object */
    private TermList<Term> axiom;
    /** Optional solution pairer, used if solution keyset is non-empty */
    private SolutionPairer solutionPairer;

    /**
     * Construct Unifier object
     * @param template Template performing unification
     * @param axiom Axiom performing unification
     * @param termMapping Int array mapping operand indexes to name=matched axiom indexes
     * @param solution Contains result of query up to this stage
     */
    public Unifier(
        Template template, 
        TermList<Term> axiom, 
        int[] termMapping, 
        Solution solution)
    {
        this.template = template;
        this.axiom = axiom;
        this.termMapping = termMapping;
        if (solution.keySet().size() > 0)
            solutionPairer = template.getSolutionPairer(solution);
    }
 
    /**
     * next
     * @see au.com.cybersearch2.classy_logic.interfaces.OperandVisitor#next(au.com.cybersearch2.classy_logic.interfaces.Operand, int)
     */
    @Override
    public boolean next(Operand operand, int depth)
    {
        if (!operand.getName().isEmpty())
        {
            int index = operand.getArchetypeIndex();
            if (index != -1)
            {   // Operand in template context
                // Pair by mapped index 
                if (index >= termMapping.length)
                    // Paranoid check - This is not expected to happen
                    return true;
                int pairIndex = termMapping[index];
                if (pairIndex != -1)
                    return pairTerms(operand, axiom.getTermByIndex(pairIndex));
            }
            else if (solutionPairer != null)
                // Operand in another template context and solution available for unification
                return solutionPairer.next(operand, 0);
        }
        return true;
    }

    /**
     * Unify operand with term
     * @param operand Operand to unify, if empty, else compare values
     * @param term Term to unify
     * @return flag set true if unification suceeded
     */
    private boolean pairTerms(Operand operand, Term term)
    {
        // Pair first term to other term if first term is empty
        if (operand.isEmpty())
        {
           // template.add(operand, otherTerm);
            operand.unifyTerm(term, template.getId());
            return true;
        }
        // Check for exit case: terms in the same name space have different values
        else 
            return operand.getValue().equals(term.getValue());
    }
}
