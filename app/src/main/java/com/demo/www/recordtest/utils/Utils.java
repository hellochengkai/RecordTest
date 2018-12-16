package com.demo.www.recordtest.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by chengkai on 18-12-17.
 */

public class Utils {
    public static void checkPath(String path)
    {
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
