package tr.yildiz.mouneszawal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AddQuestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_REQ = 2;
    private static final int SOUND_REQ = 3;
    private static final int VID_REQ = 4;
    private EditText questionTitle,optionA,optionB,optionC,optionD,optionE;
    private int rightAnswer;
    private String[] options;
    private Button vidUploader,imgUploader,sndUploader;
    private Uri addition;
    private Button submit,update;
    private String title;
    private boolean isUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        defineVars();
        defineListeners();
        Bundle b = getIntent().getExtras();
        if(b != null) {
            isUpdate=true;
            submit.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            title = b.getString("title");
            questionTitle.setText(title);
            optionA.setText(b.getString("option1"));
            optionB.setText(b.getString("option2"));
            optionC.setText(b.getString("option3"));
            optionD.setText(b.getString("option4"));
            optionE.setText(b.getString("option5"));

            rightAnswer = b.getInt("rightAns");

            if (!b.getString("addition").equals("null")){
                addition = Uri.parse(b.getString("addition"));
                if(this.getContentResolver().getType(addition).contains("audio")){
                    sndUploader.setText(addition.toString().substring(addition.toString().lastIndexOf("/")+1));
                }else if(this.getContentResolver().getType(addition).contains("image")){
                    imgUploader.setText(addition.toString().substring(addition.toString().lastIndexOf("/")+1));
                }else if (this.getContentResolver().getType(addition).contains("video")){
                    vidUploader.setText(addition.toString().substring(addition.toString().lastIndexOf("/")+1));
                }
            }
        }
        showSpinner();
    }

    private void defineVars(){
        questionTitle = (EditText)findViewById(R.id.questionTitle);
        optionA = (EditText)findViewById(R.id.optionA);
        optionB = (EditText)findViewById(R.id.optionB);
        optionC = (EditText)findViewById(R.id.optionC);
        optionD = (EditText)findViewById(R.id.optionD);
        optionE = (EditText)findViewById(R.id.optionE);
        vidUploader = (Button) findViewById(R.id.addvid);
        imgUploader = (Button) findViewById(R.id.addimg);
        sndUploader = (Button) findViewById(R.id.addsound);
        options =  new String[5];
        submit = findViewById(R.id.Submit);
        update = findViewById(R.id.update);
    }


    private void defineListeners(){

        vidUploader.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //check permission
                if (Build.VERSION.SDK_INT >= 23){
                    if(checkPermission()){
                        filePicker(2);
                    }else{
                        requestPermission();
                    }
                }else{
                    filePicker(2);
                }
            }
        });

        imgUploader.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //check permission
                if (Build.VERSION.SDK_INT >= 23){
                    if(checkPermission()){
                        filePicker(0);
                    }else{
                        requestPermission();
                    }
                }else{
                    filePicker(0);
                }
            }
        });

        sndUploader.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //check permission
                if (Build.VERSION.SDK_INT >= 23){
                    if(checkPermission()){
                        filePicker(1);
                    }else{
                        requestPermission();
                    }
                }else{
                    filePicker(1);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(questionTitle.getText().toString().equals("")
                        || optionA.getText().toString().equals("")
                        || optionB.getText().toString().equals("")
                        || optionC.getText().toString().equals("")
                        || optionD.getText().toString().equals("")
                        || optionE.getText().toString().equals("")){
                    Toast.makeText(v.getContext(), "Please Enter All fields !", Toast.LENGTH_SHORT).show();
                }else{
                    if ((isQuestExist(questionTitle.getText().toString().trim()) &&
                            title.equals(questionTitle.getText().toString().trim()) )
                        || ( !isQuestExist(questionTitle.getText().toString().trim()) &&
                            !title.equals(questionTitle.getText().toString().trim()) ) ){
                        Intent resultIntent = new Intent();
                        Bundle b = new Bundle();
                        b.putString("title",questionTitle.getText().toString().trim());
                        b.putString("option1", optionA.getText().toString().trim());
                        b.putString("option2",optionB.getText().toString().trim());
                        b.putString("option3",optionC.getText().toString().trim());
                        b.putString("option4",optionD.getText().toString().trim());
                        b.putString("option5",optionE.getText().toString().trim());
                        b.putInt("rightAns",rightAnswer);
                        b.putString("addition",(addition != null ? addition.toString() : "null"));
                        resultIntent.putExtras(b);

                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                }
            }
        });

    }

    private void showSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.rightAnswerspn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.answers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(isUpdate)
                spinner.setSelection(rightAnswer,true);
        spinner.setOnItemSelectedListener(this);
    }

    public void onSubmit(View v){
        if(questionTitle.getText().toString().equals("")
                || optionA.getText().toString().equals("")
                || optionB.getText().toString().equals("")
                || optionC.getText().toString().equals("")
                || optionD.getText().toString().equals("")
                || optionE.getText().toString().equals("")){
            Toast.makeText(v.getContext(), "Please Enter All fields !", Toast.LENGTH_SHORT).show();
        }else{
            options[0] = optionA.getText().toString().trim();
            options[1] = optionB.getText().toString().trim();
            options[2] = optionC.getText().toString().trim();
            options[3] = optionD.getText().toString().trim();
            options[4] = optionE.getText().toString().trim();
            if (!isQuestExist(questionTitle.getText().toString().trim())){
                addQuest(new Question(questionTitle.getText().toString().trim(),options,rightAnswer,(addition != null ? addition.toString() : "null")));
                Toast.makeText(this, "Question added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
            }
        }
    }

    private void addQuest(Question quest){
        String fileContents = quest.getQuesTitle() +
                "#" +quest.getOptions()[0] + "#" +quest.getOptions()[1] + "#" +quest.getOptions()[2] + "#" +quest.getOptions()[3] + "#"
                +quest.getOptions()[4] + "#" + quest.getRightAnsIndex() +"#"+ quest.getAdditions();
        try {
            FileOutputStream fos = openFileOutput(MainActivity.getCurrUserEmail()+".txt", Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isQuestExist(String title) {
        String[] currentLine;
        try {
            FileInputStream fis = openFileInput(MainActivity.getCurrUserEmail()+".txt");
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split("#");
                if (currentLine[0].equals(title)){
                    if(!isUpdate)
                        Toast.makeText(this, "The question title already exists!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void filePicker(int i) {
        if(i==0){
            Intent intent = new Intent();
            intent.setType("image/*");//
            intent.setAction(Intent.ACTION_PICK);//
            startActivityForResult(intent, IMAGE_REQ);
        } else if(i == 2){
            Intent intent = new Intent();
            intent.setType("video/*");//
            intent.setAction(Intent.ACTION_PICK);//
            startActivityForResult(intent, VID_REQ);
        }else if(i ==1){
            Intent intent = new Intent();
            intent.setType("audio/*");//
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(intent, SOUND_REQ);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_REQ) {
                if (data == null) {
                    return;
                }
                imgUploader.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
                addition = data.getData();

                sndUploader.setText("ADD-Sound");
                vidUploader.setText("ADD-Video");
            }

            if (requestCode == SOUND_REQ) {
                if (data == null) {
                    return;
                }
                sndUploader.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
                addition = data.getData();
                imgUploader.setText("ADD-Image");
                vidUploader.setText("ADD-Video");
            }

            if (requestCode == VID_REQ) {
                if (data == null) {
                    return;
                }
                vidUploader.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
                addition = data.getData();
                sndUploader.setText("ADD-Sound");
                imgUploader.setText("ADD-Image");
            }

        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddQuestionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddQuestionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AddQuestionActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddQuestionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AddQuestionActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddQuestionActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rightAnswer = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
