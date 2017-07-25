list<axiom> spin  
  { c1=3^r1, c2=2^r1, c3=0^r1, c4=1^r1 }
  { c1=0^r2, c2=1^r2, c3=2^r2, c4=3^r2 }
  { c1=2^r3, c2=1^r3, c3=3^r3, c4=0^r3 }
  (
    integer r1 = system.random(4),
    integer r2 = system.random(4),
    integer r3 = system.random(4)
  );

axiom fruit() {"apple ", "orange", "banana", "lemon "};

template play
+ list<term> combo(fruit);
(combo[c1], combo[c2], combo[c3], combo[c4]);

query<axiom> gamble(spin : play);
