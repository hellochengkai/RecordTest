package com.demo.www.recordtest;

import android.os.Handler;

import com.demo.www.recordtest.utils.PcmData;
import com.demo.www.recordtest.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chengkai on 18-12-17.
 */

public class SplitHelper {
    public static void split(String inputPath, final String outLeftPath, final String outRightPath,final Runnable runnable) {
        final File file = new File(inputPath);
        if (!file.exists()) {
            return;
        }
        Utils.checkPath(outLeftPath);
        Utils.checkPath(outRightPath);
        new Thread() {
            @Override
            public void run() {
                super.run();
                FileOutputStream fileOutputStreamLeft = null;
                FileOutputStream fileOutputStreamRight = null;

                FileInputStream fileInputStream = null;
                PcmData pcmData = new PcmData();
                byte[] bytes = new byte[10 * 1024];

                try {
                    fileOutputStreamLeft = new FileOutputStream(new File(outLeftPath));
                    fileOutputStreamRight = new FileOutputStream(new File(outRightPath));
                    fileInputStream = new FileInputStream(file);
                    int len = 0;
                    while ((len = fileInputStream.read(bytes)) > 0) {
                        boolean ret = pcmData.load(bytes, len, RecordHelper.getSamplesBits(), RecordHelper.getChannels());
                        if (ret) {
                            fileOutputStreamLeft.write(pcmData.getLeftBytes());
                            fileOutputStreamRight.write(pcmData.getRightBytes());
                        }
                    }
                    fileOutputStreamLeft.close();
                    fileOutputStreamRight.close();
                    fileInputStream.close();
                    runnable.run();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
