package com.example.ashira.joysticktcp6;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    TCPClient TCP;
    //Part from TCP
    public static int check_mode=0;
    public static ListView mList;
    public static ArrayList<String> arrayList;
    public static MyCustomAdapter mAdapter;
    public static TCPClient mTcpClient;
    public static String SERVERIP = "192.168.0.102";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonconnect = (Button)findViewById(R.id.buttonCon);
        Button buttonnext = (Button)findViewById(R.id.buttonNext);
        final Context context = this;

        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextScreen =  new Intent(context , SecondScreenActivity.class);

                // starting new activity
                startActivity(nextScreen);

            }
        });



        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);


        Button send = (Button)findViewById(R.id.send_button);
        send.setVisibility(View.INVISIBLE);

        final EditText edittext = (EditText)findViewById(R.id.editText2);


        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);


        //For invisibility mList
        mList.setVisibility(View.INVISIBLE);


        buttonconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SERVERIP = edittext.getText().toString();
                TCP.SERVERIP = SERVERIP;
                //Part from tcp client
                arrayList = new ArrayList<String>();




                mAdapter = new MyCustomAdapter(context, arrayList);
                mList.setAdapter(mAdapter);


                // connect to the server
                new connectTask().execute("");
            }
        });

        //Beginning frome tcp client





        Button btnNextScreen = (Button) findViewById(R.id.btnNextScreen);

        //Listening to button event
        btnNextScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent

                String message = "001,00000,000,000,000,000,001,00";
                check_mode = 1;
                //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                //add the text in the arrayList
                arrayList.add("c: " + message);
                send_messege(message);
                Intent nextScreen =  new Intent(context , SecondScreenActivity.class);

                // starting new activity
                startActivity(nextScreen);

            }
        });
    }


    public void send_messege(String message)
    {


        //sends the message to the server
        mTcpClient.sendMessage(message);


        //refresh the list
        mAdapter.notifyDataSetChanged();
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }

}
