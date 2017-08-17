template charge_plus_gst
(
  currency total = amount * 1.1,
  string total_text = "Total + gst: " + total.format
);

scope french (language="fr", region="FR")
{
  template charge_plus_gst
  (
    currency total = amount * 1.33,
    string total_text = "le total + gst: " + total.format
  );
}

scope german (language="de", region="DE")
{
  axiom item( amount ) { currency ("12.345,67 €") };
  query<term> french_item_query(german.item : french.charge_plus_gst);
}

query<term> item_query(german.item : german.charge_plus_gst);
