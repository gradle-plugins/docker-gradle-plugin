package com.devbliss.docker

import com.devbliss.docker.task.StartDependenciesTask
import de.gesellix.gradle.docker.DockerPlugin as ParentDockerPlugin
import de.gesellix.gradle.docker.tasks.DockerBuildTask
import de.gesellix.gradle.docker.tasks.DockerPsTask
import de.gesellix.gradle.docker.tasks.DockerPullTask
import de.gesellix.gradle.docker.tasks.DockerPushTask
import de.gesellix.gradle.docker.tasks.DockerRmTask
import de.gesellix.gradle.docker.tasks.DockerRunTask
import de.gesellix.gradle.docker.tasks.DockerStartTask
import de.gesellix.gradle.docker.tasks.DockerStopTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerPlugin implements Plugin<Project> {

  private static Logger LOGGER = LoggerFactory.getLogger(DockerPlugin)

  @Override
  public void apply(Project project) {

    project.getPlugins().apply(ParentDockerPlugin)

    def Configuration configuration = new Configuration(project)

    //Tasks from gesellix docker plugin
    DockerPullTask dockerPullTask = project.task('pullDockerImage', type: DockerPullTask)
    DockerPushTask dockerPushTask = project.task('pushDockerImage', type: DockerPushTask)
    DockerBuildTask dockerBuildTask = project.task('buildDockerImage', type: DockerBuildTask)
    DockerStopTask dockerStopTask = project.task('stopDockerContainer', type: DockerStopTask)
    DockerStartTask dockerStartTask = project.task('startDockerContainer', type: DockerStartTask)
    DockerRunTask dockerRunTask = project.task('runDockerContainer', type: DockerRunTask)
    DockerRmTask dockerRmTask = project.task('removeDockerContainer', type: DockerRmTask)
    DockerPsTask dockerPsTask = project.task('startDependencies', type: DockerPsTask)

    //Tasks that depends on other tasks
    dockerBuildTask.dependsOn('bootRepackage')
  }
}
