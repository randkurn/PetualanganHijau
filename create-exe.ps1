# Script untuk membuat EXE menggunakan Launch4j
Write-Host "=== Creating EXE for Petualangan Hijau ===" -ForegroundColor Green

$projectRoot = $PSScriptRoot
$toolsDir = Join-Path $projectRoot "tools"
$launch4jZip = Join-Path $toolsDir "launch4j.zip"
$launch4jDir = Join-Path $toolsDir "launch4j"
$buildDir = Join-Path $projectRoot "build"
$configFile = Join-Path $projectRoot "launch4j-config.xml"

# Create build directory if not exists
if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir -Force | Out-Null
}

# Extract Launch4j if not already extracted
if (-not (Test-Path $launch4jDir)) {
    Write-Host "Extracting Launch4j..." -ForegroundColor Yellow
    Expand-Archive -Path $launch4jZip -DestinationPath $toolsDir -Force
    Write-Host "Launch4j extracted successfully" -ForegroundColor Green
}

# Find launch4jc.exe
$launch4jExe = Get-ChildItem -Path $launch4jDir -Filter "launch4jc.exe" -Recurse | Select-Object -First 1

if (-not $launch4jExe) {
    Write-Host "Error: launch4jc.exe not found in extracted files" -ForegroundColor Red
    Write-Host "Looking for launch4j executable..." -ForegroundColor Yellow
    Get-ChildItem -Path $launch4jDir -Filter "*.exe" -Recurse | ForEach-Object {
        Write-Host "  Found: $($_.FullName)" -ForegroundColor Cyan
    }
    exit 1
}

Write-Host "Using Launch4j: $($launch4jExe.FullName)" -ForegroundColor Cyan

# Check if JAR exists
$jarFile = Join-Path $projectRoot "dist\PetualangHijau_New.jar"
if (-not (Test-Path $jarFile)) {
    Write-Host "Error: JAR file not found at $jarFile" -ForegroundColor Red
    Write-Host "Please build the project first using NetBeans" -ForegroundColor Yellow
    exit 1
}

Write-Host "JAR file found: $jarFile" -ForegroundColor Green

# Run Launch4j
Write-Host "Creating EXE..." -ForegroundColor Yellow
& $launch4jExe.FullName $configFile

if ($LASTEXITCODE -eq 0) {
    $exeFile = Join-Path $buildDir "PetualanganHijau.exe"
    if (Test-Path $exeFile) {
        $exeSize = (Get-Item $exeFile).Length / 1MB
        Write-Host "`nEXE created successfully!" -ForegroundColor Green
        Write-Host "Location: $exeFile" -ForegroundColor Cyan
        Write-Host "Size: $([math]::Round($exeSize, 2)) MB" -ForegroundColor Cyan
        
        # Copy JAR to build directory for distribution
        Write-Host "`nCopying JAR to build directory..." -ForegroundColor Yellow
        Copy-Item -Path $jarFile -Destination $buildDir -Force
        
        Write-Host "`nDistribution files ready in: $buildDir" -ForegroundColor Green
        Write-Host "  - PetualanganHijau.exe (launcher)" -ForegroundColor Cyan
        Write-Host "  - PetualangHijau_New.jar (game files)" -ForegroundColor Cyan
        Write-Host "`nNote: Both files are needed for distribution!" -ForegroundColor Yellow
    }
    else {
        Write-Host "Error: EXE file was not created" -ForegroundColor Red
        exit 1
    }
}
else {
    Write-Host "Error: Launch4j failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}
