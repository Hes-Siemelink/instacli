# Where to take it

* Run all Digital.ai examples
* Instacli!
* Run Release templates
* Plaxolotl - yay is just an interface to a portable execution format. This format has all the metadata in the defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* Evaluate like a lisp

# Yay as a cli

* Cross compile to native using GraalVM, gradle and GitHub actions  
  https://katmatt.github.io/posts/graalvm-cross-compile-gh-actions/
* Parse command line
* yay-context.yaml

# From Yay to Yak

* Do in parallel
* Repeat - Until
* Raw and live / Apply variables
* Print as JSON / Print as YAML
* Write file / Read file
* Http: save as / verify certificate
* Test server demo data
* Webhook: On Http request

# Yay as glue

* Shell and pipe support
* Database support (SQLlite or something)
* Web server support

# Yay language

## Difference between Python Yay

* Variable path implementation with the simpler JsonPointer (comes with Jackson) and not JsonPath
  JsonPointer is basically just dot-referencing and array indexing and that's it.
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'.
  See `Merge data.yay`
* If got a 'then'
* `Print` expands ObjectNodes to Yaml, not JSON. Makes more sense for printing structured output
* `User Input` (current) vs `User input` (consistent with other commands)

## Join and Merge

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

Wait a minute, this could be written as:

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

Most Yay like:

```yaml
For each:
  recipe:
    - Mango ice cream
    - Ratatouille
    - Meatballs
  Add to vairable: all_recipes
```

(Wait, that is the single item invocation of Merge?)

# Fork or not

* Will I ever add code to Python yay? => No
* Need to check reference implementation? => Only a bit
