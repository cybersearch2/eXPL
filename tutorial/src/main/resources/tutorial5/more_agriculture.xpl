include "agriculture-land.xpl";
include "surface-land.xpl";

template agri_20y (double agri_change = Y2010 - Y1990, country ? agri_change > 1.0);
template surface_area_increase (
  country? country == agri_20y.country,
  double surface_area = (agri_20y.agri_change)/100.0 * surface_area_Km2);
query<axiom> more_agriculture(Data : agri_20y, surface_area : surface_area_increase); 