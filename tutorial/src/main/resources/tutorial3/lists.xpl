list<integer> dice = { 2, 5, 1 };
list<double> dimensions = { 12.54, 6.98, 9.12 };

// Long.MAX_VALUE is 9,223,372,036,854,775,807
list<decimal> huges = { "9,223,372,036,854,775,808", "-9,223,372,036,854,775,808" };

list<boolean> flags = { 1 == 1, 1 == 0 };

axiom bright_stars ( a, b, c )
  { "Sirius" ,"Canopus", "Rigil Kentaurus" };
  
list<axiom> greatest 
    { "The Godfather" }
    { "The Shawshank Redemption" }
    { "Schindler's List" };

template fruit
+ list<string> fruit = { "apple", "pear", "orange" };
( 
  fruit[0], fruit[1], fruit[2], size = fruit.length
);

template dice
( 
  dice[0], dice[1], dice[2], size = dice.length
);

template dimensions
( 
  dimensions[0], dimensions[1], dimensions[2], size = dimensions.length
);

template huges
( 
  huges[0], huges[1], size = huges.length
);

template flags
( 
  flags[0], flags[1], size = flags.length
);

template stars
+ list<term> stars(global.bright_stars);
( 
  stars[0], stars[1], stars[2], size = stars.length
);

template movies
(
  greatest[0][0],
  greatest[1][0],
  greatest[2][0],
  size = greatest.length
);