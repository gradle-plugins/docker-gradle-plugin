#docker-gradle-plugin
====================

Gradle Plugin for projects that use docker. It adds Tasks to handle multiple containers with one task. It will be used for ecosystem to start depending containers transitive.

##Default tasks from devbliss
We have to separate groups of docker tasks. The first group handles only the project container itself and the second one is for multiple containers.

###Project docker service tasks
Task to pull the docker image of the current service from the devbliss registry.

```bash
./gradlew pullDockerImage
```

Task to push the local docker image to the devbliss registry.

```bash
./gradlew pushDockerImage
```

Stop the ruuning service container of the current service. The name of the running container will be the project name.

```bash
./gradlew stopDockerContainer
```

Start a non-running service container. The service container will be the project service. If no container with this name exists it will fail.

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
./gradlew startDependencies
```

Stops all running docker containers.

```bash
./gradlew stopAllRunningContainers
```

##Configuration
In most cases the names are self explaining and for more information the docker api itself can be used. For example what an image name or a version tag is and how they are handled in docker.

###dockerHost
Address or socketfile where the docker daemon is running. All containers are running on this machine. All actions are executed there.

###authConfigPlain
Contains the information for docker to find the registry where the images are retrieved from. Can contain the authentication information if necessary.

###imageName
Name of the docker image. At the ecosystem we use the name of the service to identify images.

###versionTag
Version of the docker image.

###registryName
Name of the registry where the docker images are saved.

###repositoryName
Name of the docker repository. For ecosystem images it's always ecosystem. All depending containers are automatically set with this repository name.

###buildContextDirectory
Directory of the Dockerfile or the Dockerfile itself.

###dependingContainers
Service containers the project depends on. They will be started with ```startDependencies```and can contain additional information.

The normal pattern to set them is ```{name}#{port},{name2}#{port2}```
Name is the name of the image and container at the same time. The port will be mapped 1:1. The services are seperated by commas.

For additional configuration it's possible to set custom names or portmappings with ```{name}_{customName}#{portFrom}-{portTo}```

```name```is the name of the service image. ```customName``` is name of the container. ```portFrom``` is the accessible port from outside. ```portTo``` is the port in the container the portFrom is redirected to.

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