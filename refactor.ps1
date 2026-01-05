$specifics = @(
    @("import entity.NPCManager", "import controller.NPCManager"),
    @("import entity.EnvironmentManager", "import controller.EnvironmentManager"),
    @("import story.StoryManager", "import controller.StoryManager"),
    @("import map.MapManager", "import controller.MapManager"),
    @("import map.Map", "import model.Map"),
    @("import save.SaveManager", "import controller.SaveManager"),
    @("import inventory.Inventory", "import model.Inventory"),
    @("import inventory.Tool", "import model.Tool"),
    @("import inventory.ItemAssets", "import view.ItemAssets"),
    @("import cutscene.Cutscene", "import model.Cutscene"),
    @("import cutscene.CutsceneManager", "import controller.CutsceneManager")
)

$generals = @(
    @("import app.", "import controller."),
    @("import input.", "import controller."),
    @("import time.", "import controller."),
    @("import pathfinding.", "import controller."),
    @("import tools.", "import controller."),
    @("import entity.", "import model."),
    @("import object.", "import model."),
    @("import story.", "import model."),
    @("import settings.", "import model."),
    @("import map.", "import model."),
    @("import save.", "import model."),
    @("import inventory.", "import model."),
    @("import ui.", "import view."),
    @("import tile.", "import view."),
    @("import audio.", "import view."),
    @("import world.", "import model.")
)

function Update-File($path, $pkg) {
    if (-not (Test-Path $path)) { return }
    
    # Read as single string
    $content = [System.IO.File]::ReadAllText($path)
    
    # 1. Update Package
    # Regex to replace package declaration at start
    $content = $content -replace "package\s+[\w\.]+;", "package $pkg;"
    
    # 2. Apply Specific Replacements
    foreach ($pair in $specifics) {
        $from = [regex]::Escape($pair[0])
        $to = $pair[1]
        $content = $content -replace $from, $to
    }
    
    # 3. Apply General Replacements
    foreach ($pair in $generals) {
        $from = [regex]::Escape($pair[0])
        $to = $pair[1]
        $content = $content -replace $from, $to
    }
    
    [System.IO.File]::WriteAllText($path, $content)
    Write-Host "Updated $path"
}

Write-Host "Processing Controller Files..."
Get-ChildItem -Path "src/main/java/controller" -Filter *.java | ForEach-Object { Update-File $_.FullName "controller" }

Write-Host "Processing Model Files..."
Get-ChildItem -Path "src/main/java/model" -Filter *.java | ForEach-Object { Update-File $_.FullName "model" }

Write-Host "Processing View Files..."
Get-ChildItem -Path "src/main/java/view" -Filter *.java | ForEach-Object { Update-File $_.FullName "view" }
