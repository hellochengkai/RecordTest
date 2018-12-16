package com.demo.www.recordtest.utils;

import android.media.AudioFormat;

/**
 * Created by chengkai on 18-1-26.
 */

public class PcmData {

    private byte[] bytesStereo = null;
    private byte[] rightBytes = null;
    private byte[] leftBytes = null;
    private int bytesStereoLen = 0;

    public PcmData()
    {
        bytesStereoLen = 1;
        this.rightBytes = new byte[bytesStereoLen];
        this.leftBytes = new byte[bytesStereoLen];
    }

    public byte[] getRightBytes() {
        return rightBytes;
    }

    public byte[] getLeftBytes() {
        return leftBytes;
    }

    public boolean load(byte[] buffer, int len ,int samplesBits,int channels)
    {
        if(buffer == null || buffer.length < len)
            return false;
        switch (channels){
            case  AudioFormat.CHANNEL_IN_STEREO:{
                //拆分左右声道
                bytesStereo = buffer;
                bytesStereoLen = len;
                split(bytesStereo,len,samplesBits);
                break;
            }
            case  AudioFormat.CHANNEL_IN_MONO:{
                bytesStereo = null;
                bytesStereoLen = len*2;
                this.rightBytes = buffer;
                this.leftBytes = buffer;
                break;
            }
            default:
                break;
        }
        return true;
    }


    private void split(byte[] buffer, int len, int samplesBits){
        if(buffer == null)
            return;
        if(rightBytes == null || leftBytes == null ||
                rightBytes.length < len/2 || leftBytes.length < len/2){
            rightBytes = new byte[len/2];
            leftBytes = new byte[len/2];
        }

        int i = 0;
        switch (samplesBits){
            case AudioFormat.ENCODING_PCM_16BIT:
                for (i = 0; i < len; i+=4) {
                    if((i + 3) < buffer.length){
                        leftBytes[i/2 + 0] = buffer[i + 0];
                        leftBytes[i/2 + 1] = buffer[i + 1];
                        rightBytes[i/2 + 0] = buffer[i + 2];
                        rightBytes[i/2 + 1] = buffer[i + 3];
                    }
                }
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                for (i = 0; i < len; i+=2) {
                    if((i + 1) < buffer.length){
                        leftBytes[i/2 + 0] = buffer[i + 0];
                        rightBytes[i/2 + 0] = buffer[i + 1];
                    }
                }
                break;
            default:
                return;
        }
    }
}
