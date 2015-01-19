#docker-gradle-plugin
====================

Gradle Plugin for projects that use docker. It add Tasks to handle multiple containers with one task. It will be used for ecosystem to start depending containers transitive.

##Default tasks from devbliss

```bash
./gradlew pullDockerImage
```

```bash
./gradlew pushDockerImage
```

```bash
./gradlew stopDockerContainer
```

```bash
./gradlew startDockerContainer
```

```bash
./gradlew runDockerContainer
```

```bash
./gradlew removeDockerContainer
```

```bash
./gradlew startServiceDependencies
```

```bash
./gradlew stopAllRunningContainers
```

```bash
./gradlew buildAndPushDockerImage
```