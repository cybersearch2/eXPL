axiom grades (student, english, maths, history)
  {"George", 15, 13, 16}
  {"Sarah", 12, 17, 15}
  {"Amy", 14, 16, 6};

calc score
(
  <- school.subjects(english, maths, history) -> (marks_list),
  string report = student + ": " + 
    marks_list[0][0] + ":" + marks_list[0][1] + ", " + 
    marks_list[1][0] + ":" + marks_list[1][1] + ", " + 
    marks_list[2][0] + ":" + marks_list[2][1] 
);

query<axiom> marks(grades : score);
