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
package au.com.cybersearch2.classy_logic.expression;

import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * AxiomCallOperand
 * @author Andrew Bowley
 * 1Sep.,2017
 */
public class AxiomCallOperand extends CallOperand<Axiom>
{

    public AxiomCallOperand(QualifiedName qname, Template template,
            CallEvaluator<Axiom> callEvaluator)
    {
        super(qname, template, callEvaluator);
    }

    @Override
    protected void doCall(List<Term> termList)
    {
        Axiom axiom = (Axiom)callEvaluator.evaluate(termList);
        if (axiom == null)
            clearValue();
        else if (axiom.getTermCount() == 1)
            setValue(axiom.getTermByIndex(0).getValue());
        else
            setValue(axiom);
    }

}
