package lab1.elmp7.lab1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Constants
    private static final int STRIKEOUT = 3;
    private static final int WALK = 4;
    private String PREFERENCES = "PREFS";
    private static final String STRIKE_COUNT = "strikeCount";
    private static final String BALL_COUNT = "ballCount";
    private static final String OUT_COUNT = "outCount";
    private static final String OUTSFILE = "outs";

    //GUI Components
    private Button strikeButton;
    private Button ballButton;
    private TextView strikeDisplay;
    private TextView ballDisplay;
    private TextView outDisplay;

    //Variables
    private int ballCount;
    private int strikeCount;
    private int outCount;
    private int TTS;
    ActionMode mActionMode;
    AlertDialog.Builder alertMessage;

    //Streams
    FileOutputStream fos;
    FileInputStream fis;
    TextToSpeech talker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find GUI Components
        strikeButton = (Button) findViewById(R.id.strikeButton);
        ballButton = (Button) findViewById(R.id.ballButton);
        strikeDisplay = (TextView) findViewById(R.id.strikeCounter);
        ballDisplay = (TextView) findViewById(R.id.ballCounter);
        outDisplay = (TextView) findViewById(R.id.outsCounterText);
        talker=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=talker.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{

                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            strikeCount = savedInstanceState.getInt(STRIKE_COUNT);
            ballCount = savedInstanceState.getInt(BALL_COUNT);
            outCount = savedInstanceState.getInt(OUT_COUNT);
            displayAllText();
            getTTSPrefs();
        } else {
            // Probably initialize members with default values for a new instance
            readAllFromFile();
            displayAllText();
            getTTSPrefs();
        }

        buildAlert();
        strikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++strikeCount;
                if (strikeCount == STRIKEOUT) {
                    ++outCount;
                    newBatter();
                    displayAlert(getResources().getString(R.string.out));
                    displayAllText();
                } else {
                    //strikeDisplay.setText(String.valueOf(strikeCount));
                    displayAllText();
                }
            }
        });

        strikeButton.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });

        ballButton.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });

        ballButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++ballCount;
                if (ballCount == WALK) {
                    newBatter();
                    displayAlert(getResources().getString(R.string.walk));
                } else {
                    displayAllText();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's currentstate
        savedInstanceState.putInt(STRIKE_COUNT, strikeCount);
        savedInstanceState.putInt(BALL_COUNT, ballCount);
        savedInstanceState.putInt(OUT_COUNT, outCount);
        writeAllToFile();

        super.onSaveInstanceState(savedInstanceState);
    }
    private void readAllFromFile(){
        try {
            fis = openFileInput(OUTSFILE);
            outCount = fis.read();
            ballCount = fis.read();
            strikeCount = fis.read();
            fis.close();
            displayAllText();
            fis = openFileInput(PREFERENCES);
            TTS = fis.read();
            fis.close();
        } catch (Exception e) {
            Log.i("readAllFromFile", e.getMessage());
        }
    }

    @Override
    public void onResume(){
        getTTSPrefs();
        super.onResume();
    }

    private void writeAllToFile(){
        try {
            fos = openFileOutput(OUTSFILE, Context.MODE_PRIVATE);
            fos.write(outCount);
            fos.write(ballCount);
            fos.write(strikeCount);
            fos.flush();
            fos.close();
        }
        catch (Exception e){
            Log.i("writeAllToFile", e.getMessage());
        }
    }

    private void newBatter(){
        ballCount = 0;
        strikeCount = 0;
        displayAllText();
    }

    private void getTTSPrefs(){
        try {
            fis = openFileInput(PREFERENCES);
            TTS = fis.read();
            fis.close();
        } catch (Exception e) {
            TTS=0;
            Log.i("readAllFromFile", e.getMessage());
        }
    }

    private void displayAllText(){
        strikeDisplay.setText(String.valueOf(strikeCount));
        ballDisplay.setText(String.valueOf(ballCount));
        outDisplay.setText(String.valueOf(outCount));
    }

    private void buildAlert(){
        alertMessage = new AlertDialog.Builder(MainActivity.this);
        alertMessage.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
    private void displayAlert(String displayMessage){
        alertMessage.setMessage(displayMessage);
        if (TTS == 1){

            talker.setLanguage(Locale.US);
            talker.speak(displayMessage, TextToSpeech.QUEUE_ADD, null);
        }
        alertMessage.show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.aboutItem:
                Intent i = new Intent(getApplicationContext(), aboutActivity.class);
                startActivity(i);
                return true;
            case R.id.resetItem:
                newBatter();
                return true;
            case R.id.settingsItem:
                Intent SettingsI = new Intent(getApplicationContext(), settingsActivity.class);
                startActivity(SettingsI);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.LongPressBall:
                    ballButton.callOnClick();
                    //mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.LongPressStrike:
                    strikeButton.callOnClick();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };


}

