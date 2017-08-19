axiom grades 
  (student, english, math, history)
  {"Amy",    14, 16, 6}
  {"George", 15, 13, 16}
  {"Sarah",  12, 17, 14};
  
template score(student, integer total = math.add(english, math, history));

query<axiom> marks(grades : score);
