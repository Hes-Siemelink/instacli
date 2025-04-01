# Prompt example

Simple interactive prompt

## User interaction

We use the `Prompt` command to display an interactive prompt to the user.

```yaml instacli
Prompt: What is your name?
```

The user's input is stored in the `${output}` variable. We can use this variable in the rest of the script.

Let's print a personalized greeting:

```yaml instacli
Print: Hello ${output}!
```