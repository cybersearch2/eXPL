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
package au.com.cybersearch2.classy_logic.axiom;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
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
        if (leftOperand.isEmpty() && rightOperand.getValueClass() == AxiomList.class) // Just assign left to right if this operand is empty
            return (AxiomList)rightOperand.getValue();
        // Check for congruence. Both Operands must be AxiomOperands with
        // AxiomLists containing matching Axioms
        AxiomList rightAxiomList = null;
        AxiomList leftAxiomList = null;
        boolean argumentsValid = (leftOperand.getValueClass() == AxiomList.class) && 
                ((rightOperand.getValueClass() == AxiomList.class) || (rightOperand.getValueClass() == AxiomTermList.class));
        if (argumentsValid)
        {
            if (rightOperand.getValueClass() == AxiomList.class)
                rightAxiomList = (AxiomList)rightOperand.getValue();
            else
            {
                AxiomTermList rightAxiomTermList = (AxiomTermList)rightOperand.getValue();
                rightAxiomList = new AxiomList(rightAxiomTermList.getQualifiedName(), rightAxiomTermList.getKey());
                rightAxiomList.assignItem(0, rightAxiomTermList);
            }
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
        return (AxiomList) leftOperand.getValue();
    }

    /**
     * Returns flag set true if two aciom lists are size-wise congruent. 
     * @param leftAxiomList
     * @param rightAxiomList
     * @return boolean
     */
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
// TODO - Allow for strict comparison ie. matching term names            
//            int index = 0;
//            for (String termName: rightTermNames)
//                if (!termName.equalsIgnoreCase(leftTermNames.get(index++)))
//                    return false;
        }
        else if ((leftTermNames != null) || (rightTermNames != null))
            return false;
        return true;
    }
    

}
