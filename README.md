# PersonalProject

A programming project, featuring a Java game and an Arduino controller.
This project is done for the IB Personal Project programme. The
repository only contains files related to the project itself as the
report, journal and extra files contain personal information.

### File rundown:

- `/Controller/controller/` contains the controller program for the Arduino
- `/Controller/models/` contains the models:
  - `./STL/` contains the ready STL files for importing into any program
  - `./SketchUp/` contains the intact, editable SketchUp files
  - `./GCode/` contains the ready files for printing on an Ultimaker 3
- `/Game/` contains the game files:
  - The `./src/` folder contains the source code
  - The `./nbproject/` folder has info for the Netbeans IDE
  - The `./lib/` folder contains external libraries not made by me; see the
  `README.md` file in that folder for credits
  - The `./build.xml` file builds the project and supports "fat" (standalone)
  jar creation.
- `/photos/` contains progress images of the controller, plans, etc.
- `/github/` contains files e.g. images for this `README.md` file.
- `/.gitignore` is the file git uses to ignore junk files e.g. autosaves and
builds
- `/LICENSE` is the [GPL-2.0 license][license] file for this project
- `/README.md` is this file :)

### Configuration/Building:

##### Arduino
Assemble the Arduino controller as follows:

- Potentiometer 1 (Joystick X) to analog pin A0
- Potentiometer 2 (Joystick Y) to analog pin A1
- Button 1 (Joystick push sensor) to digital pin 12 with the pin "pulled down" to 5V
- The START button to digital pin 10 with the pin "pulled down" to ground
- The A button to digital pin 10 with the pin "pulled down" to ground
- The B button to digital pin 9 with the pin "pulled down" to ground
- The X button to digital pin 7 with the pin "pulled down" to ground
- The Y button to digital pin 8 with the pin "pulled down" to ground
- One of the sides of the ON/OFF switch to digital pin 10 with the pin "pulled down" to ground

Upload the `controller.ino` file from the `/Controller/controller/` folder to
your Arduino Uno (other models will likely work too) and connect it with a
USB cable to the USB port on your computer.

If you would like a shell for your controller, the models are in the
`/Controller/models/` folder (`./STL/` for the models, `./SketchUp/` for the
editable SketchUp files or `./GCode/` for ready Ultimaker 3 print files).
The model requires filing depending on the size of the buttons you are using,
and a hole drilled in the bottom for the USB cable (I didn't think of modelling
it originally, unfortunately)

#### PC
The JavaFX library that I am using for this project should be installed by
default with Java 8 on Windows, but not always on Linux. If you are a Windows
user who needs Java + the Netbeans IDE, install it from
[the Java JDK + Netbeans download page][javadl]. Linux users can install Java
(although it should be installed by default) using the command line using
`sudo apt install default-jdk`, Netbeans using `sudo apt install netbeans`
and JavaFX using `sudo apt install openjfx`. You may also install the JavaFX
source files for reference using `sudo apt install openjfx-source`.

To run the game, download the latest fat (standalone) jar from the
[releases page][releases] if it exists. Run it either by double-clicking
or by doing `java -jar Game_fat.jar` using the command prompt/terminal in the
same folder.

If you want to build the latest commit, open it in the Netbeans IDE and 
click the !["Clean and Build"][cleanbuild] button in the top bar. This
will output `Game.jar` in `/Game/dist/` with dependencies in another folder.

To build a standalone ("fat") jar with all dependencies inside it, go to the
files tab in Netbeans, right-click on the build.xml file under the Game folder
and navigate to `Run Target > Other Targets > package-for-store`.

![Image for the above instructions][fatbuild]

This will output `Game_fat.jar` in the `/Game/dist` folder.


### Credits

- Credits for external libraries can be found in `/Game/lib/README.md`.
- All assets were made by me, except:
  - All .mp3 audio files come from <https://www.zapsplat.com>. Some audio files were modified by me.
  - The player character comes from <https://opengameart.org/content/spaceship-set-4-pixel-art-space-ships>

[license]: https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
[javadl]: http://www.oracle.com/technetwork/articles/javase/jdk-netbeans-jsp-142931.html#close
[releases]: https://github.com/PixelSergey/PersonalProject/releases
[cleanbuild]: ./github/cleanbuild.png
[fatbuild]: ./github/fatbuild.png
