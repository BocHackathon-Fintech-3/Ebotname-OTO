import com.typesafe.sbt.SbtNativePackager.autoImport.{executableScriptName, packageName}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport.defaultLinuxInstallLocation
import sbt.Keys.version
import sbt.Project


object DockerConfigSettings {
  def get(project: Project) = {

    project.settings(
      packageName in Docker := packageName.value,
      version in Docker := version.value,
      dockerBaseImage := "openjdk:8-jdk-alpine",
      dockerCommands ++= Seq(
        // setting the run script executable
        ExecCmd("RUN",
          "chmod", "u+x",
          s"${(defaultLinuxInstallLocation in Docker).value}/bin/${executableScriptName.value}"),

        Cmd("LABEL", "maintainer", "threeel"),
        Cmd("USER", "daemon"),

        // Setting Enviroment
        Cmd("ENV", "APPLICATION_KEY", "VX2D3UtF6PmVwWpiTByTPH5qZZ7fPFcIXJUXUv55KhgzIGCEvxdlPeoDRTeb9Gqt"),

        // CRM DB Configurations
        Cmd("ENV", "BOC_CLIENT_ID", "051bda43-e3bd-45ff-bd3b-231b0f176e7c"),
        Cmd("ENV", "BOC_CLIENT_SECRET", "K7nG0oK7sR0uU8uQ1cB8hT8qH8xS5aT1sH6lD4dF3jO5mT6eR6"),
        Cmd("ENV", "BOC_APP_URL", "https://0524153a.ngrok.io")



      ),
      dockerExposedPorts := Seq(9000, 9443),
      dockerExposedVolumes := Seq(
        "/data", // Output of Uploaded Files
        "/app/conf", // Configurations of the app
        "/app/logs"
      ),
      dockerUpdateLatest := true,
      dockerRepository :=Some("registry.gitlab.com/threeel-projects/hackathons/oto-bank"),
      defaultLinuxInstallLocation in Docker := "/app"
      ,
      dockerEntrypoint in Docker := Seq(
        s"${(defaultLinuxInstallLocation in Docker).value}/bin/${executableScriptName.value}"
      )
    )
  }
}
