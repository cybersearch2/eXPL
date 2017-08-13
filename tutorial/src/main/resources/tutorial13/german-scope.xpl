axiom item (amount) : parameter;

axiom german.lexicon 
  ( Total)
  {"Gesamtkosten"};
  
calc total
(
  currency amount,
  amount *= 1.1
);

calc format_total
+ list<term> lexicon@scope;
(
  string total_text = 
    lexicon->Total + 
    " + gst: " + 
    total.amount.format
);

scope german (language="de", region="DE")
{
  query<term> item_query(item : total) -> (format_total);
}
