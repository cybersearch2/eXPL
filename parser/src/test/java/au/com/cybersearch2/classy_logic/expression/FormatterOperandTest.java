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
package au.com.cybersearch2.classy_logic.expression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * FormatterOperandTest
 * @author Andrew Bowley
 * 14 Mar 2015
 */
public class FormatterOperandTest
{
	@Test
	public void testFormatWorldNumber() throws IOException
	{
    	File worldNumbreList = new File("src/test/resources", "world_number.lst");
     	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(worldNumbreList), "UTF-8"));
	    for (Locale locale : Locale.getAvailableLocales()) 
	    {  
			if (!locale.getCountry().isEmpty()/* && 
					 locale.getScript().isEmpty()*/)
			{
		    	DoubleOperand targetOperand = new TestDoubleOperand("Number", Double.parseDouble("1234567.89"));
	 	        FormatterOperand formatOperand = new FormatterOperand(QualifiedName.parseName(locale.getCountry()), targetOperand, locale);
	 	        assertThat(formatOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
	 	        //System.out.println(formatOperand.getValue().toString() + " " + locale.toString());
	 	        assertThat(formatOperand.getValue().toString() + " " + locale.toString()).isEqualTo(reader.readLine());
			}
	    }
	    reader.close();
	}

	@Test
	public void testFormatWorldDate() throws IOException
	{
    	//File worldNumbreList = new File("src/test/resources", "world_number.lst");
     	//LineNumberReader reader = new LineNumberReader(new FileReader(worldNumbreList));
	    for (Locale locale : Locale.getAvailableLocales()) 
	    {  
			if (!locale.getCountry().isEmpty() /*&& 
					 locale.getScript().isEmpty()*/)
			{
				Calendar date = GregorianCalendar.getInstance();
				date.setTime(new Date());
		    	Variable targetOperand = new TestVariable("Date");
		    	targetOperand.assign(new Parameter(Term.ANONYMOUS, date));
	 	        FormatterOperand formatOperand = new FormatterOperand(QualifiedName.parseName(locale.getCountry()), targetOperand, locale);
	 	        assertThat(formatOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
	 	        //System.out.println(formatOperand.getValue().toString() + " " + locale.toString());
	 	        //assertThat(formatOperand.getValue().toString() + " " + locale.toString()).isEqualTo(reader.readLine());
			}
	    }
	    //reader.close();
	}
}
