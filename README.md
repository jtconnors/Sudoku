# Sudoku

JavaFX version of the Sudoku game

Started circa 2007, this program was first written in the now defunct Java FX Script language and pre-dates the introduction of mature layout managers. It served as a reference application for the book **_JavaFX Developing Rich Internet Applications_**. The source contained here has been converted to the Java-based JavaFX 2.x platform but still positions componenents in a very manual fashion without the aid of any layout manager.

The project represented by this tag (```2.0-JDK21```) should be run and compiled with a JDK 21 instance.  It is specific to JDK 21 and can be built with the ```apache maven``` build lifecycle system. Scripts contained in this project will insist that JDK 21 be used.

This version of the Sudoku source has been modularized and runs completely from the module path.  It has been divided into two modules, represented by two separate subdirectories with two separate project ```pom.xml``` files.
1. ```playsudoku``` - This module contains the logic for the Sudoku game.  The code was lifted from sourceforge.net, is quite old, and has been integrated into this application and modularized.
2. ```sudokufx``` - Sudoku User Interface module, written in JavaFX with a dependency on the ```playsudoku``` module.


**Requirements:**
1. Your default JDK should point to a valid Oracle JDK 21 runtime in your ```PATH```.
2. Either the ```JAVA_HOME``` or ```$env:JAVA_HOME``` (depending upon the platform in question) environment variable should be set to a valid JDK 21 runtime.
3. In order to generate ```EXE``` or ```MSI``` installers for Windows using the scripts in this project, the WiX toolkit version 3.0 or greater must be installed and placed on the ```PATH```.
4. For certain Linux distributions (e.g. Oracle Linux ...) additional tooling, like for example ```rpmbuild```, may be required in order to fully utilize the ```jpackage``` utility.

Of note, the following maven goals can be executed:

   - ```mvn clean```
   - ```mvn dependency:copy-dependencies``` - to pull down and dependencies
   - ```mvn compile``` - to build the application
   - ```mvn package``` - to package the two Sudoku modules (```playsudoku``` and ```sudokufx```) into jar files

   The following scripts are provided in the ```sh/``` and ```ps1\``` directories respectively to aid in the running and packaging of the application:
   - ```sh/run.sh``` or ```ps1\run.ps1``` - script file to run the application from the module path
   - ```sh/run-simplified.sh``` or ```ps1\run-simplified.ps1``` - alternative script file to run the application, determines main class from ```sudokufx``` module
   - ```sh/link.sh``` or ```ps1\link.ps1``` - creates a runtime image using the ```jlink``` utility
   - ```sh/create-appimage.sh``` or ```ps1\create-appimage.ps1``` - creates a native package image of application using JEP-392 jpackage tool
   - ```sh/create-deb-installer.sh``` - creates a native Linux DEB installer of this application using JEP-392 jpackage tool
   - ```sh/create-dmg-installer.sh``` - creates a native MacOS DMG installer of this application using JEP-392 jpackage tool
   - ```ps1\create-exe-installer.ps1``` - creates a native Windows EXE installer of this application using JEP-392 jpackage tool
   - ```ps1\create-msi-installer.ps1``` - creates a native Windows MSI installer of this application using JEP-392 jpackage tool
   - ```sh/create-pkg-installer.sh``` - creates a native MacOS PKG installer of this application using JEP-392 jpackage tool
   - ```sh/create-rpm-installer.sh``` - creates a native Linux RPM installer of this application using JEP-392 jpackage tool

Notes:
   - These scripts have a few available command-line options.  To print out
the options, add ```-?``` or ```--help``` as an argument to any script.
   - These scripts share common properties that can be found in ```env.sh``` or ```env.ps1```.  These may need to be slightly modified to match  your specific configuration.
   - A sample ```Microsoft.PowerShell_profile.ps1``` file has been included to help configure a default Powershell execution environment.   In a similar manner, a ```sample-profile.sh``` file has been furnished for Linux/MacOS environments.