axiom grades (student, english, maths, history)
  {"George", 15, 13, 16}
  {"Sarah", 12, 17, 15}
  {"Amy", 14, 16, 6};

calc score
(
. marks_list = school.subjects(english, maths, history),
  string report = student + ": " + 
    marks_list[0].subject + ":" + marks_list[0].mark + ", " + 
    marks_list[1].subject + ":" + marks_list[1].mark + ", " + 
    marks_list[2].subject + ":" + marks_list[2].mark 
);

query<axiom> marks(grades : score);