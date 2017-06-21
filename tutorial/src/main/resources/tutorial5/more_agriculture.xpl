include "agriculture-land.xpl";
include "surface-land.xpl";

template agri_10y (double agri_change = Y2010 - Y1990, country ? agri_change > 1.0);
calc surface_area_increase (
  country? country == agri_10y.country,
  double surface_area = (agri_10y.agri_change)/100.0 * surface_area_Km2);
query<axiom> more_agriculture(Data : agri_10y, surface_area : surface_area_increase); 