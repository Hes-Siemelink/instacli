# Command: Connect to

By using `Connect to`, setting up a connection to an Http Endpoint is simplified by moving the connection logic behind
the scenes.

| Content type | Supported |
|--------------|-----------|
| Value        | Yes       |
| List         | implicit  |
| Object       | no        |

[Connect to.schema.yaml](schema/Connect%20to.schema.yaml)

## Basic usage

**Connect to** takes a symbolic name and usually
configures [Http request defaults](../http/Http%20request%20defaults.spec.md) for subsequent REST API calls.

A script `connect-to.cli` would look like this:

<!-- yaml instacli
Http request defaults:
  url: http://localhost:2525
-->

```yaml file=connect-to.cli
Code example: Use a connection

Connect to: Instacli Samples

GET: /items

Expected output:
  - 1
  - 2
  - 3
```

In order for this to work, you need to configure a _connection script_ for the **Instacli Samples** endpoint. You do
this in the  `.instacli.yaml` file in the same directory

```yaml file=.instacli.yaml
connections:
  Instacli Samples: connect.cli
```

The connect script `connect.cli` will be responsible for selecting the account. This way the main script does not need
to know the user credentials and other connection logic.

Here's an example connection script:

```yaml file=connect.cli
# Set up endpoint for subsequent HTTP calls
Http request defaults:
  url: http://localhost:2525
```

Now we can run it

```shell cli
cli -o connect-to
```

and it will output:

```output
- 1
- 2
- 3
```

## More examples

This is a very simple example, but you can put more in this script. For example, managing user credentials, obtaining a
session token, etc. See the [samples](../../../../samples) directory for some real world examples, for example on how to
connect to Spotify.
