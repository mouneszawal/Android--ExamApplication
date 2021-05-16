package tr.yildiz.mouneszawal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ListExams extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exams);
        ListExamsAdapter adapter = new ListExamsAdapter();
        adapter.setList(getExamDatabase());

        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
    }

    private List<String> getExamDatabase() {
        List<String> list = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("examDatabase.txt");
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                System.out.println(line);
                if(line.contains(MainActivity.getCurrUserEmail())){
                    System.out.println(line.substring(MainActivity.getCurrUserEmail().length()));
                    list.add(line.substring(MainActivity.getCurrUserEmail().length()));
                }

            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}