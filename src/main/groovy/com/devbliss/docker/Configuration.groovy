package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.tasks.*
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 *
 * Configuration class that applies the devblissDocker configuration to alle docker tasks of known type.
 */
class Configuration {

  /**
   * Adds default tasks and applies configuration to the project.
   * Added tasts are startServiceDependencies and stopAllRunningContainers to handle depending services
   * and buildAndPushDockerImage to publish a new image for local dev setup.
   */
  public Configuration(Project project) {
    def devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)

    project.task("startServiceDependencies", type: StartDependenciesTask)
    project.task("stopAllRunningContainers", type: StopAllRunningContainersTask)

    BuildAndPushDockerImageTask buildAndPushDockerImage = project.task('buildAndPushDockerImage', type:
      BuildAndPushDockerImageTask)
    Task bootRepackageTask = project.getTasks().findByPath('bootRepackage');
    if (bootRepackageTask != null) {
      buildAndPushDockerImage.dependsOn('bootRepackage')
    }
    buildAndPushDockerImage.dependsOn('buildDockerImage')

    configureAllAbstractTasks(project, devblissDockerExtension)
    configurePullTasks(project, devblissDockerExtension)
    configurePushTasks(project, devblissDockerExtension)
    configureBuildTasks(project, devblissDockerExtension)
    configureStopTasks(project, devblissDockerExtension)
    configureStartTasks(project, devblissDockerExtension)
    configureRmTasks(project, devblissDockerExtension)
    configureRunTasks(project, devblissDockerExtension)
  }

  /**
   * Set configuration for all Tasks that are type of AbstractDockerTask or extend it.
   */
  public void configureAllAbstractTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(AbstractDockerTask) { task ->
      task.dockerHost = extension.dockerHost
      task.authConfigPlain = extension.authConfigPlain
      task.authConfigEncoded = extension.authConfigEncoded
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerPullTask or extend it.
   */
  public void configurePullTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerPullTask) { task ->
      task.registry = extension.registryName
      task.imageName = extension.repositoryName + '/' + extension.imageName
      task.tag = extension.versionTag
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerPushTask or extend it.
   */
  public void configurePushTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerPushTask) { task ->
      task.registry = extension.registryName
      task.repositoryName = extension.repositoryName + '/' + extension.imageName
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerBuildTask or extend it.
   */
  public void configureBuildTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerBuildTask) { task ->
      task.buildContextDirectory = extension.buildContextDirectory
      task.imageName = extension.repositoryName + '/' + extension.imageName
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerStopTask or extend it.
   */
  public void configureStopTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerStopTask) { task ->
      task.containerId = extension.imageName
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerStartTask or extend it.
   */
  public void configureStartTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerStartTask) { task ->
      task.containerId = extension.imageName
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerRmTask or extend it.
   */
  public void configureRmTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerRmTask) { task ->
      task.containerId = extension.imageName
    }
  }

  /**
   * Set configuration for all Tasks that are type of DockerRunTask or extend it.
   */
  public void configureRunTasks(Project project, DockerPluginExtension extension) {
    project.tasks.withType(DockerRunTask) { task ->
      task.containerName = extension.imageName
      task.imageName = extension.registryName + '/' + extension.repositoryName + '/' + extension.imageName
    }
  }
}
