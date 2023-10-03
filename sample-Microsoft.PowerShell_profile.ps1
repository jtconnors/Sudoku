#
# Sample Microsoft.PowerShell_profile.ps1 file. This will
# set the PowerShell environment at PowerShell start up.
#
# This file can be modified to reflect your JDK envirnoment and sourced,
# for example, inside a Visual Studio terminal, to set up a proper Java SE 8
# environment that incorporates support for JavaFX
#
$env:JAVA_HOME = 'C:\devel\jdk\defaultjdk'
$env:PATH = $env:JAVA_HOME + '\bin;' + $env:PATH