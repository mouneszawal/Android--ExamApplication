package tr.yildiz.mouneszawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ExamSettings extends AppCompatActivity {
    private EditText duration,score,diffLevel;
    private Button save;
    public static final String SHARED_NAME = MainActivity.getCurrUserEmail()+"_examSetting";
    public static final String DURATION = "duration";
    public static final String DIFF_LEVEL = "diffLevel";
    public static final String SCORE = "score";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init_var();
        loadData();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid()){
                    saveData();
                    Intent intent = new Intent(v.getContext(), MenuActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(ExamSettings.this, "Difficulty level should be between 2 and 5 only !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean valid() {
        return Integer.parseInt(diffLevel.getText().toString().trim()) <= 5
                && Integer.parseInt(diffLevel.getText().toString().trim()) >= 2;
    }

    private void saveData() {
        SharedPreferences shPref = getSharedPreferences(SHARED_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();

        editor.putString(DURATION  ,duration.getText().toString().trim());
        editor.putString(SCORE     ,score.getText().toString().trim());
        editor.putString(DIFF_LEVEL,diffLevel.getText().toString().trim());
        editor.apply();
        Toast.makeText(this, "Data saved to shared preferences !", Toast.LENGTH_SHORT).show();
    }

    private void loadData(){
        SharedPreferences shPref = getSharedPreferences(SHARED_NAME,MODE_PRIVATE);
        score.setText(shPref.getString(SCORE,"0"));
        duration.setText(shPref.getString(DURATION,"0"));
        diffLevel.setText(shPref.getString(DIFF_LEVEL,"2"));
    }

    private void init_var() {
        duration = findViewById(R.id.duration);
        score = findViewById(R.id.score);
        diffLevel = findViewById(R.id.diffLevel);
        save = findViewById(R.id.save);
    }


}