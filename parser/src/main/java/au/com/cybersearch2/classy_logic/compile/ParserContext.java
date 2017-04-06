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
package au.com.cybersearch2.classy_logic.compile;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;

/**
 * ParserContext
 * Aggregates variables required while parsing 
 * @author Andrew Bowley
 * 6Apr.,2017
 */
public class ParserContext
{
    QueryProgram queryProgram;
    /** Current source marker */
    SourceMarker sourceMarker;
    /** Current scope */
    Scope scope;
    /** Current parserAssembler */
    ParserAssembler parserAssembler;
    /** Current operand map */
    OperandMap operandMap;
    
    public ParserContext(QueryProgram queryProgram)
    {
        this.queryProgram = queryProgram;
        resetScope();
    }

    public void setScope(Scope scope)
    {
        this.scope = scope;
        parserAssembler = scope.getParserAssembler();
        operandMap = parserAssembler.getOperandMap();
    }

    public void resetScope()
    {
        setScope(queryProgram.getGlobalScope());
    }

    public Scope getScope()
    {
        return scope;
    }
    
    /**
     * @return the sourceMarker
     */
    public SourceMarker getSourceMarker()
    {
        return sourceMarker;
    }

    /**
     * @param sourceMarker the sourceMarker to set
     */
    public void setSourceMarker(SourceMarker sourceMarker)
    {
        this.sourceMarker = sourceMarker;
    }

    /**
     * @return the parserAssembler
     */
    public ParserAssembler getParserAssembler()
    {
        return parserAssembler;
    }

    public OperandMap getOperandMap()
    {
        return operandMap;
    }

    public QualifiedName getContextName()
    {
        return operandMap.getQualifiedContextname();
    }
    
    public void setContextName(QualifiedName qualifiedName)
    {
        operandMap.setQualifiedContextname(qualifiedName);
    }
    
    /**
     * @param parserAssembler the parserAssembler to set
     */
    public void setParserAssembler(ParserAssembler parserAssembler)
    {
        this.parserAssembler = parserAssembler;
    }

    /**
     * @return the queryProgram
     */
    public QueryProgram getQueryProgram()
    {
        return queryProgram;
    }

}
