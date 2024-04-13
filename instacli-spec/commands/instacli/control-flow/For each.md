# Command: For each

Use `For each` to loop over a list and do stuff.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[For each.schema.yaml](schema/For%20each.schema.yaml)

## Basic usage

**For each** always takes an object.

In the first field you can declare the loop variable. The variable is defined in the field name with syntax

    ${variable_name} in

The field value contains the list of items to loop over.

The remaining fields in **For each** must be commands. They are executed by Instacli for each item in the list.

Here's a simple example:

```yaml instacli
Code example: Basic 'For each' usage

For each:
  ${name} in:
    - Alice
    - Bob
    - Carol
  Print: Hello ${name}!
```

The console output of the above example would be:

    Hello Alice!
    Hello Bob!
    Hello Carol!

## Transform a list

**For each** will store the output of the last command for each item in a list. You can use this feature to transform a
list into something else, like the `map()` function in some programming languages.

```yaml instacli
Code example: Transform a list

For each:
  ${name} in:
    - Alice
    - Bob
    - Carol
  Output: Hello ${name}!

Expected output:
  - Hello Alice!
  - Hello Bob!
  - Hello Carol!
```

## Loop over the output variable

If you don't specify a loop variable in **For each**, it will loop over the current value of `${output}`, with loop
variable `${item}`.

```yaml instacli
Code example: For each on output

Output:
  - one
  - two
  - three

For each:
  Print: ${item}
```

This will print:

    one
    two
    three
