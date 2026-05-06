@REM Maven Wrapper script for Windows
@SET MAVEN_PROJECTBASEDIR=%~dp0

@IF "%JAVA_HOME%" == "" (
  SET "JAVA_CMD=java"
) ELSE (
  SET "JAVA_CMD=%JAVA_HOME%\bin\java"
)

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
@SET WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

@IF NOT EXIST %WRAPPER_JAR% (
  @ECHO Downloading Maven Wrapper...
  @powershell -NoProfile -NonInteractive -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile %WRAPPER_JAR% -UseBasicParsing"
)

@"%JAVA_CMD%" %MAVEN_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %*
