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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TemplateArchetype;

/**
 * TemplateAssembler
 * @author Andrew Bowley
 * 12May,2017
 */
public class TemplateAssembler
{
    /** The templates */
    protected Map<QualifiedName, Template> templateMap;
    /** Scope */
    protected Scope scope;

    public TemplateAssembler(Scope scope)
    {
        this.scope = scope;
        templateMap = new HashMap<QualifiedName, Template>();
    }
    /**
     * Add contents of another TemplateAssembler to this object
     * @param templateAssembler Other TemplateAssembler object
     */
    public void addAll(TemplateAssembler templateAssembler) 
    {
        templateMap.putAll(templateAssembler.templateMap);
    }

    /**
     * Returns set of qualified name template keys
     * @return QualifiedName set
     */
    public Set<QualifiedName> getTemplateNames()
    {
        return templateMap.keySet();
    }

    /**
     * Add a new template to this ParserAssembler
     * @param qualifiedName Qualified template name or axiom name if for Choice
     * @param isCalculator Flag true if template declared a calculator
     */
    public Template createTemplate(QualifiedName qualifiedName, boolean isCalculator)
    {
        boolean isChoice = qualifiedName.getTemplate().isEmpty() && isCalculator;
        QualifiedName qualifiedTemplateName = qualifiedName;
        if (isChoice)
            qualifiedTemplateName = new QualifiedTemplateName(scope.getAlias(), qualifiedName.getName());
        Template template = new Template(new TemplateArchetype(qualifiedTemplateName));
        template.setCalculator(isCalculator);
        if (isChoice)
            template.setChoice(true);
        templateMap.put(qualifiedTemplateName, template);
        return template;
    }

    public Template createChoiceTemplate(Template outerTemplate, QualifiedName choiceQualifiedName)
    {
        Template choiceTemplate = outerTemplate.choiceInstance(getTemplate(choiceQualifiedName));
        templateMap.put(choiceTemplate.getQualifiedName(), choiceTemplate);
        return choiceTemplate;
    }
    
    /**
     * Add a term to a template
     * @param qualifiedTemplateName Qualified template name
     * @param term Operand object
     */
    public void addTemplate(QualifiedName qualifiedTemplateName, Operand term)
    {
        Template template = templateMap.get(qualifiedTemplateName);
        template.addTerm(term);
    }

    /**
     * Set template properties - applies only to Calculator
     * @param qualifiedTemplateName Qualified template name
     * @param properties
     * @see au.com.cybersearch2.classy_logic.pattern.Template#initialize()
     */
    public void addTemplate(QualifiedName qualifiedTemplateName, Map<String, Object> properties)
    {
        Template template = templateMap.get(qualifiedTemplateName);
        template.addProperties(properties);
    }

    /**
     * Returns template with specified qualified name
     * @param qualifiedTemplateName Qualified template name
     * @return Template object or null if template not found
     */
    public Template getTemplate(QualifiedName qualifiedTemplateName)
    {
        return templateMap.get(qualifiedTemplateName);
    }

    /**
     * Returns template with specified name
     * @param textName
     * @return Template object or null if template not found
     */
    public Template getTemplate(String textName)
    {
        return templateMap.get(QualifiedName.parseTemplateName(textName));
    }

    /**
     * Returns scope of axiom source specified by qname
     * @param qname Qualified name
     * @return Scope object or null if axiom source not found
     */
    public Scope findTemplateScope(QualifiedName qname)
    {
        if (qname.getTemplate().isEmpty())
            // qname must be in axiom form
            return null;
        Template template = templateMap.get(qname);
        if (template != null)
            return scope;
        if (!qname.getScope().isEmpty())
            qname = new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, qname.getTemplate());
        template = scope.getGlobalTemplateAssembler().getTemplate(qname);
        if (template != null)
            return scope.getGlobalScope();
        return null; 
    }
 
    /**
     * Create new template and add to head template chain
     * @param outerTemplateName Qualified name of head template
     * @param name Qualified name of inner template
     * @return Template object
     */
    public Template chainTemplate(QualifiedName outerTemplateName, String name) 
    {
        Template template = getTemplate(outerTemplateName);
        Template chainTemplate = template.innerTemplateInstance(name);
        templateMap.put(chainTemplate.getQualifiedName(), chainTemplate);
        return chainTemplate;
    }

    /**
     * Run parser task for every non=empty template. For each operand in the template, 
     * the task walks the operand tree and adds all terms to the archetype which belong
     * to the template name space.
     */
    public void doParserTask()
    {
        for (Template template: templateMap.values())
            if (template.getTermCount() > 0)
                template.getParserTask().run();
    }

}
