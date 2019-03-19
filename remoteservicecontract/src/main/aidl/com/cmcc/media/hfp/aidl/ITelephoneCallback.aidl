/**
 * [单方通话接口回调]<BR>
 */
package com.cmcc.media.hfp.aidl;

// Declare any non-default types here with import statements

interface ITelephoneCallback {

    /**
     * 手柄通知STB切换通道
     */
    boolean setSpeakerOn(boolean state);

    /**
     * 手柄通知STB接听来电
     */
    boolean answerCall();

    /**
     * 手柄通知STB挂断通话
     */
    boolean hangupCall();

    /**
     * 手柄通知STB发起呼叫
     */
    boolean dialCall(String phoneNumber);

    /**
     * 手柄上报DTMF键值
     */
    boolean sendDtmf(int dtmf);
}
