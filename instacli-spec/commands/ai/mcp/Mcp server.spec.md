# Command: Mcp server

`Mcp server` starts an MCP server.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | no        |
| Object       | yes       |

[McpServer.schema.yaml](schema/McpServer.schema.yaml)

## Basic usage

Use **Mcp server** to start a server with tools, resources and prompts.

```yaml instacli
Code example: Simple MCP server

Mcp server:
  name: my-server
  version: "1.0.0"
  tools:
    hello:
      description: Get a greeting
      inputSchema:
        name:
          type: string
          description: Your name
      script:
        Output: Hello ${input.name}!
```

### Stop the server

Stop and remove the server with the `stop` command:

```yaml instacli
Code example: Stop server

Mcp server:
  name: my-server
  version: "1.0.0"
  stop: true
```
