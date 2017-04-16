// Uninitialized variable matches to any value
// Choice selects on hex color value to get color name and rgb values
choice swatch 
(rgb,      color,   red, green, blue)
{0x00FFFF, "aqua",    0, 255, 255}
{0x000000, "black",   0,   0,   0}
{0x0000FF, "blue",    0,   0, 255}
{0xFFFFFF, "white", 255, 255, 255}
{unknown,  "unknown", 0,   0,   0};

axiom shade (rgb) : parameter;

query<term> color(shade : swatch);
