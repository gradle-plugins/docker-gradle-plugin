package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.tasks.*
import org.gradle.api.Project

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 */
class Configuration {

  public Configuration(Project project) {
    def devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)

    project.afterEvaluate {
      project.task("startServiceDependencies", type: StartDependenciesTask)
      project.task("stopAllRunningContainers", type: StopAllRunningContainersTask)

      BuildAndPushDockerImageTask buildAndPushDockerImage = project.task('buildAndPushDockerImage', type:
              BuildAndPushDockerImageTask)
      buildAndPushDockerImage.dependsOn('bootRepackage')
      buildAndPushDockerImage.dependsOn('buildDockerImage')
      buildAndPushDockerImage.dependsOn('pushDockerImage')

      def dockerHost = devblissDockerExtension.dockerHost
      def authConfigPlain = devblissDockerExtension.authConfigPlain
      def authConfigEncoded = devblissDockerExtension.authConfigEncoded
      def imageName = devblissDockerExtension.imageName
      def repositoryName = devblissDockerExtension.repositoryName
      def registryName = devblissDockerExtension.registryName
      def versionTag = devblissDockerExtension.versionTag
      def buildContextDirectory = devblissDockerExtension.buildContextDirectory

      project.tasks.withType(AbstractDockerTask) { task ->
        task.dockerHost = dockerHost
        task.authConfigPlain = authConfigPlain
        task.authConfigEncoded = authConfigEncoded
      }

      project.tasks.withType(DockerPullTask) { task ->
        task.registry = registryName
        task.imageName = repositoryName + '/' + imageName
        task.tag = versionTag
      }

      project.tasks.withType(DockerPushTask) { task ->
        task.registry = registryName
        task.repositoryName = repositoryName + '/' + imageName
      }

      project.tasks.withType(DockerBuildTask) { task ->
        task.buildContextDirectory = buildContextDirectory
        task.imageName = repositoryName + '/' + imageName
      }

      project.tasks.withType(DockerStopTask) { task ->
        task.containerId = imageName
      }

      project.tasks.withType(DockerStartTask) { task ->
        task.containerId = imageName
      }

      project.tasks.withType(DockerRmTask) { task ->
        task.containerId = imageName
      }

      project.tasks.withType(DockerRunTask) { task ->
        task.containerName = imageName
        task.imageName = registryName + '/' + repositoryName + '/' + imageName
      }
    }
  }
}
