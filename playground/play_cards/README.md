# play cards
Introduction
Conditionals If
Logical Operators
Java supports the three logical operators && (AND), || (OR), and ! (NOT).

If statement
The underlying type of any conditional operation is the boolean type, which can have the value of true or false. Conditionals are often used as flow control mechanisms to check for various conditions. For checking a particular case an if statement can be used, which executes its code if the underlying condition is true like this:

int val;

if(val == 9) {
// conditional code
}
In scenarios involving more than one case many if statements can be chained together using the else if and else statements.

if(val == 10) {
// conditional code
} else if(val == 17) {
// conditional code
} else {
// executes when val is different from 10 and 17
}
Switch statement
Java also provides a switch statement for scenarios with multiple options.

int val;

// switch statement on variable content
switch(val) {
case 1:
// conditional code
break;
case 2: case 3: case 4:
// conditional code
break;
default:
// if all cases fail
break;
}
Instructions
In this exercise we will simulate the first turn of a Blackjack game.

You will receive two cards and will be able to see the face up card of the dealer. All cards are represented using a string such as "ace", "king", "three", "two", etc. The values of each card are:

card	value	card	value
ace	11	eight	8
two	2	nine	9
three	3	ten	10
four	4	jack	10
five	5	queen	10
six	6	king	10
seven	7	other	0
Note: Commonly, aces can take the value of 1 or 11 but for simplicity we will assume that they can only take the value of 11.

Depending on your two cards and the card of the dealer, there is a strategy for the first turn of the game, in which you have the following options:

- Stand (S)
- Hit (H)
- Split (P)
- Automatically win (W)
  Although not optimal yet, you will follow the strategy your friend Alex has been developing, which is as follows:

Category: Large Hand

If you have a pair of aces you must always split them.
If you have a Blackjack (two cards that sum up to a value of 21), and the dealer does not have an ace, a figure or a ten then you automatically win. If the dealer does have any of those cards then you'll have to stand and wait for the reveal of the other card.
Category: Small Hand

If your cards sum up to 17 or higher you should always stand.
If your cards sum up to 11 or lower you should always hit.
If your cards sum up to a value within the range [12, 16] you should always stand unless the dealer has a 7 or higher, in which case you should always hit.
