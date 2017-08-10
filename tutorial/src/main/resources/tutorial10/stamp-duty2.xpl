choice bracket
    (    amount,     threshold, base, percent)
    {amount > 500000, 500000, 21330.00, 5.50}
    {amount > 300000, 300000, 11330.00, 5.00}
    {amount > 250000, 250000,  8955.00, 4.75}
    {amount > 200000, 200000,  6830.00, 4.25}
    {amount > 100000, 100000,  2830.00, 4.00}
    {amount >  50000,  50000,  1080.00, 3.50}
    {amount >  30000,  30000,   480.00, 3.00}
    {amount >  12000,  12000,   120.00, 2.00}
    {amount >   5000,      0,     0.00, 1.00};
 
axiom transaction_amount 
  ( id,     amount     )
  { 100077,    3789.00 }
  { 100078,  123458.00 }
  { 100079,   55876.33 }
  { 100080, 1245890.00 };
  
calc stamp_duty_payable(
. currency $ "US" amount,
. percent = 0.0,
. currency $ "US" duty = 20.00,
. choice bracket(),
  ? percent > 0.0
  {
    duty = base + (amount - threshold) 
           * (percent / 100)
  },
  id,
  Amount = amount.format,
  string Duty = duty.format,
  Bracket = bracket
);

query<axiom> stamp_duty_query (transaction_amount : stamp_duty_payable);
