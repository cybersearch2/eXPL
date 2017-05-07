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
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * TermPair
 * Contains paired Terms for unification
 * @author Andrew Bowley
 * 7May,2017
 */
public class TermPair
{
    private Operand term1;
    private Term term2;
    private TermPair next;

    public TermPair(Operand term1, Term term2)
    {
        this.term1 = term1;
        this.term2 = term2;
    }

    /**
     * @return the next
     */
    public TermPair getNext()
    {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(TermPair next)
    {
        this.next = next;
    }

    /**
     * @return the term1
     */
    public Operand getTerm1()
    {
        return term1;
    }

    /**
     * @return the term2
     */
    public Term getTerm2()
    {
        return term2;
    }

    /**
     * @param term1 the term1 to set
     */
    public void setTerm1(Operand term1)
    {
        this.term1 = term1;
    }

    /**
     * @param term2 the term2 to set
     */
    public void setTerm2(Term term2)
    {
        this.term2 = term2;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String name = term1.getName().isEmpty() ? term2.getName() : term1.getName();
        String value = term2.getValue().toString();
        return name + "=" + value;
    }

}
