/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * QueryOperand
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class QueryOperand extends ExpressionOperand implements ParserRunner
{

    public QueryOperand(QualifiedName qname)
    {
        super(qname);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps()
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps()
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm)
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm)
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm)
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
    }

}
