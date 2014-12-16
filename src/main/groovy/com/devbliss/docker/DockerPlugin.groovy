package com.devbliss.docker

import com.devbliss.docker.task.AbstractDockerTask
import com.devbliss.docker.task.DockerImagesTask
import com.devbliss.docker.task.DockerPsTask
import com.devbliss.docker.task.DockerPullTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    def extension = project.extensions.create('docker', DockerPluginExtension)

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

    project.task('pullDockerImage', type: DockerPullTask)
    project.task('psDockerContainers', type: DockerPsTask)
    project.task('showDockerImages', type: DockerImagesTask)
  }
}

