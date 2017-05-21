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
 * Proxies operator according to delegate type
 * @see DelegateType
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

    /** Defines operations that an Operand performs with other operands. */
    protected Operator proxy;
    /** Current operator DelegateType */
    protected DelegateType delegateType;
    protected boolean isProxyAssigned;

    /**
     * Construct DelegateOperator object
     */
    public DelegateOperator()
    {   // Default to type ASSIGN_ONLY
        delegateType = DelegateType.ASSIGN_ONLY;
        proxy = operatorInstance(DelegateType.ASSIGN_ONLY);
    }
    
    /**
     * Delegates operator depending on given value class
     */
    public void setDelegate(Class<?> clazz)
    {
        DelegateType newDelegateType = delegateTypeMap.get(clazz);
        if (newDelegateType == null)
            newDelegateType = DelegateType.ASSIGN_ONLY;
        if (newDelegateType != delegateType)
        {
            delegateType = newDelegateType;
            if (!isProxyAssigned)
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

    public void setProxy(Operator proxy)
    {
        this.proxy = proxy;
        isProxyAssigned = true;
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
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return proxy.getTrait();
    }

    /**
     * getRightOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return proxy.getRightOperandOps();
    }

    /**
     * getLeftOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return proxy.getLeftOperandOps();
    }

    /**
     * getStringOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getStringOperandOps()
     */
     @Override
     public OperatorEnum[] getStringOperandOps()
     {
         return proxy.getStringOperandOps();
     }

    /**
     * numberEvaluation - unary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return proxy.numberEvaluation(operatorEnum2, rightTerm);
    }

    /**
     * numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return proxy.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * booleanEvaluation
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return proxy.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * Returns new instance of operator for given delegate type
     * @param delegateType
     * @return Operator object
     */
    protected Operator operatorInstance(DelegateType delegateType)
    {
        return delegateType.getOperatorFactory().delegate();
    }

    /**
     * Returns flag set true if give class is a delegate class
     * @param clazz Class to check
     * @return boolean
     */
    public static boolean isDelegateClass(Class<?> clazz)
    {
        return delegateTypeMap.containsKey(clazz);
    }

}
