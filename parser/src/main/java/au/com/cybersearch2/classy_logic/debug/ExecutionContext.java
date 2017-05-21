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
package au.com.cybersearch2.classy_logic.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.TemplateAssembler;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ExecutionContext
 * @author Andrew Bowley
 * 17May,2017
 */
public class ExecutionContext
{
    protected Map<QualifiedName, List<String>> emptyTemplateMap;
 
    public ExecutionContext()
    {
        emptyTemplateMap = new HashMap<QualifiedName, List<String>>();
    }
    
    public void beforeEvaluate(Operand operand)
    {
        OperandType operandType = operand.getOperator().getTrait().getOperandType();
        if (operandType != OperandType.UNKNOWN)
            System.out.println(operandType.toString().toLowerCase() + ": " + operand.toString());
        else
            System.out.println(operand.toString());
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    public void beforeEvaluate(QualifiedName qname, int position)
    {
        List<String> emptyTemplate = emptyTemplateMap.get(qname);
        System.out.println(emptyTemplate.get(position));
    }

    public void init(Scope scope)
    {
        TemplateAssembler templateAssembler = scope.getParserAssembler().getTemplateAssembler();
        Set<QualifiedName> templateNames = templateAssembler.getTemplateNames();
        for (QualifiedName qname: templateNames)
        {
            Template template = templateAssembler.getTemplate(qname);
            setEmptyTemplate(template);
        }
    }
    
    protected void setEmptyTemplate(Template template)
    {
        List<String> emptyTemplate = new ArrayList<String>();
        for (int i = 0; i < template.getTermCount(); ++i)
            emptyTemplate.add(template.getTermByIndex(i).toString());
        emptyTemplateMap.put(template.getQualifiedName(), emptyTemplate);
    }
}
