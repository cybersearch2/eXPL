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

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomParameterOperand
 * @author Andrew Bowley
 * 9 Aug 2015
 */
public class AxiomParameterOperand extends AxiomOperand
{
    /** Performs function using parameters contained in expression and returns value */
    protected ParameterList<AxiomList> parameterList;

    public AxiomParameterOperand(String name, Operand argumentExpression)
    {
        super(name, argumentExpression);
        parameterList = new ParameterList<AxiomList>(argumentExpression, axiomGenerator());
    }
    
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
                // Give axiom same name as operand
                Axiom axiom = new Axiom(name);
                List<String> axiomTermNameList = new ArrayList<String>();
                for (Term arg: argumentList)
                {
                    if (axiomTermNameList != null) 
                    {    
                        if (!arg.getName().equals(Term.ANONYMOUS))
                            axiomTermNameList.add(arg.getName());
                        else // Cannot use term name list with anonymous terms
                            axiomTermNameList = null;  
                    }
                    axiom.addTerm(arg);
                }
                // Wrap axiom in AxiomList object to allow interaction with other AxiomLists
                AxiomTermList axiomTermList = new AxiomTermList(name, name);
                axiomTermList.setAxiom(axiom);
                axiomTermList.setAxiomTermNameList(axiomTermNameList);
                AxiomList axiomList = new AxiomList(getName(), name);
                axiomList.assignItem(0, axiomTermList);
                axiomList.setAxiomTermNameList(axiomTermNameList);
               return axiomList;
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
