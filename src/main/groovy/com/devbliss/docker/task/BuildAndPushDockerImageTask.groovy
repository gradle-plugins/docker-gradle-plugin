package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 14.01.15.
 */
class BuildAndPushDockerImageTask extends AbstractDockerTask {

  BuildAndPushDockerImageTask() {
    description = "Build and Push your images and start depending containers for this Project"
    group = "Devbliss"
  }

  @TaskAction
  public void run() {

  }
}
