# ============================================
# Build EXE dengan jpackage - Petualangan Hijau
# ============================================

Write-Host "Creating Windows EXE Installer..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$APP_NAME = "PetualanganHijau"
$APP_VERSION = "1.0.0"
$MAIN_JAR = "build\PetualangHijau_New.jar"
$MAIN_CLASS = "controller.App"
$OUTPUT_DIR = "build\exe"

if (-not (Test-Path $MAIN_JAR)) {
    Write-Host "[ERROR] JAR file not found: $MAIN_JAR" -ForegroundColor Red
    Write-Host "[INFO] Please run build.ps1 first!" -ForegroundColor Yellow
    exit 1
}

if (Test-Path $OUTPUT_DIR) {
    Remove-Item $OUTPUT_DIR -Recurse -Force
}
New-Item -ItemType Directory -Path $OUTPUT_DIR -Force | Out-Null

Write-Host "[1/2] Preparing..." -ForegroundColor Green
Write-Host "[2/2] Running jpackage..." -ForegroundColor Green

try {
    & jpackage --type exe --name $APP_NAME --app-version $APP_VERSION --input build --main-jar PetualangHijau_New.jar --main-class $MAIN_CLASS --dest $OUTPUT_DIR --vendor "CMPT276 Group 7" --description "Petualangan Hijau - Game Edukasi Lingkungan" --win-console --win-shortcut --win-menu --win-dir-chooser
    
    $exePath = Get-ChildItem -Path $OUTPUT_DIR -Filter "*.exe" -Recurse | Select-Object -First 1
    
    if ($exePath) {
        Write-Host "" -ForegroundColor Green
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "SUCCESS! EXE Created!" -ForegroundColor Green
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "Location: $($exePath.FullName)" -ForegroundColor Yellow
        Write-Host "Size: $([math]::Round($exePath.Length / 1MB, 2)) MB" -ForegroundColor Yellow
        Write-Host "" -ForegroundColor Green
        Write-Host "To install, run the EXE installer!" -ForegroundColor Cyan
    }
    else {
        Write-Host "[ERROR] EXE not found in output directory!" -ForegroundColor Red
    }
    
}
catch {
    Write-Host "[ERROR] jpackage failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host "" -ForegroundColor Red
    Write-Host "Trying alternative method with Launch4j..." -ForegroundColor Yellow
    exit 1
}
