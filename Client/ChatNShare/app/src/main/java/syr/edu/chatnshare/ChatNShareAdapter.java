/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 *
 * File: ChatNShareAdapter.java
 * Functionalities:
 * Displays bubbles for the chat text to be displayed on the main chat window.
 *
 */
package syr.edu.chatnshare;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class ChatNShareAdapter extends ArrayAdapter<ChatNShareMessage> {

    //private variables declaration
    private TextView chatText; //Textview for chat window
    private List<ChatNShareMessage> chatMessageList = new ArrayList<ChatNShareMessage>(); //ArrayList to show messages on chat window
    private LinearLayout ChatNShareContainer; //Container for Message Window

    @Override
    //Method to add chat message to List View for display
    public void add(ChatNShareMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    //Adapter
    public ChatNShareAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    //Method to return size of chat message list
    public int getCount() {
        return this.chatMessageList.size();
    }

    //returns an item from chat message list
    public ChatNShareMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    // Method to get the initial view for chat window
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Inflating the layout window
            row = inflater.inflate(R.layout.chatnshare_message, parent, false);
        }
        ChatNShareContainer = (LinearLayout) row.findViewById(R.id.ChatNShareContainer);//Container for chat window
        ChatNShareMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.ChatNShareMessage);
        chatText.setText(chatMessageObj.message);

        //putting the images for displaying messages
        chatText.setBackgroundResource(chatMessageObj.side ? R.drawable.bubble_b : R.drawable.bubble_a);
        ChatNShareContainer.setGravity(chatMessageObj.side ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

}
