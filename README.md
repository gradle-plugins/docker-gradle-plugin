#docker-gradle-plugin
====================

Gradle Plugin for projects that use docker. It add Tasks to handle multiple containers with one task. It will be used for ecosystem to start depending containers transitive.

##Default tasks from devbliss
We have to seperate groups of docker tasks. The first group handles only the project container itself and the second one is for multiple containers.

###Project docker service tasks
Task to pull the docker image of the current service from the devbliss registry.

```bash
./gradlew pullDockerImage
```

Task to push the local docker image to the devbliss registry.

```bash
./gradlew pushDockerImage
```

Stop the running service container of the current service. The name of the running container will be the project name.

```bash
./gradlew stopDockerContainer
```

Start a stopped service container. The service container will be the project service. If no container with this name exists it will fail.

```bash
./gradlew startDockerContainer
```

Run a service container. The container name will be the project name and the start command will default that is defined in the Dockerfile.

```bash
./gradlew runDockerContainer
```

Remove the project service container. Will fail if the container is still running.

```bash
./gradlew removeDockerContainer
```

Builds the current project as a docker image and push it to the devbliss registry.

```bash
./gradlew buildAndPushDockerImage
```

###Docker tasks for multiple containers

Starts all containers the service depends on. The depending services are defined over the devblissDocker extension which is explained later.

```bash
./gradlew startServiceDependencies
```

Stops all running docker containers.

```bash
./gradlew stopAllRunningContainers
```

##Configuration

###Example configuration

gradle.properties

```properties
dependingEcosystemServices=ecosystem-zuul#8070,learning-edge-service#8095

dockerDaemonHost=http://172.17.42.1:2375
dockerRegistry=d-v229-xen:5000
dockerRepository=ecosystem
dockerImage=qti-player
dockerTag=latest
```

build.gradle

```gradle
devblissDocker {
  dockerHost = dockerDaemonHost
  authConfigPlain = ["serveraddress": "http://${dockerRegistry}/v1"]
  buildContextDirectory = file('./')
  registryName = dockerRegistry
  repositoryName = dockerRepository
  dependingContainers = dependingEcosystemServices
  imageName = dockerImage
  versionTag = dockerTag
}
```