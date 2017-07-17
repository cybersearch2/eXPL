list<string> euro_amounts = 
{
  "14.567,89",
  "14 197,52",
  "590,00"
};

calc all_amounts
+ list<currency> amount_list;
(
  currency total = 0,
  currency $ "DE" euro_amount = euro_amounts.cursor,
  {
    ? euro_amount.fact,
    amount_list += euro_amount++,
    total += amount_list
  }
);

query<term> parse_amounts(all_amounts);