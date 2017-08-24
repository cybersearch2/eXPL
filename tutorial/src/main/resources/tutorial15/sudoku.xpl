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
 
// The puzzle is solved using a 4x4 matrix
export list<axiom> matrix {};

// The puzzle implemented as a 16-integer matrix
calc puzzle
+list<axiom> matrix = matrix@; 
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
 