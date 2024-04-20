# Command: Script info with types

`Script info` contains the description of a script and the definition of its input and output.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | yes       |

[Script info.schema.yaml](schema/Script%20info.schema.yaml)

## Using types

You can define the input and output of a Script as _types_

```yaml not yet compiling instacli
Code example: Define input and output types on script

Types:
  FullName:
    description: Full name
    properties:
      first_name:
        label: First name
        type: string
      last_name:
        label: Last name

Stock answers:
  First name: Alice
  Last name: Bob

---
Script info:
  description: Get name details

  input type: FullName
  output type: string

Output: ${input.first_name} ${input.last_name}

---
Expected output: Alice Bob
```

