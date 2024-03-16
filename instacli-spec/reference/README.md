# Overview of standard commands in Instacli

## REST API interaction

### Http client

* [GET](http/GET.md) - Sends a GET request to an HTTP endpoint
* [POST](http/POST.md) - Sends a POST request to an HTTP endpoint
* [PUT](http/PUT.md) - Sends a PUT request to an HTTP endpoint
* [PATCH](http/PATCH.md) - Sends a PATCH request to an HTTP endpoint
* [DELETE](http/DELETE.md) - Sends a DELETE request to an HTTP endpoint
* [Http request defaults](http/Http%20request%20defaults.md) - Sets the default parameters for all subsequent HTTP
  commands.

### Manage connection credentials

* [Connect to](connections/Connect%20to.md) - Sets up a connection to a named endpoint
* [Create account](connections/Create%20account.md) - Configures a connection to an endpoint and saves it in the user's
  preferences.
* [Get account](connections/Get%20account.md) - Gets the default account from the user's account list.
* [Get accounts](connections/Get%20accounts.md) - Gets all accounts from the user's account list
* [Set default account](connections/Set%20default%20account.md) - Sets the default account for the user's account list
* [Delete account](connections/Delete%20account.md) - Deletes an account from the user's account list

### Http server

* [Http server](http/Http%20server.md) - Starts an embedded HTTP server, based on an OpenAPI-flavored spec and backed by
  Instacli scripts

## Core instacli

### Script definition

* [Script info](script-info/Script%20info.md) - Contains script description and input parameter definitions.

### Variables

* [${..} assignment](variables/Assignment.md) - Sets a variable value
* [As](variables/As.md) - Sets a variable to the contents of the output variable
* [Output](variables/Output.md) - Sets the output variable

### Testing

* [Test case](testing/Test%20case.md) - Marks a test case
* [Assert that](testing/Assert%20that.md) - Executes a condition
* [Assert equals](testing/Assert%20equals.md) - Tests two objects for equality
* [Code example](testing/Code%20example.md) - Marks example code
* [Expected output](testing/Expected%20output.md) - Tests the output variable against a given value
* [Stock answers](testing/Stock%20answers.md) - Prerecords answers for prompts, so they can pass automated tests.

### Control flow

* [Do](control-flow/Do.md) - Executes one or more commands
* [Exit](control-flow/Exit.md) - Stops running the current script
* [For each](control-flow/For%20each.md) - Loops over or transforms a list or object
* [Repeat](control-flow/Repeat.md) - Executes a block of code until a condition is satisfied
* [If](control-flow/If.md) - exectues commands if a condition holds
* [When](control-flow/When.md) - Executes a single command from a list of conditions
* [Eval](control-flow/Eval.md) - Runs code in a functional style 🐍

### Error handling

* [Error](errors/Error.md) - Raises an error
* [On error](errors/On%20error.md) - Handles any error
* [On error type](errors/On%20error%20type.md) - Handles an error of a specific type

### User interaction

* [Prompt](user-interaction/Prompt.md) - Asks the user for input with an interactive prompt
* [Prompt object](user-interaction/Prompt%20object.md) - Asks mutlitple questions and stores the answers into one object

### Data manipulation

* [Add](data-manipulation/Add.md) - Adds items
* [Add to](data-manipulation/Add%20to.md) - Adds items to existing variables
* [Append](data-manipulation/Append.md) - Adds items to the output variable
* [Find](data-manipulation/Find.md) - Retrieves a snippet from a larger object
* [Replace](data-manipulation/Replace.md) - Does a text-based find&replace
* [Size](data-manipulation/Size.md) - Gives you the size of things
* [Sort](data-manipulation/Sort.md) - Sorts an array

### Files

* [ReadFile](files/Read%20file.md) - Loads Yaml or Json from a file
* [SaveAs](files/Save%20as.md) - Saves content to a file as Yaml

### Call other scripts

* [Instacli files as commands](files/Instacli%20files%20as%20commands.md) - To run any instacli file in the same
  directory as a regular command
* [Run script](files/Run%20script.md) - Runs another Instacli script
* [Shell](files/Shell.md) - Executes a shell command

### Util

* [Print](util/Print.md) - Prints to the console
* [Wait](util/Wait.md) - Waits a while
* [Base64 encode](util/Base64%20encode.md) - Does a Base64 encoding
* [Base64 decode](util/Base64%20decode.md) - Decodes a Base64 encoded text
