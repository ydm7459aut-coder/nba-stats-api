# NBA Stats - Windows Dev Setup Script
# Run as Administrator in PowerShell:
# Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass; .\setup.ps1

$ErrorActionPreference = "Stop"

function Write-Step($msg) { Write-Host "`n>> $msg" -ForegroundColor Cyan }
function Write-OK($msg)   { Write-Host "   OK: $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "   !! $msg" -ForegroundColor Yellow }

Write-Host "`n====================================" -ForegroundColor Magenta
Write-Host "  NBA Stats - Dev Environment Setup" -ForegroundColor Magenta
Write-Host "====================================`n" -ForegroundColor Magenta

# ── 1. Check winget ──────────────────────────────────────────────────────────
Write-Step "Checking winget..."
if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
    Write-Warn "winget not found. Install App Installer from the Microsoft Store, then re-run this script."
    exit 1
}
Write-OK "winget available"

# ── 2. Java 21 ───────────────────────────────────────────────────────────────
Write-Step "Installing Java 21 (Eclipse Temurin)..."
winget install --id EclipseAdoptium.Temurin.21.JDK --silent --accept-package-agreements --accept-source-agreements 2>$null
if ($LASTEXITCODE -ne 0) { Write-Warn "Java 21 may already be installed or install failed - continuing..." }

# Find the newest Java 17+ installation
$javaBase = "C:\Program Files\Eclipse Adoptium"
$javaHome = Get-ChildItem "$javaBase" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -match "jdk-2[1-9]|jdk-1[7-9]" } |
    Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName

if (-not $javaHome) {
    # Fallback: search common locations
    $candidates = @(
        "C:\Program Files\Java",
        "C:\Program Files\Microsoft",
        "C:\Program Files\Amazon Corretto"
    )
    foreach ($base in $candidates) {
        $found = Get-ChildItem $base -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match "jdk-?2[1-9]|jdk-?1[7-9]|jdk21|jdk17" } |
            Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
        if ($found) { $javaHome = $found; break }
    }
}

if (-not $javaHome) {
    Write-Warn "Could not locate Java 17+ installation. Please install from https://adoptium.net and re-run."
    exit 1
}

Write-OK "Java found at: $javaHome"

# Set JAVA_HOME system-wide
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
$env:JAVA_HOME = $javaHome

# Add to PATH if not already there
$sysPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$javaBin = "$javaHome\bin"
if ($sysPath -notlike "*$javaBin*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$javaBin;$sysPath", "Machine")
    $env:PATH = "$javaBin;$env:PATH"
    Write-OK "Added Java to system PATH"
} else {
    Write-OK "Java already in PATH"
}

$jv = & "$javaHome\bin\java" -version 2>&1 | Select-Object -First 1
Write-OK "Java version: $jv"

# ── 3. Git ────────────────────────────────────────────────────────────────────
Write-Step "Installing Git..."
winget install --id Git.Git --silent --accept-package-agreements --accept-source-agreements 2>$null
$gitPath = "C:\Program Files\Git\bin"
if (Test-Path $gitPath) {
    $sysPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    if ($sysPath -notlike "*$gitPath*") {
        [System.Environment]::SetEnvironmentVariable("Path", "$gitPath;$sysPath", "Machine")
        $env:PATH = "$gitPath;$env:PATH"
    }
    Write-OK "Git ready"
} else { Write-Warn "Git path not found, may need a re-login to take effect" }

# ── 4. Node.js LTS ───────────────────────────────────────────────────────────
Write-Step "Installing Node.js LTS..."
winget install --id OpenJS.NodeJS.LTS --silent --accept-package-agreements --accept-source-agreements 2>$null
Write-OK "Node.js installed (restart terminal to use npm/node)"

# ── 5. VS Code ───────────────────────────────────────────────────────────────
Write-Step "Installing VS Code..."
winget install --id Microsoft.VisualStudioCode --silent --accept-package-agreements --accept-source-agreements 2>$null
Write-OK "VS Code installed"

# ── 6. VS Code Extensions ─────────────────────────────────────────────────────
Write-Step "Installing VS Code extensions..."
$codePath = "C:\Program Files\Microsoft VS Code\bin\code.cmd"
if (Test-Path $codePath) {
    $extensions = @(
        "vscjava.vscode-java-pack",
        "pivotal.vscode-spring-boot",
        "ms-vscode.powershell",
        "eamodio.gitlens",
        "esbenp.prettier-vscode",
        "bradlc.vscode-tailwindcss"
    )
    foreach ($ext in $extensions) {
        & $codePath --install-extension $ext --force 2>$null
        Write-OK "Installed: $ext"
    }
} else { Write-Warn "VS Code not found in default path, skipping extensions" }

# ── 7. Verify & Summary ───────────────────────────────────────────────────────
Write-Host "`n====================================" -ForegroundColor Magenta
Write-Host "  Setup Complete!" -ForegroundColor Green
Write-Host "====================================`n" -ForegroundColor Magenta

Write-Host "Installed:" -ForegroundColor White
Write-Host "  Java 21    -> $javaHome" -ForegroundColor Gray
Write-Host "  Git        -> winget" -ForegroundColor Gray
Write-Host "  Node.js    -> winget" -ForegroundColor Gray
Write-Host "  VS Code    -> winget" -ForegroundColor Gray

Write-Host "`nNext steps:" -ForegroundColor White
Write-Host "  1. Close and reopen PowerShell (so PATH changes take effect)" -ForegroundColor Gray
Write-Host "  2. cd C:\Users\AUT\nba-stats-api" -ForegroundColor Gray
Write-Host "  3. `$env:ANTHROPIC_API_KEY='sk-ant-YOUR_KEY'" -ForegroundColor Gray
Write-Host "  4. .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
Write-Host "  5. Open http://localhost:8080`n" -ForegroundColor Gray
