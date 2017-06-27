axiom item (amount) : parameter;

axiom german.lexicon 
  ( Total)
  {"Gesamtkosten"};
  
calc total
(
  currency amount,
  amount *= 1.1
);

calc format_total(string total_text = translate->Total + " + gst: " + format(total.amount));

local translate(lexicon);

scope german (language="de", region="DE")
{
  query<term> item_query(item : total) >> (format_total);
}
