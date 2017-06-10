axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 list<string> mark =
 {
   "", // Index = 0 is out of range
   "f-", "f", "f+", "e-", "e", "e+", "d-", "d", "d+", 
   "c-", "c", "c+", "b-", "b", "b+", "a-", "a", "a+"
 };
 template score(student, mark[english], mark[maths], mark[history]);
 query<axiom> marks(grades : score);
