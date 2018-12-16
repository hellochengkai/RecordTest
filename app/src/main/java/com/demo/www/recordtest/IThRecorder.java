package com.demo.www.recordtest;

/**
 * Created by chengkai on 18-3-6.
 */

public interface IThRecorder {
    int init(int samplesRate, int channels, int samplesBits);
    int release();
    int start();
    int read(byte[] bytes, int offset, int len);
    int stop();
    int setVolume(int vol);
}
