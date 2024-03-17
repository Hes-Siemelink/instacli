# Instacli Integration Tests

## Digital.ai Platform Integration Tests

In order to run the integration tests against Digital.ai Platform, configure a valid account in

    src/integrationTest/resources/instacli-home/credentials.yaml

This file is in `.gitignore` so it is safe to store your credentials.

If the file not there, the integration test will be skipped.

### Configuring an account

The best way to configure credentials is to use the following command that will create the file interactively.

    cli samples/digitalai/platform/login/create-platform-connection.cli

And then copy the resulting `~/.instacli/credentials.yaml` to  `resources/instacli-home`.

