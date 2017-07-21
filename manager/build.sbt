import java.util
import java.util.Collections

import com.spotify.docker.client.messages._
import com.spotify.docker.client._
import sbt.inc.Analysis

import scala.collection.JavaConverters._

name := "manager"

enablePlugins(DockerSpotifyClientPlugin)

lazy val dataBaseName = "docker"
lazy val dataBaseUser = "docker"
lazy val dataBasePassword = "docker"
lazy val dataBaseUrl = s"jdbc:postgresql://localhost:5432/$dataBaseName"

lazy val startDatabase = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val cli: DockerClient = DefaultDockerClient.fromEnv().build()

  cli.listContainers(DockerClient.ListContainersParam.allContainers(true)).asScala
    .filter(p => p.names().contains("/postgres_compile"))
    .foreach(p => cli.removeContainer(p.id(), DockerClient.RemoveContainerParam.forceKill(true)))

  val containerId = cli.createContainer(ContainerConfig.builder()
    .env(s"POSTGRES_DB=$dataBaseName", s"POSTGRES_USER=$dataBaseUser", s"POSTGRES_PASSWORD=$dataBasePassword")
    .hostConfig(HostConfig.builder()
      .portBindings(Collections.singletonMap("5432",
        util.Arrays.asList(PortBinding.of("0.0.0.0", 5432))))
      .build())
    .exposedPorts("5432")
    .image("postgres:9.6-alpine")
    .build(), "postgres_compile").id()
  cli.startContainer(containerId)
  Thread.sleep(10000)
  println(s"starting database...$containerId")
}


compile in Compile <<= (compile in Compile)
  .dependsOn(slickCodeGenTask)
  .dependsOn(flywayMigrate in migration)
  .dependsOn(startDatabase) map { analysis =>
  //Stop database
  val cli: DockerClient = DefaultDockerClient.fromEnv().build()
  cli.listContainers(DockerClient.ListContainersParam.allContainers(true)).asScala
    .filter(p => p.names().contains("/postgres_compile"))
    .foreach(p => cli.removeContainer(p.id(), DockerClient.RemoveContainerParam.forceKill(true)))

  Analysis.Empty
}

lazy val migration = project.settings(
  flywayUrl := dataBaseUrl,
  flywayUser := dataBaseUser,
  flywayPassword := dataBasePassword,
  flywaySchemas := Seq("hydro_serving"),
  flywayLocations := Seq(s"filesystem:${baseDirectory.value}/../src/main/resources/db/migration/"),
  libraryDependencies += "org.postgresql" % "postgresql" % "42.1.3" % "runtime"
)

//This helps to Idea
unmanagedSourceDirectories in Compile += sourceManaged.value

lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = dir.getPath
  val url = dataBaseUrl
  val jdbcDriver = "org.postgresql.Driver"
  val slickDriver = "io.hydrosphere.slick.HydrospherePostgresDriver"
  val pkg = "io.hydrosphere.serving.manager.db"
  toError(r.run("io.hydrosphere.slick.HydrosphereCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, dataBaseUser, dataBasePassword), s.log))
  println("Generated")
}
