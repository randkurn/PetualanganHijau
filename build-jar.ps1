# Build Script untuk Petualangan Hijau
Write-Host "=== Building Petualangan Hijau ===" -ForegroundColor Green

# Setup paths
$projectRoot = $PSScriptRoot
$srcDir = Join-Path $projectRoot "src\main\java"
$resourcesDir = Join-Path $projectRoot "src\main\resources"
$libDir = Join-Path $projectRoot "lib"
$buildDir = Join-Path $projectRoot "build"
$classesDir = Join-Path $buildDir "classes"
$jarFile = Join-Path $buildDir "PetualanganHijau.jar"

# Clean build directory (skip if locked)
Write-Host "Preparing build directory..." -ForegroundColor Yellow
if (Test-Path $classesDir) {
    Remove-Item -Path $classesDir -Recurse -Force -ErrorAction SilentlyContinue
}
New-Item -ItemType Directory -Path $classesDir -Force | Out-Null

# Find all Java files
Write-Host "Finding Java source files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# Create file list for javac (use proper encoding)
$fileListPath = Join-Path $buildDir "sources.txt"
[System.IO.File]::WriteAllLines($fileListPath, $javaFiles, [System.Text.Encoding]::UTF8)

# Build classpath
$classpath = ""
if (Test-Path $libDir) {
    $jars = Get-ChildItem -Path $libDir -Filter "*.jar" -Recurse
    if ($jars) {
        $classpath = ($jars | Select-Object -ExpandProperty FullName) -join ";"
    }
}

# Compile
Write-Host "Compiling Java files..." -ForegroundColor Yellow
$compileCmd = "javac -encoding UTF-8 -d `"$classesDir`" -sourcepath `"$srcDir`""

if ($classpath) {
    $compileCmd += " -cp `"$classpath`""
}

$compileCmd += " @`"$fileListPath`""

Invoke-Expression $compileCmd

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

# Copy resources
Write-Host "Copying resources..." -ForegroundColor Yellow
if (Test-Path $resourcesDir) {
    Copy-Item -Path "$resourcesDir\*" -Destination $classesDir -Recurse -Force
}

# Copy libraries
if ($classpath) {
    Write-Host "Copying dependencies..." -ForegroundColor Yellow
    $libTargetDir = Join-Path $classesDir "lib"
    New-Item -ItemType Directory -Path $libTargetDir -Force | Out-Null
    $jars = Get-ChildItem -Path $libDir -Filter "*.jar" -Recurse
    foreach ($jar in $jars) {
        Copy-Item -Path $jar.FullName -Destination $libTargetDir -Force
    }
}

# Create MANIFEST.MF
Write-Host "Creating manifest..." -ForegroundColor Yellow
$manifestContent = "Manifest-Version: 1.0`r`nMain-Class: controller.App`r`n"
if ($classpath) {
    $manifestContent += "Class-Path: " + (($jars | ForEach-Object { "lib/" + $_.Name }) -join " ") + "`r`n"
}
$manifestPath = Join-Path $buildDir "MANIFEST.MF"
[System.IO.File]::WriteAllText($manifestPath, $manifestContent, [System.Text.Encoding]::ASCII)

# Create JAR
Write-Host "Creating JAR file..." -ForegroundColor Yellow
Push-Location $classesDir
& jar -cfm $jarFile $manifestPath *
Pop-Location

if (Test-Path $jarFile) {
    $jarSize = (Get-Item $jarFile).Length / 1MB
    Write-Host "Build successful!" -ForegroundColor Green
    Write-Host "JAR created at: $jarFile" -ForegroundColor Cyan
    Write-Host "Size: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Cyan
    
    Write-Host "`nTo run: java -jar `"$jarFile`"" -ForegroundColor Yellow
}
else {
    Write-Host "JAR creation failed!" -ForegroundColor Red
    exit 1
}
