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

First you need to define the types in the file, and then you can use them in the script. Types are defined in the
file `types.yaml`, in the same directory as the script.

```yaml file:types.yaml
FullName:
  base: object
  properties:
    first_name:
      description: First name
      type: string
    last_name:
      description: Last name
```

Then you can use the types in the script:

<!-- yaml instacli before
${input}:
  first_name: Alice
  last_name: Wonderland
-->

```yaml instacli
Code example: Define input and output types on script

Script info:
  description: Get name details
  input: FullName
```

<!-- yaml instacli after
Output: Hello, ${input.first_name} ${input.last_name}

Expected output: Hello, Alice Wonderland
-->
