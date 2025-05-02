# Developer notes

## Releasing Instacli

1. Update the version in `build.gradle.kts`

2. Create a new release in GitHub

```shell
./gradlew githubRelease
```

3. Update the version to the new version in `build.gradle.kts`