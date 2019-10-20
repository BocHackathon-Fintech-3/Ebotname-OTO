import sbt.Keys.sLog
import sbt.Project
import com.typesafe.sbt.SbtGit.git

object ApplicationVersionSettings {

  def get(project: Project) = {
    project.settings(
      git.formattedShaVersion := git.gitHeadCommit.value map { sha => s"v$sha" },
      git.useGitDescribe := true,
      git.gitTagToVersionNumber := { tag: String =>
        val log = sLog.value
        log.info(s"Tag Version:$tag")
        if (tag matches "[0-9]+\\..*") Some(tag)
        else None
      }
    )
  }

}
