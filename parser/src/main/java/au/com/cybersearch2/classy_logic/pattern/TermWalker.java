/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermVisitor;

/**
 * TermWalker
 * @author Andrew Bowley
 * 31 Aug 2015
 */
public class TermWalker
{
    /** Single term */
    protected Term term;
    /** Term list */
    protected List<Term> termList;

    /**
     * Construct TermWalker object
     * @param termList List of terms to navigate
     */
    public TermWalker(List<Term> termList) 
    {
        this.termList = termList;
    }

    /**
     * Construct TermWalker object
     * @param term Single term to navigate
     */
    public TermWalker(Term term) 
    {
        this.term = term;
    }

    /**
     * Navigate term tree for all terms. 
     * The supplied visitor may cut the navigation short causing
     * a false result to be returned.
     * @param visitor Implementation of TermVisitor interface 
     * @return flag set true if entire tree navigated. 
     */
    public boolean visitAllNodes(TermVisitor visitor)
    {
        if (term != null)
            return visit(term, visitor, 1);
        for (Term term: termList)
        {
            if (!visit(term, visitor, 1))
                return false;
        }
        return true;
    }

    /**
     * Visit a node of the Term tree. Recursively navigates left and right terms, if any.
     * @param term The term being visited
     * @param visitor Object implementing TermVisitor interface
     * @param depth Depth in tree. The root has depth 1.
     * @return flag set true if entire tree formed by this term is navigated. 
     */
    public boolean visit(Term term, TermVisitor visitor, int depth)
    {
        if (!visitor.next(term, depth))
            return false;
        // Only Terms which also implement Term interface will have left and right Terms
        if (!(term instanceof Operand))
            return true;
        Operand operand = (Operand)term;
        if ((operand.getLeftOperand() != null) &&
             !visit(operand.getLeftOperand(), visitor, depth + 1))
            return false;
        if ((operand.getRightOperand() != null) &&
             !visit(operand.getRightOperand(), visitor, depth + 1))
            return false;
        return true;
    }

}
