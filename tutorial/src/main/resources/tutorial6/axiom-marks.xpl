axiom grades (student, english, maths, history)
  {"George", 15, 13, 16}
  {"Sarah", 12, 17, 15}
  {"Amy", 14, 16, 6};
  
axiom alpha_marks()
{
  "", // Start at index 1
  "f-", "f", "f+",
  "e-", "e", "e+",
  "d-", "d", "d+",
  "c-", "c", "c+",
  "b-", "b", "b+",
  "a-", "a", "a+"
};
list<term> mark(alpha_marks);

template score(student, English = mark[english], Maths = mark[maths], History = mark[history]);

query<axiom> marks(grades : score);
