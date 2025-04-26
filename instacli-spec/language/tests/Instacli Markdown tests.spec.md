# Instacli Markdown tests

This document specifies the nitty-gritty around writing Instacli scripts in Markdown.

## Hidden before blocks

Hidden code blocks that become before actual code may contain a script that contains `---` dividers.

<!-- yaml instacli
${one}: one
---
${two}: two
-->

```yaml instacli
Test case: Hidden before block with dividers

Assert that:
  - item: ${one}
    equals: one
  - item: ${two}
    equals: two
```