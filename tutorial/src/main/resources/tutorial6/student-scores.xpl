axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 list<string> mark;
 mark[1]  = "f-";
 mark[2]  = "f";
 mark[3]  = "f+";
 mark[4]  = "e-";
 mark[5]  = "e";
 mark[6]  = "e+";
 mark[7]  = "d-";
 mark[8]  = "d";
 mark[9]  = "d+";
 mark[10] = "c-";
 mark[11] = "c";
 mark[12] = "c+";
 mark[13] = "b-";
 mark[14] = "b";
 mark[15] = "b+";
 mark[16] = "a-";
 mark[17] = "a";
 mark[18] = "a+";
 template score(student, mark[english], mark[maths], mark[history]);
 query<axiom> marks(grades : score);
