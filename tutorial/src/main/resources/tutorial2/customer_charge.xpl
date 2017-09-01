axiom charge() 
  {"Athens", 23 }
  {"Sparta", 13 }
  {"Milos", 17};
		
axiom customer()
  {"Marathon Marble", "Sparta"}
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
  
template freight(city, charge);
template customer_freight(name, city ? city == freight.city, charge);
		
query customer_charge(charge:freight, customer:customer_freight);
