package com.devbliss.docker

import com.devbliss.docker.task.*
import de.gesellix.gradle.docker.tasks.*
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Configuration class that applies the devblissDocker configuration to all docker tasks of known type.
 */
class Configuration {

    private DockerPluginExtension devblissDockerExtension
    private GetServiceDependenciesTask getServiceDependenciesTask
    private StartDependenciesTask startDependenciesTask
    private CleanupOldContainersTask cleanupOldContainersTask
    private PullDependingImagesTask pullDependencyImages
    private BuildAndPushDockerImageTask buildAndPushDockerImage
    private Task bootRepackageTask

    public Configuration(Project project) {
        devblissDockerExtension = project.extensions.create(Constant.GRADLE_EXTENSION__DEVBLISS_DOCKER, DockerPluginExtension)

        instantiateAllTasksForConfiguration(project)

        configureTaskDependsOnStartDependencies()
        configureTaskDependsOnBuildAndPushDockerImage()

        setAllPropertiesWithConfigurationForTasks(project)
    }

    private void instantiateAllTasksForConfiguration(Project project) {
        pullDependencyImages = project.getTasks().getByName(Constant.TASK_NAME__PULL_DEPENDENCY_IMAGES)
        startDependenciesTask = project.getTasks().getByName(Constant.TASK_NAME__START_DEPENDENCIES)
        buildAndPushDockerImage = project.getTasks().getByName(Constant.TASK_NAME__BUILD_AND_PUSH_DOCKER_IMAGE)
        cleanupOldContainersTask = project.getTasks().getByName(Constant.TASK_NAME__CLEANUP_OLD_CONTAINERS)
        getServiceDependenciesTask = project.getTasks().getByName(Constant.TASK_NAME__GET_SERVICE_DEPENDENCIES)
        bootRepackageTask = project.getTasks().findByPath(Constant.TASK_NAME__BOOT_REPACKAGE);
    }

    private void setAllPropertiesWithConfigurationForTasks(Project project) {
        project.afterEvaluate {
            configureStartServiceDependenciesTasks(startDependenciesTask, devblissDockerExtension)
            configureGetServiceDependenciesTasks(getServiceDependenciesTask, devblissDockerExtension)
            configureAllAbstractTasks(project, devblissDockerExtension)
            configurePullTasks(project, devblissDockerExtension)
            configurePushTasks(project, devblissDockerExtension)
            configureBuildTasks(project, devblissDockerExtension)
            configureStopTasks(project, devblissDockerExtension)
            configureStartTasks(project, devblissDockerExtension)
            configureRmTasks(project, devblissDockerExtension)
            configureRunTasks(project, devblissDockerExtension)
            configurePullDependingImagesTasks(project, devblissDockerExtension)
            configureCleanupOldContainersTasks(project, devblissDockerExtension)
        }
    }

    private void configureTaskDependsOnStartDependencies() {
        startDependenciesTask.dependsOn cleanupOldContainersTask
        cleanupOldContainersTask.dependsOn pullDependencyImages
    }

    private void configureTaskDependsOnBuildAndPushDockerImage() {
        if (bootRepackageTask != null) {
            buildAndPushDockerImage.dependsOn(Constant.TASK_NAME__BOOT_REPACKAGE)
        }
        buildAndPushDockerImage.dependsOn(Constant.TASK_NAME__BUILD_DOCKER_IMAGE)
        buildAndPushDockerImage.finalizedBy(Constant.TASK_NAME__PUSH_DOCKER_IMAGE)
    }

    private void configureStartServiceDependenciesTasks(StartDependenciesTask startDependenciesTask,
                                                        DockerPluginExtension extension) {
        startDependenciesTask.dependingContainers = extension.dependingContainers
        startDependenciesTask.dockerHost = extension.dockerHost
        startDependenciesTask.authConfigPlain = extension.authConfigPlain
        startDependenciesTask.authConfigEncoded = extension.authConfigEncoded
        startDependenciesTask.versionTag = extension.versionTag
        startDependenciesTask.dockerRegistry = extension.registryName
        startDependenciesTask.dockerRepository = extension.repositoryName
    }

    private void configureGetServiceDependenciesTasks(GetServiceDependenciesTask getServiceDependenciesTask,
                                                      DockerPluginExtension extension) {
        getServiceDependenciesTask.dependingContainers = extension.dependingContainers
    }

    private void configureAllAbstractTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(AbstractDockerTask) { task ->
            task.dockerHost = extension.dockerHost
            task.authConfigPlain = extension.authConfigPlain
            task.authConfigEncoded = extension.authConfigEncoded
        }
    }

    private void configurePullTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerPullTask) { task ->
            task.registry = extension.registryName
            task.imageName = extension.repositoryName + '/' + extension.imageName
            task.tag = extension.versionTag
        }
    }

    private void configurePushTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerPushTask) { task ->
            task.registry = extension.registryName
            task.repositoryName = extension.repositoryName + '/' + extension.imageName
        }
    }

    private void configureBuildTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerBuildTask) { task ->
            task.buildContextDirectory = extension.buildContextDirectory
            task.imageName = extension.repositoryName + '/' + extension.imageName
        }
    }

    private void configureStopTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerStopTask) { task ->
            task.containerId = extension.imageName
        }
    }

    private void configureStartTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerStartTask) { task ->
            task.containerId = extension.imageName
        }
    }

    private void configureRmTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerRmTask) { task ->
            task.containerId = extension.imageName
        }
    }

    private void configureRunTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(DockerRunTask) { task ->
            task.containerName = extension.imageName
            task.imageName = extension.registryName + '/' + extension.repositoryName + '/' + extension.imageName
        }
    }

    private void configurePullDependingImagesTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(PullDependingImagesTask) { task ->
            task.dependingContainers = extension.dependingContainers
            task.dockerHost = extension.dockerHost
            task.authConfigPlain = extension.authConfigPlain
            task.authConfigEncoded = extension.authConfigEncoded
            task.versionTag = extension.versionTag
            task.dockerRegistry = extension.registryName
            task.dockerRepository = extension.repositoryName
        }
    }

    private void configureCleanupOldContainersTasks(Project project, DockerPluginExtension extension) {
        project.tasks.withType(CleanupOldContainersTask) { task ->
            task.dependingContainers = extension.dependingContainers
            task.dockerHost = extension.dockerHost
        }
    }
}
