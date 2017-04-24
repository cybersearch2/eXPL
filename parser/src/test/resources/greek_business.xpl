axiom charge() 
  { "Athens", 23}
  { "Sparta", 13}
  { "Milos", 17};
  
axiom customer()
  {"Marathon Marble", "Sparta"}  
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
  
template customer(name, city);
template charge(city ? city == customer.city, fee);  
  