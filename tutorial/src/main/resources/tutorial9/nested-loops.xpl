axiom unsorted() {12, 3, 1, 5, 8};
list<term> sorted(unsorted);
calc insert_sort 
(
  integer i = 1, 
  {
    integer j = i - 1, 
    integer temp = sorted[i], 
    {
      ? temp < sorted[j],
      sorted[j + 1] = sorted[j],
      ? --j >= 0
    },
    sorted[j + 1] = temp,
    ? ++i < length(sorted)
  }
);
query sort_axiom (insert_sort);
