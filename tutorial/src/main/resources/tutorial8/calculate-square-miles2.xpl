include "surface-land.xpl";

calc filter_area 
(
  country { "United States" , "Australia" },
. double surface_area_Km2,
  string surface_area,
  string units = "km2",
  // Use imperial measurements if country is USA
  ? country == "United States"
  {
    surface_area_Km2 *= 0.3861,
    units = "mi2"
  },
. surface_area = surface_area_Km2.format
);

query<axiom> surface_area_query(surface_area : filter_area);
