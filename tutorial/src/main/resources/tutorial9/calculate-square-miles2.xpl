include "surface-land.xpl";
calc filter_area 
(
  country { "United States" , "Australia" },
  double surface_area = surface_area_Km2,
  string units = "km2",
  // Use imperial measurements if country is USA
  ? country == "United States"
  {
    surface_area *= 0.3861,
    units = "mi2"
  }
);
list surface_area_by_country(filter_area);
query surface_area_query(surface_area : filter_area);
