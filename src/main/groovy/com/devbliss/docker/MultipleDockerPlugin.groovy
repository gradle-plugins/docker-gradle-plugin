package com.devbliss.docker

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import java.util.concurrent.Callable

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
class MultipleDockerPlugin implements Plugin<Project> {
	
  @Override
  public void apply(Project project) {
    project.getPlugins().apply()
  }
}

