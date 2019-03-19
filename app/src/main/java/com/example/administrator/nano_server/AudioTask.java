package com.example.administrator.nano_server;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.LinkedList;

public class AudioTask {

    private final String TAG = "AudioTask";

    private int sampleRateInHz = 16000;
    private int channelConfig  = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat    = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * AudioRecord 写入缓冲区大小
     */
    protected int m_in_buf_size;
    /**
     * 录制音频对象
     */
    private AudioRecord m_in_rec=null;
    /**
     * 录入的字节数组
     */
    private byte[] m_in_bytes;
    /**
     * 存放录入字节数组的大小
     */
    private LinkedList<byte[]> m_in_q;
    /**
     * AudioTrack 播放缓冲大小
     */
    private int m_out_buf_size;
    /**
     * 播放音频对象
     */
    private AudioTrack m_out_trk=null;
    /**
     * 播放的字节数组
     */
    private byte[] m_out_bytes;
    /**
     * 录制音频线程
     */
    private Thread record;
    /**
     * 播放音频线程
     */
    private Thread play;
    /**
     * 让线程停止的标志
     */
    protected static boolean threadRun = false;

    /** 初始化 AudioRecord 和 AudioTrack **/
    void audioDevInit()
    {
        // AudioRecord 得到录制最小缓冲区的大小
        m_in_buf_size = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig,
                audioFormat);

        // 实例化播放音频对象
        if(m_in_rec == null){
            m_in_rec = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRateInHz,
                    channelConfig,
                    audioFormat, m_in_buf_size);
        }

        // 实例化一个字节数组，长度为最小缓冲区的长度
        m_in_bytes = new byte[m_in_buf_size];

        // 实例化一个链表，用来存放字节组数
        m_in_q = new LinkedList<byte[]>();

        // AudioTrack 得到播放最小缓冲区的大小
        m_out_buf_size = AudioTrack.getMinBufferSize(16000,
                channelConfig,
                audioFormat);

        // 实例化播放音频对象
        if(m_out_trk==null){
            m_out_trk = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 16000,
                    AudioFormat.CHANNEL_CONFIGURATION_DEFAULT,
                    AudioFormat.ENCODING_DEFAULT, m_out_buf_size,
                    AudioTrack.MODE_STREAM);
        }

        // 实例化一个长度为播放最小缓冲大小的字节数组
        m_out_bytes = new byte[m_out_buf_size];
    }

    /** 开始录播 **/
    public void start_Record_Play()
    {
        //创建录播设备
        audioDevInit();

        //设置录播线程状态
        threadRun=true;

        // 创建录音线程
        record = new Thread(new recordSound());
        // 启动播放线程
        record.start();

        // 创建播放线程
        play = new Thread(new playRecord());
        // 启动播放线程
        play.start();
    }

    /** 停止录播 **/
    public void stop_Record_Play()
    {
        //退出录播线程
        threadRun=false;

        //停止-释放 audio record
        if(m_in_rec != null) {
            m_in_rec.stop();
            m_in_rec.release();
            m_in_rec = null;
        }

        //停止-释放 audio track
        if(m_out_trk != null) {
            m_out_trk.stop();
            m_out_trk.release();
            m_out_trk = null;
        }
    }

    /** 录音线程 **/
    class recordSound implements Runnable
    {
        @Override
        public void run()
        {
            Log.i(TAG, "........recordSound run()......");
            byte[] bytes_pkg;

            // 开始录音
            m_in_rec.startRecording();

            while (threadRun)
            {
                m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
                bytes_pkg = m_in_bytes.clone();
                Log.i(TAG, "........recordSound bytes_pkg = " + bytes_pkg.length);
                if (m_in_q.size() >= 2)
                {
                    m_in_q.removeFirst();
                }
                m_in_q.add(bytes_pkg);
            }
        }
    }

    /** 播放线程 **/
    class playRecord implements Runnable
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            Log.i(TAG, "........playRecord run()......");
            byte[] bytes_pkg = null;

            // 开始播放
            m_out_trk.play();

            while (threadRun)
            {
                try {
                    m_out_bytes = m_in_q.getFirst();
                    bytes_pkg = m_out_bytes.clone();
                    m_out_trk.write(bytes_pkg, 0, bytes_pkg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}