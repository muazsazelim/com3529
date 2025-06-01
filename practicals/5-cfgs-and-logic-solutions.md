# Control Flow and Logic Analysis for Testing

## Control Flow Graphs (CFGs)

1. The control flow graph (CFG) for the [`Triangle.classify`](../code/lib/src/main/java/uk/ac/shef/com3529/Triangle.java) method:
   
```mermaid
flowchart TD
    Start(("S")) --> A(["12-14"])
    A -- T --> Swap1(["15-17"])
    A -- F --> B(["18-19"])
    Swap1 --> B
    B -- T --> Swap2(["20-22"])
    B -- F --> C(["23-24"])
    Swap2 --> C
    C -- T --> Swap3(["25-27"])
    C -- F --> D(["28-30"])
    Swap3 --> D
    D -- T --> Exception(["31"])
    D -- F --> F(["32-34"])
    F -- T --> G(["35"])
    F -- F --> I(["40"])
    G -- T --> H(["36"])
    G -- F --> J(["38"])
    I -- T --> K(["41"])
    I -- F --> Return(["45"])
    H --> Return
    J --> Return
    K --> Return
    Exception --> End(("E"))
    Return --> End

    style Exception color:#FFFFFF, fill:#FF0000, stroke:#FF0000
```

## Logic Analysis and Testing


The code segment that returns when a triangle is isosceles could be re-written as:

```java
if ((side1 + side2 > side3) &&
      (side1 == side2 || side2 == side3) &&
      (side1 != side2 || slide2 != side3)) {
    return Type.ISOCELES;
}
```

1. Conditions in the predicate:

<center>

| ID  | Condition | Conjunct |
| --- | --------- | --------: |
| C1  | side1 + side2 \> side3 | 1 |
| C2  | side1 == side2         | 2 |
| C3  | side2 == side3         | 2 |
| C4  | side1 != side2         | 3 |
| C5  | side2 != side3         | 3 |

</center>

Since C2 is equivalent to C4, and C3 is equivalent to C5, we ignore C4
and C5.

2. The complete multiple condition table is:

<center>

| ID | Condition C1 | Condition C2 | Condition C3 | Branch Predicate |
| --- | -------- | ------ | ------- |------- |
| 1   | T    | T    | T   | F  |
| 2   | T    | T    | F   | T  |
| 3   | T    | F    | T   | T  |
| 4   | T    | F    | F   | F  |
| 5   | F    | T    | T   | F  |
| 6   | F    | T    | F   | F  |
| 7   | F    | F    | T   | F  |
| 8   | F    | F    | F   | F  |

</center>

- C1 is major if we consider ID2 and ID6 or ID3 and ID7
- C2 is major if we consider ID2 and ID4 or ID1 and ID3
- C3 is major if we consider ID1 and ID2 or ID3 and ID4

3. To achieve **Restricted MCDC**

<center>

| ID  |  C1 |  C2 |  C3 | Branch predicate | Input side1 | Input side2 | Input side3 |
| ---:| --- | --- | --- | :--------------: | ----------: | ----------: | ----------: |
|   1 |   T |   T | T   |       F          |      3      |       3     |           3 |
|   2 |   T |   T | F   |       T          |      3      |       3     |           4 |
|   4 |   T |   F | F   |       F          |      3      |       4     |           5 |
|   6 |   F |   T | F   |       F          |      1      |       1     |           5 |

</center>

4. To achieve **Correlated MCDC** we only need tests ID3 and ID6

<center>

| ID  |  C1 |  C2 |  C3 | Branch predicate | Input side1 | Input side2 | Input side3 |
| ---:| --- | --- | --- | :--------------: | ----------: | ----------: | ----------: |
|   3 |   T |   F | T   |       T          |      3      |       4     |           4 |
|   6 |   F |   T | F   |       F          |      1      |       1     |           5 |

</center>

5. The JUnit test suite would look like this:

```java
package uk.ac.shef.com3529;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TriangleMCDCTest {

    @Test
    public void shouldClassifyIsoceles() {
        // MCDC test requirement ID 3
        Triangle.Type result = Triangle.classify(3, 4, 4);
        assertEquals(Triangle.Type.ISOSCELES, result);
    }

    @Test
    public void shouldNotClassifyIsoceles() {
        // MCDC test requirement ID 6
        Triangle.Type result = Triangle.classify(1, 1, 5);
        assertNotEquals(Triangle.Type.ISOSCELES, result);
    }

}
```

When executing these tests against the `Triangle` class, the second test crashes with the following exception:

```
uk.ac.shef.com3529.InvalidTriangleException: (1, 1, 5) is not a valid triangle
 at uk.ac.shef.com3529.Triangle.classify(Triangle.java:31)
 at uk.ac.shef.com3529.TriangleMCDCTest.shouldClassifyIsocelesWhenSidesAreOutOfOrder(TriangleMCDCTest.java:21)
 at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
 at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
```

This indicates that while the test requirement in theory helps us satisfy correlated MCDC, in practice, it is not possible to execute the branch predicate as expected, because the combination of input values needed are handled elsewhere in the code, i.e., line 31 in `Triangle.java`.