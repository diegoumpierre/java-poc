# Instructions
Given a year, report if it is a leap year.

## The tricky thing here is that a leap year in the Gregorian calendar occurs:

on every year that is evenly divisible by 4
except every year that is evenly divisible by 100
unless the year is also evenly divisible by 400
For example, 1997 is not a leap year, but 1996 is. 1900 is not a leap year, but 2000 is.

## Notes
Though our exercise adopts some very simple rules, there is more to learn!

For a delightful, four minute explanation of the whole leap year phenomenon, go watch this youtube video.

Before you start, make sure you understand how to write code that can pass the test cases. For more context, check out this tutorial.

Most Java exercises include multiple test cases. These cases are structured to support a useful process known as test-driven development (TDD). TDD involves repeating a structured cycle that helps programmers build complex functionality piece by piece rather than all at once. That cycle can be described as follows:

Add a test that describes one piece of desired functionality your code is currently missing.
Run the tests to verify that this newly-added test fails.
Update your existing code until:
All the old tests continue to pass;
The new test also passes.
Clean up your code, making sure that all tests continue to pass. This typically involves renaming variables, removing duplicated chunks of logic, removing leftover logging, etc.
Return to step 1 until all desired functionality has been built!
The test files in this track contain all the tests your solution should pass to be considered valid. That doesn't immediately seem to be compatible with the cycle described above, in which tests are written one by one. However, the tool that we use to write our tests, JUnit, provides an @Ignore annotation that can be used to temporarily skip an already-written test. Using this annotation, we make sure that the test files we deliver to you satisfy the following rules:

The first test in any test file is not skipped by default.
All but the first test in any test file are skipped by default.
This allows you to simulate the TDD cycle by following these slightly-modified steps:

Run the tests to verify that at most one test currently fails.
Update your existing code until all the non-skipped tests pass.
Clean up your code, making sure that all non-skipped tests continue to pass.
Remove the topmost @Ignore annotation in the test file.
Return to step 1 until no tests are skipped and all tests pass!


## 1. Identify the year you want to check. Calculating leap years requires having a year that you want to check. Use a past year, the current year, or a future year as your starting point.[1]
For example, you could start by checking 1997 or 2012 if you want to look at a past year, or go with 2019 to check the current year, or select 2025 or 2028 to check a future year.

## 2. See if the number is evenly divisible by 4. Dividing the year by 4 will result in a whole number with no remainder if the number is evenly divisible. The number must be evenly divisible by 4! Otherwise, it is not a leap year.[2]
For example, dividing 1997 by 4 gives you 499.25, which is not a whole number because it ends with a decimal. Therefore, it is not a leap year.
When you divide 2012 by 4, you get 503, which is a whole number. This means that 2012 is likely a leap year.

## 3. Confirm the number isn't evenly divisible by 100. If a year is evenly divisible by 4, but it is not evenly divisible 100, then it is a leap year. If a year is divisible by both 4 and 100, then it might not be a leap year, and you will have to perform 1 more calculation to check.[3]
For example, 2012 is evenly divisible by 4, but not 100 since it results in a decimal answer (20.12). This means that 2012 is definitely a leap year.
2000 is divisible by 4 and it is also evenly divisible by 100 since it leaves a result of 20. That means that 2000 might not be a leap year and you will have to divide it 1 more time.

## 4. Check if the number is evenly divisible by 400 to confirm a leap year. If a year is divisible by 100, but not 400, then it is not a leap year. If a year is divisible by both 100 and 400, then it is a leap year.[4]
For example, 1900 is evenly divisible by 100, but not 400 since it gives you a result of 4.75. This means that 1900 is not a leap year.
On the other hand, 2000 is evenly divisible by 100 and 400, since it gives you a result of 5. That means that the year 2000 is a leap year.