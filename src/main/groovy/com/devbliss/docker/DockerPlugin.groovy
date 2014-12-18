package com.devbliss.docker

import com.devbliss.docker.task.AbstractDockerTask
import com.devbliss.docker.task.DockerBuildTask
import com.devbliss.docker.task.DockerPullTask
import com.devbliss.docker.task.DockerPushTask
import com.devbliss.docker.task.DockerRmTask
import com.devbliss.docker.task.DockerRunTask
import com.devbliss.docker.task.DockerStartTask
import com.devbliss.docker.task.DockerStopTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DockerPlugin implements Plugin<Project> {

  private static Logger logger = LoggerFactory.getLogger(DockerPlugin)

  @Override
  public void apply(Project project) {
    def extension = project.extensions.create('docker', DockerPluginExtension)

    project.task('pullDockerImage', type: DockerPullTask)
    project.task('pushDockerImage', type: DockerPushTask)
    project.task('buildDockerImage', type: DockerBuildTask)
    project.task('stopContainer', type: DockerStopTask)
    project.task('startContainer', type: DockerStartTask)
    project.task('runContainer', type: DockerRunTask)
    project.task('removeContainer', type: DockerRmTask)


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
        task.imageName = extension.repositoryName + '/' + extension.imageName
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
