package com.devbliss.docker.task

import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Tar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.gradle.api.tasks.bundling.Compression.GZIP

class DockerBuildTask extends AbstractDockerTask {

  private static Logger logger = LoggerFactory.getLogger(DockerBuildTask)

  @Input
  @Optional
  def imageName
  @Input
  @Optional
  def buildContext
  @InputDirectory
  @Optional
  File buildContextDirectory

  def tarOfBuildcontextTask

  DockerBuildTask() {
    super("builds an image from the given build context")
  }

  @Override
  Task configure(Closure closure) {
    def configureResult = super.configure(closure)
    if (getBuildContextDirectory()) {
      tarOfBuildcontextTask = project.task(["type": Tar], "tarOfBuildcontext") {
        description = "creates a tar of the buildcontext"
        from getBuildContextDirectory()
        compression = GZIP
        baseName = "buildContext_${getNormalizedImageName()}"
        destinationDir getTemporaryDir()
      }
      tarOfBuildcontextTask.exclude {
        it.file == tarOfBuildcontextTask.archivePath
      }
      dependsOn tarOfBuildcontextTask
    }
    return configureResult
  }

  @TaskAction
  def build() {
    logger.info "running build..."

    if (getBuildContextDirectory()) {
      // only one of buildContext and buildContextDirectory shall be provided
      assert !getBuildContext()

      assert tarOfBuildcontextTask
      logger.info "temporary buildContext: ${tarOfBuildcontextTask.archivePath}"
      buildContext = new FileInputStream(tarOfBuildcontextTask.archivePath as File)
    }

    // at this point we need the buildContext
    assert getBuildContext()

    def imageId = getDockerClient().build(getBuildContext())
    if (getImageName()) {
      logger.info "tag $imageId as '${getImageName()}'..."
      getDockerClient().tag(imageId, getImageName())
    }
    return imageId
  }

  def getNormalizedImageName() {
    if (!getImageName()) {
      return UUID.randomUUID().toString()
    }
    return getImageName().replaceAll("\\W", "_")
  }
}
