-----------------------------------------------------------------
Prerequisities-
-----------------------------------------------------------------

1. Start the server on the machine which will host the connected clients.

2. Run ChatServer.java and FileServer.java files in eclipse.

3. After this, run the application in Android Studio.

4.In file ChatNSharMainActivity.java, set the IP address on which the server is hosted.

for example,
private String host_="10.1.70.50";

5. On successful setup of the server on Desktop and on running the application you should be able to see

"Chat server started on port 1337!
 File server started on port 1339!"  

6. On successful installation of client on an Android device and run the application.
Once a user logs in by entering his/her name. They should see "Welcome to ChatNShare" message from the server.

-----------------------------------------------------------------
USER STEPS-
-----------------------------------------------------------------

1.When the user opens the application, the application asks for a name to be used for the group chat.

2.Once the user enters the user name, user can start using all the functionalities.

3.Once all the users join the group chat, they can send text messages, share all types of files and also click pictures and send to each other.

4.The chat history is preserved and is displayed as the user logs in every time.

5.The overflow menu on action bar provides link for - "File Transfer", "Clear Conversation" and "Send Location" as follows -

 -The File Transfer allows user to browse the phone and select a file to be transferred.
 -The clear conversation allows the user to delete the chat history being displayed.
 -Send Location-This feature is still under development. As of now, it will send the current location coordinates (latitude and longitude) of the user. 
   When complete, it should allow the user to load a Google map and send the current as well as any selected location coordinates to the group.

6.The camera button next to the send button allows the user to capture an image and send it to the server for broadcast.
  The captured image is saved in the Download Folder as "temp.jpg".


