include "surface-land.xpl";
template surface_area(country, double surface_area_Km2);
// Calculator declaration:
calc km2_to_mi2 (country, double surface_area_mi2 = surface_area.surface_area_Km2 *= 0.3861);
// Result list receives calculator solution
list surface_area(km2_to_mi2);
// Chained query with calculator performing conversion:
query surface_area_mi2(surface_area : surface_area)
  >> (km2_to_mi2);
