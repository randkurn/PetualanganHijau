# Build Script untuk Petualangan Hijau (Aselole Edition)
Write-Host "=== Building Petualangan Hijau (Aselole Edition) ===" -ForegroundColor Green

$projectRoot = $PSScriptRoot
$srcDir = Join-Path $projectRoot "src\main\java"
$resourcesDir = Join-Path $projectRoot "src\main\resources"
$distDir = Join-Path $projectRoot "dist"
$tempDir = Join-Path $projectRoot "temp_build"
$classesDir = Join-Path $tempDir "classes"
$outputJar = Join-Path $distDir "PetualanganHijau_Aselole.jar"

# 1. Bersihkan & Siapkan Folder
Write-Host "Cleaning temporary folders..." -ForegroundColor Yellow
if (Test-Path $tempDir) { Remove-Item -Path $tempDir -Recurse -Force -ErrorAction SilentlyContinue }
New-Item -ItemType Directory -Path $classesDir -Force | Out-Null

# 2. Cari Source Files
Write-Host "Finding source files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# 3. Buat sources.txt (Gunakan encoding ASCII untuk menghindari BOM yang merusak javac)
$sourcesList = Join-Path $tempDir "sources.txt"
[System.IO.File]::WriteAllLines($sourcesList, $javaFiles)

# 4. Compile Source Files
Write-Host "Compiling source files..." -ForegroundColor Yellow
$libJar = Join-Path $distDir "libraries.jar"
if (Test-Path $libJar) {
    & javac -encoding UTF-8 -d $classesDir -cp $libJar "@$sourcesList"
}
else {
    Write-Host "Error: dist/libraries.jar not found!" -ForegroundColor Red
    exit 1
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

# 5. Ekstrak Library dan Merge ke classesDir
Write-Host "Merging libraries into JAR..." -ForegroundColor Yellow
Push-Location $classesDir
& jar -xf $libJar
# Hapus metadata library yang tidak perlu agar tidak conflict
if (Test-Path "META-INF") {
    Get-ChildItem -Path "META-INF" -Filter "*.SF" -Recurse | Remove-Item -Force
    Get-ChildItem -Path "META-INF" -Filter "*.DSA" -Recurse | Remove-Item -Force
    Get-ChildItem -Path "META-INF" -Filter "*.RSA" -Recurse | Remove-Item -Force
}
Get-ChildItem -Path . -Filter "module-info.class" -Recurse | Remove-Item -Force -ErrorAction SilentlyContinue
Pop-Location

# 6. Salin Resources
Write-Host "Copying resources..." -ForegroundColor Yellow
if (Test-Path $resourcesDir) {
    Copy-Item -Path "$resourcesDir\*" -Destination $classesDir -Recurse -Force
}

# 7. Buat Manifest
Write-Host "Creating manifest..." -ForegroundColor Yellow
$manifestContent = "Manifest-Version: 1.0`r`nMain-Class: controller.App`r`n"
$manifestPath = Join-Path $tempDir "MANIFEST.MF"
[System.IO.File]::WriteAllText($manifestPath, $manifestContent, [System.Text.Encoding]::ASCII)

# 8. Package ke JAR (Fat JAR)
Write-Host "Creating Fat JAR..." -ForegroundColor Yellow
Push-Location $classesDir
& jar -cfm $outputJar $manifestPath *
Pop-Location

# 9. Bersihkan
Remove-Item -Path $tempDir -Recurse -Force -ErrorAction SilentlyContinue

if (Test-Path $outputJar) {
    $size = (Get-Item $outputJar).Length / 1MB
    Write-Host "`nSUCCESS! JAR created: $outputJar" -ForegroundColor Green
    Write-Host "Size: $([math]::Round($size, 2)) MB" -ForegroundColor Cyan
    Write-Host "`nSILAKAN BUILD EXE ULANG DI LAUNCH4J MENGGUNAKAN JAR INI." -ForegroundColor Yellow
}
else {
    Write-Host "Failed to create JAR!" -ForegroundColor Red
}
