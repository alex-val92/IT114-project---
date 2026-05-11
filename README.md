The Deal setup guide
Based on your terminal errors, follow these exact commands in order:

1. COMPILE THE CODE:
   In your PowerShell window, run:
   javac AuctionServer.java AuctionClientHandler.java AuctionGUI.java

2. START THE SERVER:
   Run the following command to start the backend:
   java -cp . AuctionServer

3. START THE CLIENT (GUI):
   Open a SECOND PowerShell window, navigate to the same folder, and run:
   java TheDealGui

COMMON FIXES:
- The "-cp ." tells Java to look in the current folder for your files.
- If "javac *.java" fails, PowerShell is the problem; listing the filenames manually (as shown in Step 1) fixes it.
- If "java filename" fails javac --release 8 filename.java should fix 
