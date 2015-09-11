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
import au.com.cybersearch2.classy_logic.expression.Variable;
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
    public static final int NO_MATCH = -1;
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
	        QualifiedName qualifiedContextname = parserAssembler.getOperandMap().getQualifiedContextname();
	        QualifiedName qualifiedTermName = QualifiedName.parseName(termName, qualifiedContextname);
	        Operand operand = parserAssembler.getOperandMap().get(qualifiedTermName);
	        if (operand == null)
	            operand = new Variable(qualifiedTermName);
	        /*
	        if ((operand == null) && !qualifiedTermName.getTemplate().isEmpty())
	        {
	            qualifiedTermName.clearTemplate();
	            // Variables are placed in global scope
	            operand = parserAssembler.getOperandMap().get(qualifiedTermName);
	        }
            if ((operand == null) && !qualifiedTermName.getScope().isEmpty())
            {
                qualifiedTermName.clearScope();
                // Variables are placed in global scope
                operand = parserAssembler.getOperandMap().get(qualifiedTermName);
            }
            */
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
	public boolean completeSolution(Solution solution, Template template, Axiom axiom)
	{
	    // The emplate performs selection
		int position = template.select();
		if (position == NO_MATCH)
		    return false;
		// Get selected axiom
        Axiom choiceAxiom = choiceAxiomList.get(position);
        // Get selection value
        Term term = template.getTermByIndex(position);
        if (term.getValue() instanceof Null) // Ensure value is valid
        {   // Use initializer axiom term as fallback
            Term selectionTerm = axiom.getTermByName(choiceAxiom.getTermByIndex(0).getName());
            if (selectionTerm != null)
                term = selectionTerm;
        }
        // Create solution axiom using Template toAxiom() method
		Template solutionTemplate = new Template(template.getQualifiedName());
		// Selection term
		int index = 0;
		Operand operand = variableList.get(index++);
        operand.backup(0);
        term.unifyTerm(operand, template.getId());
		solutionTemplate.addTerm(operand);
		// Constants
		while (index < choiceAxiom.getTermCount())
		{
			operand = variableList.get(index);
            operand.backup(0);
			choiceAxiom.getTermByIndex(index).unifyTerm(operand, template.getId());
			solutionTemplate.addTerm(operand);
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
	            solutionTemplate.addTerm(operand);
		    }
            ++index;
		}
		solution.put(template.getQualifiedName().toString(), solutionTemplate.toAxiom());
		return true;
	}
	
    /**
     * Complete solution for given parameters
     * @param template Template used to calculate choice
     * @return Flag set true if selection match was found
     */
    public boolean completeSolution(Template template, int id)
    {
        int index = 0;
        // Get selection operand
        Operand operand = variableList.get(index++);
        // Unifiy template terms if this has not already happened
        for (int i = 0; i < template.getTermCount(); i++)
        {   // Every term in the choice template has the same name
            // abd all terms are set to the same value
            Term term = template.getTermByIndex(i);
            if (!term.isEmpty())
                break; // Term is already populated so proceed to selection
            term.unifyTerm(operand, id);
        }
        int position = template.select();
        if (position == NO_MATCH)
            return false;
        // Get selected axiom, default being last one in choice axiom list
        Axiom choiceAxiom = choiceAxiomList.get(position);
        operand.unifyTerm(template.getTermByIndex(position), id);
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
