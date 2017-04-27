include "surface-land.xpl";

scope global (location = "United States"){}

scope australia (location = "Australia"){}

calc country_area 
(
  country { "United States" , "Australia" },
  double surface_area_Km2,
  string units = "km2",
  ? scope^location == "United States"
  {
    surface_area_Km2 *= 0.3861,
    units = "mi2"
  }
);

query<axiom> au_surface_area_query(surface_area : australia.country_area);
query<axiom> surface_area_query(surface_area : country_area);
