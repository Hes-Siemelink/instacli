# Command: Assert equals

`Assert equals` tests two objects for equality.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Assert equals.schema.yaml](schema/Assert%20equals.schema.yaml)

## Basic usage

**Assert equals** is a synonym for [Assert that](Assert%20that.spec.md#object-equals) equality, but using parameters
`actual`
and `expected` that make it read nicer in tests.

It works for simple values

```yaml instacli
Code example: Values should be equal

Assert equals:
  actual: one
  expected: one
```

and also for complex objects

```yaml instacli
Code example: Compare complex objects

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