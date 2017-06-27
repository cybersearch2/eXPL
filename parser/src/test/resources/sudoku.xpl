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

// The puzzle implemented as a 16-integer matrix
calc<term> puzzle
(
  integer s11, integer s12, integer s13, integer s14,
  integer s21, integer s22, integer s23, integer s24,
  integer s31, integer s32, integer s33, integer s34,
  integer s41, integer s42, integer s43, integer s44
);

// Encode puzzle initial parameters
// Cells missing numbers are encoded as a bit value containing  candidate numbers
calc encode_puzzle
(
. i = 0,
  {
    j = 0,
    mask = 0xF,
    {
      item = puzzle[i + j],
      : item == 0
      {
        mask ^= encode[item].bit
      },
      ? ++j < 4
    },
    j = 0,
    {
      item = puzzle[i + j],
      : item == 0
      {
        puzzle[i+j] = encode[item].bit
      },
      ? item == 0
      {
        puzzle[i+j] = mask
      },
      ? ++j < 4
    },
    : (i += 4) == 16
  }
);

// Eliminate bit values for row groupings
calc row
(
. i = 0,
  {
    j = 0,
    mask = 0,
    {
      item = decode[puzzle[i + j]].number,
      : item == 0
      {
        mask |= encode[item].bit
      },
      ? ++j < 4
    },
    j = 0,
    {
      flags = puzzle[i+j],
      item = decode[flags].number,
      ? item == 0
      {
         puzzle[i+j] = flags & ~mask
      },
       ? ++j < 4
    },
    : (i += 4) == 16
  }
);

// Eliminate bit values for column groupings
calc column
(
. i = 0,
  {
    j = 0,
    mask = 0,
    {
      item = decode[puzzle[i + j]].number,
      : item == 0
      {
        mask |= encode[item].bit
      },
      ? (j += 4) < 16
    },
    j = 0,
    {
      flags = puzzle[i+j],
      item = decode[flags].number,
      ? item == 0
      {
        puzzle[i+j] = flags & ~mask
      },
      ? (j += 4) < 16
    },
    : ++i == 4
  }
);

// Eliminate bit values for square groupings
calc square
(
. i = 0,
  {
    mask = 0,
    j = 0, k = 0,
    {
      item = decode[puzzle[i + j + k]].number,
      : item == 0
      {
        mask |= encode[item].bit
      },
      ? ++j == 2 { j = 0, k += 4 },
      ? k < 8
    },
    j = 0, k = 0,
    {
      flags = puzzle[i+j+k],
      item = decode[flags].number,
      ? item == 0
      {
        puzzle[i+j] = flags & ~mask
      },
      ? ++j == 2 { j = 0, k += 4 },
      ? k < 8
    },
    i += 2,
    ? i == 4 { i = 8 },
    : i == 12
  }
);

// Convert puzzle bit values to numbers
calc decode_puzzle
(
. integer i = 0,
  {
    puzzle[i] = decode[puzzle[i]].number,
    : ++i == 16
  }
);

// Print final solution
calc print_puzzle
(
. integer i = 0,
  {
    system.print(puzzle[i], ", ", puzzle[i+1], ", ", puzzle[i+2], ", ", puzzle[i+3], ","), 
    : (i += 4) == 16
  }
);

// Solve suduko
query sudoku(puzzle)
(
  0, 0, 2, 3,
  0, 0, 0, 0,
  0, 0, 0, 0,
  3, 4, 0, 0
) >> (encode_puzzle) >> (column) >> (row) >> (column) >> (square) >> (square) >> (decode_puzzle) >> (print_puzzle);
 