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

scope school
{
   calc subjects
   + list<axiom> marks_list {};
  (
    integer english,
    integer maths,
    integer history,
    marks_list =
       axiom { "English", mark[english] }
             { "Math",    mark[maths] }
             { "History", mark[history] }
  );
}

calc score
(
  <- school.subjects(english, maths, history) -> (marks_list),
  string report = student + ": " + 
    marks_list[0][0] + ":" + marks_list[0][1] + ", " + 
    marks_list[1][0] + ":" + marks_list[1][1] + ", " + 
    marks_list[2][0] + ":" + marks_list[2][1] 
);

query<axiom> marks(grades : score);
