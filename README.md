# PersonalProject

A programming project, featuring a Java game and an Arduino controller.
Done for the IB Personal Project programme. This repository only contains
files related to the programming parts of the project; the report, journal
and extra files contain personal information.

### Configuration/Building:

##### Arduino
Assemble the Arduino controller as follows:

- Potentiometer 1 (Joystick X) to A0
- Potentiometer 2 (Joystick Y) to A1
- Button 1 (Joystick push sensor) to digital pin 12 and "pressed down" to 5V

Upload the `controller.ino` file in the `/Controller/controller/` folder to
your Arduino Uno (other models will likely work too) and connect it with a
USB cable to the USB port on your computer.

#### PC
The JavaFX library that I am using for this project should be installed by
default with Java on Windows, but not always on Linux. If you are a Windows
user who needs Java + the Netbeans IDE, install it from
[the Java JDK + Netbeans download page][javadl]. Linux users can install Java
(although it should be installed by default) using the command line using
`sudo apt install default-jdk`, Netbeans using `sudo apt install netbeans`
and JavaFX using `sudo apt install openjfx`. You may also install the JavaFX
source files for poking around using `sudo apt install openjfx-source`

To run the game, download the latest fat (standalone) jar from the
[releases page][releases] if it exists. Run it either by double-clicking
or by doing `java -jar Game.jar` using the command prompt/terminal in the
same folder.

To build the latest commit, open it in the Netbeans IDE and click the
!["Clean and Build"][cleanbuild] button in the top bar. This will output
`Game.jar` in `/Game/dist/` with dependencies in another folder.

To build a standalone ("fat") jar, go to the files tab in Netbeans, right-click
on the build.xml file under the Game folder and navigate to
`Run Target > Other Targets > package-for-store`.
![Image for the above instructions][fatbuild]
This will output `Game_fat.jar` in the `/Game/dist` folder.


### Credits
Credits for external libraries can be found in `/Game/lib/README.md`.


[javadl]: http://www.oracle.com/technetwork/articles/javase/jdk-netbeans-jsp-142931.html#close
[releases]: https://github.com/PixelSergey/PersonalProject/releases
[cleanbuild]: ./github/cleanbuild.png
[fatbuild]: ./github/fatbuild.png
