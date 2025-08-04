import com.typesafe.config.ConfigFactory
import org.gradle.api.Project

object Metadata {
  const val GROUP = "com.desiderantes"
  const val ARTIFACT_ID = "detect-indent"
  const val VERSION = "1.0.0"
  const val PROJECT_NAME = "detect-indent"
  const val PROJECT_OWNER = "desiderantes"
  const val DESCRIPTION = "A simple library to detect the indentation of a file"
  const val INCEPTION_YEAR = "2025"
  const val VENDOR = "desiderantes"

  val IS_SNAPSHOT = VERSION.endsWith("-SNAPSHOT")


  object Links {
    const val HOST = "github.com"
    const val WEBSITE = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME"
    const val ISSUE_TRACKER = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME/issues"
    const val SCM = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME.git"
    const val PROJECT = "https://$HOST/$PROJECT_OWNER/$PROJECT_NAME"
    const val SCM_CONNECTION = "scm:git:https://$HOST/$PROJECT_OWNER/$PROJECT_NAME.git"
    const val SCM_DEVELOPER_CONNECTION = "scm:git:git@$HOST:$PROJECT_OWNER/$PROJECT_NAME.git"
  }

  object License {
    const val NAME = "MIT"
    const val URL = "https://opensource.org/licenses/MIT"
  }

  object Repositories {
    val GITHUB = Repository(
      name = "GitHubPackages",
      url = "https://maven.pkg.github.com/$PROJECT_OWNER/$PROJECT_NAME",
      prefix = "GITHUB_"
    )
    val SONATYPE = Repository(
      name = "Sonatype",
      url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/",
      prefix = "SONATYPE_"
    )
  }
}

data class Repository(val name: String, val url: String, val prefix: String)

data class Author(
  val id: String,
  val name: String,
  val email: String,
  val website: String = "",
  val roles: List<String> = listOf(),
  val notes: String = ""
)

fun Project.getAuthorsFromFile(): List<Author> {
  ConfigFactory.parseFile(rootProject.layout.projectDirectory.file("AUTHORS.hocon").asFile).let {
    val authors = it.getConfig("authors")
    return it.getObject("authors").keys.map { key ->
      Author(
        id = key,
        name = authors.getConfig(key).getString("name"),
        email = authors.getConfig(key).getString("email"),
        website = authors.getConfig(key).getString("website"),
        roles = authors.getConfig(key).getStringList("roles"),
        notes = authors.getConfig(key).getString("notes")
      )
    }
  }
}