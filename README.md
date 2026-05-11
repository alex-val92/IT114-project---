AUCTION SYSTEM SETUP GUIDE
==========================

Based on your terminal errors, follow these exact commands in order:

1. COMPILE THE CODE:
   In your PowerShell window, run:
   javac AuctionServer.java AuctionClientHandler.java AuctionGUI.java

2. START THE SERVER:
   Run the following command to start the backend:
   java -cp . AuctionServer

   (Note: Keep this window open. You should see "Server Live on Port5000")

3. START THE CLIENT (GUI):
   Open a SECOND PowerShell window, navigate to the same folder, and run:
   java -cp . AuctionGUI

COMMON FIXES:
- The "-cp ." tells Java to look in the current folder for your files.
- If "javac *.java" fails, PowerShell is the problem; listing the filenames manually (as shown in Step 1) fixes it.
