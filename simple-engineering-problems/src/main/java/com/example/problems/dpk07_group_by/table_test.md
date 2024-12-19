DPK07 Group By

1. function need receive list of object and size for group
2. Create a list with a list Objects for the result
3. Create a list object (spot) and counter
4. for each item from the list received 
   1. validate the counter is less them the size for group
      1. if yes
         1. add the item to the spot 
         2. increment in one the counter 
      2. if no:
         1. add the spot to the result
         2. reset the spot
         3. add the item to the spot
         4. set the counter to 1
5. in the end add the spot to the result
6. return the result