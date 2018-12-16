package com.demo.www.recordtest;

import android.media.AudioFormat;
import android.util.Log;

import com.demo.www.recordtest.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;

/**
 * Created by chengkai on 18-12-17.
 */

public class EncodeHelper{
    private static final String TAG = "EncodeHelper";
    private static final int outBitrate = 128;
    private static final int quality = 5;//0~9

    public static void encodeMp3(final String inputPath, final String outputPath, final Runnable runnable)
    {
        if(!new File(inputPath).exists()){
            return;
        }
        Utils.checkPath(outputPath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(new File(inputPath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] ID3V1bytes = null;
                byte[] ID3V2bytes = null;

                int inChannel = 2;
                if(RecordHelper.getChannels() != AudioFormat.CHANNEL_IN_STEREO){
                    inChannel = 1;
                }
                TDLameUtil tdLameUtil= new TDLameUtil();
                tdLameUtil.init(RecordHelper.getSamplesRate(),inChannel,RecordHelper.getSamplesRate(), outBitrate,quality);
                File file = new File(outputPath);
                if(file.exists()){
                    file.delete();
                }
                byte[] bytes = new byte[10 * 1024];
                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(file,"rw");
                    ID3V2bytes = tdLameUtil.getID3V2Buffer();
                    randomAccessFile.write(ID3V2bytes,0,ID3V2bytes.length);
                    byte[] mp3data = null;
                    Log.d(TAG,"saveRecordFile: encode mp3 file:开始编码文件  "+ outputPath);
                    while (true){
                        int len = fileInputStream.read(bytes);
                        if(len > 0){
                            byte[] tmp =  new byte[len];
                            System.arraycopy(bytes,0,tmp,0,len);
                            mp3data = tdLameUtil.encode(tmp);
                        }else {
                            break;
                        }
                        if(mp3data == null)
                            continue;
                        randomAccessFile.write(mp3data,0,mp3data.length);
                    }
                    fileInputStream.close();
                    ID3V1bytes = tdLameUtil.getID3V1Buffer();
                    byte[] bytes1 = tdLameUtil.flush(mp3data);
                    if(bytes1 != null){
                        randomAccessFile.write(bytes1,0,bytes1.length);
                    }
                    randomAccessFile.write(ID3V1bytes,0,ID3V1bytes.length);
                    randomAccessFile.close();
                    tdLameUtil.close();
                    runnable.run();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
