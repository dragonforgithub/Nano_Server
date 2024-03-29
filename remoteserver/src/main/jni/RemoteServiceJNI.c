/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <nano_socket.h>
/* Header for class com_cmcc_media_hfp_aidl_RemoteService */

#define TAG "nano-jni"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    stringFromJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_stringFromJNI
        (JNIEnv *env, jobject thiz){
    return (*env)->NewStringUTF(env, "Hi! RemoteService, I`m JNI ~");
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanoOpen
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanoOpen
        (JNIEnv *env, jobject thiz){
    return Nano_Open(NULL);
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanoPollEvent
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanoPollEvent
        (JNIEnv *env, jobject thiz, jbyteArray dataBuf, jint size){
    jboolean isCopy;
    unsigned char* local = (*env)->GetByteArrayElements(env, dataBuf, &isCopy);
    if(!local){
        LOGW("invalid buff\n");
        return -1;
    }

    if ((*env)->ExceptionCheck(env)) {
        (*env)->ReleaseByteArrayElements(env,dataBuf,local,0);
        return -1;
    }

    int ret = Nano_PollEvent(local,size);
    //使用完一定要释放减少引用计数
    (*env)->ReleaseByteArrayElements(env,dataBuf,local,0);

    return ret;
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanosetSpeakerOn
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanosetSpeakerOn
        (JNIEnv *env, jobject thiz, jboolean state){
    unsigned char s = (unsigned char)state;
    LOGD("Speaker state : %d",s);
    return Nano_setSpeakerOn(s);
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanodialCall
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanodialCall
        (JNIEnv *env, jobject thiz, jstring phoneNumber){
    if(!phoneNumber){
        LOGD("phoneNumber is null!");
        return 0;
    }

    char* number = (char *) (*env)->GetStringUTFChars(env, phoneNumber, 0);
    LOGD("phoneNumber %s",number);
    return Nano_dialCall(number);
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanoincomingCall
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanoincomingCall
        (JNIEnv *env, jobject thiz, jstring phoneNumber){
    if(!phoneNumber){
        LOGD("phoneNumber is null!");
        return 0;
    }

    char* number = (char *) (*env)->GetStringUTFChars(env, phoneNumber, 0);
    LOGD("phoneNumber %s",number);
    return Nano_incomingCall(number);
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanoanswerCall
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanoanswerCall
        (JNIEnv *env, jobject thiz, jstring phoneNumber){
    if(!phoneNumber){
        LOGD("phoneNumber is null!");
        return 0;
    }

    char* number = (char *) (*env)->GetStringUTFChars(env, phoneNumber, 0);
    LOGD("phoneNumber %s",number);
    return Nano_answerCall(number);
}

/*
 * Class:     com_cmcc_media_hfp_aidl_RemoteService
 * Method:    NanohangupCall
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_cmcc_media_hfp_aidl_RemoteService_NanohangupCall
        (JNIEnv *env, jobject thiz, jstring phoneNumber){
    if(!phoneNumber){
        LOGD("phoneNumber is null!");
        return 0;
    }

    char* number = (char *) (*env)->GetStringUTFChars(env, phoneNumber, 0);
    LOGD("phoneNumber %s",number);
    return Nano_hangupCall(number);
}

#ifdef __cplusplus
}
#endif
