# Command: Assert that

`Assert that` executes a condition. Conditions can also be used in other commands like [If](../control-flow/If.md)
and [Repeat](../control-flow/Repeat.md)

| Content type                | Supported |
|-----------------------------|-----------|
| Value                       | no        |
| List                        | implicit  |
| Object                      | yes       |
| _[Conditions](#Conditions)_ | required  |

## Basic usage

**Assert that** throws an exception if the condition is not true and execution of the script is stopped.

## Conditions

### Object equals

Compare two objects, one is in field `item`; the other in `equals`.

```yaml script
Code example: Comparing values

Assert that:
  item: one
  equals: one
```

This is also works for lists

```yaml script
Code example: Comparing lists

Assert that:
  item:
    - one
    - two
  equals:
    - one
    - two
```

and objects

```yaml script
Code example: Comparing objects

Assert that:
  item:
    one: 1
    two: 2
  equals:
    one: 1
    two: 2
```

### Contains

You can also test if something is inside something else with the `'item'` and `in`.

```yaml script
Code example: Check if an object is in a list

Assert that:
  item: one
  in:
    - one
    - two
    - three
```

You can also test for parts of an object.

```yaml script
Code example: Check if an object contains some properties

Assert that:
  item:
    one: 1
    two: 2
  in:
    one: 1
    two: 2
    three: 3
```

### Empty

Check if an array or value is empty

```yaml script
Code example: Empty list and string

Assert that:
  - empty: [ ]
  - empty: ""
```

It's a shorthand for checking equality. This makes more sense in an `If` statement when you are checking the value that
is coming from somewhere else.

```yaml script
Code example: If with empty

${values}: [ ]

If:
  empty: ${values}
  then:
    Exit: Nothing to process
```

### All, Any and Not

**all**, **any** and **not** are container condition that take other conditions as a subcondition.

The condition **all** is a logical AND.

```yaml script
Code example: All condition

Assert that:
  all:
    - item: one
      equals: one
    - item: two
      equals: two
```

The conditions **any** is a logical OR.

```yaml script
Code example: Any condition

Assert that:
  any:
    - item: one
      equals: one
    - item: three
      equals: four
```

The conditions **not** is the negation

```yaml script
Code example: Not condition

Assert that:
  not:
    empty:
      - one
      - two
```


