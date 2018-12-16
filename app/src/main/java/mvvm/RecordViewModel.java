package mvvm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.AudioFormat;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.demo.www.recordtest.EncodeHelper;
import com.demo.www.recordtest.MainActivity;
import com.demo.www.recordtest.RecordHelper;
import com.demo.www.recordtest.SplitHelper;

public class RecordViewModel extends BaseObservable {

    private RecordHelper recordHelper;
    private String recordInfo = "录音参数信息:SamplesRate 48000,Channels 2,SamplesBits 16";

    private RecordModel model;

    public RecordViewModel(final RecordModel model) {
        this.model = model;
        int channels = 2;
        int samplesBits = 16;
        if (RecordHelper.getChannels() == AudioFormat.CHANNEL_IN_MONO) {
            channels = 1;
        }
        if (RecordHelper.getSamplesBits() == AudioFormat.ENCODING_PCM_8BIT) {
            samplesBits = 8;
        }

        recordInfo = String.format(
                "录音参数信息:SamplesRate %d,Channels %d,SamplesBits %d",
                RecordHelper.getSamplesRate(),
                channels,
                samplesBits);
        recordHelper = RecordHelper.getInstance();
        recordHelper.setRunningListener(new RecordHelper.RunningListener() {
            @Override
            public void updateTime(long time) {
                model.setTime(time);
                notifyPropertyChanged(BR.timeText);
            }

            @Override
            public void updateLen(long len) {
                model.setLen(len);
                notifyPropertyChanged(BR.lenText);
            }
        });
    }

    public String getRecordInfo() {
        return recordInfo;
    }

    private String startBTText = "开始";
    private String stopBTText = "停止";

    private String timeText = "录制时长:";
    private String lenText = "录制大小:";

    private String PCM_Path = "/sdcard/record/test.pcm";
    private String MP3_Path = "/sdcard/record/test.mp3";

    private String LEFT_Path = "/sdcard/record/left.pcm";
    private String RIGHT_Path = "/sdcard/record/right.pcm";


    private String pcmPath = "null";
    private String mp3Path = "null";

    private String leftPath = "null";
    private String rightPath = "null";

    @Bindable
    public boolean isEnableBT() {
        return !isRecording();
    }

//    public void setEnableBT(boolean enableBT) {
//        this.enableBT = enableBT;
//        notifyChange();
//    }

    @Bindable
    public String getButtonText() {
        if (isRecording()) {
            return stopBTText;
        } else {
            return startBTText;
        }
    }

    @Bindable
    public String getTimeText() {

        int times = (int) (model.getTime() / 1000);

        int h = times / 60 / 60;
        int m = times / 60 % 60;
        int s = times % (60);

        return timeText + String.format("%d:%d.%d s", h, m, s);
    }

    @Bindable
    public String getLenText() {
        if (model.getLen() > 1024 * 1024) {
            return lenText + String.format("%02fM", model.getLen() / 1024f / 1024f);
        } else {
            return lenText + String.format("%dK", model.getLen() / 1024);
        }
    }

    private boolean recording = false;

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public void onRecordClick(View v) {
        if (isRecording()) {
            pcmPath = PCM_Path;
            recordHelper.stop();
            setRecording(false);
        } else {
            pcmPath = "录音中...";
            recordHelper.start(PCM_Path);
            setRecording(true);
        }
        notifyChange();
    }


    public void onEncodeClick(View v) {
        if (model.getLen() == 0) {
            MainActivity.showToast("请先录音");
            return;
        }
        mp3Path = "编码中...";
        EncodeHelper.encodeMp3(PCM_Path, MP3_Path, new Runnable() {
            @Override
            public void run() {
                mp3Path = MP3_Path;
                MainActivity.showToast("编码MP3成功");
                notifyChange();
            }
        });
        notifyChange();
    }

    public void onSplitClick(View v) {
        leftPath = "null";
        rightPath = "null";
        if (model.getLen() == 0) {
            MainActivity.showToast("请先录音");
            return;
        }
        leftPath = "拆分中...";
        rightPath = "拆分中...";
        SplitHelper.split(PCM_Path, LEFT_Path, RIGHT_Path, new Runnable() {
            @Override
            public void run() {
                MainActivity.showToast("拆分声道成功");
                leftPath = LEFT_Path;
                rightPath = RIGHT_Path;
                notifyChange();
            }
        });
        notifyChange();
    }

    private static final String baseTest = "输出目录:";

    @Bindable
    public String getPcmPath() {
        return baseTest + pcmPath;
    }

    @Bindable
    public String getMp3Path() {
        return baseTest + mp3Path;
    }

    @Bindable
    public String getSplitPath() {
        return baseTest + "\n left:" + leftPath + "\n right:" + rightPath;
    }

}
