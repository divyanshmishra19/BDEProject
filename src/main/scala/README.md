## Running WordCount.scala Prerequisites:

  

### Before you run WordCount.scala, make sure that:

  

+ **You are using JDK 8.**
	+ Spark has an issue with not finding a package during runtime with JDK 17 and 18. Using JDK 8 solves this issue. You can switch JDKs in IntelliJ by going to File -> Project Structure -> SDK -> Add SDK. if you don't have JDK 8, you can download corretto-1.8 through IntelliJ and that works too.

  

+ **Make sure you have netcat installed.**

	+ In order to pass stream input into WordCount, you have to pass it through socket software such as netcat. Mac and Linux users should have netcat installed by default. If you are on windows, you can install it here: https://serverspace.io/support/help/how-to-install-ncat-tool-on_windows-and-linux/. You can enter nc or ncat on the command line to see if you have it installed.

  

+ **Address the "Failed to locate the winutils binary in the hadoop binary path" error for Windows.**
	
	+ Running WordCount.scala on Windows will generate this error and other errors which will clutter the output on the console. To get rid of these errors, download winutils.exe from this link: https://github.com/steveloughran/winutils/blob/master/hadoop-2.7.1/bin/winutils.exe. Then, create the following directory structure in your C drive: "C:\hadoop\bin". Now the setProperty method in WordCount.scala will work and eliminate the hadoop errors.

	+ If you don't care about the errors, then you can comment out the setProperty method.

  
  

## How to run WordCount.scala:

+ On a terminal, run a netcat command that creates a socket on localhost and port 9999.

	+ nc -lk 9999 (for mac and linux)

	+ ncat -lk 9999 (for windows)

	+ The cursor will go away which will allow you to keep entering words after you type text and hit enter

+ Click play button that says run WordCount.scala in IntelliJ to run it.

+ Click the stop button in IntelliJ to stop. Press Ctrl + C in command line to stop netcat

+ The checkpoint and receivedBlockMetadata directories will contain new files after every run, so make sure to maintain it accordingly
