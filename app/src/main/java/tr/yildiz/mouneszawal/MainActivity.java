package tr.yildiz.mouneszawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private int attempts=0;
    public static String currUserEmail = null;
    private final String filename = "usersList.txt";
    public static String getCurrUserEmail() {
        return currUserEmail;
    }

    public static void setCurrUserEmail(String currUserEmail) {
        MainActivity.currUserEmail = currUserEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PopulateDataBase();
    }

    private void PopulateDataBase() {
        if (!isHere("test@gmail.com",SignUpActivity.hash("test1234"))){
            setCurrUserEmail("test@gmail.com");
            addUser(SignUpActivity.hash("test1234"));

            addQuest(new Question("Remote Procedure Calls are used ____________"
                    ,new String[]{"for communication between two processes remotely different from each other on the same system"
                    ,"for communication between two processes on the same system"
                    ,"for communication between two processes on separate systems"
                    ,"all of the mentioned"
                    ,"none of the mentioned"}
                    ,2
                    ,"null"));

            addQuest(new Question("To differentiate the many network services a system supports ______ are used."
                    ,new String[]{"Variables"
                    ,"Sockets"
                    ,"Ports"
                    ,"Service names"
                    ,"none of the mentioned"}
                    ,2
                    ,"null"));

            addQuest(new Question("RPC provides a(an) _____ on the client-side, a separate one for each remote procedure."
                    ,new String[]{"stub"
                    ,"identifier"
                    ,"name"
                    ,"process identifier"
                    ,"none of the mentioned"}
                    ,0
                    ,"null"));

            addQuest(new Question("What is stub?"
                    ,new String[]{"transmits the message to the server where the server side stub receives the message and invokes procedure on the server side"
                    ,"packs the parameters into a form transmittable over the network"
                    ,"locates the port on the server"
                    ,"all of the mentioned"
                    ,"none of the mentioned"}
                    ,3
                    ,"null"));

            addQuest(new Question("What is the full form of RMI?"
                    ,new String[]{"Remote Memory Installation"
                    ,"Remote Memory Invocation"
                    ,"Remote Method Installation"
                    ,"Remote Method Invocation"
                    ,"none of the mentioned"}
                    ,3
                    ,"null"));

            addQuest(new Question("Which module gives control of the CPU to the process selected by the short-term scheduler?"
                    ,new String[]{"dispatcher"
                    ,"interrupt"
                    ,"scheduler"
                    ,"process queue"
                    ,"none of the mentioned"}
                    ,0
                    ,"null"));

            addQuest(new Question("The processes that are residing in main memory and are ready and waiting to execute are kept on a list called _____________"
                    ,new String[]{"job queue"
                    ,"ready queue"
                    ,"execution queue"
                    ,"process queue"
                    ,"priority queue"}
                    ,1
                    ,"null"));

            addQuest(new Question("Which algorithm is defined in Time quantum?"
                    ,new String[]{"shortest job scheduling algorithm"
                    ,"round robin scheduling algorithm"
                    ,"priority scheduling algorithm"
                    ,"multilevel queue scheduling algorithm"
                    ,"FCFS scheduling algorithm"}
                    ,1
                    ,"null"));

        }
    }


    private void addQuest(Question quest){
        String fileContents = quest.getQuesTitle() +
                "#" +quest.getOptions()[0] + "#" +quest.getOptions()[1] + "#" +quest.getOptions()[2] + "#" +quest.getOptions()[3] + "#"
                +quest.getOptions()[4] + "#" + quest.getRightAnsIndex() +"#"+ quest.getAdditions();
        try {
            FileOutputStream fos = openFileOutput("test@gmail.com.txt", Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUser(String pass){
        String fileContents = "test@gmail.com" + "#" + pass + "#" + "0546712493" + "#" + "Mounes" + "#" + "Zawal" + "#" + "12/12/2020"+"#"+"null";
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onBtnClick(View view){
        EditText name = (EditText)findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        String emailStr = name.getText().toString();
        String PsdStr = password.getText().toString();
        if(isHere(emailStr.trim(),SignUpActivity.hash(PsdStr))){//Person.ifExist
            Intent myintent = new Intent(view.getContext(), MenuActivity.class);
            setCurrUserEmail(emailStr.trim());
            startActivity(myintent);
        }else{
            attempts++;
            //System.out.println(userNameStr);
            Toast.makeText(view.getContext() , "Please enter your information correctly !", Toast.LENGTH_SHORT).show();

            if(attempts >= 3){
                // disable the button
//                findViewById(R.id.button).setEnabled(false);
                Toast.makeText(view.getContext() , "Create a new account!", Toast.LENGTH_SHORT).show();
                onSigClick(view);
            }
        }
    }

    public void onSigClick(View view){
        Intent intent = new Intent(view.getContext(), SignUpActivity.class);
        startActivity(intent);
    }


    public boolean isHere(String email,String pass){
        String[] currentLine = null;
        try {
            FileInputStream fis = openFileInput(filename);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split("#");
                if(currentLine[0].equals(email)  && currentLine[1].equals(pass)){
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}