name := """PlaySilhouetteRestSlick"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

val silhouetteVer = "4.0.0"

libraryDependencies ++= Seq(
  ws,
  filters,
  "com.typesafe.play"       %% "play-slick"                       % "2.0.0",
  "com.typesafe.play"       %% "play-slick-evolutions"            % "2.0.0",
  "org.postgresql"          %  "postgresql"                       % "42.1.1",
  "net.codingwell"          %% "scala-guice"                      % "4.1.0",
  "com.iheart"              %% "ficus"                            % "1.2.6",        // config lib, used by Silhouette,
  "com.mohiva"              %% "play-silhouette"                  % silhouetteVer,
  "com.mohiva"              %% "play-silhouette-password-bcrypt"  % silhouetteVer,
  "com.mohiva"              %% "play-silhouette-crypto-jca"       % silhouetteVer,
  "com.mohiva"              %% "play-silhouette-persistence"      % silhouetteVer,
  "com.mohiva"              %% "play-silhouette-testkit"          % silhouetteVer   % "test",
  "org.scalatestplus.play"  %% "scalatestplus-play"               % "1.5.1"         % Test,
  "org.slf4j"               %  "slf4j-nop"                        % "1.6.4"
)

resolvers += Resolver.jcenterRepo

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"