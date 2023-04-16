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

# New features

* Rethink Join / Merge

# Technical stuff

* Define accepted type & structure in commands. For example, this command takes text, objects with field 'expected'
  and 'actual'
* Rename package 'commands' to 'core'

# Difference between Python Yay

* Very lightweight variable path implementation, basically just dot-referencing and array indees are supported and
  nothing fancy. Reason: using JsonPointer (comes with Jackson) and not JsonPath
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'.
  See `Merge data.yay`
* If got a 'then'
