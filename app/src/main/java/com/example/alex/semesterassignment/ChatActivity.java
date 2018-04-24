package com.example.alex.semesterassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    LinearLayout chatLayout;


    private ArrayList<String> messagesList;
    private ArrayAdapter<String> messageAdapter;
    private ListView messagesView;
    private EditText sendingMessage;

    SharedPreferences loginData;
    SharedPreferences messageData;
    SharedPreferences.Editor messageWriter;

    String address;
    String user;

    Socket sc;
    BufferedReader socketReader;
    PrintWriter socketWriter;

    /**
     * Set up preferences for the app and starts one thread listening to socket. Also sets up a
     * long click listener on the List to save messages.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        loginData = getSharedPreferences("login", Context.MODE_PRIVATE);
        messageData = getSharedPreferences("messages",Context.MODE_PRIVATE);
        messageWriter = messageData.edit();

        address = loginData.getString("address","");
        user = loginData.getString("user","");

        messagesList = new ArrayList<String>();
        messageAdapter = new ArrayAdapter<String>(this,R.layout.messageview,messagesList);
        messagesView = (ListView) findViewById(R.id.messages);
        messagesView.setAdapter(messageAdapter);



        sendingMessage = (EditText) findViewById(R.id.messageText);

        messagesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                storeMesssage(view);
                return true;
            }
        });

        new Thread(){
             public void run(){
                 try {
                     sc = new Socket("192.168.56.1",7777);
                     socketReader = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                     socketWriter = new PrintWriter(sc.getOutputStream());

                     socketWriter.println(user);
                     socketWriter.flush();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 while(true){
                     try {
                         int i = 0;
                         String aux = socketReader.readLine();
                         messagesList.add(aux);
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 messageAdapter.notifyDataSetChanged();
                             }
                         });
                     } catch (IOException e) {
                         Log.i("conexion","Fallo al conectar");
                     }
                 }
             }
         }.start();
    }

    /**
     * Starts a thread to send the message to the server. It should be in other thread because
     * you can't use "block" action on the UI thread.
     * @param v
     */
    public void sendMessage(View v) {
        new Thread(){
            public void run(){
                String aux = sendingMessage.getText().toString();
                socketWriter.println(aux);
                socketWriter.flush();
            }
        }.start();
        sendingMessage.setText("");
    }

    /**
     * First of all converts the ArrayList with messages to JSON and then store it in preferences.
     * Finally it shows a toast.
     * @param v
     */
    public void storeMesssage(View v){
        Gson json = new Gson();

        String arrayInJson = json.toJson(messagesList);

        messageWriter.putString("stored",arrayInJson);
        messageWriter.commit();


        //Maybe we should do this inside an intent
        Toast toast = Toast.makeText(getApplicationContext(), "Stored messages", Toast.LENGTH_SHORT);
        toast.show();
    }

}














