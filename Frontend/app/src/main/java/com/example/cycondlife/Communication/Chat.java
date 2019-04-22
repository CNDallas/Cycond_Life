package com.example.cycondlife.Communication;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cycondlife.Game.Player;
import com.example.cycondlife.R;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public class Chat extends AppCompatActivity {

    private static Context context;
    private Button sendBut;
    private Button refreshBut;
    private TextView userMessage;
    private TextView chatText;
    private URI forReconnect;
    private boolean firstCreate = true;
    private Activity thisActivity;
    private Player player = Player.get_instance();
    private ChatSender sender = player.getSender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBut = findViewById(R.id.sendButton);
        refreshBut = findViewById(R.id.refreshButton);
        userMessage = findViewById(R.id.userText);
        chatText = findViewById(R.id.chatBox);
        context = getApplicationContext();

        chatText.setMovementMethod(new ScrollingMovementMethod());

        try {
            forReconnect = new URI("ws://cs309-sd-6.misc.iastate.edu:8080/websocket/" + player.getName());
        }
        catch (URISyntaxException e)    {
            e.printStackTrace();
        }

        thisActivity = this;


        //noConnection = new Toast.makeText(getChatContext(), "Failed to connect to server", Toast.LENGTH_SHORT);

        //The following was for when chat was not persistent, most likely can be delted in the future
//        try {
//            toUse = new URI("wss://echo.websocket.org");
//        }   catch (URISyntaxException e)    {
//            e.printStackTrace();
//        }
//
//        if(firstCreate) {
//            sender = new ChatSender();
//            sender.passChatBox(chatText);
//            sender.connectWebSocket(toUse);
//            firstCreate = false;
//        }

        sender.passChatBox(chatText);
        sender.passActivity(thisActivity);
        sender.setChatText();


        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
                userMessage.setText("");
            }
        });

        refreshBut.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                WebSocketClient chat = sender.getWebSocket();

                if(chat.isOpen())  {
                    chat.close();
                }

                sender.connectWebSocket(forReconnect);
                sender.passChatBox(chatText);
                sender.passActivity(thisActivity);
                sender.setChatText();
            }
        });
    }



    public void send()    {
        sender.sendMsg("CHAT " + userMessage.getText().toString());

        //chatText.setText(sender.getReceivedStuff());
    }

    public static Context getChatContext()  {
        return context;
    }


}