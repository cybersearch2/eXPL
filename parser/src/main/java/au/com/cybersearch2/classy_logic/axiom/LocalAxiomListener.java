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
package au.com.cybersearch2.classy_logic.axiom;

import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * LocalAxiomListener
 * @author Andrew Bowley
 * 29May,2017
 */
public class LocalAxiomListener
        implements
            LocaleListener,
            ParserRunner
{
    protected QualifiedName qualifiedAxiomName;
    protected AxiomListener axiomListener;
    
    /**
     * 
     */
    public LocalAxiomListener(QualifiedName qualifiedAxiomName, AxiomListener axiomListener)
    {
        this.qualifiedAxiomName = qualifiedAxiomName;
        this.axiomListener = axiomListener;
    }

    /**
     * Assign backing axiom to local list. If no axiom source is found, create one
     * in which each item is "unknown"
     */
    @Override
    public void onScopeChange(Scope scope) 
    {
        Scope globalScope = scope.getGlobalScope();
        // Register locale listener with Global scope in which all local axioms must be declared
        ParserAssembler parserAssembler = globalScope.getParserAssembler();
        Axiom localAxiom = null;
        QualifiedName qname = new QualifiedName(scope.getName(), qualifiedAxiomName.getName());
        AxiomSource axiomSource = scope.getParserAssembler().getAxiomSource(qname);
        if (axiomSource == null)
            axiomSource = parserAssembler.getAxiomSource(qname);
        if (axiomSource == null)
        {
            axiomSource = scope.getGlobalParserAssembler().getAxiomSource(qualifiedAxiomName);
            if (axiomSource != null)
                localAxiom = createUnknownAxiom(qname.toString(), axiomSource.getArchetype().getTermNameList());
            else if (scope.getName().equals(QueryProgram.GLOBAL_SCOPE))
                return; // This is not an error when global scope context is being reset
            else
                throw new ExpressionException("Axiom source \"" + qualifiedAxiomName.toString() + "\" not found");
        }
        if (localAxiom == null)
        {
            Iterator<Axiom> iterator = axiomSource.iterator();
            if (iterator.hasNext())
                localAxiom = iterator.next();
            else
                localAxiom = createUnknownAxiom(qname.toString(), axiomSource.getArchetype().getTermNameList());
        }
        axiomListener.onNextAxiom(qualifiedAxiomName, localAxiom);
    }

    @Override
    public void run(ParserAssembler parserAssembler)
    {
        Scope scope = parserAssembler.getScope();
        for (String scopeName: scope.getScopeNames())
        {
            onScopeChange(scope.getScope(scopeName));
        }
    }

    /**
     * Create placeholder axiom with "unknown" items
     * @param axiomName
     * @param termNameList List of term names
     * @return Axiom object
     */
    private Axiom createUnknownAxiom(String axiomName, List<String> termNameList)
    {
        Axiom axiom = new Axiom(axiomName);
        Unknown unknown = new Unknown();
        for (String termName: termNameList)
            axiom.addTerm(new Parameter(termName, unknown));
        return axiom;
    }


}
