package tr.yildiz.mouneszawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class PersonelInformation extends AppCompatActivity {
    private TextView name,fname,lname,email,phone;
    private ImageView img;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personel_information);
        defineVariables();
        setInformation();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setInformation() {

        String mail = MainActivity.getCurrUserEmail();
        String[] currentLine = null;
        try {
            FileInputStream fis = openFileInput("usersList.txt");
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split("#");
                System.out.println(line);
                if(currentLine[0].equals(mail)){
                    if (!currentLine[6].equals("null")){
                        img.setImageURI(Uri.parse(currentLine[6]));
                    }else{
                        img.setImageResource(R.drawable.download);
                    }

                    name.setText(String.format("%s %s", currentLine[3], currentLine[4]));
                    fname.setText(currentLine[3]);
                    lname.setText(currentLine[4]);
                    email.setText(currentLine[0]);
                    phone.setText(currentLine[2]);
                    return;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void defineVariables() {
        name = findViewById(R.id.Name);
        fname = findViewById(R.id.Fname);
        lname = findViewById(R.id.Lname);
        email = findViewById(R.id.Email);
        phone = findViewById(R.id.Phone);
        img = findViewById(R.id.prsonalAvatar);
        back = findViewById(R.id.back);
    }


}