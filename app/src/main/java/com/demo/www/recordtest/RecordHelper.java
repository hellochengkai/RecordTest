package com.demo.www.recordtest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.demo.www.recordtest.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chengkai on 18-12-17.
 */

public class RecordHelper {
    private AudioRecord audioRecord = null;

    private static int samplesRate = 48000;
    private static final int samplesBits = AudioFormat.ENCODING_PCM_16BIT;
    private static final int channels = AudioFormat.CHANNEL_IN_STEREO;
    private static RecordHelper instance;

    public static int getSamplesRate() {
        return samplesRate;
    }

    public static int getSamplesBits() {
        return samplesBits;
    }

    public static int getChannels() {
        return channels;
    }

    public static RecordHelper getInstance() {
        if(instance == null){
            instance = new RecordHelper();
        }
        return instance;
    }

    public RecordHelper() {
        int minBufferSize = AudioRecord.getMinBufferSize(samplesRate, channels, samplesBits);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                samplesRate, channels, samplesBits,
                minBufferSize * 2);//增大地层缓冲区大小
    }

    private RecordThread thread;

    public void start(String path)
    {
        Utils.checkPath(path);
        stop();
        thread = new RecordThread(path);
        flag = true;
        thread.start();
    }

    private boolean flag = true;
    public void stop(){
        flag = false;
        if(thread == null){
            return;
        }
        if(thread.isAlive()){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
    }

    private RunningListener runningListener;

    public void setRunningListener(RunningListener runningListener) {
        this.runningListener = runningListener;
    }

    private class RecordThread extends Thread{

        private String path;

        public RecordThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {

            super.run();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(new File(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            long len = 0;
            long startTime = System.currentTimeMillis();
            byte[] bytes = new byte[10 * 1024];
            audioRecord.startRecording();
            while (flag){
                int ret = audioRecord.read(bytes,0,bytes.length);
                if(ret > 0){
                    len += ret;
                    if(runningListener != null){
                        runningListener.updateLen(len);
                        runningListener.updateTime(System.currentTimeMillis() - startTime);
                    }
                    try {
                        fileOutputStream.write(bytes,0,ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            audioRecord.stop();
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface RunningListener{
        void updateTime(long time);
        void updateLen(long len);
    }
}
