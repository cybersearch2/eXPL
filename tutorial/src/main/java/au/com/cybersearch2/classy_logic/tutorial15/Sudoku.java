/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial15;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Sudoku
 * Demonstrates multi stage solution to well-known puzzle
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class Sudoku
{
/* sudoku.xpl
// Table to encode a number in range 1 - 4 as a bit value
// Zero represents "any number"
axiom encode
( bit )
{ 0xF }
{   1 }
{   2 }
{   4 }
{   8 };  

list encode(encode);

// Table to decode bit value back to number
// Any invalid bit value is decoded to zero
axiom decode
( number )
{     0  }
{     1  }
{     2  }
{     0  }
{     3  }
{     0  }
{     0  }
{     0  }
{     4  }
{     0  }
{     0  }
{     0  }
{     0  }
{     0  }
{     0  }
{     0  };

list decode(decode);

// Matrix coordinates to navigate the cell groupings
// There are 3 groupings, each in separate scope for
// access using a context list variable
scope row
{
  axiom path
    (row, col)
    { 0,0 }{ 0,1 }{ 0,2 }{ 0,3 }
    { 1,0 }{ 1,1 }{ 1,2 }{ 1,3 }
    { 2,0 }{ 2,1 }{ 2,2 }{ 2,3 }
    { 3,0 }{ 3,1 }{ 3,2 }{ 3,3 };
 }
 
scope col
{
  axiom path
    (row, col)
    { 0,0 }{ 1,0 }{ 2,0 }{ 3,0 }
    { 0,1 }{ 1,1 }{ 2,1 }{ 3,1 }
    { 0,2 }{ 1,2 }{ 2,2 }{ 3,2 }
    { 0,3 }{ 1,3 }{ 2,3 }{ 3,3 };
}

scope square
{
  axiom path
    (row, col)
    { 0,0 }{ 0,1 }{ 1,0 }{ 1,1 }
    { 0,2 }{ 0,3 }{ 1,2 }{ 1,3 }
    { 2,0 }{ 2,1 }{ 3,0 }{ 3,1 }
    { 2,2 }{ 2,3 }{ 3,2 }{ 3,3 };
}
 

// The puzzle implemented as a 16-integer matrix
calc puzzle 
(
  integer s11, integer s12, integer s13, integer s14,
  integer s21, integer s22, integer s23, integer s24,
  integer s31, integer s32, integer s33, integer s34,
  integer s41, integer s42, integer s43, integer s44,
  matrix += axiom { s11, s12,  s13, s14 },
  matrix += axiom { s21, s22,  s23, s24 },
  matrix += axiom { s31, s32,  s33, s34 },
  matrix += axiom { s41, s42,  s43, s44 }
);

// The puzzle is solved using a 4x4 matrix
export list<axiom> matrix {};

// Encode puzzle initial parameters
// Cells missing numbers are encoded as a bit value containing  candidate numbers
calc encode_puzzle
+ cursor row(matrix);
(
  {
    ? row.fact,
    j = 0,
    {
      item = row[0][j],
      : item == 0
      {
        row[0][j] = encode[item].bit
      },
      ? item == 0
      {
        row[0][j] = 0xF
      },
      ? ++j < 4
    },
    row += 1
  }
);

// Eliminate bit values for matrix groupings
calc eliminate
+ cursor group(path@scope);
(
. i = 0,
  {
    j = 0,
    group = i * 4,
    select = index,
    mask = 0,
    {
      row1 = group[0].row,
      col1 = group[0].col,
      item = decode[matrix[row1][col1]].number,
      : item == 0
      {
        mask |= encode[item].bit
      },
      group += 1,
      ? ++j < 4
    },
    j = 0,
    group = i * 4,
    {
      row2 = group[0].row,
      col2 = group[0].col,
      flags = matrix[row2][col2],
      item = decode[flags].number,
      ? item == 0
      {
          matrix[row2][col2] = flags & ~mask
      },
      group += 1,
      ? ++j < 4
    },
    : ++i == 4
  }
);

// Convert puzzle bit values to numbers
calc decode_puzzle
+ cursor row(matrix);
(
  {
    ? row.fact,
    j = 0,
    {
      row[0][j] = decode[row[0][j]].number,
      : ++j == 4
    },
    row += 1
  }
);

// Print final solution
calc print_puzzle
+ cursor row(matrix);
(
  {
    ? row.fact,
    system.print(row[0][0], ", ", row[0][1], ", ", row[0][2], ", ", row[0][3], ","), 
    row += 1
  }
);

// Solve sudoku
query sudoku(puzzle)
(
  0, 0, 2, 3,
  0, 0, 0, 0,
  0, 0, 0, 0,
  3, 4, 0, 0
) -> (encode_puzzle) 
  -> (row.eliminate)
  -> (col.eliminate) 
  -> (row.eliminate) 
  -> (col.eliminate) 
  -> (square.eliminate) 
  -> (square.eliminate) 
  -> (decode_puzzle) 
  -> (print_puzzle);
 
 */
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Sudoku()
    {
        File resourcePath = new File("src/main/resources/tutorial15");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the sudoku.xpl script and runs the "" queries.<br/>
     * The expected results:<br/>
        4, 1, 2, 3,<br/>
        2, 3, 4, 1,<br/>
        1, 2, 3, 4,<br/>
        3, 4, 1, 2<br/>
     * @return Axiom iterator
     */
    public void  calculateSudoku()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("sudoku.xpl");
        parserContext = queryProgramParser.getContext();
        queryProgram.executeQuery("sudoku");
      /*Iterator<Axiom> iterator = result.axiomIterator("matrix");
        while (iterator.hasNext())
         System.out.println(iterator.next().toString());*/
    }

    FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
        functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
        return functionManager;
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * @param args
     */
    public static void main(String[] args)
    {
        try 
        {
            Sudoku sudoku = new Sudoku();
            sudoku.calculateSudoku();
        } 
        catch (ExpressionException e) 
        { 
            e.printStackTrace();
            System.exit(1);
        }
        catch (QueryExecutionException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
