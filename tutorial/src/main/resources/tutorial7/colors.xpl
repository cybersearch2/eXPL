axiom swatch 
  ( name )
  { "aqua" }
  { "black" }
  { "blue" };

axiom color_map 
  (   aqua  ,  black ,  blue    )  
  { | "red" | "white"|"yellow"| };

template shade
+ list<term> inverse_color(color_map);
( 
  inverse_of = name,
  inverse_color[name]
);

query<axiom> inverse_colors(swatch : shade);
