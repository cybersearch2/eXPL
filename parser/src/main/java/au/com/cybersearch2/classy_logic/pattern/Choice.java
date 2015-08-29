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
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Choice
 * Container for choice axioms and operands
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class Choice 
{
    /** Constant returned from Template select() when no match found */
    protected static final int NO_MATCH = -1;
    /** List of axioms, with each axiom representing one row of the choice */
    protected List<Axiom> choiceAxiomList;
    /** List of operands, with each operand representing the selection term of one row */
    protected List<Operand> variableList;
    /** Choice term names */
    protected List<String> termNameList;

	/**
	 * Construct Choice object
	 * @param name Key to identify choice
	 * @param scope Scope
	 */
	public Choice(String name, Scope scope) 
	{
	    // Get axiom source for this Choice and determine it's scope
	    AxiomSource choiceAxiomSource = scope.getGlobalScope().findAxiomSource(name); 
	    if (choiceAxiomSource == null)
	        choiceAxiomSource = scope.getAxiomSource(name);
	    else
	        scope = scope.getGlobalScope();
	    ParserAssembler parserAssembler = scope.getParserAssembler();
	    // Populate choiceAxiomList from axiom source with choice identity
		Iterator<Axiom> iterator = choiceAxiomSource.iterator();
		choiceAxiomList = new ArrayList<Axiom>();
		while (iterator.hasNext())
			choiceAxiomList.add(iterator.next());
		// Populate variableList from operand map using axiom term name keys
		variableList = new ArrayList<Operand>();
		termNameList = choiceAxiomSource.getAxiomTermNameList();
	    for (String termName: termNameList)
	    {
	        QualifiedName qualifiedTermName = new QualifiedName(scope.getAlias(), name, termName);
	        Operand operand = parserAssembler.getOperandMap().get(qualifiedTermName);
	        if (operand == null)
	        {
	            qualifiedTermName.clearTemplate();
	            // Variables are placed in global scope
	            operand = parserAssembler.getOperandMap().get(qualifiedTermName);
	        }
	    	variableList.add(operand);
	    }
	}

	/**
	 * Returns the list of choice axioms
	 * @return the choiceAxiomList
	 */
	public List<Axiom> getChoiceAxiomList() 
	{
		return choiceAxiomList;
	}

	public List<String> getTermNameList()
    {
        return termNameList;
    }

    /**
	 * Complete solution for given parameters
     * @param axiom Initializer axiom
	 * @param solution Solution containing query results so far
	 * @param template Template used to calculate choice
	 * @return Flag set true if selection match was found
	 */
	public boolean completeSolution(Solution solution, Template template, Axiom axiom)
	{
	    // Get template to perform selection
		int position = template.select();
		if (position == NO_MATCH)
		    return false;
		// Get selected axiom, default being last one in choice axiom list
        Axiom choiceAxiom = choiceAxiomList.get(position);
        // Get value to assign as result, default being match value
        Object value = template.getTermByIndex(position).getValue();
        if (value instanceof Null) // Ensure value is valid
        {   // Use initializer axiom term as fallback
            Term defaultTerm = axiom.getTermByName(choiceAxiom.getTermByIndex(0).getName());
            if (defaultTerm != null)
                value = defaultTerm.getValue();
        }
        // Create solution axiom using Template toAxiom() method
		Template solutionTemplate = new Template(template.getQualifiedName());
		int index = 0;
		Operand operand = variableList.get(index++);
		operand.assign(value);
		solutionTemplate.addTerm(operand);
		while (index < choiceAxiom.getTermCount())
		{
			operand = variableList.get(index);
			operand.assign(choiceAxiom.getTermByIndex(index).getValue());
			solutionTemplate.addTerm(operand);
			++index;
		}
		solution.put(template.getQualifiedName().toString(), solutionTemplate.toAxiom());
		return true;
	}
}
