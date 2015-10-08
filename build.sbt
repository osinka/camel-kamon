organization := "com.osinka.camel"

name := "camel-kamon"

homepage := Some(url("https://github.com/osinka/camel-kamon"))

startYear := Some(2015)

scalaVersion := "2.11.7"

licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

organizationName := "Osinka"

description := """Kamon metrics and traces for Apache Camel routes, processors"""

scalacOptions ++= List("-deprecation", "-unchecked", "-feature")

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "+q", "-v")

val CamelVer = "2.15.3"

val KamonVer = "0.5.2"

libraryDependencies ++= Seq(
  "org.apache.camel"          %  "camel-core"             % CamelVer,
  "io.kamon"                  %% "kamon-core"             % KamonVer,
  "org.scalatest"             %% "scalatest"              % "2.2.5"   % "test",
  "com.novocode"              %  "junit-interface"        % "0.11"    % "test",
  "org.apache.camel"          %  "camel-test"             % CamelVer  % "test",
  "io.kamon"                  %% "kamon-log-reporter"     % KamonVer  % "test",
  "org.slf4j"                 %  "slf4j-simple"           % "1.7.12"  % "test"
)

credentials += Credentials(Path.userHome / ".ivy2/credentials_sonatype")

pomIncludeRepository := { x => false }

publishTo <<= (version) { version: String =>
  Some(
    if (version.trim endsWith "SNAPSHOT")
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    else
      "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
  )
}

useGpg := true

pomExtra := <xml:group>
    <developers>
      <developer>
        <id>alaz</id>
        <email>azarov@osinka.com</email>
        <name>Alexander Azarov</name>
        <timezone>+3</timezone>
      </developer>
    </developers>
    <scm>
      <connection>scm:git:git://github.com/osinka/camel-kamon.git</connection>
      <developerConnection>scm:git:git@github.com:osinka/camel-kamon.git</developerConnection>
      <url>http://github.com/osinka/camel-kamon</url>
    </scm>
    <issueManagement>
      <system>github</system>
      <url>http://github.com/osinka/camel-kamon/issues</url>
    </issueManagement>
  </xml:group>
