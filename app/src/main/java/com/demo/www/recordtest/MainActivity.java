package com.demo.www.recordtest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.demo.www.recordtest.databinding.ActivityMainBinding;

import mvvm.RecordModel;
import mvvm.RecordViewModel;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_CODE_STARTAUDIO = 753;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    ActivityMainBinding binding;
    RecordViewModel viewModel;
    private static Handler handler;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new RecordViewModel(new RecordModel());
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        // Example of a call to a native method

        binding.setViewModel(viewModel);

        if (PackageManager.PERMISSION_GRANTED ==   ContextCompat.
                checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
        }else{
            //提示用户开户权限音频
            String[] perms = {"android.permission.RECORD_AUDIO"};
            ActivityCompat.requestPermissions(this,perms, RESULT_CODE_STARTAUDIO);
        }
        handler = new Handler();
        context = getApplicationContext();
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case RESULT_CODE_STARTAUDIO:
                boolean albumAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(!albumAccepted){
                    Toast.makeText(getApplicationContext(),"请开启应用录音权限",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static void postEventToMainThread(Runnable runnable)
    {
        handler.post(runnable);
    }

    public static void showToast(final String msg)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
