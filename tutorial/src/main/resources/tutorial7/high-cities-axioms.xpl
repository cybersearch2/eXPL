axiom city (name, altitude) 
    {"bilene", 1718}
    {"addis ababa", 8000}
    {"denver", 5280}
    {"flagstaff", 6970}
    {"jacksonville", 8}
    {"leadville", 10200}
    {"madrid", 1305}
    {"richmond",19}
    {"spokane", 1909}
    {"wichita", 1305};
axiom high_cities = {};
// Template for name and altitude of a high city
template high_city(
  altitude ? altitude > 5000,
  high_cities += axiom high_city { name , altitude }
);
query high_cities (city : high_city);
