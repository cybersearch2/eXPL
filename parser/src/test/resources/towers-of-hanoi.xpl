list<axiom> moves {};

calc tower_of_hanoi
(
  n, from_rod, to_rod, aux_rod,
  ? n == 1
  {
    moves += axiom { string move = "Move disk 1 from rod " + from_rod + " to rod " + to_rod }
    //system.print("Move disk 1 from rod ", from_rod, " to rod ", to_rod)
  },
  : n == 1,
  <- tower_of_hanoi(disk=n - 1, from=from_rod, to=aux_rod, aux=to_rod),
  moves += axiom { string move = "Move disk " + n + " from rod " + from_rod + " to rod " + to_rod },
  //system.print("Move disk ", n, " from rod ", from_rod, " to rod ", to_rod),
  <- tower_of_hanoi(disk=n - 1, from=aux_rod, to=to_rod, aux=from_rod)
);

query towers_of_hanoi(tower_of_hanoi)(n=3, from_rod="A", to_rod="C", aux_rod="B");
