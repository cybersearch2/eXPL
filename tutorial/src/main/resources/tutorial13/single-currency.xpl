template charge_plus_gst
(
  currency total = amount * 1.1,
  string total_text = "Total + gst: " + total.format
);

template french_charge_plus_gst
(
  currency total = french.item->amount * 1.33,
  string total_text = "le total + gst: " + total.format
);

scope french (language="fr", region="FR")
{
  axiom item( amount ) { currency ("500,00 €") };
  
  template charge_plus_gst
  (
    currency total = amount * 1.33,
    string total_text = "le total + gst: " + total.format
  );
}

scope german (language="de", region="DE")
{
  axiom item( amount ) { currency ("12.345,67 €") };
  query<term> french_item_query(item : french.charge_plus_gst);
}

query<term> item_query(german.item : german.charge_plus_gst);
query<term> french_item_query(french_charge_plus_gst);
