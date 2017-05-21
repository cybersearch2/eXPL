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
package au.com.cybersearch2.classy_logic.expression;

import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.OperandParam;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * ParameterOperand
 * Variable which is set using a supplied function object and parameters contained in an Operand tree
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterOperand<R> extends Variable implements DebugTarget
{
    /** Collects parameters from an Operand tree and passes them to a supplied function object */
    protected ParameterList<R> parameterList;
    /** Root of Operand tree for unification */
    protected Operand paramsTreeRoot;

    /**
     * Construct a ParameterOperand object
     * @param qname Qualified name
     * @param operandParamList List of Operand arguments or null for no arguments
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterOperand(QualifiedName qname, List<OperandParam> operandParamList, CallEvaluator<R> callEvaluator) 
    {
        super(qname);
        parameterList = new ParameterList<R>(operandParamList, callEvaluator);
        if ((operandParamList != null) && 
            !operandParamList.isEmpty())
            paramsTreeRoot = OperandParam.buildOperandTree(operandParamList);
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    public EvaluationStatus evaluate(int id)
    {
        R returnValue = parameterList.evaluate(id);
        if ((returnValue != null) && (returnValue instanceof AxiomTermList))
        {
            AxiomTermList axiomTermList = (AxiomTermList)returnValue;
            Axiom axiom = axiomTermList.getAxiom();
            if (axiom.getTermCount() == 1)
                setValue(axiom.getTermByIndex(0).getValue());
            else
                setValue(axiom);
        }
        else
            setValue(returnValue);
        this.id = id;
        return EvaluationStatus.COMPLETE;
    }
 
    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        if (paramsTreeRoot != null)
            paramsTreeRoot.backup(id);
        return super.backup(id);
    }
    
    /**
     * Returns operand tree fur parameter unification     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return paramsTreeRoot;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.expression.Variable#toString()
     */
    @Override
    public String toString()
    {
        if (empty)
        {
            StringBuilder builder = new StringBuilder(qname.toString());
            builder.append('(');
            if (paramsTreeRoot != null)
            {
                List<OperandParam> opParamList = parameterList.getOperandParamList();
                OperandParam op1 = opParamList.get(0);
                builder.append(getOperandText(op1));
                if (opParamList.size() > 1)
                    builder.append(" ... ").append(getOperandText(opParamList.get(opParamList.size() - 1)));
            }
            builder.append(')');
            return builder.toString();
        }
        return super.toString();
    }
 
    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        parameterList.setExecutionContext(context);
    }
    
    protected String getOperandText(OperandParam param)
    {
       return param.getName().isEmpty() ? param.getOperand().toString() : param.getName(); 
    }


}
