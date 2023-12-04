# Command: PATCH

`PATCH` sends a PATCH request to an HTTP endpoint.

| Content type | Supported                         |
|--------------|-----------------------------------|
| Value        | no                                |
| List         | implicit                          |
| Object       | yes                               |
| url          | base URL of the request           |
| path         | path of the request               |
| body         | request body                      |
| username     | username for Basic authentication |
| password     | password for Basic authentication |
| headers      | request headers                   |
| saveAs       | save the result to a file         |

## Basic usage

Set the HTTP server and credentials, then issue a normal **PATCH**

```yaml
Code example: Simple PATCH

PATCH:
  url: http://localhost:25125/items
  body:
    item: one
```

## Http endpoint

As with all Http commands, you can use [Http endpoint](Http%20endpoint.md) to set the defaults for common fields.

```yaml
Code example: Http endpoint and PATCH

Http endpoint:
  url: http://localhost:25125

PATCH:
  path: /items
  body:
    item: one
```

See [Http endpoint](Http%20endpoint.md) for more information on how to configure all fields.
