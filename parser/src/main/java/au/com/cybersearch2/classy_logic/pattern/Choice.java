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

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
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

	/**
	 * Construct Choice object
	 * @param name Key to identify choice
	 * @param parserAssembler ParserAssembler compiler object
	 */
	public Choice(String name, ParserAssembler parserAssembler) 
	{
	    // Populate choiceAxiomList from axiom source with choice identity
		Iterator<Axiom> iterator = parserAssembler.getAxiomSource(name).iterator();
		choiceAxiomList = new ArrayList<Axiom>();
		while (iterator.hasNext())
			choiceAxiomList.add(iterator.next());
		// Populate variableList from operand map using axiom term name keys
		variableList = new ArrayList<Operand>();
	    List<String> termNameList = parserAssembler.getAxiomTermNameList(name);
	    for (String termName: termNameList)
	    	variableList.add(parserAssembler.getOperandMap().get(termName));
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
	 * Complete solution for given parameters
	 * @param solution Solution containing query results so far
	 * @param template Template used to calculate choice
	 * @param matchValue Value used to select choice, needed for default scenario
	 */
	public void completeSolution(Solution solution, Template template, Object matchValue)
	{
	    // Get template to perform selection
		int position = template.select();
		// Get selected axiom, default being last one in choice axiom list
        Axiom choiceAxiom = choiceAxiomList.get(position != NO_MATCH ? position : template.getTermCount() - 1);
        // Get value to assign as result, default being match value
        Object value = position != NO_MATCH ? template.getTermByIndex(position).getValue() : matchValue;
        // Create solution axiom using Template toAxiom() method
		Template solutionTemplate = new Template(template.getName());
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
		solution.put(template.getName(), solutionTemplate.toAxiom());
	}
}
