package com.example.alex.semesterassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //Login Activity


    SharedPreferences loginData;
    SharedPreferences storedMessages;

    private ArrayList<String> messagesList;
    private ArrayAdapter<String> messageAdapter;
    private ListView messagesView;

    /**
     * Sets up the MessageView(List with stored messages if requiere) and loads some preferences
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messagesList = new ArrayList<String>();
        messageAdapter = new ArrayAdapter<String>(this,R.layout.messageview,messagesList);
        messagesView = (ListView) findViewById(R.id.storedMessagesView);
        messagesView.setAdapter(messageAdapter);

        storedMessages = getSharedPreferences("messages",Context.MODE_PRIVATE);
    }


    /**
     * Store data to login(Socket Address and Username) and then start a new activity
     * @param view
     */
    public void login(View view){
            loginData = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginData.edit();

            EditText addressField = (EditText) findViewById(R.id.addressText);
            EditText userField  = (EditText) findViewById(R.id.userText);

            String address = addressField.getText().toString();
            String user = userField.getText().toString();

            editor.putString("address",address);
            editor.putString("user",user);
            editor.commit();

            Intent intent = new Intent(this,ChatActivity.class);
            startActivity(intent);
    }


    /**
     * Use json to get stored messages and then show it in the ListView.
     * Before adding new data, it deletes data of the list.
     * @param view
     */
    public void storedMessages(View view){
        Gson json = new Gson();

        String aux = storedMessages.getString("stored","");
        Log.i("ArrayList",aux);

        ArrayList<String> auxList = json.fromJson(aux,new TypeToken<ArrayList<String>>() {
        }.getType());

        messagesList.clear();
        for (String message: auxList) {
            messagesList.add(message);
            messageAdapter.notifyDataSetChanged();
        }
        messageAdapter.notifyDataSetChanged();
    }
}




















