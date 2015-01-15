package com.devbliss.docker

import de.gesellix.gradle.docker.DockerPlugin as ParentDockerPlugin
import de.gesellix.gradle.docker.tasks.*
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

    DockerPsTask dockerPsTask = project.task('startDependencies', type: DockerPsTask)
    DockerPullTask dockerPullTask = project.task('pullDockerImage', type: DockerPullTask)
    DockerPushTask dockerPushTask = project.task('pushDockerImage', type: DockerPushTask)
    DockerStopTask dockerStopTask = project.task('stopDockerContainer', type: DockerStopTask)
    DockerStartTask dockerStartTask = project.task('startDockerContainer', type: DockerStartTask)
    DockerRunTask dockerRunTask = project.task('runDockerContainer', type: DockerRunTask)
    DockerRmTask dockerRmTask = project.task('removeDockerContainer', type: DockerRmTask)

    //Tasks that depends on other tasks
    DockerBuildTask dockerBuildTask = project.task('buildDockerImage', type: DockerBuildTask)
    dockerBuildTask.dependsOn('bootRepackage')
  }
}
