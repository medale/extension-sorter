name := "extension-sorter"

version := "1.0"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0" withSources() withJavadoc()

libraryDependencies += "commons-io" % "commons-io" % "2.4" withSources() withJavadoc()

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test" 

libraryDependencies += "junit" % "junit" % "4.11" % "test"

EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.Unmanaged, EclipseCreateSrc.Source, EclipseCreateSrc.Resource)

mainClass in Compile := Some("com.uebercomputing.extsort.Sorter")
