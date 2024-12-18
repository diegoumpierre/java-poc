How the revert list (begin to end)
1. input = [1,2,3,4,5]
2. result = [5]
3. length = 5
   i=0
   input[i] = 1
   result[(5-1)-0] = input[0] <-- result[4] = 1
4. length = 5
   i=1
   input[i] = 4
   result[(5-1)-1] = input[1] <-- result[3] = 2
5. length = 5
   i=2
   input[i] = 3
   result[(5-1)-2] = input[2] <-- result[2] = 3
6. length = 5
   i=3
   input[i] = 2
   result[(5-1)-3] = input[3] <-- result[1] = 2
7. length = 5
   i=4
   input[i] = 1
   result[(5-1)-4] = input[4] <-- result[0] = 1

How the revert list (end to begin)
1. input = [1,2,3,4,5]
2. result = [5]
3. length = 5 
   i=4
   input[i] = 5
   result[(5-1)-4] = input[4] <-- result[0] = 5  
4. length = 5
   i=3
   input[i] = 4
   result[(5-1)-3] = input[3] <-- result[1] = 4  
5. length = 5
   i=2
   input[i] = 3
   result[(5-1)-2] = input[2] <-- result[2] = 3
6. length = 5
   i=1
   input[i] = 2
   result[(5-1)-1] = input[1] <-- result[3] = 2
7. length = 5
   i=0
   input[i] = 1
   result[(5-1)-0] = input[0] <-- result[4] = 1