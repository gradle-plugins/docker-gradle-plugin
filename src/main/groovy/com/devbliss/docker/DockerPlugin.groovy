package com.devbliss.docker

  import de.gesellix.gradle.docker.DockerPlugin as ParenteDockerPlugin
import de.gesellix.gradle.docker.tasks.DockerPushTask

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

    def devblissDockerExtension = project.extensions.create('devblissDocker', DockerPluginExtension)


    DockerPullTask dockerPullTask = project.task('pullDockerImage', type: DockerPullTask)
    DockerPushTask dockerPushTask = project.task('pushDockerImage', type: DockerPushTask)
    DockerBuildTask dockerBuildTask = project.task('buildDockerImage', type: DockerBuildTask)
    DockerStopTask dockerStopTask = project.task('stopDockerContainer', type: DockerStopTask)
    DockerStartTask dockerStartTask = project.task('startDockerContainer', type: DockerStartTask)
    DockerRunTask dockerRunTask = project.task('runDockerContainer', type: DockerRunTask)
    DockerRmTask dockerRmTask = project.task('removeDockerContainer', type: DockerRmTask)
    DockerPsTask dockerPsTask = project.task('startDependencies', type: DockerPsTask)

    /*dockerPullTask.dependsOn()*/

    project.afterEvaluate {
      project.tasks.withType(AbstractDockerTask) { task ->
        task.dockerHost = devblissDockerExtension.dockerHost
        task.authConfigPlain = devblissDockerExtension.authConfigPlain
        task.authConfigEncoded = devblissDockerExtension.authConfigEncoded
      }

      project.tasks.withType(DockerPullTask) { task ->
        task.registry = devblissDockerExtension.registryName
        task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName
        task.tag = devblissDockerExtension.tag
      }

      project.tasks.withType(DockerPushTask) { task ->
        task.registry = devblissDockerExtension.registryName
        /*task.imageName = devblissDockerExtension.repositoryName + '/' + devblissDockerExtension.imageName*/
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
