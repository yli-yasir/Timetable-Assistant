# Timetable Assistant
This program was made as practice with Java FX 8. The aim is to assist students of NEU with making _**their own**_ printable timetables from the huge Microsoft Excel (.XLSX) which is a hall timetable.

* For this to work properly, each piece of course information needs to be located in at most one column/row. For example, this wouldn't work if the time of courses was located in two different columns.

## End User Guide
**Use this at your own risk, I am in no way responsible for any damages. This software still has to undergo a lot of testing.**

1. [Click here](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) to Download Java 8, If you don't already have it.

2. [Click here](https://github.com/yli-yasir/Timetable-Assistant/releases/download/v0.91-beta/Timetable.Assistant.jar) to download the program.

3. Open the program, and click choose file. You will have two options:
    1. From Internet: This option grabs the latest timetable from the internet automatically for you.
    1. From PC: This allows you to browse your computer for the timetable file. Which you can manually download from [here](http://library.neu.edu.tr/uploaded-files/classtimetable/index_en.html).
    
<img src="https://i.imgur.com/6zC6uNr.gif" height="400" width="500"/>

4. Provide example information. Start by selecting a course from the table, then select the day, time and hall for that course. It's critical that you provide correct information or you will get incorrect results when you generate your own timetable.

<img src="https://i.imgur.com/D3xy4LL.gif" height="400" width="500"/>

5. Searching will show you courses that are available in the timetable, click a course to add it. If you see two identical courses in the table (You normally shouldn't see that), make sure to add them both.

<img src="https://i.imgur.com/2F5vUJq.gif" height="400" width="500"/>

6. Clicking generate will generate an image of an oraganized timetable for you, which is based on the courses that you have added. You can change the font size as suitable, then click the image to save it.

<img src="https://i.imgur.com/dNRmUXh.gif" height="400" width="500"/>


## Developer Guide
Clone the repo, and get the [Apache POI 3.17](https://poi.apache.org/download.html#POI-3.17) and the [Apache Commons IO 2.6](https://commons.apache.org/proper/commons-io/download_io.cgi) libraries.


## Known Issues

* You might see two of the same course when you are searching, make sure to add them both. This is because one of the courses name might be written in English letters while the others might be written in Turkish letters. (You will not see any difference while using the program, but you will see it in the original .XLSX file.)

* Information in the program (Selected example info, search results...) persist even when you change the .XLSX file.

* Loading a file from the internet might not work, if the program is being started from some locations. This should be fixed later. For now, try moving the program to your desktop and check if the problem persists.

## TODO
* Add ability to alter course details.

    


