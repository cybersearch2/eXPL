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
package au.com.cybersearch2.classy_logic.operator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;

/**
 * DelegateOperator
 * @author Andrew Bowley
 * 29Apr.,2017
 */
public class DelegateOperator implements Operator
{
    /** Map value class to Operator delegate type */
    protected static Map<Class<?>, DelegateType> delegateTypeMap;

    static
    {
        delegateTypeMap = new HashMap<Class<?>, DelegateType>();
        delegateTypeMap.put(String.class, DelegateType.STRING);
        delegateTypeMap.put(Integer.class, DelegateType.INTEGER);
        delegateTypeMap.put(Long.class, DelegateType.INTEGER);
        delegateTypeMap.put(Boolean.class, DelegateType.BOOLEAN);
        delegateTypeMap.put(Double.class, DelegateType.DOUBLE);
        delegateTypeMap.put(BigDecimal.class, DelegateType.DECIMAL);
        delegateTypeMap.put(AxiomTermList.class, DelegateType.ASSIGN_ONLY);
        delegateTypeMap.put(AxiomList.class, DelegateType.AXIOM);
        delegateTypeMap.put(Null.class, DelegateType.NULL);
    }

    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected Operator proxy;
    /** Current operator DelegateType */
    protected DelegateType delegateType;

    public DelegateOperator()
    {
        delegateType = DelegateType.ASSIGN_ONLY;
        proxy = operatorInstance(DelegateType.ASSIGN_ONLY);
    }
    
    /**
     * Creates delegate instance according to type of value
     */
    public void setDelegate(Class<?> clazz)
    {
        DelegateType newDelegateType = delegateTypeMap.get(clazz);
        if (newDelegateType == null)
            newDelegateType = DelegateType.ASSIGN_ONLY;
        if (newDelegateType != delegateType)
        {
            delegateType = newDelegateType;
            proxy = operatorInstance(newDelegateType);
        }
    }

    /**
     * @return the proxy
     */
    public Operator getProxy()
    {
        return proxy;
    }

    /**
     * @return the delegateType
     */
    public DelegateType getDelegateType()
    {
        return delegateType;
    }

    /**
     * Set delegate type
     * @param delegateType
     */
    public void setDelegateType(DelegateType delegateType)
    {
        this.delegateType = delegateType;
        proxy = operatorInstance(delegateType);
    }
    
    /**
     * @return the delegateType
     */
    public DelegateType getDelegateTypeForClass(Class<?> clazz)
    {
        return delegateTypeMap.get(clazz);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return proxy.getRightOperandOps();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return proxy.getLeftOperandOps();
    }

    /**
     * Returns OperatorEnum values for which this Term is a valid String operand
     * @return OperatorEnum[]
     */
     @Override
     public OperatorEnum[] getStringOperandOps()
     {
         return proxy.getStringOperandOps();
     }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return proxy.numberEvaluation(operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return proxy.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return proxy.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    @Override
    public Trait getTrait()
    {
        return proxy.getTrait();
    }

    protected Operator operatorInstance(DelegateType delegateType)
    {
        return delegateType.getOperatorFactory().delegate();
    }
    
    public static boolean isDelegateClass(Class<?> clazz)
    {
        return delegateTypeMap.containsKey(clazz);
    }

}
