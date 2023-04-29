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
  ${recipe} in:
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

* Cross compile to native using GraalVM, gradle and GitHub actions  
  https://katmatt.github.io/posts/graalvm-cross-compile-gh-actions/

# New features

* Rethink Join / Merge

# Technical stuff

# Difference between Python Yay

* Variable path implementation with the simpler JsonPointer (comes with Jackson) and not JsonPath
  JsonPointer is basically just dot-referencing and array indexing and that's it.
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'.
  See `Merge data.yay`
* If got a 'then'
* `Print` expands ObjectNodes to Yaml, not JSON. Makes more sense for printing structured output

# Rethinking

* Instacli!