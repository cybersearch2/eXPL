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

list<axiom> translate {};
  
calc charge_plus_gst
+ list<term> lexicon@scope; 
(
  currency amount = item_list[catalog_no],
  choice tax_rate(country),
  currency total = amount * (1.0 + percent/100),
  translate += axiom { Total=lexicon->Total, tax="(" + percent + "% " + lexicon->tax + ")", country=scope->language + "_" + scope->region }
);

calc format_text
+ cursor lexicon(translate);
(
  string country,
  string total,
  {
    ? lexicon.fact,
    : lexicon[0].country == country,
    lexicon += 1
  },
  string text = country + " " + lexicon[0].tax + " " + lexicon[0].Total
);

calc format_total
(
  catalog_no,
  <- format_text("de_DE") -> (text),
  string german_total = text + " " + german.charge_plus_gst.total.format,
  <- format_text("fr_FR") -> (text),
  string french_total = text + " " + french.charge_plus_gst.total.format,
  <- format_text("fr_BE") -> (text),
  string belgium_fr_total = text + " " + belgium_fr.charge_plus_gst.total.format,
  <- format_text("nl_BE") -> (text),
  string belgium_nl_total = text + " " + belgium_nl.charge_plus_gst.total.format
);

scope german (language="de", region="DE"){}
scope french (language="fr", region="FR"){}
scope belgium_fr (language="fr", region="BE"){}
scope belgium_nl (language="nl", region="BE"){}

query<term> item_query
  (catalog_no : german.charge_plus_gst) ->
  (catalog_no : french.charge_plus_gst) -> 
  (catalog_no : belgium_fr.charge_plus_gst) ->
  (catalog_no : belgium_nl.charge_plus_gst) -> 
  (catalog_no : format_total);
