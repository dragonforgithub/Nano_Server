package com.example.administrator.nano_server;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

import com.cmcc.media.hfp.aidl.ITelephone;
import com.cmcc.media.hfp.aidl.ITelephoneCallback;


/*Nanosic Server Packet*/
public class MainActivity extends AppCompatActivity {
    public final String TAG = "APP_Client";
    final String ZTE_SERVICE_ACTION = "com.cmcc.media.hfp.client.service";
    final String ZTE_SERVICE_PACKAGE_NAME = "com.cmcc.media.hfp.aidl";

    static final int KEY_VIDEOCALL_ONOFF = 546;
    static final int KEY_VOICE_SWITCH = 547;


    private static String URI_PCM = "/mnt/sdcard/duandian.wav";

    private boolean mBound = false;
    private boolean gRC_channel = false;
    private ITelephone iRemoteService;
    private TextView tvCurEvent;
    private TextView tvCamInfo;

    //audio task test
    private Button btnAudioTask;
    private AudioTask mAudioTask = null;

    //media player test
    private Button btnMusic;
    private MediaPlayer mediaPlayer=null;
    private List<String> list_music=new ArrayList<String>();
    private Spinner spinner_list;
    private String musicPlayPath;

    //opensl es test
    private Button btnOpl;
    private boolean isOplPlaying=false;

    //camere test
    private Button btnCamera;
    private Camera mCamera = null;
    private CameraPreview mCameraSurPreview = null;
    private SurfaceView previewCamera = null;
    private boolean isCameraOpened = false;

    private final int MY_PERMISSION_REQUEST_CODE = 10000;

    private final String[] strPermissions  = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.loadLibrary("nano_opensles");

        tvCurEvent = findViewById(R.id.curEvent);
        tvCamInfo  = findViewById(R.id.camInfo);

        /**
         *发送广播
         */
        findViewById(R.id.sendBroadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent( "rtk.ota.broadcast" );

                intent.setComponent( new ComponentName( "com.nanosic.www.wnf1x0.remoteupgrade" ,
                        "com.nanosic.www.wnf1x0.remoteupgrade.BootBroadcastReceiver"));

                sendBroadcast(intent);
                alert("发送广播包:");
                */

                byte cmd=1;
                Log.d(TAG, "Start update remote control");
                Intent intent = new Intent();
                intent.setAction("rtk.ota.broadcast");
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.putExtra("rtk.ota.broadcast",cmd);
                sendBroadcast(intent);
            }
        });

        /**
         *注册回调
         */
        findViewById(R.id.registerCallback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }

                try {
                    if (iRemoteService != null){
                        iRemoteService.registerCallBack(mCallback);
                        tvCurEvent.setText("注册回调");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         *注销回调
         */
        findViewById(R.id.unregisterCallback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }

                if (iRemoteService != null) {
                    try {
                        iRemoteService.unregisterCallBack(mCallback);
                        tvCurEvent.setText("注销回调");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /**
         *通知手柄切换通道按钮
         */
        findViewById(R.id.setSpeakerOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }

                if (iRemoteService != null) {
                    try {
                        iRemoteService.setSpeakerOn(gRC_channel);
                        tvCurEvent.setText("通知手柄切换通道->"+(gRC_channel==true?"HDMI":"手柄"));
                        gRC_channel=!gRC_channel;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /**
         *通知手柄发起呼叫按钮
         */
        findViewById(R.id.dialCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }

                if (iRemoteService != null) {
                    try {
                        iRemoteService.dialCall("11111111111");
                        tvCurEvent.setText("通知手柄发起呼叫->"+"11111111111");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /**
         *通知手柄来电振铃
         */
        findViewById(R.id.incomingCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }
                if (iRemoteService != null) {
                    try {
                        iRemoteService.incomingCall("22222222222");
                        tvCurEvent.setText("通知手柄来电振铃->"+"22222222222");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        /**
         *通知手柄接听来电
         */
        findViewById(R.id.answerCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }
                if (iRemoteService != null) {
                    try {
                        iRemoteService.answerCall("33333333333");
                        tvCurEvent.setText("通知手柄接听来电->"+"33333333333");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        /**
         *通知手柄挂断通话
         */
        findViewById(R.id.hangupCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    alert("未连接到远程服务");
                    return;
                }
                if (iRemoteService != null) {
                    try {
                        iRemoteService.hangupCall("44444444444");
                        tvCurEvent.setText("通知手柄挂断通话->"+"44444444444");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        /** 判断SDK版本，确认是否动态申请权限 **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /** 第 1 步: 检查是否有相应的权限 **/
            if(checkPermissionAllGranted(strPermissions)==false) {
                /** 第 2 步: 请求权限,一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉 **/
                ActivityCompat.requestPermissions(
                        this,
                        strPermissions,
                        MY_PERMISSION_REQUEST_CODE
                );
            }

            /** 第 3 步: 判断权限申请结果，如用户未同意则引导至设置界面打开权限 **/
            int[] grantResults={0};
            onRequestPermissionsResult(MY_PERMISSION_REQUEST_CODE,strPermissions,grantResults);
        }

        /** 录播初始化 **/
        btnAudioTask=(Button) findViewById(R.id.audioTask);
        btnAudioTask.setOnClickListener(new bTnOnClickListener());
        mAudioTask = new AudioTask();

        /**初始化音乐播放器**/
        createEngine(); //创建opensl es
        initMediaPlayer(); //创建 mediaplayer

        /**初始化**/
        initCamera();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) { //重写的键盘按下监听

        switch (keyCode){
            case KEY_VIDEOCALL_ONOFF:
                break;
            case KEY_VOICE_SWITCH:
                try {
                    iRemoteService.setSpeakerOn(gRC_channel);
                    tvCurEvent.setText("通知手柄切换通道->"+(gRC_channel==true?"HDMI":"手柄"));
                    gRC_channel=!gRC_channel;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
        alert("获取系统keyCode : "+ keyCode);
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        super.onDestroy();

        if (iRemoteService != null) {
            try {
                iRemoteService.unregisterCallBack(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        if(mAudioTask !=null){
            mAudioTask.stop_Record_Play();
            mAudioTask=null;
        }

        shutdown();

        Log.i("Sheldon", "onDestroy() is called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            attemptToBindService();
        }
        Log.i("Sheldon", "onStart() is called");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        Log.i("Sheldon", "onStop() is called");
    }

    /**
     * 尝试与服务端建立连接
     */
    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setAction(ZTE_SERVICE_ACTION);
        intent.setPackage(ZTE_SERVICE_PACKAGE_NAME);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(getLocalClassName(), "service connected");
            iRemoteService = ITelephone.Stub.asInterface(service);
            if (iRemoteService != null) {
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(getLocalClassName(), "service disconnected");
            mBound = false;
        }
    };

    /**
     * 添加回调到服务端
     */
    private ITelephoneCallback mCallback = new ITelephoneCallback.Stub() {

        /**
         * 手柄通知STB切换通道
         */
        public boolean setSpeakerOn(boolean state) throws RemoteException {
            Log.d(TAG, String.format("setSpeakerOnCallback -> %s",state==true?'1':'0'));
            tvCurEvent.setText("手柄通知STB切换通道->"+(state?"HDMI":"手柄"));
            alert("setSpeakerOn:"+state);
            //return state;
            return false;
        }

        /**
         * 手柄通知STB接听来电
         */
        public boolean answerCall() throws RemoteException {
            Log.d(TAG, String.format("answerCallCallback"));
            tvCurEvent.setText("手柄通知STB接听来电");
            return false;
        }

        /**
         * 手柄通知STB挂断通话
         */
        public boolean hangupCall() throws RemoteException {
            Log.d(TAG, String.format("hangupCallCallback"));
            tvCurEvent.setText("手柄通知STB挂断通话");
            return false;
        }

        /**
         * 手柄通知STB发起呼叫
         */
        public boolean dialCall(String phoneNumber) throws RemoteException {
            Log.d(TAG, String.format("TV call -> %s",phoneNumber));
            tvCurEvent.setText("手柄通知STB发起呼叫->"+phoneNumber);
            return false;
        }

        /**
         * 手柄上报DTMF键值
         */
        public boolean sendDtmf(int dtmf){
            Log.d(TAG, String.format("sendDtmf -> %d",dtmf));
            return false;
        }
    };


    /**
     * Toast提示
     */
    private void alert(String str) {
        //解决在子线程中调用Toast的异常情况处理(还是有异常)
        //Looper.prepare();
        Toast.makeText(this, str, LENGTH_SHORT).show();
        //Looper.loop();
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (!isAllGranted) {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /** media player 模块初始化 **/
    public void initMediaPlayer() {
        btnMusic = (Button) findViewById(R.id.music);
        btnMusic.setOnClickListener(new bTnOnClickListener());

        btnOpl = (Button) findViewById(R.id.opensles);
        btnOpl.setOnClickListener(new bTnOnClickListener());

        try {
            //扫描sdcard
            list_music.clear();
            String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
            tvCurEvent.setText("歌曲(英文命名)请下载至:"+sdcard_path);

            //将歌曲添加到播放列表
            processShowMusic(sdcard_path);

            //创建播放器
            if(!list_music.isEmpty()){
                mediaPlayer = new MediaPlayer(); //创建mediaplayer播放器
                createAudioPlayer(URI_PCM);//创建openSL ES播放器
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*掃描目錄下的歌曲*/
    private void processShowMusic(String FilePath) {

        File file = new File(FilePath);
        if(file.exists()){
            File[] files = file.listFiles();
            //Log.i(TAG, "files.length =" + files.length);

            for (int i = files.length-1; i >= 0; i--) {
                if (files[i].isFile()) {
                    String filename = files[i].getName();
                    //获取.mp3格式文件
                    if (filename.endsWith(".mp3") || filename.endsWith(".wav")) {
                        String filePath = files[i].getAbsolutePath();
                        list_music.add(filename);
                        Log.i(TAG, "files[" + i + "].getAbsolutePath() = " + filePath);
                    }
                } else if (files[i].isDirectory()) {
                    FilePath = files[i].getAbsolutePath();
                    processShowMusic(FilePath);
                }
            }
        }else {
            file.mkdirs();
            Log.e(TAG,FilePath+":not exist,then mkdirs.");
        }

        //设置下拉列表的风格
        ArrayAdapter<String> adapter_res;
        adapter_res=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,list_music);

        //将adapter 添加到spinner中
        spinner_list=(Spinner)findViewById(R.id.musicSpinner);
        spinner_list.setAdapter(adapter_res);

        //設置背景顏色
        //spinner_list.setBackgroundColor(Color.parseColor("#111111"));

        //添加事件Spinner事件监听
        spinner_list.setOnItemSelectedListener(new SpinnerSelectedListener());
    }

    //播放列表事件监听---------------------------------------------
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            switch (arg0.getId()) {
                case R.id.musicSpinner:
                    String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    musicPlayPath = sdcard_path+"/"+list_music.get(arg2);

                    File mfile = new File(musicPlayPath);
                    if(mfile.exists()){
                        tvCurEvent.setText("歌曲路径:"+musicPlayPath);
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(musicPlayPath);
                            mediaPlayer.prepare();
                            mediaPlayer.setLooping(true);  // 设置循环播放
                            btnMusic.setText("播放>>");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else{
                        alert("未找到有效歌曲！");
                    }
                    break;
                default:
                    Toast.makeText(MainActivity.this, "select error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        public void onNothingSelected(AdapterView<?> arg0) {
            Log.w(TAG, "onNothingSelected!");
        }
    }

    /** camera 模块初始化 **/
    public void initCamera() {
        btnCamera = (Button) findViewById(R.id.camSwitch);
        btnCamera.setOnClickListener(new bTnOnClickListener());

        int cameras = Camera.getNumberOfCameras();
        tvCamInfo.setText("当前camera数量:"+cameras);
        try {
            if(mCamera == null){
                Log.i(TAG, "open camera :" + 0);
                mCamera = Camera.open(0);
            }
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(this, "camera open fail", Toast.LENGTH_LONG).show();
            Log.e(TAG,e.getMessage());
            return;
        }

        previewCamera = (SurfaceView) this.findViewById(R.id.preView);
        mCameraSurPreview = new CameraPreview(this, mCamera, previewCamera, 0);
        //Camera.Parameters parameters = mCamera.getParameters();
        //picWidth = Integer.parseInt((String.valueOf(supportedPictureSizes.get(0).width)));
        //picHeight = Integer.parseInt((String.valueOf(supportedPictureSizes.get(0).height)));
        //parameters.setPictureSize(picWidth, picHeight);
        //parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);
        //parameters.setRotation(90); //default picture rotation
        mCamera.setDisplayOrientation(90);
        //mCamera.setParameters(parameters);
    }

    /** 按键监听 **/
    private class bTnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.music:
                    if(mediaPlayer!=null){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                            btnMusic.setText("播放>>");
                        }else{
                            mediaPlayer.start();
                            btnMusic.setText("暂停||");
                        }
                    }else{
                        alert("音乐播放器未初始化,请重启app!");
                    }
                    break;

                case R.id.opensles:
                    isOplPlaying=!isOplPlaying;
                    setPlayingAudioPlayer(isOplPlaying);
                    if(isOplPlaying)
                        btnOpl.setText("opensles暂停||");
                    else
                        btnOpl.setText("opensles播放>>");
                    break;

                case R.id.camSwitch:
                    if(mCamera!=null){
                        if(isCameraOpened){
                            mCameraSurPreview.surfaceClose(previewCamera);
                            btnCamera.setText("开启摄像头");
                            isCameraOpened=false;
                        }else{
                            mCameraSurPreview.surfaceOpen(previewCamera);
                            btnCamera.setText("关闭摄像头");
                            isCameraOpened=true;
                        }
                    }else{
                        alert("摄像头未初始化,请重启app!");
                    }
                    break;

                case R.id.audioTask:
                    if(mAudioTask != null){
                        if(mAudioTask.threadRun){
                            mAudioTask.stop_Record_Play();
                            btnAudioTask.setText("开始录播>>");
                        }else{
                            mAudioTask.start_Record_Play();
                            btnAudioTask.setText("停止录播||");
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native boolean createAudioPlayer(String uri);
    public static native void setPlayingAudioPlayer(boolean isPlaying);
    public static native void setVolumeAudioPlayer(int millibel);
    public static native void setMutAudioPlayer(boolean mute);
    public static native void shutdown();
}