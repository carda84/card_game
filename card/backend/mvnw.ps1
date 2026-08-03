# Maven Wrapper for PowerShell
$wrapperDir = $PSScriptRoot + "\.mvn\wrapper"
$wrapperJar = "$wrapperDir\maven-wrapper.jar"
$wrapperProps = "$wrapperDir\maven-wrapper.properties"

# Download wrapper jar if not present
if (-not (Test-Path $wrapperJar)) {
    Write-Host "Downloading Maven Wrapper..."
    $url = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    Invoke-WebRequest -Uri $url -OutFile $wrapperJar
}

# Find Java
$javaExe = "java"
if ($env:JAVA_HOME) {
    $javaExe = "$env:JAVA_HOME\bin\java"
}

# Run Maven Wrapper
& $javaExe -classpath $wrapperJar org.apache.maven.wrapper.MavenWrapperMain $args
