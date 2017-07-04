axiom literals
( 
  Boolean, 
  String, 
  Integer, 
  Double, 
  Decimal, 
  Currency, 
  Timestamp 
)
{    
  true, 
  "penguins",   
  12345, 
  1234e2, 
  decimal(1234.56), 
  currency $ "DE" ("12.345,67 €"),
  system.timestamp()
};

template variables
( 
  Boolean, 
  String, 
  Integer, 
  Double, 
  Decimal, 
  Currency, 
  timestamp = Timestamp
);
query<term> types(literals:variables);   
           
              