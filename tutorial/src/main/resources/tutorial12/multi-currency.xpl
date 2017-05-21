include "world_currency.xpl";

calc total
(
  currency $ country amount,
  amount *= 1.1
);

calc format_total
(
  string total_text = total.country + " Total + gst: " + format(total.amount)
);

query<axiom> price_query(price : total) >> (format_total);
