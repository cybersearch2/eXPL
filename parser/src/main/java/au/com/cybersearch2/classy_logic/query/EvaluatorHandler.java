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
package au.com.cybersearch2.classy_logic.query;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.operator.OperatorTerm;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.LiteralParameter;
import au.com.cybersearch2.classy_logic.terms.LiteralType;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * EvaluatorHandler
 * @author Andrew Bowley
 * 6Jun.,2017
 */
public class EvaluatorHandler implements SolutionHandler, DebugTarget
{
    /** Inner Template to receive query results */
    protected Template innerTemplate;
    /** Name to use to obtain solution */
    protected String solutionName;
    /** AxiomTermList to contain query result */
    protected AxiomTermList axiomTermList;
    /** Execution context for debugging */
    protected ExecutionContext context;
    
    /**
     * Construct EvaluatorHandler object
     * @param innerTemplate Inner Template to receive query results
     * @param solutionName Name to use to obtain solution
     * @param axiomTermList AxiomTermList to contain query result
     */
    public EvaluatorHandler(Template innerTemplate, String solutionName, AxiomTermList axiomTermList)
    {
        this.innerTemplate = innerTemplate;
        this.solutionName = solutionName;
        this.axiomTermList = axiomTermList;
    }

    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        this.context = context;
    }

    @Override
    public boolean onSolution(Solution solution)
    {
        Axiom axiom = solution.getAxiom(solutionName);
        if (axiom.getTermCount() > 0)
        {
            // backup to prepare for unification/evaluation
            innerTemplate.backup(false);
            if (innerTemplate.unify(axiom, solution))
            {
                if (innerTemplate.evaluate(context) == EvaluationStatus.COMPLETE);
                {
                    Axiom innerAxiom = new Axiom(innerTemplate.getKey());
                    for (int i = 0; i < (innerTemplate.getTermCount()); i++)
                    {
                        Operand operand = innerTemplate.getTermByIndex(i);
                        String termName = operand.getName();
                        Term term = axiom.getTermByName(termName);
                        if (term == null)
                            term = new Parameter(termName);
                        else if (operand.getOperator().getTrait().getOperandType() != OperandType.UNKNOWN)
                            term = new OperatorTerm(term.getName(), term.getValue(), operand.getOperator());
                        innerAxiom.addTerm(term);
                    }
                    innerAxiom.getArchetype().clearMutable();
                    axiomTermList.setAxiom(innerAxiom);
                }
            }
        }
        else
        {
            Axiom innerAxiom = new Axiom(innerTemplate.getKey());
            for (int i = 0; i < (innerTemplate.getTermCount()); i++)
            {
                String termName = innerTemplate.getTermByIndex(i).getName();
                Term term = new LiteralParameter(termName, new Unknown(), LiteralType.unknown);
                innerAxiom.addTerm(term);
            }
            innerAxiom.getArchetype().clearMutable();
            axiomTermList.setAxiom(innerAxiom);
        }
        return true;
    }
}
