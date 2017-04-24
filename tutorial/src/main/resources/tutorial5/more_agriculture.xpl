include "agriculture-land.xpl";
include "surface-land.xpl";

template agri_10y (double Y1990, double Y2010, country ? Y2010 - Y1990 > 1.0);
template surface_area_increase (
  country? country == agri_10y.country,
  double surface_area = (agri_10y.Y2010 - agri_10y.Y1990)/100
    * surface_area_Km2);
query<axiom> more_agriculture(Data : agri_10y, surface_area : surface_area_increase); 