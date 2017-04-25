/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;

/**
 * QueryProgramParser
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class QueryProgramParser
{
    protected ProviderManager providerManager;
    protected FunctionManager functionManager;
    protected File resourcePath;
    protected ParserContext context;

    public QueryProgramParser(ResourceAxiomProvider resourceAxiomProvider)
    {
        this.resourcePath = resourceAxiomProvider.getResourcePath();
        providerManager = new ProviderManager(resourcePath);
        providerManager.putAxiomProvider(resourceAxiomProvider);
    }

    public QueryProgramParser(File resourcePath)
    {
        this.resourcePath = resourcePath;
    }
    
    public QueryProgramParser(File resourcePath, AxiomProvider... axiomProvider)
    {
        this.resourcePath = resourcePath;
        providerManager = new ProviderManager(resourcePath);
        for (AxiomProvider provider: axiomProvider)
            providerManager.putAxiomProvider(provider);
    }
    
    public QueryProgramParser(File resourcePath, FunctionManager functionManager)
    {
        this.resourcePath = resourcePath;
        providerManager = new ProviderManager(resourcePath);
        this.functionManager = functionManager;
    }
    
    public QueryProgram loadScript(String programFile)
    {
        File filePath = new File(resourcePath, programFile);
        QueryProgram queryProgram = null;
        InputStream stream = null;
        try
        {
            stream = new FileInputStream(filePath);
            QueryParser queryParser = new QueryParser(stream);
            if (functionManager != null)
                queryProgram = new QueryProgram(providerManager, functionManager);
            else
                queryProgram = new QueryProgram(providerManager);
            queryProgram.setResourceBase(resourcePath);
            queryProgram.setResourceBase(resourcePath);
            context = new ParserContext(queryProgram, filePath.toString());
            queryParser.input(context);
            return queryProgram;
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
    }

    /**
     * @return the context
     */
    public ParserContext getContext()
    {
        return context;
    }
}
