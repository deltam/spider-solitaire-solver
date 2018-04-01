# spider-soltaire-solver

Spider Solitaire Game & Solver written by Clojure.

## Usage

### Play Game

start `$ lein repl`.

```clojure
user=> (require '[spider-solitaire-solver.core :as ss])
nil
user=> (do (ss/init) (ss/display-current)) ; init
0: * * * * * 4
1: * * * * * 9
2: * * * * * 4
3: * * * * * 5
4: * * * * Q
5: * * * * 3
6: * * * * Q
7: * * * * 5
8: * * * * J
9: * * * * K
draw:  50
nil
user=> (do (ss/move false 0 3 1) (ss/display-current)) ; move card
0: * * * * 8
1: * * * * * 9
2: * * * * * 4
3: * * * * * 5 4
4: * * * * Q
5: * * * * 3
6: * * * * Q
7: * * * * 5
8: * * * * J
9: * * * * K
draw:  50
nil
user=> (do (ss/move true 0 0 0) (ss/display-current)) ; draw card
0: * * * * 8 6
1: * * * * * 9 7
2: * * * * * 4 Q
3: * * * * * 5 4 6
4: * * * * Q 8
5: * * * * 3 8
6: * * * * Q 10
7: * * * * 5 10
8: * * * * J 6
9: * * * * K 2
draw:  40
nil
user=> (do (ss/move-undo) (ss/display-current)) ; undo
0: * * * * 8
1: * * * * * 9
2: * * * * * 4
3: * * * * * 5 4
4: * * * * Q
5: * * * * 3
6: * * * * Q
7: * * * * 5
8: * * * * J
9: * * * * K
draw:  50
nil
```

## License

Copyright (c) 2018 Masaru MISUMI(deltam@gmail.com).

Licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
