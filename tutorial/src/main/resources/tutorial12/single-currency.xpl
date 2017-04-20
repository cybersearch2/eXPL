axiom item() {"$1234.56"};

template charge(currency $ "AU" amount);

calc charge_plus_gst
(
  currency $ "AU" total = amount * 1.1,
  string total_text = "Total + gst: " + format(total)
);

query<term> item_query(item : charge) >> (charge_plus_gst);
