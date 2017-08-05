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
    


// Template to filter high cities
// Solution is a list named 'high_cities'
template<axiom> high_cities
(
  name,
  altitude ? altitude > 5000
);

// Calculator to perform insert sort on high_cities
calc insert_sort 
(
  // i is index to last item appended to the list
  integer i = high_cities.length - 1,
  // Skip first time when only one item in list
  : i < 1,
  // j is the swap index
  integer j = i - 1,
  // Save axiom to swap
  temp = high_cities[i],
  // Shuffle list until sort order restored
  {
    ? altitude < high_cities[j].altitude,
    high_cities[j + 1] = high_cities[j],
    ? --j >= 0
  },
  // Insert saved axiom in correct position
  high_cities[j + 1] = temp
);

query high_cities (city : high_cities) -> (insert_sort); 
