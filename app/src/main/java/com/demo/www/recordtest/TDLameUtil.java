package com.demo.www.recordtest;


import android.util.Log;


/**
 * Created by chengkai on 17-10-20.
 */

public class TDLameUtil {
    private static final String TAG = "TDLameUtil";
    int lame = 0;
    native static int TDLameInit(int inSamplerate,int inChannel, int outSamplerate, int outBitrate, int quality);
    native static int TDLameEncodeMono(int lame,short[]buffer_l,short[]buffer_r,int samples,byte[] mp3buf);
    native static int TDLameEncodeStereo(int lame,short[]buffer,int samples,byte[] mp3buf);
    native static int TDLameFlush(int lame,byte[] mp3buf);
    native static void TDLameClose(int lame);
    native static byte[] TDLameGetID3V1Buffer(int lame);
    native static byte[] TDLameGetID3V2Buffer(int lame);
    public byte[] getID3V1Buffer()
    {
        byte[] bytes = TDLameGetID3V1Buffer(lame);
        if(bytes!=null){
            Log.d(TAG,"TDLameGetID3V1Buffer {" + bytes.length +"}"+ bytes);
        }
        return bytes;
    }

    public byte[] getID3V2Buffer()
    {
        byte[] bytes = TDLameGetID3V2Buffer(lame);
        if(bytes!=null){
            Log.d(TAG,"TDLameGetID3V2Buffer {" + bytes.length +"}"+ bytes);
        }
        return bytes;
    }


    public static short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) (src[i * 2 + 1] << 8 | src[2 * i] & 0xff);
        }
        return dest;
    }
    private int Channel = 0;

    public void init(int inSamplerate,int inChannel, int outSamplerate, int outBitrate, int quality)
    {
        Channel = inChannel;
        lame = TDLameInit(inSamplerate,inChannel, outSamplerate, outBitrate, quality);
    }
    public byte[] encode(byte[] buffer)
    {
        if(buffer == null)
            return null;
        int ret = 0;
        short[] pcmData = toShortArray(buffer);
        int length = (int)(1.25 * pcmData.length) + 7200;
        byte[] mp3buf = new byte[length];
        if(Channel == 1){
            ret = TDLameEncodeMono(lame,pcmData,pcmData,pcmData.length,mp3buf);
        }else if(Channel == 2){
            ret = TDLameEncodeStereo(lame,pcmData,pcmData.length/2,mp3buf);
        }
        if(ret> 0){
            byte[] bytes = new byte[ret];
            System.arraycopy(mp3buf,0,bytes,0,ret);
            return bytes;
        }
        return null;
    }

    public byte[] flush(byte[] mp3buf)
    {
        if(mp3buf == null)
            return null;
        int length = mp3buf.length + 7200;
        byte[] bytes = new byte[length];
        System.arraycopy(mp3buf,0,bytes,0,mp3buf.length);
       int ret = TDLameFlush(lame,bytes);
        if(ret> 0){
            byte[] bytes1 = new byte[ret];
            System.arraycopy(bytes1,0,bytes,0,ret);
            return bytes1;
        }
        return null;
    }
    public void close()
    {
        TDLameClose(lame);
        lame = 0;
    }
}
