choice bracket
  (amount,       threshold,  base,    percent, id)
  {amount <  12000,      0,     0.00, 1.00}
  {amount <  30000,  12000,   120.00, 2.00}
  {amount <  50000,  30000,   480.00, 3.00}
  {amount < 100000,  50000,  1080.00, 3.50}
  {amount < 200000, 100000,  2830.00, 4.00}
  {amount < 250000, 200000,  6830.00, 4.25}
  {amount < 300000, 250000,  8955.00, 4.75}
  {amount < 500000, 300000, 11330.00, 5.00}
  {amount > 500000, 500000, 21330.00, 5.50};

axiom transaction_amount (id, amount) : parameter;

calc payable(id, duty = base + (amount - threshold) * (percent / 100));

query<term> stamp_duty(transaction_amount : bracket) -> (payable);
