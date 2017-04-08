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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;

/**
 * ParserResources
 * Provides access to external scripts via streaming. 
 * @author Andrew Bowley
 * 9 Dec 2014
 */
public class ParserResources 
{
    /** Context for parse operation */
	protected ParserContext context;
    protected File resourceBase;

    /**
     * Construct ParserResources object
     * @param queryProgram Main compiler object
     */
	public ParserResources(ParserContext context) 
	{
		this(context, context.getQueryProgram().getResourceBase());
	}

    /**
     * Construct ParserResources object
     * @param queryProgram Main compiler object
     */
	public ParserResources(ParserContext context, File resourceBase) 
	{
		this.context = context;
		this.resourceBase = resourceBase;
	}

    /**
     * Include script from an external streaming source	
     * @param resourceName
     * @throws IOException
     * @throws ParseException
     */
	public void includeResource(String resourceName) throws IOException, ParseException
	{	
		InputStream instream = openResource(resourceName);
		QueryParser parser = new QueryParser(instream);
		context.pushSourceDocument(resourceName);
		try
		{
			parser.input(context);
		}
		finally
		{
			close(instream, resourceName);
			context.popSourceDocument();
		}
	}

    /**
     * Closes input stream quietly
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
    
    private InputStream openResource(String resourceName) throws IOException 
    {
        return new FileInputStream(new File(resourceBase, resourceName));
    }

}
