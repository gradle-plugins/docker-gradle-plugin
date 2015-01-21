package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.tasks.*
import java.util.concurrent.Callable
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
   * Applies configuration to the project.
   * Added tasks are startDependencies and stopAllRunningContainers to handle depending services
   * and buildAndPushDockerImage to publish a new image for local dev setup.
   */
  public Configuration(Project project) {
    DockerPluginExtension devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)
    StartDependenciesTask startDependenciesTask = project.getTasks().getByName('startDependencies')
    
    project.afterEvaluate {
      configureStartServiceDependenciesTasks(startDependenciesTask, devblissDockerExtension)
      configureAllAbstractTasks(project, devblissDockerExtension)
      configurePullTasks(project, devblissDockerExtension)
      configurePushTasks(project, devblissDockerExtension)
      configureBuildTasks(project, devblissDockerExtension)
      configureStopTasks(project, devblissDockerExtension)
      configureStartTasks(project, devblissDockerExtension)
      configureRmTasks(project, devblissDockerExtension)
      configureRunTasks(project, devblissDockerExtension)
    }

    BuildAndPushDockerImageTask buildAndPushDockerImage = project.getTasks().getByName('buildAndPushDockerImage')
    Task bootRepackageTask = project.getTasks().findByPath('bootRepackage');
    if (bootRepackageTask != null) {
      buildAndPushDockerImage.dependsOn('bootRepackage')
    }
    buildAndPushDockerImage.dependsOn('buildDockerImage')
    buildAndPushDockerImage.finalizedBy('pushDockerImage')
  }

  /**
   * Set configuration for a StartDependenciesTask.
   */
  public void configureStartServiceDependenciesTasks(StartDependenciesTask startDependenciesTask, DockerPluginExtension extension) {
      startDependenciesTask.dependingContainers = extension.dependingContainers
      startDependenciesTask.dockerHost = extension.dockerHost
      startDependenciesTask.authConfigPlain = extension.authConfigPlain
      startDependenciesTask.authConfigEncoded = extension.authConfigEncoded
      startDependenciesTask.versionTag = extension.versionTag
      startDependenciesTask.dockerRegistry = extension.registryName
      startDependenciesTask.dockerRepository = extension.repositoryName
  }
  
  /**
   * Set configuration for all Tasks that are type of AbstractDockerTask.
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
