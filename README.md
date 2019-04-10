# ArtStationApplication

This application was created as a developer tool to aid in the creation of Processing sketches. Various shapes can be drawn and editted on  screen. See screenshot below:

![Screenshot](https://github.com/jwilder4690/ArtStationApplication/blob/master/artStationScreenShot2.png)

The export option can be used output the processing code to your clipboard or to create an image or SVG file. The JAR file is available above in the ArtStation.zip. This application is still in development, please report any issues. 

Submitted for approval!

## Contributor/Developer Notes:

Hello! Thanks for your interest in contributing to my project! You can get started with some different ways of contributing here.

### Project Directory Structure
This project was developed in the NetBeans IDE 8.2 with the JDK 1.8. 

- src/ contains all the source code for the library, which is topically organized into separated modules. This is what you'll work on if you are working on a bug or enhancement.
- tests/ contains unit and behavior tests which ensure the library continues to function correctly as changes are made.

### How to Contribute
Known bugs and intended new features are tracked using GitHub issues. Issue labels are used to sort issues into categories, such as those which are suitable for beginners. If you'd like to start working on an existing issue, comment on the issue that you plan to work on it so other contributors know it's being handled and can offer help. Once you have completed your work on this issue, submit a pull request (PR) against the master branch. In the description field of the PR, include "resolves #XXXX" tagging the issue you are fixing. If the PR addresses the issue but doesn't completely resolve it (ie the issue should remain open after your PR is merged), write "addresses #XXXX". Because this application is in active development, be sure that you are working from the most recent version when you submit your pull request. 

### How to Test Changes
I am currently working on developing a testing suite for this project. There are a few unit tests and a behavior test that are currently operational. To test your changes, run the ArtStationApplicationTest.java file. This test should run sucessfully and generate an image called "output.png" in the testOutputs folder. Visually compare this image to the image saved in expectedOutputs folder, and also open up the save file called "outputs.txt" inside the testSaveFile folder. All three should be identical. 

## Task List:

- [x] Initial launch with all basic functionality 
- [x] Refactor
- [x] Prepare for Integration as Processing tool
  - [x] Drop new release
  - [x] Complete Requirements listed at https://github.com/processing/processing/wiki/Tool-Guidelines
  - [x] Submit for approval 
- [x] Address major Issues
- [ ] Prepare for final feature drop
  - [ ] Groups 
  - [ ] Final bugs
- [ ] Update Testing Environment



