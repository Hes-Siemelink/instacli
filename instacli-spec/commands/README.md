# Instacli Command Reference

Overview of standard commands in Instacli.

## Core instacli

### Script definition

* [Script info](instacli/script-info/Script%20info.spec.md) - Contains script description and input parameter
  definitions.

### Variables

* [${..} assignment](instacli/variables/Assignment.spec.md) - Sets a variable value
* [As](instacli/variables/As.spec.md) - Sets a variable to the contents of the output variable
* [Output](instacli/variables/Output.spec.md) - Sets the output variable

### Data manipulation

* [Add](instacli/data-manipulation/Add.spec.md) - Adds items
* [Add to](instacli/data-manipulation/Add%20to.spec.md) - Adds items to existing variables
* [Append](instacli/data-manipulation/Append.spec.md) - Adds items to the output variable
* [Find](instacli/data-manipulation/Find.spec.md) - Retrieves a snippet from a larger object
* [Replace](instacli/data-manipulation/Replace.spec.md) - Does a text-based find&replace
* [Size](instacli/data-manipulation/Size.spec.md) - Gives you the size of things
* [Sort](instacli/data-manipulation/Sort.spec.md) - Sorts an array

### Control flow

* [Do](instacli/control-flow/Do.spec.md) - Executes one or more commands
* [Exit](instacli/control-flow/Exit.spec.md) - Stops running the current script
* [For each](instacli/control-flow/For%20each.spec.md) - Loops over or transforms a list or object
* [Repeat](instacli/control-flow/Repeat.spec.md) - Executes a block of code until a condition is satisfied
* [If](instacli/control-flow/If.spec.md) - exectues commands if a condition holds
* [When](instacli/control-flow/When.spec.md) - Executes a single command from a list of conditions

### Error handling

* [Error](instacli/errors/Error.spec.md) - Raises an error
* [On error](instacli/errors/On%20error.spec.md) - Handles any error
* [On error type](instacli/errors/On%20error%20type.spec.md) - Handles an error of a specific type

### Testing

* [Test case](instacli/testing/Test%20case.spec.md) - Marks a test case
* [Assert that](instacli/testing/Assert%20that.spec.md) - Executes a condition
* [Assert equals](instacli/testing/Assert%20equals.spec.md) - Tests two objects for equality
* [Code example](instacli/testing/Code%20example.spec.md) - Marks example code
* [Expected output](instacli/testing/Expected%20output.spec.md) - Tests the output variable against a given value
* [Expected error](instacli/testing/Expected%20error.spec.md) - Tests if an error was raised
* [Answers](instacli/testing/Answers.spec.md) - Prerecords answers for prompts, so they can pass automated tests.

### User interaction

* [Confirm](instacli/user-interaction/Confirm.spec.md) - Asks the user for confirmation
* [Prompt](instacli/user-interaction/Prompt.spec.md) - Asks the user for input with an interactive prompt
* [Prompt object](instacli/user-interaction/Prompt%20object.spec.md) - Asks multiple questions and stores the answers
  into one object

### Util

* [Print](instacli/util/Print.spec.md) - Prints to the console
* [Wait](instacli/util/Wait.spec.md) - Waits a while
* [Base64 encode](instacli/util/Base64%20encode.spec.md) - Does a Base64 encoding
* [Base64 decode](instacli/util/Base64%20decode.spec.md) - Decodes a Base64 encoded text

## Local IO

### Files

* [Read file](instacli/files/Read%20file.spec.md) - Loads Yaml or Json from a file
* [Write file](instacli/files/Write%20file.spec.md) - Saves content to a file as Yaml
* [Temp file](instacli/files/Temp%20file.spec.md) - Saves content to a temporary file
* [Run script](instacli/files/Run%20script.spec.md) - Runs another Instacli script
* [Instacli files as commands](instacli/files/Instacli%20files%20as%20commands.spec.md) - To run any instacli file in
  the same directory as a regular command

### Shell

* [Cli](instacli/shell/Cli.spec.md) - Executes the Instacli command
* [Shell](instacli/shell/Shell.spec.md) - Executes a shell command

## REST API interaction

### Http client

* [GET](instacli/http/GET.spec.md) - Sends a GET request to an HTTP endpoint
* [POST](instacli/http/POST.spec.md) - Sends a POST request to an HTTP endpoint
* [PUT](instacli/http/PUT.spec.md) - Sends a PUT request to an HTTP endpoint
* [PATCH](instacli/http/PATCH.spec.md) - Sends a PATCH request to an HTTP endpoint
* [DELETE](instacli/http/DELETE.spec.md) - Sends a DELETE request to an HTTP endpoint
* [Http request defaults](instacli/http/Http%20request%20defaults.spec.md) - Sets the default parameters for all
  subsequent HTTP commands.

### Http server

* [Http server](instacli/http/Http%20server.spec.md) - Starts an embedded HTTP server, based on an OpenAPI-flavored spec
  and backed by Instacli scripts

### Manage credentials

* [Connect to](instacli/connections/Connect%20to.spec.md) - Sets up a connection to a named endpoint
* [Create credentials](instacli/connections/Create%20credentials.spec.md) - Configures a credentials for an endpoint and
  saves it in the user's preferences.
* [Credentials](instacli/connections/Credentials.spec.md) - Use a different credentials file than the default one from
  the home directory
* [Get credentials](instacli/connections/Get%20credentials.spec.md) - Gets the default credentials for an endpoint
* [Get all credentials](instacli/connections/Get%20all%20credentials.spec.md) - Gets all credential for an endpoint
* [Set default credentials](instacli/connections/Set%20default%20credentials.spec.md) - Sets the default credentials for
  an endpoint
* [Delete credentials](instacli/connections/Delete%20credentials.spec.md) - Deletes credentials for an endpoint

