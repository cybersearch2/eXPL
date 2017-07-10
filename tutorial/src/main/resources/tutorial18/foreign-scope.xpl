axiom item (amount) : parameter;

choice tax_rate
 (country, percent)
     {"DE",18.0}
     {"FR",15.0}
     {"BE",11.0};
     
axiom lexicon (Total, tax);
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
  currency amount,
  <- tax_rate(country = scope->region) -> (percent /= 100),
  currency total = amount * (1.0 + percent)
);

calc format_total
(
  string country = scope->region,
  string text = " " + translate->Total + " " + translate->tax + ": " + 
    charge_plus_gst.total.format
);

scope german (language="de", region="DE"){}
scope french (language="fr", region="FR"){}
scope belgium_fr (language="fr", region="BE"){}
scope belgium_nl (language="nl", region="BE"){}

query item_query(item : german.charge_plus_gst) -> (german.format_total) ->
   (item : french.charge_plus_gst) -> (french.format_total) ->
   (item : belgium_fr.charge_plus_gst) -> (belgium_fr.format_total) ->
   (item : belgium_nl.charge_plus_gst) -> (belgium_nl.format_total);
