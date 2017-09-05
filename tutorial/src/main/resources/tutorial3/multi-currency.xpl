include "world_currency.xpl";

template total
(
  currency $ country amount *= 1.1,
  country_code = country
);

template format_total
(
  string total_text = country_code + " Total + gst: " + amount.format
);

query<axiom> price_query(price : total) -> (format_total);
