package com.devbliss.docker

import com.devbliss.docker.task.StartDependenciesTask
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin implements Plugin<Project> {

  def existingContainers = []

  def void apply(Project project) {
    project.getPlugins().apply('de.gesellix.docker')

    project.task("startDependencies",
            description: 'Runs all configured docker containers',
            group: 'Docker',
            type: StartDependenciesTask
    )

    project.tasks.withType(AbstractDockerTask) { task ->
      task.existingContainers = existingContainers
    }
  }
}

