# On my mind

* Use Kotlin Json
* Report nested exceptions

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined explicitly. For example: content type,
  variable replacement yes/no, etc.
* Auto-generate Instacli based on OpenAPI metadata. Use AI to make sense of stuff?
* Run Release templates

# Command-line support

* Parse command line properly
* Kotlin native?

# Instacli as glue

* Shell and pipe support
* Database support (SQLite or something)
* Web server support
* Write file / Read file
* Http: verify certificate
* Webhook (Web server): On Http request

# Instacli language

* Combine `If` and `When`? Introduce `else`
* Raw and live / Apply variables
* Evaluate like a lisp. For example with exclamation point in syntax: `!Do`, `!For each`, `!Load file`, etc.

## Difference between Python Yay

* Variable path implementation with the simpler JsonPointer (comes with Jackson) and not JsonPath
  JsonPointer is basically just dot-referencing and array indexing and that's it.
* Merge does not merge object content into output anymore. This was an obscure feature that can easily done with 'Join'.
  See `Merge data.cli`
* If got a 'then'
* `Print` expands ObjectNodes to Yaml, not JSON. Makes more sense for printing structured output
* Replaced `User Input` with `Ask user` that has a slightly different syntax
* Replaced `Check input` with `Input`
* On Http commands, `path` is optional and will be appended to `url`
* Dropped `Join` in favor of `Merge`