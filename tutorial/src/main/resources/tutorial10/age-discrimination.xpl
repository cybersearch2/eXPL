choice age_rating
  (star_age       , age_weight)
  {star_age > 28.5, 0.3}
  {star_age > 24.9, 0.6}
  {star_age > 19.5, 1.0};
 
axiom person (name, sex, age, starsign)
             {"Clancy", "m", 18, "capricorn"}
             {"John", "m", 23, "gemini"} 
             {"Sue", "f", 19, "cancer"} 
             {"Sam", "m", 34, "scorpio"} 
             {"Jenny", "f", 28, "gemini"} 
             {"Andrew", "m", 26, "virgo"} 
             {"Alice", "f", 20, "pisces"} 
             {"Ingrid", "f", 23, "cancer"} 
             {"Jack", "m", 32, "pisces"} 
             {"Sonia", "f", 33, "gemini"} 
             {"Alex", "m", 22, "aquarius"} 
             {"Jill", "f", 33, "cancer"} 
             {"Fiona", "f", 29, "gemini"} 
             {"Melissa", "f", 30, "virgo"} 
             {"Tom", "m", 22, "cancer"} 
             {"Bill", "m", 19, "virgo"}; 
 
list<string> starsigns =
{
  "capricorn", 
  "aquarius", 
  "pisces",
  "aries", 
  "taurus", 
  "gemini",
  "cancer", 
  "leo",
  "virgo", 
  "libra", 
  "scorpio", 
  "sagittarius" 
};
              
calc rate_person
(
  Name = name,
  Starsign = starsign,
. fraction = 0,
  {
    : starsigns[fraction] == starsign,
    ++fraction
  },
. double age,
. choice age_rating(star_age = age + (fraction / 12.0)),
  ? age_rating != -1,
  Age = star_age.format,
  Rating = age_weight
);

query<axiom> rate_age (person : rate_person);
