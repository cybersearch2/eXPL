list<term> color(swatch);
axiom swatch (name, red, green, blue)
{"aqua", 0, 255, 255}
{"black", 0, 0, 0}
{"blue", 0, 0, 255};
template shade(name, color[red], color[green], color[blue]);
query colors(swatch : shade);
