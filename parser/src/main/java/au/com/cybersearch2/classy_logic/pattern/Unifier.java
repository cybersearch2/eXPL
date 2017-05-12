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
 * Attempts to pair one operand with a term and add the combination to a supplied list.
 * The term will be an item from a given axiom or optionally from an axiom selected
 * from a given solution.
 * @author Andrew Bowley
 * 9May,2017
 */
public class Unifier implements OperandVisitor
{
    /** The template containing operands to be unified */
    private Template template;
    /** Int array mapping operand indexes to nmae=matched axiom indexes */
    private int[] termMapping;
    /** Axiom reduced to a TermList object */
    private TermList<Term> axiom;
    /** Optional solution pairer, used if solution keyset is non-empty */
    private SolutionPairer solutionPairer;

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
            solutionPairer = new SolutionPairer(solution, template);
    }
    
    @Override
    public boolean next(Operand operand, int depth)
    {
        if (!operand.getName().isEmpty())
        {
            int index = operand.getArchetypeIndex();
            if (index != -1)
            {
                // Pair by mapped index 
                if (index >= termMapping.length)
                    // This is not expected to happen
                    return true;
                int pairIndex = termMapping[index];
                if (pairIndex != -1)
                    return pairTerms(operand, axiom.getTermByIndex(pairIndex));
            }
            if (solutionPairer != null)
                return solutionPairer.next(operand, 0);
        }
        return true;
    }

    private boolean pairTerms(Operand operand, Term otherTerm)
    {
        // Pair first term to other term if first term is empty
        if (operand.isEmpty())
        {
           // template.add(operand, otherTerm);
            operand.unifyTerm(otherTerm, template.getId());
            return true;
        }
        // Check for exit case: terms in the same name space have different values
        else 
            return operand.getValue().equals(otherTerm.getValue());
    }
}
