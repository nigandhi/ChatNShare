/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 *
 * File: ChatNShareMessage.java
 * Functionalities:
 * Creates chat object for the ChatAdapter
 */
package syr.edu.chatnshare;

// Class for aligning text messages on proper sides on the user screen
public class ChatNShareMessage {

    //variables declaration
    public boolean side;
    public String message;

    public ChatNShareMessage(boolean side, String message) {
        super();
        this.side = side;
        this.message = message;
    }
}

