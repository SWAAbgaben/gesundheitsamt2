# Copyright (C) 2020 -  Juergen Zimmermann, Hochschule Karlsruhe
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

# https://docs.microsoft.com/en-us/powershell/scripting/developer/cmdlet/approved-verbs-for-windows-powershell-commands?view=powershell-7

# Aufruf:   .\sonarqube.ps1 [start|scan]

# "Param" muss in der 1. Zeile sein
Param (
    [string]$cmd = ''
)

Set-StrictMode -Version Latest

$versionMinimum = [Version]'7.2.0'
$versionCurrent = $PSVersionTable.PSVersion
if ($versionMinimum -gt $versionCurrent) {
    throw "PowerShell $versionMinimum statt $versionCurrent erforderlich"
}

# Titel setzen
$script = $myInvocation.MyCommand.Name
$host.ui.RawUI.WindowTitle = $cmd

$version = '8.6.0-community';
$versionScanner = '4.6';
$containerName = 'sonarqube';

function Start-SonarQube {
    # login=admin, password=Software Engineering WI.

    $port = '9000';
    $sonarqubeDir = 'C:\Zimmermann\volumes\sonarqube'

    Write-Output ''
    Write-Output "URL fuer den SonarQube-Container: http://localhost:$port"
    Write-Output ''

    docker run --publish ${port}:${port} `
        --mount type=bind,source=${sonarqubeDir}/data,destination=/opt/sonarqube/data `
        --mount type=bind,source=${sonarqubeDir}/logs,destination=/opt/sonarqube/logs `
        --mount type=bind,source=${sonarqubeDir}/language-plugins,destination=/opt/sonarqube/lib/extensions,readonly `
        --env TZ=Europe/Berlin `
        --memory 1024m --cpus 2 `
        --name $containerName --rm `
        sonarqube:$version
}

function Invoke-Scan {
    docker run `
        --mount type=bind,source=${PWD},destination=/usr/src `
        --env SONAR_HOST_URL=http://host.docker.internal:9000 `
        --memory 1024m --cpus 2 `
        --name sonar-scanner-cli --rm `
        sonarsource/sonar-scanner-cli:${versionScanner}
}

switch ($cmd) {
    '' { Start-SonarQube; break }
    'start' { Start-SonarQube; break }
    'scan' { Invoke-Scan; break }
    default { Write-Output "$script [start|scan]" }
}
