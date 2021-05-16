package tr.yildiz.mouneszawal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    private static final int IMAGE_REQ = 20;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private EditText fName,lName ,phone , email, password ,rePass ;
    private ImageView img;
    private Calendar c;
    private Date date;
    private Button dtbtn;
    Uri image = null;
    private final String filename = "usersList.txt";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        variables();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23){
                    if(checkPermission()){
                        filePicker();
                    }else{
                        requestPermission();
                    }
                }else{
                    filePicker();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_REQ) {
                if (data == null) {
                    return;
                }
//                imgUploader.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/")+1));
                image = data.getData();
                img.setImageURI(image);
            }

        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(SignUpActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void filePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");//
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(intent, IMAGE_REQ);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SignUpActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                filePicker();
            } else {
                Toast.makeText(SignUpActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void variables(){
        fName = (EditText) findViewById(R.id.firstName);
        lName = (EditText) findViewById(R.id.lastName);
        phone = (EditText) findViewById(R.id.Phone);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        rePass = (EditText) findViewById(R.id.repassword);
        img = findViewById(R.id.imageView2);
    }

    public void onDateClick(View view){
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int myear, int mmonth, int mday) {
                mmonth++;
                String dt = mday + "/" + mmonth + "/" + myear;
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dtbtn = (Button) findViewById(R.id.datePicker);
                dtbtn.setText(mday + "/" + mmonth + "/" + myear);
                try {
                    date = formatter.parse(dt);
                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, day, month + 1, year);
        dpd.show();

    }


    public void signUp(View view){
        if(fName.getText().toString().equals("")
        || lName.getText().toString().equals("")
        || email.getText().toString().equals("")
        || password.getText().toString().equals("") ){
            Toast.makeText(view.getContext() , "Please Complete your information", Toast.LENGTH_SHORT).show();
        }else if (validation()){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            addUser(hash(password.getText().toString()),
                    email.getText().toString(),
                    phone.getText().toString(),
                    fName.getText().toString(),
                    lName.getText().toString(),
                    (date != null ? dateFormat.format(date) : "null"),
                    (image == null ? "null" : image.toString())
            );
            Intent intent = new Intent(view.getContext(),MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "New account has been created successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addUser(String pass, String email, String phone, String fname, String lname, String date,String imgURI){
        String fileContents = email + "#" + pass + "#" + phone + "#" + fname + "#" + lname + "#" + date + "#" +imgURI;
        try {
            FileOutputStream fos = openFileOutput(getFilename(), Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean validation(){
        if(validatePass() && validateUserName() && !alreadyUser()){
//            Person.users.add(new Person(hash(password.getText().toString()), email.getText().toString(),
//                    phone.getText().toString(), fName.getText().toString(), lName.getText().toString(),date));
            return true;
        }else{
            return false;
        }

    }

    private boolean validatePass(){
        if(password.getText().toString().equals(rePass.getText().toString())){
            return true;
        }else{
            Toast.makeText(this, "passwords don't match!!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateUserName(){
        if(fName.getText().toString().trim().length() <3 || lName.getText().toString().trim().length() <3){
            Toast.makeText(this, "First name and last name should be at least 3 character long!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean alreadyUser(){
        String mail = email.getText().toString();
        String[] currentLine = null;
        try {
            FileInputStream fis = openFileInput(getFilename());
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split("#");
                if(currentLine[0].equals(mail)){
                    Toast.makeText(this, "This Email is already registered!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hash(String pass){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes(),0,pass.length());
            return new BigInteger(1,md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String getFilename() {
        return filename;
    }
}