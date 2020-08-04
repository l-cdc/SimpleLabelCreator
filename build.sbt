name := "SimpleLabelCreator"
 
version := "0.2.0"

maintainer := "Lorenzo Costanzia di Costigliole <lorenzo.costanzia@gmail.com>"
      
lazy val `labelCreator` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.12"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
libraryDependencies += "com.itextpdf" % "itext7-core" % "7.1.12"

unmanagedResourceDirectories in Test += { baseDirectory ( _ /"target/web/public/test" ).value }

      