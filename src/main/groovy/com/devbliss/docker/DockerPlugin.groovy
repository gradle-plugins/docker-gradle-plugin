package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.CleanupOldContainersTask
import com.devbliss.docker.task.GetServiceDependenciesTask
import com.devbliss.docker.task.PullDependingImagesTask
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.DockerPlugin as ParentDockerPlugin
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import de.gesellix.gradle.docker.tasks.DockerBuildTask
import de.gesellix.gradle.docker.tasks.DockerPullTask
import de.gesellix.gradle.docker.tasks.DockerPushTask
import de.gesellix.gradle.docker.tasks.DockerRmTask
import de.gesellix.gradle.docker.tasks.DockerRunTask
import de.gesellix.gradle.docker.tasks.DockerStartTask
import de.gesellix.gradle.docker.tasks.DockerStopTask
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

        createDefaultTasks(project)
        configureDependsOn(project)

        setAllPropertiesWithConfigurationForTasks(project)
    }

    private void createDefaultTasks(Project project) {
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
    }

    private void configureDependsOn(Project project) {
        bootRepackageTask = project.getTasks().findByPath(Constant.TASK_NAME__BOOT_REPACKAGE)

        configureTaskDependsOnStartDependencies()
        configureTaskDependsOnBuildAndPushDockerImage()
        configureTaskDependsOnBuildDockerImage()
    }

    private void setAllPropertiesWithConfigurationForTasks(Project project) {
        project.afterEvaluate {
            configureStartServiceDependenciesTasks(startDependenciesTask)
            configureGetServiceDependenciesTasks(getServiceDependenciesTask)
            configureAllAbstractTasks(project)
            configurePullTasks(project)
            configurePushTasks(project)
            configureBuildTasks(project)
            configureStopTasks(project)
            configureStartTasks(project)
            configureRmTasks(project)
            configureRunTasks(project)
            configurePullDependingImagesTasks(project)
            configureCleanupOldContainersTasks(project)
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

    private void configureStartServiceDependenciesTasks(StartDependenciesTask startDependenciesTask) {
        startDependenciesTask.dependingContainers = devblissDockerExtension.dependingContainers
        startDependenciesTask.dockerHost = devblissDockerExtension.dockerHost
        startDependenciesTask.authConfigPlain = devblissDockerExtension.authConfigPlain
        startDependenciesTask.authConfigEncoded = devblissDockerExtension.authConfigEncoded
        startDependenciesTask.versionTag = devblissDockerExtension.versionTag
        startDependenciesTask.dockerRegistry = devblissDockerExtension.registryName
        startDependenciesTask.dockerRepository = devblissDockerExtension.repositoryName
    }

    private void configureGetServiceDependenciesTasks(GetServiceDependenciesTask getServiceDependenciesTask) {
        getServiceDependenciesTask.dependingContainers = devblissDockerExtension.dependingContainers
    }

    private void configureAllAbstractTasks(Project project) {
        project.tasks.withType(AbstractDockerTask) { task ->
            task.dockerHost = devblissDockerExtension.dockerHost
            task.authConfigPlain = devblissDockerExtension.authConfigPlain
            task.authConfigEncoded = devblissDockerExtension.authConfigEncoded
        }
    }

    private void configurePullTasks(Project project) {
        project.tasks.withType(DockerPullTask) { task ->
            task.registry = devblissDockerExtension.registryName
            task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
            task.tag = devblissDockerExtension.versionTag
        }
    }

    private void configurePushTasks(Project project) {
        project.tasks.withType(DockerPushTask) { task ->
            task.registry = devblissDockerExtension.registryName
            task.repositoryName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
        }
    }

    private void configureBuildTasks(Project project) {
        project.tasks.withType(DockerBuildTask) { task ->
            task.buildContextDirectory = devblissDockerExtension.buildContextDirectory
            task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
        }
    }

    private void configureStopTasks(Project project) {
        project.tasks.withType(DockerStopTask) { task ->
            task.containerId = devblissDockerExtension.imageName
        }
    }

    private void configureStartTasks(Project project) {
        project.tasks.withType(DockerStartTask) { task ->
            task.containerId = devblissDockerExtension.imageName
        }
    }

    private void configureRmTasks(Project project) {
        project.tasks.withType(DockerRmTask) { task ->
            task.containerId = devblissDockerExtension.imageName
        }
    }

    private void configureRunTasks(Project project) {
        project.tasks.withType(DockerRunTask) { task ->
            task.containerName = devblissDockerExtension.imageName
            task.imageName = devblissDockerExtension.registryName + '/' + devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
        }
    }

    private void configurePullDependingImagesTasks(Project project) {
        project.tasks.withType(PullDependingImagesTask) { task ->
            task.dependingContainers = devblissDockerExtension.dependingContainers
            task.dockerHost = devblissDockerExtension.dockerHost
            task.authConfigPlain = devblissDockerExtension.authConfigPlain
            task.authConfigEncoded = devblissDockerExtension.authConfigEncoded
            task.versionTag = devblissDockerExtension.versionTag
            task.dockerRegistry = devblissDockerExtension.registryName
            task.dockerRepository = devblissDockerExtension.repositoryName
        }
    }

    private void configureCleanupOldContainersTasks(Project project) {
        project.tasks.withType(CleanupOldContainersTask) { task ->
            task.dependingContainers = devblissDockerExtension.dependingContainers
            task.dockerHost = devblissDockerExtension.dockerHost
        }
    }
}
