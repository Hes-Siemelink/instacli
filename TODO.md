# Yay language

# Join command
* Choose between 'Join' or 'Update'
* variable syntax on assignment?

Before:
```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  Output: ${recipe}
  Join:
    all_recipes:
    - ${output}
```

After:
```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  Output: ${recipe}
  Join:
    ${all_recipes}:
    - ${output}
```

or even do a special syntax:

```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  Output: ${recipe}
${all_recipes} add: ${output}
${all_recipes} +: ${output}
```

Wait a minute, this should be written as:
```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  ${all_recipes} add: ${recipe}
```


Or just use `Merge`:
```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  Output: ${recipe}

Merge:
- ${all_recipes}
- ${output}
As: all_recipes
```

# Yay as a cli

# Missing features

# Technical stuff
* Helper functions to check type of data (Object, Array) and getting parameters 
* Define accepted type & structure in commands. For example, this command takes text, objects with field 'expected' and 'actual'
* ScriptException: Exception data in yaml by taking it as a parameter
* Refactor execution logic / YakScript

# Difference between Python Yay
* Very lightweight variable path implementation, basically just dot-referencing and array indees are supported and nothing fancy. Reason: using JsonPointer (comes with Jackson) and not JsonPath
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'. See `Merge data.yay`
* If got a 'then'
* Join behavior is incorrect:

Python:
```yaml
Test case: Join two dicts, second one overwrites properties from the first.

Join:
  package:
    - label: first
      header: accept
    - label: second
      content: body

Assert equals:
  actual: ${package}
  expected:
    label: second
    header: accept
    content: body
```

Kotlin:
```yaml

```