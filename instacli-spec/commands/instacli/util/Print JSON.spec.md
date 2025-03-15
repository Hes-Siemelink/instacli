# Command: Print

`Print JSON` prints contents in JSON representation.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | yes       |
| Object       | yes       |

[Print.schema.yaml](schema/Print.schema.yaml)

## Basic usage

Use **Print JSON** to see contents in JSON representation.

```yaml instacli
Code example: Print an object as JSON

Print JSON:
  greeting: Hello, World!
```

will print

```json
{
  "greeting": "Hello, World!"
}
```
