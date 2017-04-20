include "world_currency.xpl";

calc charge_plus_gst
(
  country,
  currency $ country charge = amount,
  currency $ country total = charge * 1.1,
  string total_text = country + " Total + gst: " + format(total)
);

query<axiom> price_query(price : charge_plus_gst);
