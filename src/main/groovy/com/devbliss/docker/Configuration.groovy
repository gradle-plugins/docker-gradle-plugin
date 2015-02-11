package com.devbliss.docker

import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.task.CleanupOldContainersTask
import com.devbliss.docker.task.PullDependencyImages
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.StopAllRunningContainersTask
import de.gesellix.gradle.docker.tasks.*
import java.util.concurrent.Callable
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @todo: Autor steht in Git -> Autor entfernen
 * @author Christian Soth <christian.soth@devbliss.com> on 12.01.15.
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 *
 * Configuration class that applies the devblissDocker configuration to all docker tasks of known type.
 */
class Configuration {

  // TODO: mein pattern für sowas ist: TASK_NAME__START_DEPENDENCIES
  // nächste wäre dann z.B. TASK_NAME__PULL_DEPENDENCY_IMAGES
  public static final String TASK_NAME_START_DEPENDENCIES = "startDependencies";
  // TODO: upper case und so, weißte ja schon
  public static final String dockerAlreadyHandledProperty = "docker.alreadyHandled";

  /**
   * Applies configuration to the project.
   * Added tasks are startDependencies and stopAllRunningContainers to handle depending services
   * and buildAndPushDockerImage to publish a new image for local dev setup.
   */
  public Configuration(Project project) {
    DockerPluginExtension devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)
    // TODO: auslagern in getStartDependenciesTask
    StartDependenciesTask startDependenciesTask = project.getTasks().getByName(TASK_NAME_START_DEPENDENCIES)
    PullDependencyImages pullDependencyImages = project.getTasks().getByName('pullDependencyImages')
    CleanupOldContainersTask cleanupOldContainersTask = project.getTasks().getByName('cleanupOldContainers')
    startDependenciesTask.dependsOn cleanupOldContainersTask
    cleanupOldContainersTask.dependsOn pullDependencyImages

    // TODO: auslagern in sprechende methode
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
      configurePullDependencyImagesTasks(project, devblissDockerExtension)
      configureCleanupOldContainersTasks(project, devblissDockerExtension)
    }

    // TODO: warum steht das hier unten und nicht da oben, wie startDeps? -> auslagern in sprechende methode
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

  public void configurePullDependencyImagesTasks(Project project, DockerPluginExtension extension) {
      project.tasks.withType(PullDependencyImages) { task ->
      task.dependingContainers = extension.dependingContainers
      task.dockerHost = extension.dockerHost
      task.authConfigPlain = extension.authConfigPlain
      task.authConfigEncoded = extension.authConfigEncoded
      task.versionTag = extension.versionTag
      task.dockerRegistry = extension.registryName
      task.dockerRepository = extension.repositoryName
    }
  }

  public void configureCleanupOldContainersTasks(Project project, DockerPluginExtension extension) {
      project.tasks.withType(CleanupOldContainersTask) { task ->
      task.dependingContainers = extension.dependingContainers
      task.dockerHost = extension.dockerHost
    }
  }
}
