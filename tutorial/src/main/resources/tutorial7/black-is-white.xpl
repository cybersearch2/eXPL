axiom patch (name, red, green, blue)
           {"blank", 0,     0,    0};

axiom swatch (name, red, green, blue)
            {"aqua",  0, 255,   255}
            {"black", 0,   0,     0}
            {"blue",  0,   0,   255};

axiom inverse (aqua,   black,     blue)  
             {"red", "white",  "yellow"};
            
template shade
+ list<term> color1(patch);
+ list<term> color2(patch);
+ list<term> inverse_name(inverse);
(
  before = color2->name,
  // Change name
. color1->name = inverse_name[name], 
  // Invert colors
. color1->red = red ^ 255, 
. color1->green = green ^ 255, 
. color1->blue = blue ^ 255, 
  // Check colors have expected values
  color2->name,
  color2->red, 
  color2->green, 
  color2->blue
);

query<axiom> colors(swatch : shade);
