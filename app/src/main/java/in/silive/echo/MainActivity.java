package in.silive.echo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity{
    AudioRecord record;
    AudioTrack track;
    AudioManager am;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.img);
        img.setImageResource(R.drawable.low);
        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);
        init();
        (new Thread() {
            @Override
            public void run() {
                recordAndPlay();
            }
        }).start();
    }



    private void init() {
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, min);

        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, maxJitter, AudioTrack.MODE_STREAM);
    }

    private void recordAndPlay() {
        short[] lin = new short[1024];
        int num = 0;
        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        record.startRecording();
        track.play();
        while (true) {
            num = record.read(lin, 0, 1024);
            track.write(lin, 0, num);
        }
    }

    boolean isSpeaker = false;

    public void modeChange(View view) {
        Button modeBtn=(Button) findViewById(R.id.modeBtn);
        if (isSpeaker == true) {
            am.setSpeakerphoneOn(false);
            isSpeaker = false;
            modeBtn.setText("Speaker Off");
        } else {
            am.setSpeakerphoneOn(true);
            isSpeaker = true;
            modeBtn.setText("Speaker On");
            img.setImageResource(R.drawable.high);
        }
    }

    boolean isPlaying=true;
    public void play(View view){
        Button playBtn=(Button) findViewById(R.id.playBtn);
        if(isPlaying){
            record.stop();
            track.pause();
            isPlaying=false;
            playBtn.setText("Play");
            img.setImageResource(R.drawable.no);
        }else{
            record.startRecording();
            track.play();
            isPlaying=true;
            playBtn.setText("Pause");
        }
    }
}
