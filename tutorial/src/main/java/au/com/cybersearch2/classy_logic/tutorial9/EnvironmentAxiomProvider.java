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
package au.com.cybersearch2.classy_logic.tutorial9;

import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * EnvironmentAxiomProvider
 * @author Andrew Bowley
 * 8 Jun 2015
 */
public class EnvironmentAxiomProvider implements AxiomProvider
{
    // The country code environment parameter
    protected String countryCode;

    /**
     * EnvironmentAxiomProvider
     */
    public EnvironmentAxiomProvider(String countryCode)
    {
        this.countryCode = countryCode;
    }
    
    @Override
    public String getName() 
    {
        return "environment";
    }

    @Override
    public void setResourceProperties(String axiomName,
            Map<String, Object> properties) 
    {
    }

    @Override
    public AxiomSource getAxiomSource(String axiomName,
            List<String> axiomTermNameList) 
    {
        AxiomSource axiomSource = null;
        Axiom localeAxiom = new Axiom("env", new Parameter("country_code", countryCode));
        axiomSource = new SingleAxiomSource(localeAxiom);
        return axiomSource;
    }

    @Override
    public AxiomListener getAxiomListener() 
    {
        return new AxiomListener()
        {

            @Override
            public void onNextAxiom(Axiom axiom) 
            {
            }
        };
    }

    @Override
    public boolean isEmpty() 
    {
        return false;
    }
}
