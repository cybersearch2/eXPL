integer x = 1;
integer y = 2;
calc evaluate(
  boolean can_add = x + y == 3,
  boolean can_subtract = y - x == 1,
  boolean can_multiply = x * y == 2,
  boolean can_divide = 6 / y == 3,
  boolean can_override_precedence = (y + 1) * 2 > x * 5,
  boolean can_assign = (y *= 3) == 6 && y == 6,
  boolean can_evaluate = can_add && can_subtract && can_multiply && can_divide && can_override_precedence && can_assign
 );
query expressions (evaluate);
