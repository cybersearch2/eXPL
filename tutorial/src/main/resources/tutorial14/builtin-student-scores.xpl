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

calc total_percent
(
  integer english,
  integer math,
  integer history,
  string label = "Total percent",
  double total = (english + math + history) * 100.0 / (3 * 18),
  string text_value = total.format
);

calc score
+ list<axiom> marks = score.subjects;
+ list<term>  summary = score.total_percent;
(
  <- subjects(english, maths, history) -> (marks_list),
  <- total_percent(english, maths, history) -> (label, text_value),
  string report = student + ": " + 
    marks[0].subject + ":" + marks[0].mark + ", " + 
    marks[1].subject + ":" + marks[1].mark + ", " + 
    marks[2].subject + ":" + marks[2].mark, 
. report += ", " + summary->label + ": " + summary->text_value
);

query<axiom> marks(grades : score);

query marks(grades : score);
