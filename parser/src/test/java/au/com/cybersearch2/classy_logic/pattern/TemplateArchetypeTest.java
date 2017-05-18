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
package au.com.cybersearch2.classy_logic.pattern;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * TemplateArchetypeTest
 * @author Andrew Bowley
 * 17May,2017
 */
public class TemplateArchetypeTest
{
 
    @Test
    public void test_pair_axiom_by_position()
    {
        AxiomArchetype pairArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        new Axiom(pairArchetype, "athens", 23);
        assertThat(pairArchetype.isAnonymousTerms()).isTrue();
        Template template = createChargeUnificationTarget();
        int[] termMapping = template.getTemplateArchetype().createTermMapping(pairArchetype);
        assertThat(termMapping.length).isEqualTo(2);
        assertThat(termMapping).isEqualTo(new int[]{ 0, 1 });
        assertThat(pairArchetype.isAnonymousTerms()).isFalse();
        assertThat(pairArchetype.getMetaData(0).getName()).isEqualTo("city");
        assertThat(pairArchetype.getMetaData(1).getName()).isEqualTo("fee");
    }
    
    @Test
    public void test_pair_axiom_extra_term_by_position()
    {
        AxiomArchetype pairArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        new Axiom(pairArchetype, "sparta", 13, "Spiro");
        assertThat(pairArchetype.isAnonymousTerms()).isTrue();
        Template template = createChargeUnificationTarget();
        int[] termMapping = template.getTemplateArchetype().createTermMapping(pairArchetype);
        assertThat(termMapping.length).isEqualTo(2);
        assertThat(termMapping).isEqualTo(new int[]{ 0, 1 });
        assertThat(pairArchetype.isAnonymousTerms()).isFalse();
        assertThat(pairArchetype.getMetaData(0).getName()).isEqualTo("city");
        assertThat(pairArchetype.getMetaData(1).getName()).isEqualTo("fee");
        assertThat(pairArchetype.getMetaData(2).isAnonymous()).isTrue();
    }
    
    @Test
    public void test_pair_axiom_by_name()
    {
        AxiomArchetype pairArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        new Axiom(pairArchetype, new Parameter("fee", 13), new Parameter("city", "athens"));
        assertThat(pairArchetype.isAnonymousTerms()).isFalse();
        Template template = createChargeUnificationTarget();
        int[] termMapping = template.getTemplateArchetype().createTermMapping(pairArchetype);
        assertThat(termMapping.length).isEqualTo(2);
        assertThat(termMapping).isEqualTo(new int[]{ 1,0 });
    }
    
    @Test
    public void test_pair_axiom_by_name_plus_anon_term()
    {
        AxiomArchetype pairArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        new Axiom(pairArchetype, new Parameter("fee", 13), "Spiro", new Parameter("city", "athens"));
        assertThat(pairArchetype.isAnonymousTerms()).isFalse();
        Template template = createChargeUnificationTarget();
        int[] termMapping = template.getTemplateArchetype().createTermMapping(pairArchetype);
        assertThat(termMapping.length).isEqualTo(2);
        assertThat(termMapping).isEqualTo(new int[]{ 2,0 });
    }
    
    @Test
    public void test_pair_axiom_by_name_one_anon_pair()
    {
        AxiomArchetype pairArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        new Axiom(pairArchetype, new Parameter("fee", 13), "Spiro", new Parameter("city", "athens"));
        assertThat(pairArchetype.isAnonymousTerms()).isFalse();
        Template template = createChargeUnificationTarget();
        template.addTerm(new StringOperand(QualifiedName.parseGlobalName("manager")));
        int[] termMapping = template.getTemplateArchetype().createTermMapping(pairArchetype);
        assertThat(termMapping.length).isEqualTo(3);
        assertThat(termMapping).isEqualTo(new int[]{ 2,0,-1 });
    }
    
   protected Template createChargeUnificationTarget()
    {
        QualifiedName contextName = parseTemplateName("charge");
        TemplateArchetype templateArchetype = new TemplateArchetype(contextName);
        Template template = new Template(templateArchetype);
        StringOperand city = new StringOperand(QualifiedName.parseName("city", contextName));
        template.addTerm(city);
        IntegerOperand fee = new IntegerOperand(QualifiedName.parseName("fee", contextName));
        template.addTerm(fee);
        return template;
    }
    
    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }

}
