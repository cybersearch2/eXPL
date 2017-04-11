axiom spin (r1, r2, r3, r4) {3,2,0,1};
axiom fruit() {"apple", "orange", "banana", "lemon"};
list<term> combo(fruit);
template spin(combo[(r1)], combo[(r2)], combo[(r3)], combo[(r4)]);
query spin(spin : spin);
