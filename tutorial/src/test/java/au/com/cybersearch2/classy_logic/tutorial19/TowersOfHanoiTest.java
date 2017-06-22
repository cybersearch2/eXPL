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
package au.com.cybersearch2.classy_logic.tutorial19;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * TowersOfHanoiTest
 * @author Andrew Bowley
 * 22Jun.,2017
 */
public class TowersOfHanoiTest
{
    static class SystemFunctionProvider implements FunctionProvider<Void>
    {
        private BufferedReader reader;
        
        public SystemFunctionProvider() throws IOException
        {
            File testFile = new File("src/main/resources/tutorial19", "towers-of-hanoi.txt");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
         }
        
        public void close() throws IOException
        {
            if (reader != null)
                reader.close();
        }
        
        @Override
        public String getName()
        {
            return "system";
        }

        @Override
        public CallEvaluator<Void> getCallEvaluator(String identifier)
        {
            if (identifier.equals("print"))
                return new CallEvaluator<Void>(){
    
                    @Override
                    public String getName()
                    {
                        return "print";
                    }
    
                    @Override
                    public Void evaluate(List<Term> argumentList)
                    {
                        StringBuilder builder = new StringBuilder();
                        for (Term term: argumentList)
                            builder.append(term.getValue().toString());
                        try
                        {
                            String line = reader.readLine();
                            assertThat(builder.toString()).isEqualTo(line);
                        }
                        catch (IOException e)
                        {
                            fail(e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    public void setExecutionContext(ExecutionContext context)
                    {
                    }
                    
            };
            throw new ExpressionException("Unknown function identifier: " + identifier);
        }
    }
    
    @Test
    public void testTowersOfHanoi() throws Exception
    {
        File resourcePath = new File("src/main/resources/tutorial19");
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = null;
        try
        {
            systemFunctionProvider = new SystemFunctionProvider();
            functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
            QueryProgramParser queryProgramParser = new QueryProgramParser(resourcePath, functionManager);
            QueryProgram queryProgram = queryProgramParser.loadScript("towers-of-hanoi.xpl");
            //System.out.println(" n=1");
            queryProgram.executeQuery("towers_of_hanoi1");
            //System.out.println("\n n=2");
            queryProgram.executeQuery("towers_of_hanoi2");
            //System.out.println("\n n=3");
            queryProgram.executeQuery("towers_of_hanoi3");
        }
        finally
        {
            if (systemFunctionProvider != null)
                systemFunctionProvider.close();
        }
    }
}
