/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 17-Apr-15.
 *
 * File: ChatNShareDBMethods.java
 * Functionalities:
 * Provides getter and setter methods for the database.
 */

package syr.edu.chatnshare;

// Main class for Database Methods
public class ChatNShareDBMethods {

    //class variables
    int _id;
    String _name;
    String _text;

    // Empty constructor
    public ChatNShareDBMethods(){

    }
    // constructor
    public ChatNShareDBMethods(int id, String name, String _text){
        this._id = id;
        this._name = name;
        this._text = _text;
    }

    // constructor
    public ChatNShareDBMethods(String name, String _text){
        this._name = name;
        this._text = _text;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting text message
    public String getText(){
        return this._text;
    }

    // setting text message
    public void setText(String text){
        this._text = text;
    }
}
