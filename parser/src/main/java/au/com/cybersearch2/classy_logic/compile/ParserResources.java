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
package au.com.cybersearch2.classy_logic.compile;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import au.com.cybersearch2.classyapp.ResourceEnvironment;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;

/**
 * ParserResources
 * @author Andrew Bowley
 * 9 Dec 2014
 */
public class ParserResources 
{
	protected QueryProgram queryProgram;
    @Inject ResourceEnvironment resourceEnvironment;
    
	public ParserResources(QueryProgram queryProgram) 
	{
		this.queryProgram = queryProgram;
		DI.inject(this);
	}

	
	public void includeResource(String resourceName) throws IOException, ParseException
	{	
		InputStream instream = resourceEnvironment.openResource(resourceName);
		QueryParser parser = new QueryParser(instream);
		try
		{
			parser.input(queryProgram);
		}
		finally
		{
			close(instream, resourceName);
		}
	}

    /**
     * Cloes input stream quietly
     * @param instream InputStream
     * @param filename Name of file being closed
     */
    private void close(InputStream instream, String resourceName) 
    {
        if (instream != null)
            try
            {
                instream.close();
            }
            catch (IOException e)
            {
                //log.warn(TAG, "Error closing resource " + resourceName, e);
            }
    }
}
