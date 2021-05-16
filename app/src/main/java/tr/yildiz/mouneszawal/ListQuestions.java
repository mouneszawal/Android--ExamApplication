package tr.yildiz.mouneszawal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ListQuestions extends AppCompatActivity implements AttentionDialog.DialogListener {
    private RecyclerAdapter adapter;
    private LinearLayout settings;
    public static final String SHARED_NAME = MainActivity.getCurrUserEmail()+"_examSetting";
    public static final String DURATION = "duration";
    public static final String DIFF_LEVEL = "diffLevel";
    public static final String SCORE = "score";
    private EditText duration,score,diff,title;
    private Button createExam;
    private boolean selectionMode;
    private int position ;
    private List<File> exams;

    public List<File> getExams() {
        return exams;
    }

    public void setExams(List<File> exams) {
        this.exams = exams;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_questions);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle b = getIntent().getExtras();
        adapter = new RecyclerAdapter();

        selectionMode = b.getBoolean("selectionMode");
        if (selectionMode){
            settings = findViewById(R.id.selectionLay);
            settings.setVisibility(View.VISIBLE);

            title = findViewById(R.id.getTitle);
            duration = findViewById(R.id.getDuration);
            score = findViewById(R.id.getScore);
            diff = findViewById(R.id.getDiffLevel);
            loadData();

        }
        createExam = findViewById(R.id.createExam);

        adapter.setSelectionMode(selectionMode);
        adapter.setList(getQuestionList());

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(), "Long press to delete or edit !", Toast.LENGTH_LONG).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);


        createExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedQuestionsCount()>0){
                    if (valid()){
                        saveData();
                        if (createFile(adapter.getSelectedQuestions(),
                                Integer.parseInt(diff.getText().toString().trim()),
                                title.getText().toString().trim()))
                        {
                            Toast.makeText(ListQuestions.this, "Exam created successfully!", Toast.LENGTH_SHORT).show();
                            updateExamDataBase();
                            Intent myintent = new Intent(v.getContext(), MenuActivity.class);
                            startActivity(myintent);
                        }
                    }else{
                        Toast.makeText(v.getContext(), "Difficulty level should be between 2 and 5 only and title shouldn't be empty!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ListQuestions.this, "At least one Item should be selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateExamDataBase() {
        try {
            FileOutputStream fos = openFileOutput("examDatabase.txt", Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(MainActivity.getCurrUserEmail()+title.getText().toString().trim()+".txt");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean createFile(List<Question> selected,int diffLevel,String fileName) {
        if (!fileExist(fileName+".txt")){
            try {
                FileOutputStream fos = openFileOutput(fileName+".txt", Context.MODE_APPEND);
                PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
                writer.println("Name:");
                writer.println("Exam duration: "+duration.getText().toString().trim() + " mins.");
                writer.println("Difficulty level: "+ diff.getText().toString().trim());
                int k=1;
                for (Question quest: selected){
                    writer.println("________________________________");
                    writer.println("");
                    writer.println("Question: "+quest.getQuesTitle() + " (" + score.getText().toString().trim() + " points)");
                    writer.println("");
                    String [] opt = quest.getOptions();
                    List<String> toDisplay = new ArrayList<String>();
                    toDisplay.add(quest.getOptions()[quest.getRightAnsIndex()]);
                    opt = removeTheElement(opt, quest.getRightAnsIndex());
                    toDisplay.addAll(Arrays.asList(opt).subList(0,diffLevel - 1));
                    System.out.println(toDisplay);
                    int i =0;
                    while(i < diffLevel ){
                        int rnd = new Random().nextInt(toDisplay.size());
                        writer.println(i+1 + ") " + toDisplay.get(rnd));
                        toDisplay.remove(rnd);
                        i++;
                    }
                    k++;
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }else{
            Toast.makeText(this, "An exam with the same title already exists!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String[] removeTheElement(String[] arr, int rightAnsIndex) {
        if (arr == null
                || rightAnsIndex < 0
                || rightAnsIndex >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        String[] anotherArray = new String[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == rightAnsIndex) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }


    private boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private boolean valid() {
        return Integer.parseInt(diff.getText().toString().trim()) <= 5
                && Integer.parseInt(diff.getText().toString().trim()) >= 2
                && !title.getText().toString().equals("");
    }

    private void saveData() {
        SharedPreferences shPref = getSharedPreferences(SHARED_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();

        editor.putString(DURATION  ,duration.getText().toString().trim());
        editor.putString(SCORE     ,score.getText().toString().trim());
        editor.putString(DIFF_LEVEL,diff.getText().toString().trim());
        editor.apply();
    }

    private void loadData(){
        SharedPreferences shPref = getSharedPreferences(SHARED_NAME,MODE_PRIVATE);
        score.setText(shPref.getString(SCORE,"0"));
        duration.setText(shPref.getString(DURATION,"0"));
        diff.setText(shPref.getString(DIFF_LEVEL,"2"));
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        position = item.getGroupId();
        if(!selectionMode){
            switch (item.getItemId()){
                case 12:
                    Intent myIntent = new Intent(this,AddQuestionActivity.class);
                    Bundle b = new Bundle();
                    b.putString("title",adapter.getList().get(item.getGroupId()).getQuesTitle());
                    b.putString("option1",adapter.getList().get(item.getGroupId()).getOptions()[0]);
                    b.putString("option2",adapter.getList().get(item.getGroupId()).getOptions()[1]);
                    b.putString("option3",adapter.getList().get(item.getGroupId()).getOptions()[2]);
                    b.putString("option4",adapter.getList().get(item.getGroupId()).getOptions()[3]);
                    b.putString("option5",adapter.getList().get(item.getGroupId()).getOptions()[4]);
                    b.putString("addition",adapter.getList().get(item.getGroupId()).getAdditions());

                    b.putInt("rightAns",adapter.getList().get(item.getGroupId()).getRightAnsIndex());
                    myIntent.putExtras(b);
                    startActivityForResult(myIntent,20);
                    return true;
                case 13:
                    openDialog();
                    return true;

            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if(resultCode == RESULT_OK){
                //Do something
                assert data != null;
                String[] options = new String[5];
                Bundle b = data.getExtras();
                adapter.getList().get(position).setQuesTitle(b.getString("title"));
                options[0] = b.getString("option1");
                options[1] = b.getString("option2");
                options[2] = b.getString("option3");
                options[3] = b.getString("option4");
                options[4] = b.getString("option5");
                adapter.getList().get(position).setOptions(options);
                adapter.getList().get(position).setRightAnsIndex(b.getInt("rightAns"));
                adapter.getList().get(position).setAdditions(b.getString("addition"));
                saveChanges(adapter.getList());
                adapter.setList(getQuestionList());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void openDialog() {
        AttentionDialog dialog = new AttentionDialog();
        dialog.show(getSupportFragmentManager(),"Confirm");
    }


    private List<Question> getQuestionList() {
        List<Question> list = new ArrayList<Question>();
        String[] currentLine;
        try {
            FileInputStream fis = openFileInput(MainActivity.getCurrUserEmail()+".txt");
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] options = new String[5];
                currentLine = line.split("#");
                options[0] = currentLine[1];
                options[1] = currentLine[2];
                options[2] = currentLine[3];
                options[3] = currentLine[4];
                options[4] = currentLine[5];

                list.add(new Question(currentLine[0],options,Integer.parseInt(currentLine[6]),currentLine[7]));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveChanges(List<Question> questions) {
        deleteContent(MainActivity.getCurrUserEmail()+".txt");
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(MainActivity.getCurrUserEmail()+".txt", Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Question quest: questions){
            String fileContents = quest.getQuesTitle() +
                    "#" +quest.getOptions()[0] + "#" +quest.getOptions()[1] + "#" +quest.getOptions()[2] + "#" +quest.getOptions()[3] + "#"
                    +quest.getOptions()[4] + "#" + quest.getRightAnsIndex() +"#"+(quest.getAdditions() != null ? quest.getAdditions() : "null");
            writer.println(fileContents);
        }
        writer.close();

    }


    private void deleteContent(String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
        writer.print("");
        writer.close();
    }


    @Override
    public void onDeleteClicked() {
        adapter.removeItem(position);
        saveChanges(adapter.getList());
        Toast.makeText(this, "Item deleted !", Toast.LENGTH_SHORT).show();
    }

}