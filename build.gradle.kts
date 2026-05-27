import org.jreleaser.model.Active

plugins {
  `java-library`
  `maven-publish`
  alias(libs.plugins.jreleaser)
  alias(libs.plugins.benmanes)
}

group = Metadata.GROUP
version = Metadata.VERSION
description = Metadata.DESCRIPTION



repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  api(libs.jspecify)
  testImplementation(platform(libs.junit.bom))
  testImplementation("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
}

tasks.jar {
  manifest {
    attributes("Automatic-Module-Name" to "${project.group}.${Metadata.PROJECT_NAME}")
  }
}

java {
  toolchain {
    languageVersion.set(libs.versions.java.map(JavaLanguageVersion::of))
  }
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
      url = uri(layout.buildDirectory.dir("staging-deployment"))
      name = "staging"
    }
  }

}

jreleaser {
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
    getAuthorsFromFile().forEach { author ->
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
      changelog {
        formatted = Active.ALWAYS
        preset = "conventional-commits"
      }
      sign = true
    }
  }
  deploy {

    maven {
      github {
        register("github-packages") {
          active = Active.ALWAYS
          url = "https://maven.pkg.github.com/${Metadata.PROJECT_OWNER}/${Metadata.PROJECT_NAME}"
          sourceJar = true
          javadocJar = true
          verifyPom = true
          checksums = true
          repository = Metadata.PROJECT_NAME
          stagingRepository("build/staging-deployment")
        }
      }
    }
  }
}

