# Command: Mcp server

`Mcp server` starts an embedded MCP server. Use
**MCP** to quickly prototype an MCP spec.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Mcp server.schema.yaml](schema/Mcp%20server.schema.yaml)

## Basic usage

Set up an HTTP server by defining the port and endpoints.

The following example defines an HTTP `GET` request on path `/hello` to return the text "Hello World!".

```yaml TODO instacli
Code example: Mcp server setup

Mcp server:
  port: 25002
  name: weather
  version: "1.0.0"
  tools:
    - get_alerts:
        description: Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)
        inputSchema:
          state:
            type: string
            description: Two-letter US state code (e.g. CA, NY)

    - get_forecast:
        description: Get weather forecast for a specific latitude/longitude
        inputSchema:
          latitude:
            type: number
          longitude:
            type: number
#        required: true

# Test the server
#GET: http://localhost:25001/hello

#Expected output: Hello World!
```

Let's break this down,

