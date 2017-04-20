axiom item (amount) : parameter;

axiom lexicon (Total);
axiom german.lexicon 
  ( Total)
  {"Gesamtkosten"};
  
template charge(currency amount);

calc charge_plus_gst(currency total = charge.amount * 1.1);

calc format_total(string total_text = translate[Total] + " + gst: " + format(charge_plus_gst.total));

local translate(lexicon);

scope german (language="de", region="DE")
{
  query<term> item_query(item : charge) >> (charge_plus_gst) >> (format_total);
}
