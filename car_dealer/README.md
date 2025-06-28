# Car Dealer

Suppose you are a car dealer and you have a list of n cars that you want to sell.

Cars:
```json
[
    {"maker": "Toyota", "model": "Camry", "year": 2018, "price": 15000, "miles": 50000},
    {"maker": "Ford", "model": "F-150", "year": 2020, "price": 25000, "miles": 80000},
    {"maker": "Honda", "model": "Accord", "year": 2015, "price": 10000, "miles": 120000},
    {"maker": "Toyota", "model": "Corolla", "year": 2017, "price": 12000, "miles": 60000},
    {"maker": "Nissan", "model": "Altima", "year": 2019, "price": 18000, "miles": 90000},
    {"maker": "Chevrolet", "model": "Camaro", "year": 2021, "price": 30000, "miles": 20000}
]
```

Write a function that takes in a list of cars, budget and maximum number of miles, and returns a list of the cars
that can be purchased with the given budget and that has fewer miles than requested. The list should be sorted by
price from lowest to highest.


Suppose your budget is 20000 and the maximum number of miles is 70000. Then your function should return the following list:

Output:
```
Toyota Corolla, 2017 | $12000 (60000) 
Toyota Camry, 2018 | $15000 (50000) 
```


## Build
mvn clean install