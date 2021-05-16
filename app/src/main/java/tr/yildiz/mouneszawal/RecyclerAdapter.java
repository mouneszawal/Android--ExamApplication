package tr.yildiz.mouneszawal;


import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Question> questions = new ArrayList<>();
    private List<Question> selectedQuestions = new ArrayList<>();

    public List<Question> getSelectedQuestions() {
        return selectedQuestions;
    }

    public void setSelectedQuestions(List<Question> selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }

    private OnItemClickListener mListener;
    private boolean selectionMode;

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean mode) {
        selectionMode = mode;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(itemView, mListener);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView title,option1,option2,option3,option4,option5,rightAns;
        LinearLayout cardLay;
        Button extra;
        CardView myCard;
        int position;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            title   =  itemView.findViewById(R.id.quest_title);
            option1 =  itemView.findViewById(R.id.option1);
            option2 =  itemView.findViewById(R.id.option2);
            option3 =  itemView.findViewById(R.id.option3);
            option4 =  itemView.findViewById(R.id.option4);
            option5 =  itemView.findViewById(R.id.option5);
            rightAns = itemView.findViewById(R.id.right_ans);
            extra = itemView.findViewById(R.id.extra);
            myCard = itemView.findViewById(R.id.myCard);
            cardLay = itemView.findViewById(R.id.cardLay);

            if (!selectionMode){
                myCard.setOnCreateContextMenuListener(this);
            }





            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!selectionMode){
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }else{
                        if (selectedQuestions.contains(questions.get(getAdapterPosition()))){
                            cardLay.setBackgroundColor(Color.TRANSPARENT);
                            selectedQuestions.remove(questions.get(getAdapterPosition()));

                        }else{
                            cardLay.setBackgroundColor(ResourcesCompat.getColor(v.getResources(), R.color.selected, null)); //without theme
                            selectedQuestions.add(questions.get(getAdapterPosition()));
                        }
                    }

                }
            });

            extra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( !questions.get(position).getAdditions().equals("null") && questions.get(position).getAdditions() !=null ){
                        System.out.println(v.getContext().getContentResolver().getType(Uri.parse(questions.get(position).getAdditions())));
                        Bundle myBundle = new Bundle();
                        myBundle.putString("data",questions.get(position).getAdditions());
                        MyFragment frag = new MyFragment();
                        frag.setArguments(myBundle);
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        frag.show(activity.getSupportFragmentManager(),"ADDITIONS");
                    }else{
                        Toast.makeText(v.getContext(), "No additions provided!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),12,0,"Edit this item");
            menu.add(this.getAdapterPosition(),13,1,"Delete this item");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Question currentObject = questions.get(position);

        viewHolder.title.setText(currentObject.getQuesTitle());
        viewHolder.position = position;
        viewHolder.option1.setText(String.format("1) %s", currentObject.getOptions()[0]));
        viewHolder.option2.setText(String.format("2) %s", currentObject.getOptions()[1]));
        viewHolder.option3.setText(String.format("3) %s", currentObject.getOptions()[2]));
        viewHolder.option4.setText(String.format("4) %s", currentObject.getOptions()[3]));
        viewHolder.option5.setText(String.format("5) %s", currentObject.getOptions()[4]));
        viewHolder.rightAns.setText(String.format("Right option:\n%s", currentObject.getRightAnsIndex() + 1));
        viewHolder.extra.setText("Show additions");
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public int getSelectedQuestionsCount() {
        return selectedQuestions.size();
    }

    public void setList(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        questions.remove(position);
        notifyDataSetChanged();
    }


    public List<Question> getList() {
        return questions;
    }
}
