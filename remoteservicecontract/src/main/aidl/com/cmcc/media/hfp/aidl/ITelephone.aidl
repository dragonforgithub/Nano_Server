/**
 * [单方通话接口]<BR>
 */
package com.cmcc.media.hfp.aidl;
import com.cmcc.media.hfp.aidl.ITelephoneCallback;

// Declare any non-default types here with import statements

interface ITelephone {
    /**
     * APP通知手柄切换通道
     */
    boolean setSpeakerOn(boolean state);

    /**
     * APP通知手柄发起呼叫
     */
    boolean dialCall(String phoneNumber);

    /**
     * APP通知手柄来电振铃
     */
    boolean incomingCall(String phoneNumber);

    /**
     * APP通知手柄接听来电
     */
    boolean answerCall(String phoneNumber);

    /**
     * APP通知手柄挂断通话
     */
    boolean hangupCall(String phoneNumber);

    /**
     * APP注册呼叫相关的回调接口
     *
     * @param telephoneCallback 呼叫相关的回调接口
     */
    void registerCallBack(ITelephoneCallback callback);

    /**
     * APP解除注册呼叫相关的回调接口
     *
     * @param telephoneCallback 呼叫相关的回调接口
     */
    void unregisterCallBack(ITelephoneCallback callback);
}
