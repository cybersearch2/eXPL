list<term> color(swatch);
list<term> rgb(swatch);
axiom swatch (name, red, green, blue)
            {"aqua",  0, 255,   255}
            {"black", 0,   0,     0}
            {"blue",  0,   0,   255};
template shade
(
  name, 
  // Invert colors
  color^red ^= 255, 
  color^green ^= 255, 
  color^blue ^= 255, 
  // Check colors have expected values
  integer r = rgb^red, 
  integer g = rgb^green, 
  integer b = rgb^blue
);
query<axiom> colors(swatch : shade);
