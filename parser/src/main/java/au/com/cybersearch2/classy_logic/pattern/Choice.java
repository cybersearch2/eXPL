/**
    Copyright (C) 2014  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Choice
 * Container for choice axioms and operands
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class Choice 
{
    /** Constant returned from Template select() when no match found */
    public static final int NO_MATCH = -1;
    /** List of axioms, with each axiom representing one row of the choice */
    protected List<Axiom> choiceAxiomList;
    /** List of operands, with each operand representing the selection term of one row */
    protected List<Operand> variableList;
    /** Choice term names */
    protected List<String> termNameList;
    /** Choice selection is row index or -1 for no match */
    protected int selection;

	/**
	 * Construct Choice object
	 * @param name Qualified name identification
	 * @param scope Scope
	 */
	public Choice(QualifiedName name, Scope scope) 
	{
	    // Get axiom source for this Choice and determine it's scope
	    AxiomSource choiceAxiomSource = scope.findAxiomSource(name); 
	    ParserAssembler parserAssembler = scope.getParserAssembler();
	    // Populate choiceAxiomList from axiom source with choice identity
		Iterator<Axiom> iterator = choiceAxiomSource.iterator();
		choiceAxiomList = new ArrayList<Axiom>();
		while (iterator.hasNext())
			choiceAxiomList.add(iterator.next());
		// Populate variableList from operand map using axiom term name keys
		variableList = new ArrayList<Operand>();
		termNameList = choiceAxiomSource.getAxiomTermNameList();
        QualifiedName qualifiedContextname = parserAssembler.getQualifiedContextname();
	    for (String termName: termNameList)
	    {
	        QualifiedName qualifiedTermName = QualifiedName.parseName(termName, qualifiedContextname);
	        Operand operand = parserAssembler.getOperandMap().get(qualifiedTermName);
	        if (operand == null)
	        {
	            operand = new Variable(qualifiedTermName);
	        }
	    	variableList.add(operand);
	    }
	    selection = NO_MATCH;
	}

    /**
     * @return the selection
     */
    public int getSelection()
    {
        return selection;
    }

    /**
	 * Returns the list of choice axioms
	 * @return the choiceAxiomList
	 */
	public List<Axiom> getChoiceAxiomList() 
	{
		return choiceAxiomList;
	}

	/**
	 * Returns the term names
	 * @return List of names
	 */
	public List<String> getTermNameList()
    {
        return termNameList;
    }

    /**
	 * Complete solution for given parameters
	 * @param solution Solution containing query results so far
	 * @param template Template used to calculate choice
     * @param axiom Unification axiom
	 * @return Flag set true if selection match was found
	 */
	public boolean completeSolution(Solution solution, Template template, Axiom axiom, ExecutionContext context)
	{
	    // The emplate performs selection
		selection = template.select(context);
		if (selection == NO_MATCH)
		    return false;
		// Get selected axiom
        Axiom choiceAxiom = choiceAxiomList.get(selection);
        // Get selection value
        Term term = template.getTermByIndex(selection);
        if (term.getValue() instanceof Null) // Ensure value is valid
        {   // Use initializer axiom term as fallback
            Term selectionTerm = axiom.getTermByName(choiceAxiom.getTermByIndex(0).getName());
            if (selectionTerm != null)
                term = selectionTerm;
        }
        // Set variables and create solution axiom
        Axiom solutionAxiom = new Axiom(template.getName());
		// Selection term
		int index = 0;
		Operand operand = variableList.get(index++);
        operand.backup(0);
        term.unifyTerm(operand, template.getId());
        solutionAxiom.addTerm(new Parameter(operand.getName(), operand.getValue()));
		// Constants
		while (index < choiceAxiom.getTermCount())
		{
			operand = variableList.get(index);
            operand.backup(0);
            Term choiceTerm = choiceAxiom.getTermByIndex(index);
            choiceTerm.unifyTerm(operand, template.getId());
			solutionAxiom.addTerm(choiceTerm);
			++index;
		}
		// Add pass-thru variables, if any, to solution
		while (index < variableList.size())
		{
		    term = axiom.getTermByName(termNameList.get(index));
		    if (term != null)
		    {
	            operand = variableList.get(index);
	            operand.backup(0);
		        term.unifyTerm(operand, template.getId());
		        solutionAxiom.addTerm(term);
		    }
            ++index;
		}
		Parameter selectionTerm = new Parameter(template.getName(), (long)selection);
        solutionAxiom.addTerm(selectionTerm);
        solutionAxiom.getArchetype().clearMutable();
		solution.put(template.getQualifiedName().toString(), solutionAxiom);
		return true;
	}
	
    /**
     * Complete solution for given parameters
     * @param template Template used to calculate choice
     * @return Flag set true if selection match was found
     */
    public boolean completeSolution(Template template, int id, ExecutionContext context)
    {
        int index = 0;
        // Get selection operand
        Operand operand = variableList.get(index++);
        // Unifiy template terms if this has not already happened
        for (int i = 0; i < template.getTermCount(); i++)
        {   // Every term in the choice template has the same name
            // and all terms are set to the same value
            Term term = template.getTermByIndex(i);
            if (!term.isEmpty())
                break; // Term is already populated so proceed to selection
            term.unifyTerm(operand, id);
        }
        selection = template.select(context);
        if (selection == NO_MATCH)
            return false;
        // Get selected axiom, default being last one in choice axiom list
        Axiom choiceAxiom = choiceAxiomList.get(selection);
        operand.unifyTerm(template.getTermByIndex(selection), id);
        while (index < choiceAxiom.getTermCount())
        {
            operand = variableList.get(index);
            operand.unifyTerm(choiceAxiom.getTermByIndex(index), id);
            ++index;
        }
        return true;
    }

    /**
     * Backup Choice variables
     * @param id Modification id
     */
    public void backup(int id)
    {
        for (Operand operand: variableList)
            operand.backup(id);
    }
}
