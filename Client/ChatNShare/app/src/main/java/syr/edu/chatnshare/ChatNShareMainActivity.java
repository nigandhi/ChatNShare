/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 *
 * File: ChatNShareMainActivity.java
 * Functionalities:
 * Main chat activity of ChatnShare
 * Defines methods for sending and receiving chat messages, files,
 * and events for image capture and file selection.
 */

package syr.edu.chatnshare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ChatNShareMainActivity extends Activity {

    // Socket variables Declarations
    private static Socket socket_;
    private static Socket socketFileTransfer_;
    private static String host_="10.1.70.50";
    private static int port=1337;
    private static int portFileTransfer=1339;
    // Variable Declarations
    ImageButton getImage;
    ChatNShareDBHandler db = new ChatNShareDBHandler(this);
    private Handler handler = new Handler();
    private ChatNShareAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private static final String TAG = "ChatNShareTag";
    private static String user_name="";
    private String location="";
    private String lat="";
    private String lon="";
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        //Set content view for main Activity
        setContentView(R.layout.chatnshare_activity);

        //Finding controls
        getImage = (ImageButton) findViewById(R.id.buttonimg);
        listView = (ListView) findViewById(R.id.chatView);
        buttonSend = (Button) findViewById(R.id.chatSend);

        //Set chat Adapter
        chatArrayAdapter = new ChatNShareAdapter(getApplicationContext(), R.layout.chatnshare_message);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.chatText);

        chatText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        //Calling Receive message and Receive File methods
        messageReceive();
        fileReceive();

        //On button Click Listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // Calling method to send message to server
                sendChatMessage();
            }
        });

        //Camera button click Listener
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Method to capture Image
                pickImage();
            }
        });

        //To scroll the listview till bottom
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //used for scrolling the list view till bottom of view
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //To extract Extras from the intent
        Bundle userName = getIntent().getExtras();
        int temp=-1;
        if (userName!=null) {
            user_name = userName.getString("Login_name");
            temp = userName.size();
        }

        //Displaying ChatHistory on Application startup
        //db.deleteHistory();
        List<ChatNShareDBMethods> chatHistory = db.getHistory();
        boolean flag;

        for (ChatNShareDBMethods cn : chatHistory) {
            String log = cn.getName()+":" + cn.getText();
            flag=true;

            if(cn.getName().equals(user_name))
                flag=false;
            displayMsg(flag,log);
        }
        if(temp==3) {
            user_name = userName.getString("Login_name");
            lat = userName.getString("Lat");
            lon = userName.getString("Long");
            location = "Latitude: " + lat + ", Longitude: " + lon;
            sendChatMessage();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflating Menu Items
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatnshare_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        // Calling activities according to the selected menu Item
        switch (item.getItemId()) {
            // Delete History
            case R.id.delete:
                db.deleteHistory();
                chatArrayAdapter = new ChatNShareAdapter(getApplicationContext(), R.layout.chatnshare_message);
                listView.setAdapter(chatArrayAdapter);
                return true;

            //File Transfer
            case R.id.file:
                Log.d("file button clicked","");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, 113);
                return true;

            //Location Management
            case R.id.location:
                Intent myIntent = new Intent(ChatNShareMainActivity.this,
                        ChatNShareLocation.class);
                myIntent.putExtra("Username",user_name);
                startActivity(myIntent);
                Log.d("location button clicked","");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Checking the request code and calling the respective function
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Camera Activity Capture
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
               Log.d("camera", "Image Captured");

                try {
                    sendImageFromCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //File Activity Capture
        else if (requestCode == 113 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Log.d("Document", "File Captured");
                try {
                    readTextFromUri(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//<-----------------------Camera Image Handling (Capture, Fetch and Send)------------------------------------------------->//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Method for capturing Image
    public void pickImage() {

        //Setting path and sending captured image to the location
        File picture = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "temp.jpg");

        //Calling camera Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
        Log.d("camera", "before send " + picture.toString());
        startActivityForResult(intent, 111);
    }

    //Extract captured image from Downloads folder and Send that image to the Server
    private void sendImageFromCamera() throws IOException {

        //Finding path of the Camera Image
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/"+"temp.jpg";
        FileInputStream fis=new FileInputStream(path);

        //Sending the Image file
        String filename="temp.jpg"+"\n";
        byte buf[] = new byte[1024];
        int len;

        //Output Stream for the file
        OutputStream os = socketFileTransfer_.getOutputStream();
        byte[] buf2=filename.getBytes();
        os.write(buf2,0,buf2.length);
        os.flush();
        SystemClock.sleep(100);

        while ((len = fis.read(buf)) != -1)
        {
            os.write(buf, 0, buf.length);
            os.flush();
        }

        String end="End";
        byte[] buf1=end.getBytes();
        SystemClock.sleep(100);
        Log.i(TAG,buf1.length+"");
        os.write(buf1,0,buf1.length);
        os.flush();
    }
//////////////////////////////////////////////////////////////////////////////////
//<-----------------------Chat Message Handling methods------------------------>//
//////////////////////////////////////////////////////////////////////////////////

    //Method for Message Handling
    private boolean sendChatMessage(){

        // Retrieving the name of the user through the variable in login activity
        String text="";
        if(location.equals("")) {
            text=chatText.getText().toString();

        }else {
            text=location;
            location="";
            lat="";
            lon="";
        }
        chatArrayAdapter.add(new ChatNShareMessage(false, user_name + ":\n" + text));
        //Adding message to the Database
        db.addMessage(new ChatNShareDBMethods(user_name, text));

        //Send Message to Server
        sendMessageToServer(user_name+":"+text);

        //Clearing the Send Message text box after message has been sent
        chatText.setText("");

        return true;
    }

    // Method for sending message to the Server
    public void sendMessageToServer(String str) {
        final String str1=str;
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Declaring socket
                Socket socket= new Socket();
                PrintWriter out;

                //Sending message in an Output Stream
                try {
                    socket=socket_;
                    out = new PrintWriter(socket.getOutputStream());
                    out.println(str1);
                    Log.d("Sending Msg:", str1);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Function for receiving message from the Server. Runs on seperate thread
    public void messageReceive()
    {
        //Setting up a new thread for receiving message so that the functioning becomes smooth
        new Thread(new Runnable()
        {
            @Override
            public void run() {

                // Setting the IP address
                //final String host="10.1.70.50";
                final String host=host_;
                // New socket declaration
                Socket socket = new Socket() ;
                BufferedReader in = null;
                try {
                    socket = new Socket(host,port);
                    socket_=socket;
                    int port = socket.getLocalPort();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                while(true)
                {
                    String msg = null;
                    String user=null;
                    int index=0;
                    try {
                        while(!in.ready()){}
                        msg = in.readLine();
                        Log.d("","Message:  "+ msg);
                        displayMsg(true,msg);
                        index=msg.indexOf(":");
                        if(index==-1) continue;
                        user=msg.substring(0,index);
                        msg=msg.substring(index+1);
                        db.addMessage(new ChatNShareDBMethods(user, msg));
                        //msgList.add(msg);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
////////////////////////////////////////////////////////////////////////
//<------------------Bind Adapter to the text------------------------>//
////////////////////////////////////////////////////////////////////////
    // Displaying the message and send control to the chat adapter
    public void displayMsg(final boolean side,String msg)
    {
        final String mess=msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                chatArrayAdapter.add(new ChatNShareMessage(side, mess));

                //Showing messages in the List View
                listView.setAdapter(chatArrayAdapter);
                listView.setSelection(chatArrayAdapter.getCount() - 1);
                Log.d("","Server says Hi");
            }
        });
    }

///////////////////////////////////////////////////////////////////////////////////////
//<-----------------------File Handler (Send and Receive)--------------------------->//
///////////////////////////////////////////////////////////////////////////////////////

    // Reads the file from the uri and send it to the server
    private void readTextFromUri(Intent myIntent) throws IOException {
        Uri uri=myIntent.getData();
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        String filename=returnCursor.getString(nameIndex)+"\n";
        InputStream is = getContentResolver().openInputStream(uri);

        byte buf[] = new byte[1024];
        int len;
        OutputStream os = socketFileTransfer_.getOutputStream();
        byte[] buf2=filename.getBytes();
        os.write(buf2,0,buf2.length);
        os.flush();

        SystemClock.sleep(100);

        while ((len = is.read(buf)) != -1)
        {
            os.write(buf, 0, buf.length);
            os.flush();
        }

        String end="End";
        byte[] buf1=end.getBytes();
        SystemClock.sleep(200);
        Log.i(TAG,buf1.length+"");
        os.write(buf1,0,buf1.length);
        os.flush();

    }

    //Function for receiving file from the server. Runs on a seperate thread.
    public void fileReceive()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                final String host=host_;
                //final String host="10.1.70.50";
                Socket socketFileTransfer = new Socket() ;
                BufferedReader br = null;
                InputStream in=null;
                try {
                    socketFileTransfer=new Socket(host,portFileTransfer);
                    socketFileTransfer_=socketFileTransfer;
                    int port = socketFileTransfer_.getLocalPort();
                    in=socketFileTransfer_.getInputStream();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    br = new BufferedReader(new InputStreamReader(socketFileTransfer_.getInputStream()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                while(true)
                {
                    String filename = null;
                    try {

                        filename = br.readLine();
                        Log.d("","File Received:  "+ filename);
                        displayMsg(true,filename);
                        int byteRead=0;
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
                        FileOutputStream os = new FileOutputStream(path+ "/" + filename);
                        byte[] byteArray = new byte[1024];
                        int bytes=0;
                        while((byteRead = in.read(byteArray, 0, byteArray.length))!= -1 )
                        {
                            String temp=new String(byteArray,"UTF-8");
                            if(temp.contains("End")){
                                Log.d(TAG,"File Received"+temp);
                                displayMsg(true,"File Received"+filename);
                                break;
                            }
                            os.write(byteArray, 0, byteRead);

                            bytes+=byteRead;
                            Log.d("",""+byteRead);

                        }
                        synchronized(os){
                            os.wait(100);
                        }
                        os.close();

                    }
                    catch (Exception e) {
                        System.out.println("Error thrown from wait.....!!!");
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}