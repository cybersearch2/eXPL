include "surface-land.xpl";

axiom totals (total_area) { integer(0) };

// Calculator declaration:
calc km2_to_mi2 
+ export list<term> totals(totals);
(
  country, 
  double surface_area_mi2 = surface_area_Km2 *= 0.3861,
. totals->total_area += surface_area_mi2
);

// Calculator performing conversion on it's own:
query<axiom> convert(surface_area : km2_to_mi2);

template country_data(country, double surface_area_Km2);

// Chained query with calculator performing conversion:
query<axiom> surface_area_mi2(surface_area : country_data)
  -> (km2_to_mi2);

// Cascade query with calculator performing conversion:
query<axiom> cascade(surface_area : country_data, km2_to_mi2);
  
// Calculator using parameters for one country:
query<axiom> convert_afghan(km2_to_mi2)
(country = "Afghanistan", surface_area_Km2 = "652,230.00");
 
  
  
