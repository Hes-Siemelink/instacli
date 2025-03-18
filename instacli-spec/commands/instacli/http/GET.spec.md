# Command: GET

`GET` sends a GET request to an HTTP endpoint.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | implicit  |
| Object       | yes       |

[GET.schema.yaml](schema/GET.schema.yaml)

## Basic usage

Use **GET** on a URL to get the results as JSON/YAML

```yaml instacli
Code example: Simple GET

GET: http://localhost:2525/items

Expected output:
  - 1
  - 2
  - 3
```

## Split host and path

You can split the target host and path by using the `url` and `path` properties.

```yaml instacli
Code example: Split url and path

GET:
  url: http://localhost:2525
  path: /items

Expected output:
  - 1
  - 2
  - 3
```

## Using Http request defaults

By using [Http request defaults](Http%20request%20defaults.spec.md), you can set the properties that are common to all
requests in advance

```yaml instacli
Code example: Use Http request defaults

Http request defaults:
  url: http://localhost:2525

GET:
  path: /items
```

This way you can simply write the `path` parameter on **GET**:

```yaml instacli
Code example: Use Http Endpoint with simple GET

Http request defaults:
  url: http://localhost:2525

GET: /items
```

## Basic authentication

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the
endpoint

```yaml instacli
Code example: Basic authentication

GET:
  url: http://localhost:2525/items
  username: admin
  password: admin
```

## Custom headers

Set headers with the **headers** property

```yaml instacli
Code example: Custom headers

GET:
  url: http://localhost:2525/items
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ
```

## Save the result to a file

You can save the result to a file with **save as**

```yaml instacli
Code example: Save file

GET:
  url: http://localhost:2525/items
  save as: out/items.json
```
