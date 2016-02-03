package lab1.elmp7.lab1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class settingsActivity extends AppCompatActivity {
    private String PREFERENCES = "PREFS";
    private FileOutputStream fos;
    FileInputStream fis;
    private RadioButton OnRadio;
    private RadioButton OffRadio;
    private int TTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        OnRadio = (RadioButton) findViewById(R.id.onRadio);
        OffRadio = (RadioButton) findViewById(R.id.offRadio);
        readAllFromFile();
        if (TTS == 1){
            OnRadio.setChecked(true);
            OffRadio.setChecked(false);
        }

        OffRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRadio.setChecked(false);
            }
        });

        OnRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffRadio.setChecked(false);
            }
        });

    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save the user's currentstate
//        writeAllToFile();
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    public void onPause(){
        writeAllToFile();
        super.onPause();
    }

    private void writeAllToFile(){
        try {
            fos = openFileOutput(PREFERENCES, Context.MODE_PRIVATE);
            if (OnRadio.isChecked()) {
                fos.write(1);
            }
            else{
                fos.write(0);
            }
            fos.flush();
            fos.close();
        }
        catch (Exception e){
            Log.i("writeAllToFile", e.getMessage());
        }
    }

    private void readAllFromFile(){
        try {
            fis = openFileInput(PREFERENCES);
            TTS = fis.read();
            fis.close();
        } catch (Exception e) {
            TTS=0;
            Log.i("readAllFromFile", e.getMessage());
        }
    }
}
