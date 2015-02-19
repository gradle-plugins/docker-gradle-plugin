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

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(ParentDockerPlugin)

        project.task(Constant.TASK_NAME__START_DEPENDENCIES, type: StartDependenciesTask)
        project.task(Constant.TASK_NAME__STOP_ALL_RUNNING_CONTAINERS, type: StopAllRunningContainersTask)
        project.task(Constant.TASK_NAME__GET_SERVICE_DEPENDENCIES, type: GetServiceDependenciesTask)
        project.task(Constant.TASK_NAME__BUILD_AND_PUSH_DOCKER_IMAGE, type: BuildAndPushDockerImageTask)
        project.task(Constant.TASK_NAME__PULL_DOCKER_IMAGE, type: DockerPullTask)
        project.task(Constant.TASK_NAME__PUSH_DOCKER_IMAGE, type: DockerPushTask)
        project.task(Constant.TASK_NAME__STOP_DOCKER_CONTAINER, type: DockerStopTask)
        project.task(Constant.TASK_NAME__START_DOCKER_CONTAINER, type: DockerStartTask)
        project.task(Constant.TASK_NAME__RUN_DOCKER_CONTAINER, type: DockerRunTask)
        project.task(Constant.TASK_NAME__REMOVE_DOCKER_CONTAINER, type: DockerRmTask)
        project.task(Constant.TASK_NAME__PULL_DEPENDENCY_IMAGES, type: PullDependingImagesTask)
        project.task(Constant.TASK_NAME__CLEANUP_OLD_CONTAINERS, type: CleanupOldContainersTask)

        DockerBuildTask dockerBuildTask = project.task(Constant.TASK_NAME__BUILD_DOCKER_IMAGE, type: DockerBuildTask)

        Configuration configuration = new Configuration(project)

        //Tasks that depend on other tasks
        Task bootRepackageTask = project.getTasks().findByPath(Constant.TASK_NAME__BOOT_REPACKAGE);
        if (bootRepackageTask != null) {
            dockerBuildTask.dependsOn(Constant.TASK_NAME__BOOT_REPACKAGE)
        }
    }
}
