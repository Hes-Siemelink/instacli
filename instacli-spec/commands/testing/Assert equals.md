# Command: Assert equals

`Assert equals` tests two objects for equality.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |
| `actual:`    | required  |
| `expected:`  | required  |

## Basic usage

**Assert equals** is a synonym for [Assert that](Assert%20that.md#object-equals) equality, but using parameters `actual` and `expected` that make it read nicer
in tests.

It works for simple values

```yaml
Assert equals:
  actual: one
  expected: one
```

and also for complex objects

```yaml
Assert equals:
  actual:
    something:
      id: 123
      path: /home
      token: 456
  expected:
    something:
      id: 123
      path: /home
      token: 456
```