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
       axiom { subject="English", mark=mark[english] }
             { subject="Math",    mark=mark[maths] }
             { subject="History", mark=mark[history] }
  );
}

calc score
(
  <- school.subjects(english, maths, history) -> (marks_list),
  string report = student + ": " + 
    marks_list[0].subject + ":" + marks_list[0].mark + ", " + 
    marks_list[1].subject + ":" + marks_list[1].mark + ", " + 
    marks_list[2].subject + ":" + marks_list[2].mark 
);

query<axiom> marks(grades : score);
