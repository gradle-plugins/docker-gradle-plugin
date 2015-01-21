package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.DockerPlugin as ParentDockerPlugin
import de.gesellix.gradle.docker.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 *
 * Devbliss Docker plugin to create a set of default docker tasks for a project.
 * The plugin extends the DockerPlugin from gesellix with additional tasks and configuration for devbliss needs.
 */
class DockerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(ParentDockerPlugin)

    project.task("startDependencies", type: StartDependenciesTask)
    project.task("stopAllRunningContainers", type: StopAllRunningContainersTask)
    BuildAndPushDockerImageTask buildAndPushDockerImage = project.task('buildAndPushDockerImage', type: BuildAndPushDockerImageTask)
    DockerPullTask dockerPullTask = project.task('pullDockerImage', type: DockerPullTask)
    DockerPushTask dockerPushTask = project.task('pushDockerImage', type: DockerPushTask)
    DockerStopTask dockerStopTask = project.task('stopDockerContainer', type: DockerStopTask)
    DockerStartTask dockerStartTask = project.task('startDockerContainer', type: DockerStartTask)
    DockerRunTask dockerRunTask = project.task('runDockerContainer', type: DockerRunTask)
    DockerRmTask dockerRmTask = project.task('removeDockerContainer', type: DockerRmTask)
    DockerBuildTask dockerBuildTask = project.task('buildDockerImage', type: DockerBuildTask)
    
    Configuration configuration = new Configuration(project)

    //Tasks that depend on other tasks
    Task bootRepackageTask = project.getTasks().findByPath('bootRepackage');
    if (bootRepackageTask != null) {
      dockerBuildTask.dependsOn('bootRepackage')
    }
  }
}
