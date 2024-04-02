# Instacli Command Reference

Overview of standard commands in Instacli.

## REST API interaction

### Http client

* [GET](instacli/http/GET.md) - Sends a GET request to an HTTP endpoint
* [POST](instacli/http/POST.md) - Sends a POST request to an HTTP endpoint
* [PUT](instacli/http/PUT.md) - Sends a PUT request to an HTTP endpoint
* [PATCH](instacli/http/PATCH.md) - Sends a PATCH request to an HTTP endpoint
* [DELETE](instacli/http/DELETE.md) - Sends a DELETE request to an HTTP endpoint
* [Http request defaults](instacli/http/Http%20request%20defaults.md) - Sets the default parameters for all subsequent
  HTTP commands.

### Manage credentials

* [Connect to](instacli/connections/Connect%20to.md) - Sets up a connection to a named endpoint
* [Create credentials](instacli/connections/Create%20credentials.md) - Configures a credentials for an endpoint and
  saves it in the user's preferences.
* [Get credentials](instacli/connections/Get%20credentials.md) - Gets the default credentials for an endpoint
* [Get all credentials](instacli/connections/Get%20all%20credentials.md) - Gets all credential for an endpoint
* [Set default credentials](instacli/connections/Set%20default%20credentials.md) - Sets the default credentials for an
  endpoint
* [Delete credentials](instacli/connections/Delete%20credentials.md) - Deletes credentials for an endpoint

### Http server

* [Http server](instacli/http/Http%20server.md) - Starts an embedded HTTP server, based on an OpenAPI-flavored spec and
  backed by Instacli scripts

## Core instacli

### Script definition

* [Script info](instacli/script-info/Script%20info.md) - Contains script description and input parameter definitions.

### Variables

* [${..} assignment](instacli/variables/Assignment.md) - Sets a variable value
* [As](instacli/variables/As.md) - Sets a variable to the contents of the output variable
* [Output](instacli/variables/Output.md) - Sets the output variable

### Testing

* [Test case](instacli/testing/Test%20case.md) - Marks a test case
* [Assert that](instacli/testing/Assert%20that.md) - Executes a condition
* [Assert equals](instacli/testing/Assert%20equals.md) - Tests two objects for equality
* [Code example](instacli/testing/Code%20example.md) - Marks example code
* [Expected output](instacli/testing/Expected%20output.md) - Tests the output variable against a given value
* [Stock answers](instacli/testing/Stock%20answers.md) - Prerecords answers for prompts, so they can pass automated
  tests.

### Control flow

* [Do](instacli/control-flow/Do.md) - Executes one or more commands
* [Exit](instacli/control-flow/Exit.md) - Stops running the current script
* [For each](instacli/control-flow/For%20each.md) - Loops over or transforms a list or object
* [Repeat](instacli/control-flow/Repeat.md) - Executes a block of code until a condition is satisfied
* [If](instacli/control-flow/If.md) - exectues commands if a condition holds
* [When](instacli/control-flow/When.md) - Executes a single command from a list of conditions

### Error handling

* [Error](instacli/errors/Error.md) - Raises an error
* [On error](instacli/errors/On%20error.md) - Handles any error
* [On error type](instacli/errors/On%20error%20type.md) - Handles an error of a specific type

### User interaction

* [Prompt](instacli/user-interaction/Prompt.md) - Asks the user for input with an interactive prompt
* [Prompt object](instacli/user-interaction/Prompt%20object.md) - Asks mutlitple questions and stores the answers into
  one object

### Data manipulation

* [Add](instacli/data-manipulation/Add.md) - Adds items
* [Add to](instacli/data-manipulation/Add%20to.md) - Adds items to existing variables
* [Append](instacli/data-manipulation/Append.md) - Adds items to the output variable
* [Find](instacli/data-manipulation/Find.md) - Retrieves a snippet from a larger object
* [Replace](instacli/data-manipulation/Replace.md) - Does a text-based find&replace
* [Size](instacli/data-manipulation/Size.md) - Gives you the size of things
* [Sort](instacli/data-manipulation/Sort.md) - Sorts an array

### Files

* [Read file](instacli/files/Read%20file.md) - Loads Yaml or Json from a file
* [Save as](instacli/files/Save%20as.md) - Saves content to a file as Yaml

### Call other scripts

* [Instacli files as commands](instacli/files/Instacli%20files%20as%20commands.md) - To run any instacli file in the
  same directory as a regular command
* [Run script](instacli/files/Run%20script.md) - Runs another Instacli script
* [Shell](instacli/files/Shell.md) - Executes a shell command

### Util

* [Print](instacli/util/Print.md) - Prints to the console
* [Wait](instacli/util/Wait.md) - Waits a while
* [Base64 encode](instacli/util/Base64%20encode.md) - Does a Base64 encoding
* [Base64 decode](instacli/util/Base64%20decode.md) - Decodes a Base64 encoded text
