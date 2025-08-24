# Command: Http server

`Http server` starts an embedded HTTP server, based on an OpenAPI-flavored spec and backed by Instacli scripts. Use
**Http server** to quickly prototype and API.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Http server.schema.yaml](schema/Http%20server.schema.yaml)

## Basic usage

Set up an HTTP server by defining the port and endpoints.

The following example defines an HTTP `GET` request on path `/hello` to return the text "Hello World!".

```yaml instacli
Code example: Http server setup

Http server:
  port: 25001
  endpoints:
    /hello:
      get:
        output: Hello World!

# Test the server
GET: http://localhost:25001/hello

Expected output: Hello World!
```

Let's break this down,

### Define endpoints and start server

The **port** field defines the listening port, in this case 25001.

You can run multiple servers simultaneously on different ports. In this case we start a server on port 25001. If you
call **Http server** multiple times, the endpoints you define are added to the server running on that port.

Then you define the **endpoints**. The format is inspired by OpenAPI definitions.

First, you define a path, in this case `/hello`. On the path, you need a define a method handler, one of `get`, `post`,
`put`, `patch` or `delete`. In this case we defined a `get`.

Then you define a handler. There are three handler types

* `output`: returns the content below it
* `script`: runs an inline instacli script
* `file`: runs an instacli script file

In this example we have a static Hello World greeting being returned by `output: Hello World!`

### Test the server

When defining the endpoints, the server is started right away, and we can test it with a simple GET request:

```yaml instacli
GET: http://localhost:25001/hello
```

### Stop the server

Stop and remove the server for a specific port with the `stop` command:

```yaml instacli
Code example: Stop server

Http server:
  port: 25001
  stop: true
```

This will only stop the server running on the specified port, other servers will continue operating.

If the server is still running at the end of the script, Instacli will not exit and keep serving requests until you stop
the process -- for example by pressing `^C` from the command line.

## Supplied variables

The following variables can be used in `output`, `script` and `file`

* `${input}`: - Body or query parameters. If there is a body in the request, query parameters are ignored.
* `${request.headers}`: - Request headers
* `${request.path}`: - Full path of the request
* `${request.pathParameters}`: - Map of parameters defined in the path
* `${request.query}`: - Query string
* `${request.queryParameters}`: - Map of query parameters
* `${request.body}`: - Request body
* `${request.cookies}`: - Cookies sent by the client

### Using variables with `output`

Variables are resolved in the `output` handler, making it easy to echo certain parts of the request without processing

```yaml instacli
Code example: Variables in output

Http server:
  port: 25001
  endpoints:
    /echo/headers:
      get:
        output: ${request.headers}
    /greeting:
      get:
        output: Hello ${input.name}
```

<!-- yaml instacli

--- 
Http server:
  port: 25001
  stop: true
-->

## Running an inline script

Define an Instacli script in the handler using the `script` type:

```yaml instacli
Code example: Instacli script handler

Http server:
  port: 25001
  endpoints:
    /greet-all:
      post:
        script:
          For each:
            ${name} in: ${input.names}
            Output:
              Hello ${name}!

POST:
  url: http://localhost:25001/greet-all
  body:
    names:
      - Alice
      - Bob
      - Carol

Expected output:
  - Hello Alice!
  - Hello Bob!
  - Hello Carol!
```

<!-- yaml instacli

---
Http server:
  port: 25001
  stop: true
-->

## Running a file

You can also specify an Instacli file to run.

Suppose you have a file `greet.cli`

```yaml file=greet.cli
Script info: Creates a greeting

Input parameters:
  name: Your name

Output: Hello ${input.name}!
```

You can call this directly from the http endpoint definition. Note that body or query parameters are automatically
passed as input to the script.

```yaml instacli
Code example: File handler

Http server:
  port: 25001
  endpoints:
    /greet:
      get:
        file: greet.cli

GET: http://localhost:25001/greet?name=Alice

Expected output: Hello Alice!
```

<!-- yaml instacli

---
Http server:
  port: 25001
  stop: true
-->
