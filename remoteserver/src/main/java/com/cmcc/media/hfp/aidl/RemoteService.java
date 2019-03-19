package com.cmcc.media.hfp.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


public class RemoteService extends Service {
    static final String TAG = "RemoteService";
    static boolean pthreadState = false;
    final RemoteCallbackList<ITelephoneCallback> remoteCallbackList = new RemoteCallbackList<>();

    private static final int EVENT_ONSUCCESS      = 0;
    private static final int EVENT_SETTVSPEAKERON = 1;
    private static final int EVENT_ANSWERTVCALL   = 2;
    private static final int EVENT_HANGUPTVCALL   = 3;
    private static final int EVENT_DIALTVCALL     = 4;
    private static final int EVENT_INCOMINGCALL   = 5;
    private static final int EVENT_SENDDTMF       = 6;
    private static final int EVENT_UNKNOWN        = 7;

    /*-----------------------------------------------------------------------------
     Function Name: onCreate
     Input		:
     Output		:
     Return 	:
     Describe	:
     -------------------------------------------------------------------------------*/
    public void onCreate() {
        // Used to load the 'native-lib' library on application startup.
        Nano_Printf("service onCreate");
		pthreadState = true;

        System.loadLibrary("RemoteServiceJNI");
        //Nano_Printf(String.format("<%s>",stringFromJNI()));

        DataThread datathread = new DataThread();
        datathread.start();
    }

    /*-----------------------------------------------------------------------------
     Function Name: onStartCommand
     Input		:
     Output		:
     Return 		:
     Describe		:

        onStartComand使用时，返回的是一个(int)整形：
        1):START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
        2):START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务
        3):START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
        4):START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。

        onStartComand参数flags含义
        flags表示启动服务的方式：
        START_FLAG_REDELIVERY：如果你实现onStartCommand()来安排异步工作或者在另一个线程中工作, 那么你可能需要使用START_FLAG_REDELIVERY来让系统重新发送一个intent。这样如果你的服务在处理它的时候被Kill掉, Intent不会丢失.
     -------------------------------------------------------------------------------*/
    public int onStartCommand(Intent intent, int flags, int startId) {
        Nano_Printf("service onStartCommand");
        return START_REDELIVER_INTENT;
    }

    /*-----------------------------------------------------------------------------
     Function Name: onBind
     Input		:
     Output		:
     Return 		:
     Describe		:
     -------------------------------------------------------------------------------*/
    public IBinder onBind(Intent intent) {
        Nano_Printf("service on bind,intent = %s",intent.toString());
        return binder;
    }

    /*-----------------------------------------------------------------------------
     Function Name: onDestroy
     Input		    :
     Output		    :
     Return 		:
     Describe		:
     -------------------------------------------------------------------------------*/
    public void onDestroy() {
        Nano_Printf("service onDestroy");
        pthreadState = false; //停止线程
        remoteCallbackList.kill(); // 取消掉所有的回调
    }

    /**
     * 以小端模式将byte[]/char转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 以大端模式将byte[]/char转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 打印函数
     */
    private void Nano_Printf(String...args) {
        String str = "";
        for(int i = 0; i < args.length; i++){
            str +=  args[i];
            if( i != args.length - 1){
                str += ", ";
            }
        }
        Log.d(TAG, str);
    }

    /**
     * 调用应用端回调接口通知对应事件
     */
    private void Nano_Notify(int event, String data)
    {
        if(remoteCallbackList == null) {
            Nano_Printf("remoteCallbackList is null");
            return;
        }

        final int len = remoteCallbackList.beginBroadcast();
        Nano_Printf("client callback num "+len+",event="+event);
        for (int i = 0; i < len; i++) {
            try {
                switch (event){ //调用对应callback接口
                    case EVENT_ONSUCCESS:
                        break;
                    case EVENT_SETTVSPEAKERON:
                        remoteCallbackList.getBroadcastItem(i).setSpeakerOn(data.equals("1"));
                        break;
                    case EVENT_ANSWERTVCALL:
                        remoteCallbackList.getBroadcastItem(i).answerCall();
                        break;
                    case EVENT_HANGUPTVCALL:
                        remoteCallbackList.getBroadcastItem(i).hangupCall();
                        break;
                    case EVENT_DIALTVCALL:
                        remoteCallbackList.getBroadcastItem(i).dialCall(data);
                        break;
                    case EVENT_SENDDTMF:
                        remoteCallbackList.getBroadcastItem(i).sendDtmf(123);
                        break;
                    default:
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        remoteCallbackList.finishBroadcast();
    }

    /**
     Function Name:   class DataThread
     Input		:
     Output		:
     Return 	:
     Describe	:创建数据线程，处理语音数据和按键事件
     -------------------------------------------------------------------------------*/
    private class DataThread extends Thread{  // 在线程的run()中进行处理
        @Override
        public void run() {
            Nano_Printf("Service thread Run...");
            //call nano-lib initialize
            //NanoOpen();

            while(pthreadState)
            {
                byte appbuf[] = new byte[128];
                int  type = NanoPollEvent(appbuf,appbuf.length);  //UDP线程为poll(500ms)
                if(type==EVENT_UNKNOWN) continue;

                String res = new String(appbuf);
                Nano_Printf("NanoPollEvent:type<%d>,val=%s",res.trim()); //trim跳过空字符
                /*client callback*/
                Nano_Notify(type,res.trim());
            }
            Nano_Printf("service Exit");
        }
    }

    /**
     *实现AIDL接口
     */
    private final ITelephone.Stub binder = new ITelephone.Stub() {

        public boolean setSpeakerOn(boolean state)    throws RemoteException {
            Nano_Printf("setSpeakerOn:",state?"1":"0");
            NanosetSpeakerOn(state);
            return true;
        }

        public boolean dialCall(String phoneNumber)    throws RemoteException {
            Nano_Printf("dialCall:",phoneNumber);
            NanodialCall(phoneNumber);
            return true;
        }

        public boolean incomingCall(String phoneNumber)    throws RemoteException {
            Nano_Printf("incomingCall:",phoneNumber);
            NanoincomingCall(phoneNumber);
            return true;
        }

        public boolean answerCall(String phoneNumber)    throws RemoteException {
            Nano_Printf("answerCall:",phoneNumber);
            NanoanswerCall(phoneNumber);
            return true;
        }

        public boolean hangupCall(String phoneNumber)   throws RemoteException {
            Nano_Printf("hangupCall:",phoneNumber);
            NanohangupCall(phoneNumber);
            return true;
        }

        /*提供registerCallBack方法*/
        public void registerCallBack(ITelephoneCallback callback) throws RemoteException {
            Nano_Printf("registerCallBack");
            remoteCallbackList.register(callback);
        }

        /*提供unregisterCallBack方法*/
        public void unregisterCallBack(ITelephoneCallback callback) throws RemoteException {
            Nano_Printf("unregisterCallBack");
            remoteCallbackList.unregister(callback);
        }
    };

    /*nanosic : native interface*/
    public native String   stringFromJNI();
    public native boolean  NanoOpen();
    public native int      NanoPollEvent(byte[] buf, int size);
    public native boolean  NanosetSpeakerOn(boolean state);
    public native boolean  NanodialCall(String phoneNumber);
    public native boolean  NanoincomingCall(String phoneNumber);
    public native boolean  NanoanswerCall(String phoneNumber);
    public native boolean  NanohangupCall(String phoneNumber);
}
