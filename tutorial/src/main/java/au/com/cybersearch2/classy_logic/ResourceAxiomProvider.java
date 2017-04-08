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
package au.com.cybersearch2.classy_logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;

/**
 * ResourceAxiomProvider
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class ResourceAxiomProvider implements AxiomProvider
{
    protected String resourceName;
    protected String fileName;
    protected int id;
    protected File resourcePath;

    
    public ResourceAxiomProvider(String resourceName, String fileName, int id)
    {
        this.resourceName = resourceName;
        this.fileName = fileName;
        this.id = id;
        resourcePath = new File("src/main/resources/tutorial" + id);
    }
 
    public File getResourcePath()
    {
        return resourcePath;
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#getName()
     */
    @Override
    public String getName()
    {
        return resourceName;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#open(java.util.Map)
     */
    @Override
    public void open(Map<String, Object> properties) throws ExpressionException
    {
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#close()
     */
    @Override
    public void close()
    {
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#getAxiomSource(java.lang.String, java.util.List)
     */
    @Override
    public AxiomSource getAxiomSource(String axiomName,
            List<String> axiomTermNameList)
    {
        File filePath = new File(resourcePath, fileName);
        QueryProgram queryProgram = null;
        InputStream stream = null;
        try
        {
            stream = new FileInputStream(filePath);
            QueryParser queryParser = new QueryParser(stream);
            queryProgram = new QueryProgram();
            queryProgram.setResourceBase(resourcePath);
            ParserContext context = new ParserContext(queryProgram);
            queryParser.input(context);
        }
        catch (IOException e)
        {
            throw new ExpressionException(filePath.toString(), e);
        }
        catch (ParseException e)
        {
            throw new ExpressionException(filePath.toString(), e);
        }
        finally
        {
            if (stream != null)
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        QualifiedName qname = QualifiedName.parseGlobalName(axiomName);
        return queryProgram.getGlobalScope().getParserAssembler().getAxiomSource(qname);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#getAxiomListener(java.lang.String)
     */
    @Override
    public AxiomListener getAxiomListener(String axiomName)
    {
        return null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return false;
    }

}
