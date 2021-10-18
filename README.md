# Sudoku

JavaFX version of the Sudoku game

Started circa 2007, this program was first written in the now defunct Java FX Script language and pre-dates the introduction of mature layout managers. It served as a reference application for the book **_JavaFX Developing Rich Internet Applications_**. The source contained here has been converted to the Java-based JavaFX 2.x platform but still positions componenents in a very manual fashion without the aid of any layout manager.

The project represented by this tag (```2.0-JDK8```) requires a JDK 8 instance, and more specifically an _Oracle JDK 8_ instance,  as Oracle JDK 8 includes JavaFX as a built-in component.  With the advent of JDK 11, all JDKs (including Oracle's) are the same with regards to JavaFX in that they all no longer incorporate JavaFX and must rely on the external OpenJFX class libraries.  The included _maven_ ```pom.xml``` file is tailored to specifically run with JDK 8.

The logic for the Sudoku game (```net.sourceforge.playsudoku```) was lifted from an even older code base and integrated into this application.

**Requirements:**
1. Your default JDK should point to a valid Oracle JDK 8 runtime in your ```PATH```.
2. Either the ```JAVA_HOME``` or ```$env:JAVA_HOME``` (depending upon the platform in question) environment variable should be set to a valid JDK 8 runtime.
3. Included ```sample-profile.sh``` and ```sample-Microsoft.PowerShell_profile.ps1``` files have been included as potential templates to aid in the set up of this JDK 8 environment.

Of note, the following maven goals can be executed:

   - ```mvn clean```
   - ```mvn dependency:copy-dependencies``` - to pull down and dependencies
   - ```mvn compile``` - to build the application
   - ```mvn package``` - to create the Sudoku application as a jar file
   - ```mvn exec:java``` to run the Sudoku application
