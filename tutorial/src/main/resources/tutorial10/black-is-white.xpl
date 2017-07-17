axiom aqua(name, red, green, blue)
         {"aqua",  0, 255,   255};
            
calc aqua_2_red
+ list<term> color1(global.aqua);
+ list<term> color2(global.aqua);
(
  // Change name
. color1->name = "red", 
  // Invert colors
. color1->red ^= 255, 
. color1->green ^= 255, 
. color1->blue ^= 255, 
  
  // Check colors have expected values
  color2->name,
  color2->red, 
  color2->green, 
  color2->blue 
);

query<axiom> colors(aqua_2_red);
