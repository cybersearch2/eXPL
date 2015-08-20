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
package au.com.cybersearch2.classy_logic.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.AxiomTermListVariable;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.AxiomListSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomUtils
 * Utility class to copy and assemble axioms
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomUtils
{
    public static List<String> EMPTY_NAMES_LIST;
    
    {
        EMPTY_NAMES_LIST = Collections.emptyList();
    }
    
    /**
     * Concatenate two operands containing AxiomLists
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    public static AxiomList concatenate(Operand leftOperand, Operand rightOperand)
    {
        if (rightOperand.isEmpty()) // Add empty list means no change
            return (AxiomList)leftOperand.getValue();
        if (leftOperand.isEmpty()) // Just assign left to right if this operand is empty
            return (AxiomList)rightOperand.getValue();
        // Check for congruence. Both Operands must be AxiomOperands with
        // AxiomLists containing matching Axioms
        AxiomList rightAxiomList = null;
        AxiomList leftAxiomList = null;
        boolean argumentsValid = (leftOperand.getValueClass() == AxiomList.class) && (rightOperand.getValueClass() == AxiomList.class);
        if (argumentsValid)
        {
            rightAxiomList = (AxiomList)rightOperand.getValue();
            leftAxiomList = (AxiomList)leftOperand.getValue();
            argumentsValid = AxiomUtils.isCongruent(leftAxiomList, rightAxiomList);
        }
        if (!argumentsValid)
            throw new ExpressionException("Cannot concatenate " + leftOperand.toString() + " to " + rightOperand.toString());
        // For efficiency, update the value of this operand as it will be assigned back to it anyway.
        Iterator<AxiomTermList> iterator = rightAxiomList.getIterable().iterator();
        int index = leftAxiomList.getLength();
        while (iterator.hasNext())
            leftAxiomList.assignItem(index++, iterator.next());
        List<String> leftTermNames = leftAxiomList.getAxiomTermNameList();
        if (leftTermNames == null) 
        {   // Concatenation to an empty list
            if (leftAxiomList.isEmpty())
                leftAxiomList.setAxiomTermNameList(EMPTY_NAMES_LIST);
            else
                leftAxiomList.setAxiomTermNameList(getTermNames(leftAxiomList.getItem(0).getAxiom()));
        }
        return (AxiomList) leftOperand.getValue();
    }

    /**
     * Returns an AxiomList object given a list of terms to marshall into an axiom
     * @param listName Name of axiom list to return
     * @param axiomKey Axiom name
     * @param argumentList List of terms
     * @return AxiomList object containing marshalled axiom
     */
    public static AxiomList marshallAxiomTerms(String listName, String axiomKey, List<Term> argumentList)
    {
        // Give axiom same name as operand
        Axiom axiom = new Axiom(axiomKey);
        for (Term arg: argumentList)
        {   // Copy value to Parameter to make it immutable
            Parameter param = new Parameter(arg.getName(), arg.getValue());
            axiom.addTerm(param);
        }
        List<String> axiomTermNameList = getTermNames(axiom);
        // Wrap axiom in AxiomList object to allow interaction with other AxiomLists
        AxiomTermList axiomTermList = new AxiomTermList(axiomKey, axiomKey);
        axiomTermList.setAxiom(axiom);
        axiomTermList.setAxiomTermNameList(axiomTermNameList);
        AxiomList axiomList = new AxiomList(listName, axiomKey);
        axiomList.assignItem(0, axiomTermList);
        axiomList.setAxiomTermNameList(axiomTermNameList);
        return axiomList;
    }

    public static boolean isCongruent(AxiomList leftAxiomList, AxiomList rightAxiomList)
    {
        if (leftAxiomList.isEmpty())
            // Any right hand list can be concatenated to an empty left hand list
            return true;
        List<String> leftTermNames = leftAxiomList.getAxiomTermNameList();
        List<String> rightTermNames = rightAxiomList.getAxiomTermNameList();
        if ((leftTermNames != null) && (rightTermNames != null))
        {
            if (leftTermNames.size() != rightTermNames.size())
                return false;
//            int index = 0;
//            for (String termName: rightTermNames)
//                if (!termName.equalsIgnoreCase(leftTermNames.get(index++)))
//                    return false;
        }
        else if ((leftTermNames != null) || (rightTermNames != null))
            return false;
        return true;
    }
    
    /**
     * Pack axioms into a provided axiom list
     * @param axiomList The axiom list
     * @param axioms List of axioms. Must be congruent.
     */
    public static void marshallAxioms(AxiomList axiomList, List<Axiom> axioms)
    {
        List<String> axiomTermNameList = axiomList.getAxiomTermNameList();
        String axiomKey = axiomList.getKey();
        for (int i = 0; i < axioms.size(); i++)
        {   // Each axiom is wrapped in an AxiomTermList to allow access from script
            AxiomTermList axiomTermList = new AxiomTermList(axiomKey, axiomKey);
            axiomTermList.setAxiom(axioms.get(i));
            if (axiomTermNameList != null)
                axiomTermList.setAxiomTermNameList(axiomTermNameList);
            axiomList.assignItem(i, axiomTermList);
        }
    }

    /**
     * Create a deep copy of an axiom list object
     * @param axiomList The axiom list to duplicate
     * @return AxiomList object
     */
    public static AxiomList duplicateAxiomList(AxiomList axiomList)
    {
        List<Axiom> dupAxioms = AxiomUtils.copyItemList(axiomList.getName(), axiomList);
        AxiomList dupAxiomList = new AxiomList(axiomList.getName(), axiomList.getName());
        dupAxiomList.setAxiomTermNameList(axiomList.getAxiomTermNameList());
        int index = 0;
        for (Axiom dupAxiom: dupAxioms)
        {
            AxiomTermList axiomTermList = new AxiomTermList(axiomList.getName(), axiomList.getName());
            axiomTermList.setAxiomTermNameList(axiomList.getAxiomTermNameList());
            axiomTermList.setAxiom(dupAxiom);
            dupAxiomList.assignItem(index++, axiomTermList);
        }
        return dupAxiomList;
    }

    /**
     * Returns list of term names for specified axiom
     * @param axiom The axiom
     * @return List of term names which will be empty if the axiom contains anonymous terms
     */
    public static List<String> getTermNames(Axiom axiom)
    {
        if (axiom.getTermCount() == 0) 
            return EMPTY_NAMES_LIST;
        List<String> axiomTermNameList = new ArrayList<String>(axiom.getTermCount());
        for (int i = 0; i < axiom.getTermCount(); i++)
        {
            Term term = axiom.getTermByIndex(i);
            String termName = term.getName();
            if (Term.ANONYMOUS.equals(termName))
                return AxiomListSource.EMPTY_LIST;
            axiomTermNameList.add(termName);
        }
        return axiomTermNameList;
    }

    /**
     * Convert am item list to a list of axioms
     * @param listName Name of list
     * @param itemList The item list to copy
     * @return List of axioms, the contents depends on type of item list
     */
    public static List<Axiom> copyItemList(String listName, ItemList<?> itemList)
    {
        List<Axiom> copyList = new ArrayList<Axiom>();
        // Create deep copy in case item list is cleared
        Axiom axiom = null;
        if (itemList.getItemClass().equals(Term.class))
        {   // AxiomTermList contains backing axiom
            AxiomTermList axiomTermList = (AxiomTermList)itemList;
            axiom = new Axiom(listName);
            Axiom source = axiomTermList.getAxiom();
            for (int i = 0; i < source.getTermCount(); i++)
                axiom.addTerm(source.getTermByIndex(i));
            copyList.add(axiom);
        }
        else if (!itemList.getItemClass().equals(Axiom.class))
        {   // Regular ItemList contains objects which are packed into axiom to return
            axiom = new Axiom(listName);
            Iterator<?> iterator = itemList.getIterable().iterator();
            while (iterator.hasNext())
                axiom.addTerm(new Parameter(Term.ANONYMOUS, iterator.next()));
            copyList.add(axiom);
        }
        else
        {
            AxiomList axiomList = (AxiomList)itemList;
            Iterator<AxiomTermList> iterator = axiomList.getIterable().iterator();
            while (iterator.hasNext())
                copyList.add(copyAxiom(iterator.next().getAxiom()));
        }
        return copyList;
    }

    /**
     * Returns a copy of an axiom
     * @param axiom Axiom to copy
     * @return Axiom object
     */
    public static Axiom copyAxiom(Axiom axiom)
    {
        Axiom copyAxiom = new Axiom(axiom.getName());
        for (int i = 0; i < axiom.getTermCount(); i++)
            copyAxiom.addTerm(copyTerm(axiom.getTermByIndex(i)));
        return copyAxiom;
    }

    /**
     * Returns a copy of term if it contains an item list
     * @param term
     * @return Parameter object
     */
    public static Term copyTerm(Term term)
    {
        Term copyTerm = new Parameter(term.getName());
        Object value = term.getValue();
        if (term.getValueClass() == ItemList.class)
        {
            ItemList<?> itemList = (ItemList<?>) value;
            List<Axiom> copyListAxioms = copyItemList(itemList.getName(), itemList);
            if (term.getValueClass() == AxiomList.class)
            {
                AxiomList axiomList = (AxiomList)itemList;
                AxiomList copyAxiomList = new AxiomList(axiomList.getName(), axiomList.getKey());
                copyAxiomList.setAxiomTermNameList(axiomList.getAxiomTermNameList());
                int index = 0;
                for (int i = 0; i < axiomList.getLength(); )
                {
                    if (axiomList.hasItem(index))
                    {
                        copyAxiomList.assignItem(index, copyListAxioms.get(i));
                        ++i;
                    }
                    ++index;
                }
                value = copyAxiomList;
            }
            else 
                value = copyListAxioms.get(0);
        }
        copyTerm.assign(value);
        return copyTerm;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(int, java.lang.String)
     */
    public static ItemListVariable<Object> newVariableInstance(AxiomTermList axiomTermList, int index, String suffix, int id) 
    {
        Variable variable = new Variable(axiomTermList.getName() + "." + suffix);
        Axiom axiom = axiomTermList.getAxiom();
        if (axiom.getTermCount() > 0)
        {
            axiomTermList.verify(index);
            // Assign a value to set the delegate
            variable.assign(axiom.getTermByIndex(index).getValue());
        }
        return new AxiomTermListVariable(axiomTermList, variable, index, suffix, id);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(au.com.cybersearch2.classy_logic.interfaces.Operand, java.lang.String)
     */
    public static ItemListVariable<Object> newVariableInstance(AxiomTermList axiomTermList, Operand expression, String suffix, int id) 
    {
        Variable itemOperand = new Variable(axiomTermList.getName() + "." + suffix);
        // Assign a value to set the delegate must be delayed until the expression is evaluated
        return new AxiomTermListVariable(axiomTermList, itemOperand, expression, suffix, id);
    }
}
