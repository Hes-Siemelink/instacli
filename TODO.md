# On my mind

* User interaction:
    * Add 'Prompt' that does not do all the spooky stuff Input does
    * Add support for default values
    * Add option to ask for single answer
    * Rename 'User input' to 'Choice' or 'Prompt'
    * Write tests for user input
* Create Instacli based on OpenAPI metadata
* Http Session management
* Error handling

# Where to take it

* Run Release templates
* Plaxolotl - yay is just an interface to a portable execution format. This format has all the metadata in the defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* Evaluate like a lisp

# Command-line support

* Cross compile to native using GraalVM, gradle and GitHub actions  
  https://katmatt.github.io/posts/graalvm-cross-compile-gh-actions/
* Parse command line
* yay-context.yaml

# Instacli as glue

* Shell and pipe support
* Database support (SQLlite or something)
* Web server support
* Write file / Read file
* Http: verify certificate
* Webhook (Web server): On Http request

# Instacli language

* Raw and live / Apply variables
* Evaluate liek a lisp. For example with exclamation point in syntax: `!Do`, `!For each`, `!Load file`, etc.

## Difference between Python Yay

* Variable path implementation with the simpler JsonPointer (comes with Jackson) and not JsonPath
  JsonPointer is basically just dot-referencing and array indexing and that's it.
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'.
  See `Merge data.cli`
* If got a 'then'
* `Print` expands ObjectNodes to Yaml, not JSON. Makes more sense for printing structured output
* Renamed `User Input` to `User input`  to be consistent with other commands. Same for `Check input`.
* On Http commands, `path` is optional and will be appended to `url`

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
  Add to variable: all_recipes
```

(Wait, that is the single item invocation of Merge?)


