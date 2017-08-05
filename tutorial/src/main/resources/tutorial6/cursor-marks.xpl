axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 
list<string> mark(1,18);
 mark[18] = "a+";
 mark[17] = "a";
 mark[16] = "a-";
 mark[15] = "b+";
 mark[14] = "b";
 mark[13] = "b-";
 mark[12] = "c+";
 mark[11] = "c";
 mark[10] = "c-";
 mark[9]  = "d+";
 mark[8]  = "d";
 mark[7]  = "d-";
 mark[6]  = "e+";
 mark[5]  = "e";
 mark[4]  = "e-";
 mark[3]  = "f+";
 mark[2]  = "f";
 mark[1]  = "f-";
 
list<integer> student_grades;

template<term> grades
(
  student, 
  student_grades[0] = english, 
  student_grades[1] = maths, 
  student_grades[2] = history
);

template score
+ export list<string> student_marks;
+ cursor grade(student_grades);
(
  student_marks += student + 
    ": English = " + mark[grade++] + 
    ", Maths ="    + mark[grade++] + 
    ", History = " + mark[grade]
);
 
query marks(grades : grades) -> (score);
