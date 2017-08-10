axiom person (name, sex, age, starsign)
             {"John", "m", 23, "gemini"} 
             {"Sue", "f", 19, "cancer"} 
             {"Sam", "m", 34, "scorpio"} 
             {"Jenny", "f", 28, "gemini"} 
             {"Andrew", "m", 26, "virgo"} 
             {"Alice", "f", 20, "pices"} 
             {"Ingrid", "f", 23, "cancer"} 
             {"Jack", "m", 32, "pisces"} 
             {"Sonia", "f", 33, "gemini"} 
             {"Alex", "m", 22, "aquarius"} 
             {"Jill", "f", 33, "cancer"} 
             {"Fiona", "f", 29, "gemini"} 
             {"Melissa", "f", 30, "virgo"} 
             {"Tom", "m", 22, "cancer"} 
             {"Bill", "m", 19, "virgo"};
              
choice age_rating
  (age,       age_weight)
  {age >  29, 0.3}
  {age >  25, 0.6}
  {age >= 20, 1.0}
  {age <= 19, NaN};

calc perfect_match
(
  Name = name, 
  Age = age,
  Starsign = starsign, 
. choice age_rating,
  Rating = age_weight.format
);

query<axiom> star_people(person : perfect_match);
