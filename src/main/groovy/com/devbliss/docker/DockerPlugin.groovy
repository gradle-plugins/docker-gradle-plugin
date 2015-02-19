package com.devbliss.docker

import com.devbliss.docker.task.*
import de.gesellix.gradle.docker.DockerPlugin as ParentDockerPlugin
import de.gesellix.gradle.docker.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Devbliss Docker plugin to create a set of default docker tasks for a project.
 * The plugin extends the DockerPlugin from gesellix with additional tasks and configuration for devbliss needs.
 */
class DockerPlugin implements Plugin<Project> {

    private DockerPluginExtension devblissDockerExtension
    private GetServiceDependenciesTask getServiceDependenciesTask
    private StartDependenciesTask startDependenciesTask
    private CleanupOldContainersTask cleanupOldContainersTask
    private PullDependingImagesTask pullDependencyImages
    private BuildAndPushDockerImageTask buildAndPushDockerImage
    private DockerBuildTask dockerBuildTask
    private Task bootRepackageTask

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(ParentDockerPlugin)

        devblissDockerExtension = project.extensions.create(Constant.GRADLE_EXTENSION__DEVBLISS_DOCKER, DockerPluginExtension)

        bootRepackageTask = project.getTasks().findByPath(Constant.TASK_NAME__BOOT_REPACKAGE)

        dockerBuildTask = project.task(Constant.TASK_NAME__BUILD_DOCKER_IMAGE, type: DockerBuildTask)
        pullDependencyImages = project.task(Constant.TASK_NAME__PULL_DEPENDENCY_IMAGES, type: PullDependingImagesTask)
        startDependenciesTask = project.task(Constant.TASK_NAME__START_DEPENDENCIES, type: StartDependenciesTask)
        buildAndPushDockerImage = project.task(Constant.TASK_NAME__BUILD_AND_PUSH_DOCKER_IMAGE, type: BuildAndPushDockerImageTask)
        cleanupOldContainersTask = project.task(Constant.TASK_NAME__CLEANUP_OLD_CONTAINERS, type: CleanupOldContainersTask)
        getServiceDependenciesTask = project.task(Constant.TASK_NAME__GET_SERVICE_DEPENDENCIES, type: GetServiceDependenciesTask)

        project.task(Constant.TASK_NAME__STOP_ALL_RUNNING_CONTAINERS, type: StopAllRunningContainersTask)
        project.task(Constant.TASK_NAME__PULL_DOCKER_IMAGE, type: DockerPullTask)
        project.task(Constant.TASK_NAME__PUSH_DOCKER_IMAGE, type: DockerPushTask)
        project.task(Constant.TASK_NAME__STOP_DOCKER_CONTAINER, type: DockerStopTask)
        project.task(Constant.TASK_NAME__START_DOCKER_CONTAINER, type: DockerStartTask)
        project.task(Constant.TASK_NAME__RUN_DOCKER_CONTAINER, type: DockerRunTask)
        project.task(Constant.TASK_NAME__REMOVE_DOCKER_CONTAINER, type: DockerRmTask)

        configureTaskDependsOnStartDependencies()
        configureTaskDependsOnBuildAndPushDockerImage()
        configureTaskDependsOnBuildDockerImage()

        setAllPropertiesWithConfigurationForTasks(project)
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

    private void configureTaskDependsOnBuildDockerImage() {
        if (bootRepackageTask != null) {
            dockerBuildTask.dependsOn(Constant.TASK_NAME__BOOT_REPACKAGE)
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
