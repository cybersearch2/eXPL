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
package au.com.cybersearch2.classy_logic.tutorial14;

import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * SchoolFunctionProvider
 * @author Andrew Bowley
 * 17Aug.,2017
 */
public class SchoolFunctionProvider implements FunctionProvider
{

    static String[] MARKS =
    {
        "f-", "f", "f+", "e-", "e", "e+", "d-", "d", "d+", 
        "c-", "c", "c+", "b-", "b", "b+", "a-", "a", "a+"
    };
 
    static String SUBJECT = "subject";
    static String MARK = "mark";
    
     /**
     * @see au.com.cybersearch2.classy_logic.interfaces.FunctionProvider#getName()
     */
    @Override
    public String getName()
    {
        return "school";
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.FunctionProvider#getCallEvaluator(java.lang.String)
     */
    @Override
    public CallEvaluator<Axiom> getCallEvaluator(String identifier)
    {
        if (identifier.equals("subjects"))
            return new CallEvaluator<Axiom>(){

                @Override
                public String getName()
                {
                    return "subjects";
                }

                @Override
                public Axiom evaluate(List<Term> argumentList)
                {
                    int english = ((Long) argumentList.get(0).getValue()).intValue();
                    int maths = ((Long) argumentList.get(1).getValue()).intValue();
                    int history = ((Long) argumentList.get(2).getValue()).intValue();
                    Axiom axiom = new Axiom(getName());
                    AxiomArchetype archetype = (AxiomArchetype) axiom.getArchetype();
                    QualifiedName listName = QualifiedName.parseGlobalName("subjects");
                    AxiomList axiomList = new AxiomList(listName, archetype.getQualifiedName());
                    axiom.addTerm(new Parameter(SUBJECT, "English"));
                    axiom.addTerm(new Parameter(MARK, MARKS[english - 1]));
                    archetype.clearMutable();
                    AxiomTermList termList = new AxiomTermList(listName, archetype.getQualifiedName());
                    termList.setAxiom(axiom);
                    axiomList.assignItem(0, termList);
                    axiom = new Axiom(archetype);
                    axiom.addTerm(new Parameter(SUBJECT, "Math"));
                    axiom.addTerm(new Parameter(MARK, MARKS[maths - 1]));
                    termList = new AxiomTermList(listName, archetype.getQualifiedName());
                    termList.setAxiom(axiom);
                    axiomList.assignItem(1, termList);
                    axiom = new Axiom(archetype);
                    axiom.addTerm(new Parameter(SUBJECT, "History"));
                    axiom.addTerm(new Parameter(MARK, MARKS[history - 1]));
                    termList = new AxiomTermList(listName, archetype.getQualifiedName());
                    termList.setAxiom(axiom);
                    axiomList.assignItem(2, termList);
                    QualifiedName returnName = QualifiedName.parseGlobalName("marks_list");
                    archetype = new AxiomArchetype(returnName);
                    axiom = new Axiom(archetype);
                    axiom.addTerm(new Parameter("marks_list", axiomList));
                    return axiom;
                }
                
                @Override
                public void setExecutionContext(ExecutionContext context)
                {
                    // Not supported
                }

            };
            // Throw exception for unrecognized function name   
            throw new ExpressionException("Unknown function identifier: " + identifier);
    }

}
