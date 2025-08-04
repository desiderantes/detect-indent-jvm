import org.jreleaser.model.Active

plugins {
  kotlin("jvm") version libs.versions.kotlin
  `java-library`
  `maven-publish`
  alias(libs.plugins.jreleaser)
  alias(libs.plugins.dokka.javadoc)
}

group = Metadata.GROUP
version = Metadata.VERSION
description = Metadata.DESCRIPTION



repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
  testImplementation(libs.kotest.junit5)
}

tasks.test {
  useJUnitPlatform()
}

tasks.jar {
  manifest {
    attributes("Automatic-Module-Name" to "${project.group}.detect-indent")
  }
}

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = Metadata.GROUP
      artifactId = Metadata.ARTIFACT_ID
      version = Metadata.VERSION

      from(components["java"])

      pom {
        name.set(project.name)
        description.set(Metadata.DESCRIPTION)
        url.set(Metadata.Links.WEBSITE)
        inceptionYear.set(Metadata.INCEPTION_YEAR)
        organization {
          name.set(Metadata.VENDOR)
          url.set(Metadata.Links.WEBSITE)
        }
        issueManagement {
          system.set("GitHub")
          url.set(Metadata.Links.ISSUE_TRACKER)
        }
        licenses {
          license {
            name.set(Metadata.License.NAME)
            url.set(Metadata.License.URL)
            distribution.set("repo")
          }
        }
        scm {
          connection.set(Metadata.Links.SCM_CONNECTION)
          developerConnection.set(Metadata.Links.SCM_DEVELOPER_CONNECTION)
          url.set(Metadata.Links.SCM)
        }
        developers {
          getAuthorsFromFile().map { author ->
            developer {
              id.set(author.id)
              name.set(author.name)
              email.set(author.email)
              roles.set(author.roles)
              url.set(author.website)
            }
          }
        }
      }
    }
  }
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/desiderantes/detect-indent")
      credentials {
        username = project.findProperty("gpr.user") as String?
        password = project.findProperty("gpr.key") as String?
      }
    }

    mavenCentral()
  }

}

jreleaser {
  dryrun = true
  project {
    name = Metadata.PROJECT_NAME
    version = Metadata.VERSION
    description = Metadata.DESCRIPTION
    inceptionYear = Metadata.INCEPTION_YEAR
    vendor = Metadata.VENDOR
    license = Metadata.License.NAME
    languages {
      java {
        version = libs.versions.java
      }
    }
    links {
      homepage = Metadata.Links.WEBSITE
      license = Metadata.License.URL
      bugTracker = Metadata.Links.ISSUE_TRACKER
      contribute = Metadata.Links.SCM
    }
    getAuthorsFromFile().map { author ->
      author(author.name)
    }
  }
  signing {
    active = Active.ALWAYS
    armored = true
  }

  release {
    github {
      checksums = true
      sign = true
    }
  }
  deploy {
    maven {
      mavenCentral {
        register("sonatype") {
          active = Active.ALWAYS
          url = "https://central.sonatype.com/api/v1/publisher"
          stagingRepository("target/staging-deploy")
        }
      }
    }
  }
}

