package com.devbliss.docker


class Constant {

    public static final String TASK_NAME__BOOT_REPACKAGE = "bootRepackage"
    public static final String TASK_NAME__PUSH_DOCKER_IMAGE = "pushDockerImage"
    public static final String TASK_NAME__PULL_DOCKER_IMAGE = "pullDockerImage"
    public static final String TASK_NAME__START_DEPENDENCIES = "startDependencies"
    public static final String TASK_NAME__BUILD_DOCKER_IMAGE = "buildDockerImage"
    public static final String TASK_NAME__RUN_DOCKER_CONTAINER = "runDockerContainer"
    public static final String TASK_NAME__STOP_DOCKER_CONTAINER = "stopDockerContainer"
    public static final String TASK_NAME__START_DOCKER_CONTAINER = "startDockerContainer"
    public static final String TASK_NAME__REMOVE_DOCKER_CONTAINER = "removeDockerContainer"
    public static final String TASK_NAME__STOP_ALL_RUNNING_CONTAINERS = "stopAllRunningContainers"
    public static final String TASK_NAME__PULL_DEPENDENCY_IMAGES = "pullDependencyImages"
    public static final String TASK_NAME__CLEANUP_OLD_CONTAINERS = "cleanupOldContainers"
    public static final String TASK_NAME__GET_SERVICE_DEPENDENCIES = "serviceDependencies"
    public static final String TASK_NAME__BUILD_AND_PUSH_DOCKER_IMAGE = "buildAndPushDockerImage"

    public static final String DOCKER__ALREADY_HANDLED_PROPERTY = "docker.alreadyHandled"

    public static final String GRADLE_EXTENSION__DEVBLISS_DOCKER = "devblissDocker"
}
