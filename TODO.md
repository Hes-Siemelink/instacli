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
* Add ListProcessor to Commands where needed