package com.devbliss.docker

import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply('de.gesellix.docker')
  }
}

