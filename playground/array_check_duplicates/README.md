# Problem: Array Duplicates

Sherlock detective in charge of solving a robbery case. The thief stole a valuable painting from a famous art museum and hid it somewhere in the city. The only clue that he have is a list of numbers that the thief left behind. He suspect's that the numbers might be the location of the painting, but he's are not sure.

To help sherlock to find the painting he gave you an array of integers, where each number represents a location in the city. Your task is to write a program that will help you determine if the thief has visited the same location twice. If he did, that means he might have hidden the painting there.

You only have a limited amount of time to solve the case, so you need to come up with a solution that is efficient. You are not allowed to use any external libraries or modules. You should use ZSH shell to develop your program.

The input to your program will be an array of integers, where 1 <= nums.length <= 105 and -109 <= nums[i] <= 109. Your program should output a boolean value indicating whether the array contains duplicate values.

You have to find the thief's hideout before he escapes. Can you solve the case and recover the stolen painting?

## Input
An array of integers nums where 1 <= nums.length <= 105 and -109 <= nums[i] <= 109
- Output
  A boolean value indicating whether the array contains duplicate values.

## Input: [1,2,3,1]

- Output: true

## Input: [1,2,3,4]

- Output: false

## Input: [1,1,1,3,3,4,3,2,4,2]

- Output: true


## For test
mvn clean install