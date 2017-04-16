calc factorial 
(
  integer n,
. integer i = 1,
  decimal factorial = 1,
  {
    factorial *= i,
    ? i++ < n
  }
);
query<term> factorial4 (factorial)(n = 4); 
query<term> factorial5 (factorial)(n = 5); 
