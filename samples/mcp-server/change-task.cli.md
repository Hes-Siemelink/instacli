# Change task type in Digital.ai Release

This script changes the type of a task in Digital.ai Release and updates its properties.

## Prerequisites

### Connect to Digital.ai Release

Our target system is Digital.ai Release. In Instacli, you just declare the connection to the system you want to work
with.

```yaml instacli
Connect to: Digital.ai Release
```

### Input properties

These are the input properties for the script. They may be superfluous here, because they are already defined in
the [mcp-relelese.cli](mcp-release.cli) server specification. Technically you can leave them out, but when defined here
it makes it easier the test the script in isolation and reuse it in other contexts.

```yaml instacli
Script info:
  input:

    task_id:
      description: Full ID of the task to change

    target_type:
      description: New task type. For example, xlrelease.Task
      default: jenkins.Build

    task_properties:
      description: New task details
      default:
        jobName: My Job
```

## REST API calls

There are two REST API calls in this script. The first one changes the task type, and the second one updates the task
properties.

### Change task

The first call changes the task type.
See [Change task type](https://apidocs.digital.ai/xl-release/25.1.x/rest-docs/#change-task-type) documentation for more
details.

```yaml instacli
POST: /api/v1/tasks/${input.task_id}/changeType?targetType=${input.target_type}
As: ${task}
```

We get the task details and store them in the `${task}` variable.

### Update properties

First we prepare the data for the task properties. We assume we are updating a Jython plugin task and we create the
necessary snippet for the `pythonScript` field first. We use the **Add** command to merge the provided properties with
the template for a Jython script.

```yaml instacli
Add:
  - id: ${task.pythonScript.id}
    type: ${input.target_type}
  - ${input.task_properties}
As: ${script}
```

Now we can update the task with the new properties by sending a `PUT`.

```yaml instacli
PUT:
  path: /api/v1/tasks/${input.task_id}
  body:
    id: ${input.task_id}
    type: xlrelease.CustomScriptTask
    title: ${task.title}
    pythonScript: ${script}
```