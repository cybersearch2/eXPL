include "world_currency.xpl";

template total
(
  currency $ country amount,
  amount *= 1.1
);

template format_total
(
  string total_text = total.country + " Total + gst: " + total.amount.format
);

query<axiom> price_query(price : total) -> (format_total);
