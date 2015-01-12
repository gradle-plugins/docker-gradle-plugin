package com.devbliss.docker

import com.devbliss.docker.task.StartDependenciesTask
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import de.gesellix.gradle.docker.tasks.DockerBuildTask
import de.gesellix.gradle.docker.tasks.DockerPullTask
import de.gesellix.gradle.docker.tasks.DockerPushTask
import de.gesellix.gradle.docker.tasks.DockerRmTask
import de.gesellix.gradle.docker.tasks.DockerRunTask
import de.gesellix.gradle.docker.tasks.DockerStartTask
import de.gesellix.gradle.docker.tasks.DockerStopTask
import org.gradle.api.Project

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 */
class Configuration {

  public Configuration(Project project)
  {
    def devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)

    project.afterEvaluate {
      project.task("startDependenciesDevbliss", type: StartDependenciesTask)

      project.tasks.withType(AbstractDockerTask) { task ->
        task.dockerHost = devblissDockerExtension.dockerHost
        task.authConfigPlain = devblissDockerExtension.authConfigPlain
        task.authConfigEncoded = devblissDockerExtension.authConfigEncoded
      }

      project.tasks.withType(DockerPullTask) { task ->
        task.registry = devblissDockerExtension.registryName
        task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
        task.tag = devblissDockerExtension.versionTag
      }

      project.tasks.withType(DockerPushTask) { task ->
        task.registry = devblissDockerExtension.registryName
        task.repositoryName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
      }

      project.tasks.withType(DockerBuildTask) { task ->
        task.buildContextDirectory = devblissDockerExtension.buildContextDirectory
        task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
      }

      project.tasks.withType(DockerStopTask) { task ->
        task.containerId = devblissDockerExtension.imageName
      }

      project.tasks.withType(DockerStartTask) { task ->
        task.containerId = devblissDockerExtension.imageName
      }

      project.tasks.withType(DockerRmTask) { task ->
        task.containerId = devblissDockerExtension.imageName
      }

      project.tasks.withType(DockerRunTask) { task ->
        task.containerName = devblissDockerExtension.imageName
        task.imageName = devblissDockerExtension.registryName + '/' + devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
      }
    }
  }
}
