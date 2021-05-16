package tr.yildiz.mouneszawal;



import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListExamsAdapter extends RecyclerView.Adapter<ListExamsAdapter.ViewHolder> {

    private List<String> exams = new ArrayList<>();



    private OnItemClickListener mListener;


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
                .inflate(R.layout.exam_item, viewGroup, false);

        return new ViewHolder(itemView, mListener);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView examTitle;
        ImageButton share;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            examTitle = (TextView) itemView.findViewById(R.id.exam);
            share = (ImageButton) itemView.findViewById(R.id.share);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri path = FileProvider.getUriForFile(v.getContext(),"tr.yildiz.mouneszawal",
                            new File(v.getContext().getFilesDir().toString() +"/" +exams.get(getAdapterPosition())));
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_TEXT, "Test");
                    i.putExtra(Intent.EXTRA_STREAM, path);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setType("plain/*");
                    v.getContext().startActivity(i);
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String currentObject = exams.get(position);
        viewHolder.examTitle.setText(currentObject);
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }



    public void setList(List<String> exams) {
        this.exams = exams;
        notifyDataSetChanged();
    }


    public List<String> getList() {
        return exams;
    }
}
