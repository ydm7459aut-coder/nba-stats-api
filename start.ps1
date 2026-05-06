# Quick launcher — finds Java 17+ automatically and starts the NBA Stats app
param([string]$ApiKey = "")

# Find Java 17+
$searchPaths = @(
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Java",
    "C:\Program Files\Microsoft",
    "C:\Program Files\Amazon Corretto",
    "C:\Program Files\BellSoft"
)

$javaHome = $null
foreach ($base in $searchPaths) {
    $found = Get-ChildItem $base -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -match "jdk.?(1[7-9]|2[0-9])" } |
        Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
    if ($found) { $javaHome = $found; break }
}

if (-not $javaHome -and $env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaHome = $env:JAVA_HOME
}

if (-not $javaHome) {
    Write-Host "Java 17+ not found. Run setup.ps1 first, or install from https://adoptium.net" -ForegroundColor Red
    exit 1
}

$env:JAVA_HOME = $javaHome
$env:PATH      = "$javaHome\bin;$env:PATH"

$jv = & "$javaHome\bin\java" -version 2>&1 | Select-Object -First 1
Write-Host "Using: $jv" -ForegroundColor Green
Write-Host "JAVA_HOME: $javaHome`n" -ForegroundColor Gray

if ($ApiKey) { $env:ANTHROPIC_API_KEY = $ApiKey }
if (-not $env:ANTHROPIC_API_KEY) {
    Write-Host "Tip: pass your key to enable AI chat:" -ForegroundColor Yellow
    Write-Host "     .\start.ps1 -ApiKey 'sk-ant-...'" -ForegroundColor Gray
}

Write-Host "Starting NBA Stats at http://localhost:8080 ...`n" -ForegroundColor Cyan

# Call the Maven wrapper JAR directly — bypasses mvnw.cmd quoting issues
$wrapperJar = Join-Path $PSScriptRoot ".mvn\wrapper\maven-wrapper.jar"
$projectDir  = $PSScriptRoot.TrimEnd('\')

& "$javaHome\bin\java.exe" `
    "-Dmaven.multiModuleProjectDirectory=$projectDir" `
    -classpath "$wrapperJar" `
    org.apache.maven.wrapper.MavenWrapperMain `
    spring-boot:run
