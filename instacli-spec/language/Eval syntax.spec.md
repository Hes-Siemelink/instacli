# Eval syntax

You can write code in a functional style by using the eval syntax. Be careful not to go crazy with this one and go all
nested and lispy! In order to keep your scripts legible, it's better to avoid nested logic.

## Basic usage

Inside a data block, you can use any command prefixed with `:` to have it evaluated inline.

For example:

```yaml instacli
Code example: Simple Eval

Add:
  - :Add:
      - 1
      - 2
  - 3

Expected output: 6
```

This is equivalent to:

```yaml instacli
Code example: No Eval

Add:
  - 1
  - 2
---
Add:
  - ${output}
  - 3

Expected output: 6
```

## Example with For each

Here's another example of how you could use this. Let's say we want to sum the ages of the main characters. Remember
that **For each**
returns [a list of outputs for each item](../commands/instacli/control-flow/For%20each.spec.md#transform-a-list). We can
then use that list in **Add**.

```yaml instacli
Code example: Eval with for each

${cast}:
  - first name: Romeo
    last name: Montague
    age: 16
  - first name: Juliet
    last name: Capulet
    age: 13

Add:
  :For each:
    ${character} in: ${cast}
    Output: ${character.age}

Expected output: 29

```

Note that you can 'unfold' this example to make it more linear and less nested:

```yaml instacli
Code example: Alternative to nesting

For each:
  ${character} in: ${cast}
  Output: ${character.age}
Add: ${output}

Expected output: 29

```

In the end it's a matter of taste. **Instacli** scripts are meant to be simple and legible. Choose the style that best
expresses how you would explain what is going on in words.