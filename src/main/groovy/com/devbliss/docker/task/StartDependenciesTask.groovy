package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.swing.impl.DefaultAction
import org.apache.log4j.spi.LoggerFactory
import org.gradle.api.DefaultTask
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction

import java.util.logging.Logger

class StartDependenciesTask extends AbstractDockerTask {

  def existingContainers = []

  StartDependenciesTask() {}

  @TaskAction
  def start() {
    System.out.println("Starting docker containers...")
  }
}

