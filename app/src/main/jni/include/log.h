//
// Created by Administrator on 2018/8/22.
//

#ifndef NANO_SERVER_LOG_H
#define NANO_SERVER_LOG_H

#include <android/log.h>

#ifndef DGB
#define DGB 0
#endif

#ifndef LOG_TAG
#define LOG_TAG __FILE__
#endif

#ifndef ALOGD
#if DGB
#define ALOGD(...) \
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define ALOGD(...)   ((void)0)
#endif
#endif

#endif //NANO_SERVER_LOG_H
