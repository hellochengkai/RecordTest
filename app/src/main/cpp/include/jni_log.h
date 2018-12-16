//
// Created by chengkai on 18-1-10.
//

#ifndef ANDROIDHARDWARETESTCASE_JNI_LOG_H
#define ANDROIDHARDWARETESTCASE_JNI_LOG_H
#include <android/log.h>

#define  LOG_TAG    "jniTest"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //ANDROIDHARDWARETESTCASE_JNI_LOG_H
