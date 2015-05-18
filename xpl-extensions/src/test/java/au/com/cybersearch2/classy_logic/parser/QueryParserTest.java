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
package au.com.cybersearch2.classy_logic.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryExecuter;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classyinject.DI;

/**
 * QueryParserTest
 * @author Andrew Bowley
 * 6 Dec 2014
 */
public class QueryParserTest 
{

	static final String HIGH_CITIES_JPA_XPL =
			"axiom city (name, altitude): resource \"cities\";\n" +
			"template high_city(string name, altitude ? altitude > 5000);\n"
			;

    @Before
    public void setup() throws Exception
    {
        new DI(new QueryParserModule()).validate();
    }

	@Test
	public void test_high_cities_jpa() throws Exception
	{
		InputStream stream = new ByteArrayInputStream(HIGH_CITIES_JPA_XPL.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
	    ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
	    Template highCities = parserAssembler.getTemplate("high_city");
	    highCities.setKey("city");
        QuerySpec querySpec = new QuerySpec("TEST");
		KeyName keyName = new KeyName("city", "high_city");
		querySpec.addKeyName(keyName);
	    QueryExecuter highCitiesQuery = new QueryExecuter(new QueryParams(queryProgram.getGlobalScope(), querySpec));
	    int count = 0;
 	    if (highCitiesQuery.execute())
 	    {
  	    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name = addis ababa, altitude = 8000)");
  	    	++count;
 	    }
 	    if (highCitiesQuery.execute())
 	    {
  	    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name = denver, altitude = 5280)");
  	    	++count;
 	    }
 	    if (highCitiesQuery.execute())
 	    {
  	    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name = flagstaff, altitude = 6970)");
  	    	++count;
 	    }
 	    if (highCitiesQuery.execute())
 	    {
  	    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name = leadville, altitude = 10200)");
  	    	++count;
 	    }
 	    assertThat(count).isEqualTo(4);
	}
	
	public static ParserAssembler openScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		queryParser.enable_tracing();
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
        return queryProgram.getGlobalScope().getParserAssembler();
	}
	
}
