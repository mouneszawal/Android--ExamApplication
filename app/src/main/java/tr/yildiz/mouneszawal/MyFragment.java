package tr.yildiz.mouneszawal;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;

public class MyFragment extends DialogFragment {
    Button play,pause,stop;
    ImageView img;
    VideoView vid;
    MediaPlayer mPlayer;
    boolean isAud=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_1,container,false);
        Uri additions = Uri.parse(this.getArguments().getString("data"));
        if (v.getContext().getContentResolver().getType(additions).contains("audio")){
            play = v.findViewById(R.id.play);
            pause = v.findViewById(R.id.pause);
            stop = v.findViewById(R.id.stop);
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.VISIBLE);
            pause.setEnabled(false);
            stop.setVisibility(View.VISIBLE);
            stop.setEnabled(false);

            mPlayer =new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(v.getContext(),additions);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayer.start();
                    pause.setEnabled(true);
                    stop.setEnabled(true);
                    isAud = true;
                }
            });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayer.pause();
                }
            });

            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopIt();
                    pause.setEnabled(false);
                    play.setEnabled(false);
                }
            });
        }else if(v.getContext().getContentResolver().getType(additions).contains("image")){
            img = v.findViewById(R.id.imageView);
            img.setVisibility(View.VISIBLE);
            img.setImageURI(null);
            img.setImageURI(additions);
        }else if(v.getContext().getContentResolver().getType(additions).contains("video")){
            vid = v.findViewById(R.id.videoView);
            vid.setVisibility(View.VISIBLE);
            vid.setVideoURI(additions);
            MediaController controller = new MediaController(v.getContext());
            vid.setMediaController(controller);
            controller.setAnchorView(vid);
//            controller.show();
            vid.start();
        }

        return v;
    }

    private void stopIt() {
        mPlayer.release();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isAud)
            stopIt();
    }

}
