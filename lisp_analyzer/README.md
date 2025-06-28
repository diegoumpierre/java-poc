# Java 17 Dojo - (() lisp analyzer ())

### Problem




We already did dojos with many Lisp language implementations, the big difference in Lisp languages is the parentheses notation, usually, developers have difficulty with this style.
Depending on the language we don't have good editor support. So the idea for this dojo is to create a Lisp (parentheses notation) helper, our program should read Lisp-like code and identify if some parentheses are missing, identify the line and the column that is missing
e.g
```
(defn selling-books [kind quantity price]
    (let [discount (get (router kind) quantity)]
     (let [subtotal (* price quantity]
      (- subtotal (* subtotal discount)))))
```

The output should alert that is missing something on line 3 column 37

### Build
```bash
./mvnw clean install 
```

### Test
```bash
./mvnw clean test
```