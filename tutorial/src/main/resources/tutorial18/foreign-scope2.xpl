axiom catalog_no (catalog_no) : parameter;

string country = scope->region;
list<currency $ country> item_list =
{
  "12.345,67 €",
  "500,00 €"
};

choice tax_rate
 (country, percent)
     {"DE",18.0}
     {"FR",15.0}
     {"BE",11.0};
     
axiom german.lexicon (Total, tax)
  {"Gesamtkosten","Steuer"};
axiom french.lexicon (Total, tax)
  {"le total","impôt"};
axiom belgium_fr.lexicon (Total, tax)
  {"le total","impôt"};
axiom belgium_nl.lexicon (Total, tax)
  {"totale kosten","belasting"};
  
local translate(lexicon);

calc charge_plus_gst
(
  currency amount = item_list[catalog_no],
  choice tax_rate(country),
  currency total = amount * (1.0 + percent/100)
);

calc format_total
(
  catalog_no,
  country,
  string text = " " + translate->Total + " " + translate->tax + ": " + 
    charge_plus_gst.total.format
);

scope german (language="de", region="DE"){}
scope french (language="fr", region="FR"){}
scope belgium_fr (language="fr", region="BE"){}
scope belgium_nl (language="nl", region="BE"){}

query item_query(catalog_no : german.charge_plus_gst) -> (catalog_no : german.format_total) ->
   (catalog_no : french.charge_plus_gst) -> (catalog_no : french.format_total) ->
   (catalog_no : belgium_fr.charge_plus_gst) -> (catalog_no : belgium_fr.format_total) ->
   (catalog_no : belgium_nl.charge_plus_gst) -> (catalog_no : belgium_nl.format_total);
