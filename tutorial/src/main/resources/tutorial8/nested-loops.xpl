axiom unsorted() {12, 3, 1, 5, 8};

calc insert_sort 
+ export list<term> sorted(unsorted);
(
  i = 1, 
  {
    j = i - 1, 
    temp = sorted[i], 
    {
      ? temp < sorted[j],
      sorted[j + 1] = sorted[j],
      ? --j >= 0
    },
    sorted[j + 1] = temp,
    ? ++i < sorted.length
  }
);

query sort_axiom (insert_sort);
