scalaVersion := "2.11.12"

val spinalVersion = "1.6.1"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)


libraryDependencies ++= Seq(spinalCore, spinalLib, spinalIdslPlugin)
