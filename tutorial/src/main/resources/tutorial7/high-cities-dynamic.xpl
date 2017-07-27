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
    
// Template to export a high city list
template high_city
+ export list<axiom> high_cities {};
(
  high_cities += 
    axiom high_city { name , altitude } 
      ? altitude > 5000
);

query cities(city : high_city);
