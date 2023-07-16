#Save the file as .ps1. And open power shell as administrator and run command. Set-ExecutionPolicy RemoteSigned. And say Y and enter.
# Define the paths and filenames
$repoPath = "C:\munawar\repos\pos"
$jarFileName = "pos-0.0.1-SNAPSHOT.jar"
$newJarFileName = "MSTEREO POS.jar"
$jarFilePath = Join-Path $repoPath "target\$jarFileName"
$shortcutPath = [Environment]::GetFolderPath("Desktop") + "\$newJarFileName.lnk"

# Change the working directory to the repository path
Set-Location $repoPath

# Perform git pull
git pull

# Build Maven project with skipping tests
mvn clean install -DskipTests

# Rename the JAR file
$newJarFilePath = Join-Path $repoPath "target\$newJarFileName"
Rename-Item $jarFilePath -NewName $newJarFileName -Force

# Create a shortcut for the JAR file on the desktop
$WshShell = New-Object -ComObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut($shortcutPath)
$Shortcut.TargetPath = $newJarFilePath
$Shortcut.Save()

Write-Host "Script execution complete. Shortcut created on the desktop."