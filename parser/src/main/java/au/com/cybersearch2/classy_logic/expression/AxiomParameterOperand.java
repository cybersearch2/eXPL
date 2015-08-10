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

import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;

/**
 * AxiomParameterOperand
 * Populates an axiom from a parameter list after the list operands have been evaluated.
 * As this class extends AxiomOperand it supports AxiomList concatenation and assignment.
 * @author Andrew Bowley
 * 9 Aug 2015
 */
public class AxiomParameterOperand extends AxiomOperand
{
    /** Performs function using parameters contained in expression and returns value */
    protected ParameterList<AxiomList> parameterList;

    /**
     * Construct an AxiomParameterOperand object
     * @param name Name of operand
     * @param argumentExpression Operand containing parameters as a tree of operands
     */
    public AxiomParameterOperand(String name, Operand argumentExpression)
    {
        super(name, argumentExpression);
        parameterList = new ParameterList<AxiomList>(argumentExpression, axiomGenerator());
    }

    /**
     * Returns an object which implements CallEvaluator interface returning an AxiomList
     * given a list of terms to marshall into an axiom
     * @return CallEvaluator object of generic return type AxiomList
     */
    protected CallEvaluator<AxiomList> axiomGenerator() 
    {
        return new CallEvaluator<AxiomList>(){

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public AxiomList evaluate(List<Term> argumentList)
            {
                return AxiomUtils.marshallAxiomTerms(name, name, argumentList);
            }};
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if (status == EvaluationStatus.COMPLETE)
            setValue(parameterList.evaluate());
        return status;
    }
}
