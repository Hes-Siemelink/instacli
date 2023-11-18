# Command: For each

Use `For each` to loop over a list and do stuff.

| Name           | `For each` |
|----------------|------------|
| Value content  | no         |
| List content   | no         |
| Object content | yes        |

## Usage

### Basic example

Here's a simple example:

```yaml
Code example: Basic 'For each' usage

For each:
  ${name} in:
    - Alice
    - Bob
    - Carol
  Print: Hello ${name}!
```

**For each** always takes an object.

In the first field you can declare the loop variable.
The variable is defined in the field name with syntax

    ${variable_name} in

The field value contains the list of items to loop over.

The remaining fields in **For each** must be commands. They are executed by Instacli for each item in the list.

The console output of the above example would be:

    Hello Alice!
    Hello Bob!
    Hello Carol!

### Transform a list

**For each** will store the output of the last command for each item in a list. You can use this feature to transform a list into something else, like
the `map()` function of programming languages.

```yaml
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