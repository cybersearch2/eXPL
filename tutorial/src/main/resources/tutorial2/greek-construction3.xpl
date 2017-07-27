axiom customer()
  {"Marathon Marble", "Sparta"}
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
		
axiom fee (name, fee)
  {"Marathon Marble", 61}
  {"Acropolis Construction", 47}
  {"Agora Imports", 49}
  {"Spiros Theodolites", 57}; 
		
axiom freight (city, freight) 
  {"Athens", 5 }
  {"Sparta", 16 }
  {"Milos", 22};
		
template customer(name, city);
template account(name ? name == customer.name, city = customer.city, fee);
template delivery(name = account.name, city ? city == account.city, freight);
		
query<axiom> greek_business(customer:customer) 
  -> (fee:account) -> (freight:delivery);
