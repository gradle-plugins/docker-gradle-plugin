package com.devbliss.docker

import de.gesellix.gradle.docker.DockerPlugin as ParenteDockerPlugin
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
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

  private static Logger logger = LoggerFactory.getLogger(DockerPlugin)

  @Override
  public void apply(Project project) {

    project.getPlugins().apply(ParenteDockerPlugin)

    def extension = project.extensions.create('devblissDocker', DockerPluginExtension)


    project.task('pullDockerImage', type: DockerPullTask)
    project.task('pushDockerImage', type: DockerPushTask)
    project.task('buildDockerImage', type: DockerBuildTask)
    project.task('stopDockerContainer', type: DockerStopTask)
    project.task('startDockerContainer', type: DockerStartTask)
    project.task('runDockerContainer', type: DockerRunTask)
    project.task('removeDockerContainer', type: DockerRmTask)
    project.task('startDependencies', type: DockerPsTask)

    project.afterEvaluate {
      project.tasks.withType(AbstractDockerTask) { task ->
        task.dockerHost = extension.dockerHost
        task.authConfigPlain = extension.authConfigPlain
        task.authConfigEncoded = extension.authConfigEncoded
      }

      project.tasks.withType(DockerPullTask) { task ->
        task.registry = extension.registryName
        task.imageName = extension.repositoryName + '/' + extension.imageName
        task.tag = extension.tag
      }

      project.tasks.withType(DockerPushTask) { task ->
        task.registry = extension.registryName
        //task.imageName = extension.repositoryName + '/' + extension.imageName
      }

      project.tasks.withType(DockerBuildTask) { task ->
        task.buildContextDirectory = extension.buildContextDirectory
        task.imageName = extension.repositoryName + '/' + extension.imageName
      }

      project.tasks.withType(DockerStopTask) { task ->
        task.containerId = extension.imageName
      }

      project.tasks.withType(DockerStartTask) { task ->
        task.containerId = extension.imageName
      }

      project.tasks.withType(DockerRmTask) { task ->
        task.containerId = extension.imageName
      }

      project.tasks.withType(DockerRunTask) { task ->
        task.containerName = extension.imageName
        task.imageName = extension.registryName + '/' + extension.repositoryName + '/' + extension.imageName
      }
      }
  }
}
