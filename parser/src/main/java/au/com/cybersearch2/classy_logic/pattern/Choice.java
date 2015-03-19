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
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Choice
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class Choice 
{
    protected List<Axiom> choiceAxiomList;
    protected List<Operand> variableList;

	/**
	 * 
	 */
	public Choice(String name, ParserAssembler parserAssembler) 
	{
		Iterator<Axiom> iterator = parserAssembler.getAxiomSource(name).iterator();
		choiceAxiomList = new ArrayList<Axiom>();
		while (iterator.hasNext())
			choiceAxiomList.add(iterator.next());
		variableList = new ArrayList<Operand>();
	    List<String> termNameList = parserAssembler.getAxiomTermNameList(name);
	    for (String termName: termNameList)
	    	variableList.add(parserAssembler.getOperandMap().get(termName));
	}

	/**
	 * @return the choiceAxiomList
	 */
	public List<Axiom> getChoiceAxiomList() 
	{
		return choiceAxiomList;
	}

	public void completeSolution(Solution solution, Template template)
	{
		int position = template.select();
		Template solutionTemplate = new Template(template.getName());
		Term term0 = template.getTermByIndex(position);
		int index = 0;
		Operand operand = variableList.get(index++);
		operand.assign(term0.getValue());
		solutionTemplate.addTerm(operand);
		Axiom choiceAxiom = choiceAxiomList.get(position);
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
