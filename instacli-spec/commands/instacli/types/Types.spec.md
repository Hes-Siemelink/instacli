# Types

Use types to group related data definitions for reuse.

## Using types

You can check if some data is valid according to a type definition.

This helps you by rejecting data that is not fit for processing, and making assumptions about what's there and not.

You can also add useful hints about for user interaction. This way Instacli knows what questions to ask in a form.

## Basic example

To show types in action, we can validate given data against a type definition.

```yaml instacli
Code example: Validate data with inline type

Validate type:
  item:
    name: Hes
  type: object
```

If the validation is false, you will get an error,

```yaml instacli
Code example: Invalid type

Validate type:
  item:
    name: Hes
  type: string

Expected error:
  Type validation: Validation should fail because the provided data is not a string
```

