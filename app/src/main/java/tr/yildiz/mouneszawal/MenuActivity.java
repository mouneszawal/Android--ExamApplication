package tr.yildiz.mouneszawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MenuActivity extends AppCompatActivity {
    Button addQues,listQues,createEx,settings,profile,exams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        vars();
        listeners();
    }




    private void vars(){
        profile = (Button)findViewById(R.id.profile);
        addQues = (Button)findViewById(R.id.addquestion);
        listQues = (Button)findViewById(R.id.listQuest);
        createEx = (Button)findViewById(R.id.createExam);
        settings = (Button)findViewById(R.id.settings);
        exams = (Button)findViewById(R.id.exams);
    }

    private void listeners(){

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonelInformation.class);
                startActivity(intent);
            }
        });

        addQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddQuestionActivity.class);
                startActivity(intent);
            }
        });

        listQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileExist(MainActivity.getCurrUserEmail()+".txt")){
                    Intent intent = new Intent(v.getContext(), ListQuestions.class);
                    Bundle b = new Bundle();
                    b.putBoolean("selectionMode",false);
                    intent.putExtras(b);
                    startActivity(intent);
                }else{
                    Toast.makeText(MenuActivity.this, "No questions to list", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListQuestions.class);
                Bundle b = new Bundle();
                b.putBoolean("selectionMode",true);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExamSettings.class);
                startActivity(intent);
            }
        });

        exams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListExams.class);
                startActivity(intent);
            }
        });


    }

    private boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
}
