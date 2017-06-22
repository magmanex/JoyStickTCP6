package com.example.ashira.joysticktcp6;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by ashira on 21/6/2560.
 */

public class SecondScreenActivity extends AppCompatActivity implements SensorEventListener {

    MainActivity TCP = new MainActivity();

    //Part from Joystickv4

    //Integer for messenger
    int check_high=750;
    int x_joy;
    int y_joy;
    int check_yaw=100;
    int check_angle=0;
    int check_sum=0;


    //String for mesenger
    String s_mode = convert_zero3(TCP.check_mode);
    String s_high = convert_zero5(check_high);
    String s_roll = convert_zero3(x_joy) ;
    String s_pitch = convert_zero3(y_joy);
    String s_yaw = convert_zero3(check_yaw);
    String s_angle = convert_zero3(check_angle);
    String s_sum;


    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5,tvHeading;

    com.example.ashira.joysticktcp6.JoyStickClass js;

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    private String msg;

    //Seekbar
    SeekBar seek_bar;
    TextView text_view;

    //Yaw
    TextView textView_Yaw;
    Button button_Y_L,button_Y_R;
    int Dir_Y;

    //Takeoff/on
    boolean State_power=false;
    ToggleButton toggle_state;
    TextView textView_state;


    //End of JoystickV4

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Part from TCP
    /*
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient;
    public final int TCP_PORT = 8080;
    public final String ip = "192.168.0.102";*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);




        //Beginning frome Joystickv4
        //Part from joystickv4
        // our compass image
        image = (ImageView) findViewById(R.id.imageViewCompass);

        // TextView Compass Detail
        tvHeading = (TextView) findViewById(R.id.tvCompass);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Textview Jotstick Detail
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        //Seekbar function
        seebbarr( );



        //Yaw function
        yaw();


        //Take OFF - Landing
        Power_Takeoff();


        //JoyStick
        controller_Joystick();


        //End of joystick v4


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////


        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);


        Button send = (Button)findViewById(R.id.send_button);
        send.setVisibility(View.INVISIBLE);


        //relate the listView from java to the one created in xml
        TCP.mList = (ListView)findViewById(R.id.list);


        //For invisibility mList
        TCP.mList.setVisibility(View.INVISIBLE);



        //Beginning frome tcp client
        //Part from tcp client
        TCP.arrayList = new ArrayList<String>();




        TCP.mAdapter = new MyCustomAdapter(this, TCP.arrayList);
        TCP.mList.setAdapter(TCP.mAdapter);

        // connect to the server
        new connectTask().execute("");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checksum();
                s_sum = convert_zero3(check_sum);
                String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                //add the text in the arrayList
                TCP.arrayList.add("c: " + message);
                send_messege(message);
            }
        });

        /////////////////////////////////////////////////////////////////////////
        //Test for new TCP

        x_joy = js.getX();
        y_joy = js.getY();
        //mTcpClient.send("X : " + String.valueOf(x_joy) + " Y : " + String.valueOf(y_joy),ip,TCP_PORT);
    }




    //Part from TCPclient
    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            TCP.mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            TCP.mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            TCP.arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            TCP.mAdapter.notifyDataSetChanged();
        }
    }



    /////////////////////////////////////////////////////////////////////

    //Part from Joystickv4

    private void controller_Joystick() {

        //joystick
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);
        js = new com.example.ashira.joysticktcp6.JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);



        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    x_joy = js.getX();
                    x_joy += 100;
                    if (x_joy < 0 ) x_joy= 0;
                    if (x_joy > 200 ) x_joy = 200;
                    s_roll = convert_zero3(x_joy);
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    y_joy = js.getY();
                    y_joy += 100;
                    if (x_joy < 0 ) y_joy= 0;
                    if (x_joy > 200 ) y_joy = 200;
                    s_pitch = convert_zero3(y_joy);
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_UP) {
                        textView5.setText("Direction : Up");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                    } else if(direction == com.example.ashira.joysticktcp6.JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                }
                //

                checksum();
                s_sum = convert_zero3(check_sum);
                String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                //add the text in the arrayList
                TCP.arrayList.add("c: " + message);
                send_messege(message);
                //editText.setText("");
                return true;
            }
        });

    }




    private void yaw() {

        // Textview Yaw button detail
        textView_Yaw = (TextView)findViewById(R.id.tvYaw);
        button_Y_L=(Button)findViewById(R.id.button_Y_L);
        button_Y_R=(Button)findViewById(R.id.button_Y_R);

        button_Y_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_yaw -= 1 ;
                s_yaw = convert_zero3(check_yaw);
                textView_Yaw.setText("Yaw : " + check_yaw);
                checksum();
                s_sum = convert_zero3(check_sum);
                String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                //add the text in the arrayList
                TCP.arrayList.add("c: " + message);
                send_messege(message);

            }
        });

        button_Y_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_yaw += 1 ;
                s_yaw = convert_zero3(check_yaw);
                textView_Yaw.setText("Yaw : " + check_yaw);
                checksum();
                s_sum = convert_zero3(check_sum);
                String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                //add the text in the arrayList
                TCP.arrayList.add("c: " + message);
                send_messege(message);
                // mTcpClient.send("X : " + String.valueOf(x_joy) + " Y : " + String.valueOf(y_joy),ip,TCP_PORT);
            }
        });

    }





    private void seebbarr() {
        seek_bar = (SeekBar)findViewById(R.id.seekBar);
        text_view =(TextView)findViewById(R.id.textView);
        text_view.setText("Covered : " + seek_bar.getProgress() + " / " +seek_bar.getMax());

        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        check_high = progress_value;
                        s_high = convert_zero5(check_high);
                        text_view.setText("Covered : " + progress + " / " +seek_bar.getMax());

                        checksum();
                        s_sum = convert_zero3(check_sum);
                        String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                        //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                        //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                        //add the text in the arrayList
                        TCP.arrayList.add("c: " + message);
                        send_messege(message);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                    }
                }
        );
    }



    private void Power_Takeoff() {

        final Context context = this;

        textView_state = (TextView)findViewById(R.id.textView_state);
        //State_power = ((ToggleButton)view).isChecked();
        //String msg;
        toggle_state = (ToggleButton)findViewById(R.id.toggleButton_Takeoff);
        toggle_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    State_power = true;
                    textView_state.setText("State : Landing");
                    TCP.check_mode = 3;
                    s_mode = convert_zero3(TCP.check_mode);
                    checksum();
                    s_sum = convert_zero3(check_sum);
                    String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                    //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                    //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                    //add the text in the arrayList
                    TCP.arrayList.add("c: " + message);
                    send_messege(message);
                } else {
                    State_power = false;
                    textView_state.setText("State : Take off");
                    TCP.check_mode = 1;
                    s_mode = convert_zero3(TCP.check_mode);
                    checksum();
                    s_sum = convert_zero3(check_sum);
                    String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

                    //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
                    //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
                    //add the text in the arrayList
                    TCP.arrayList.add("c: " + message);
                    send_messege(message);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent nextScreen =  new Intent(context , MainActivity.class);

                    startActivity(nextScreen);

                }
            }

        /*if(State_power)
        {
            msg = "Take off";
            textView_state.setText(msg);
        }
        else
        {
            msg = "Landing";
            textView_state.setText(msg);
        }
        */
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();



        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]) ;
        check_angle = Math.round(degree);
        s_angle = convert_zero3(check_angle);


        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees" + "\n" );

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree-90;
/*
        checksum();
        s_sum = convert_zero3(check_sum);
        String message = String.valueOf(s_mode) + "," + String.valueOf(s_high) + "," + String.valueOf(s_roll) + "," + String.valueOf(s_pitch) + "," + String.valueOf(s_yaw) + "," + String.valueOf(s_angle) + ","  + String.valueOf(s_sum) + ",00";

        //String message = "roll : " + s_roll + " check sum roll : " + String.valueOf(checksum_roll()) + "\n" +
        //                    "pitch : " + s_pitch + " check sum pitch : " + String.valueOf(checksum_pitch());
        //add the text in the arrayList
        TCP.arrayList.add("c: " + message);
        send_messege(message);*/



    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //For check sum
    public void checksum()
    {
        check_sum = checksum_mode() + checksum_roll() + checksum_pitch()
                + checksum_high()  + checksum_yaw() + checksum_angle();
    }

    public int checksum_mode()
    {
        int sum=0;
        for (int i = 0;i<s_mode.length();i++)
        {
            sum += Character.getNumericValue(s_mode.charAt(i));
        }
        return sum;
    }

    public int checksum_high()
    {
        int sum=0;
        for (int i = 0;i<s_high.length();i++)
        {
            sum += Character.getNumericValue(s_high.charAt(i));
        }
        return sum;
    }
    public int checksum_roll()
    {
        int sum=0;
        for (int i = 0;i<s_roll.length();i++)
        {
            sum += Character.getNumericValue(s_roll.charAt(i));
        }
        return sum;
    }

    public int checksum_pitch()
    {
        int sum=0;
        for (int i = 0;i<s_pitch.length();i++)
        {
            sum += Character.getNumericValue(s_pitch.charAt(i));
        }
        return sum;
    }

    public int checksum_yaw()
    {
        int sum=0;
        for (int i = 0;i<s_yaw.length();i++)
        {
            sum += Character.getNumericValue(s_yaw.charAt(i));
        }
        return sum;
    }

    public int checksum_angle()
    {
        int sum=0;
        for (int i = 0;i<s_angle.length();i++)
        {
            sum += Character.getNumericValue(s_angle.charAt(i));
        }
        return sum;
    }

    public String convert_zero3(int x)
    {
        String result="";
        if( x< 10 ) result="00" + String.valueOf(x);
        else if ( x < 100 ) result = "0" + String.valueOf(x);
        else result = String.valueOf(x);
        return result;
    }

    public String convert_zero5(int x)
    {
        String result="";
        if( x< 10 )           result = "0000" + String.valueOf(x);
        else if ( x < 100 )   result = "000" + String.valueOf(x);
        else if ( x < 1000 )  result = "00" + String.valueOf(x);
        else if ( x < 10000 ) result = "0" + String.valueOf(x);
        else                  result = String.valueOf(x);
        return result;
    }

    public void send_messege(String message)
    {


        //sends the message to the server
        TCP.mTcpClient.sendMessage(message);


        //refresh the list
        TCP.mAdapter.notifyDataSetChanged();
    }

}
