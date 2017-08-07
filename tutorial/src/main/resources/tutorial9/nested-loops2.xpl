axiom unsorted() {12, 3, 1, 5, 8};

calc insert_sort 
+ export list<term> sorted(unsorted);
+ cursor sorter(sorted);
(
  integer i = 1, 
  {
    integer temp = sorted[i], 
    j = (sorter = i - 1),
    {
      ? temp < sorter[0],
      sorter[1] = sorter--,
      ? --j >= 0
    },
    : j == i - 1
    {
      sorted[j + 1] = temp
    },
    ? ++i < sorted.length
  }
);

query sort_axiom (insert_sort);
