package com.devbliss.docker

import com.devbliss.docker.task.AbstractDockerTask
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
  }
}

