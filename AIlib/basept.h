/******************************************************************************
 *     Copyright (c) 2015 PuTao Co., Ltd.
 *     All Rights Reserved.
 ******************************************************************************/
#ifndef __BASEPT_H__
#define __BASEPT_H__

#ifdef __cplusplus
extern "C" {
#endif
    
#ifndef FALSE
#define FALSE 0
#endif
#ifndef TRUE
#define TRUE  1
#endif
    
typedef signed   char          PTS8;
typedef signed   short         PTS16;
typedef signed   int           PTS32;
typedef signed   long long     PTS64;

typedef unsigned char          PTU8;
typedef unsigned short         PTU16;
typedef unsigned int           PTU32;
typedef unsigned long long     PTU64;
typedef int                    PTBOOL;

typedef float                  PTF32;
typedef double                 PTF64;

typedef struct _POINT {
    PTS32   x;
    PTS32   y;
    _POINT() {x=0, y=0;}
    _POINT(PTS32 _x,PTS32 _y) {x=_x; y=_y;}
} PTPoint;


typedef struct _POINT2D {
    PTF64   x;
    PTF64   y;
    _POINT2D() {x=0.0f, y=0.0f;}
    _POINT2D(PTF64 _x,PTF64 _y) {x=_x; y=_y;}
} PTPoint2d;

typedef struct _SIZE {
    PTS32   width;
    PTS32   height;
    _SIZE() {width = 0; height = 0;}
    _SIZE(PTS32 w,PTS32 h) {width = w; height = h;}
} PTSize;

typedef struct _RECT {
    PTPoint tl;
    PTS32   width;
    PTS32   height;
    _RECT() {tl = PTPoint(0,0); width = 0; height = 0;}
    _RECT(PTPoint _tl, PTS32 _width, PTS32 _height) {tl = _tl; width = _width; height = _height;}
} PTRect;
    
    
#define MAX_STRING_LENGTH      32
    
typedef enum _ImageFormatEnum {
    PT_IMG_GRAY                = 0,
    PT_IMG_YCbCr420P           = 1,    /*Y, Cb and Cr are planar*/
    PT_IMG_YCrCb420P_YV12      = 2,    /*Y, Cr and Cb are planar, YV12*/
    PT_IMG_YCbCr411P           = 3,
    PT_IMG_YCbCr422P           = 4,
    PT_IMG_YCbCr444P           = 5,
    PT_IMG_YCbCr422I           = 6,    /*Y, Cb and Cr are interleaved in the order of Cb Y Cr Y*/
    PT_IMG_YCbYCr422I          = 7,    /*Y, Cb and Cr are interleaved in the order of Y Cb Y Cr*/
    PT_IMG_YCbCr420SP          = 8,    /*Y is planar, Cb and Cr is interleaved, NV12*/
    PT_IMG_YCrCb420SP          = 9,    /*Y is planar, Cr and Cb is interleaved, NV21*/
    PT_IMG_RGB565              = 10,
    PT_IMG_BGR888              = 11,
    PT_IMG_RGB888              = 12,
    PT_IMG_BGRA8888            = 13,
    PT_IMG_RGBA8888            = 14,
    PT_IMG_ABGR8888            = 15,
    PT_IMG_ARGB8888            = 16,
    PT_IMG_NV21                = 17,
    PT_IMG_FORMAT_COUNT,
} PTImageFormatEnum;

const char strImageFormat[PT_IMG_FORMAT_COUNT+1][MAX_STRING_LENGTH] = {
    "PT_IMG_GRAY", "PT_IMG_YCbCr420P", "PT_IMG_YCrCb420P_YV12", "PT_IMG_YCbCr411P",
    "PT_IMG_YCbCr422P", "PT_IMG_YCbCr444P", "PT_IMG_YCbCr422I", "PT_IMG_YCbYCr422I",
    "PT_IMG_YCbCr420SP", "PT_IMG_YCrCb420SP", "PT_IMG_RGB565", "PT_IMG_BGR888", "PT_IMG_RGB888",
    "PT_IMG_BGRA8888", "PT_IMG_RGBA8888", "PT_IMG_ABGR8888", "PT_IMG_ARGB8888",
    "NULL",
};

//#define UseCaptureVedio
typedef enum _MIRRORWRNING{
    PT_MIRROR_WARN_NOTNING = 0,
    PT_MIRROR_WARN_CENTER = 1,
    PT_MIRROR_WARN_SETUP = 2,
    PT_MIRROR_WARN_NUM = 3
} PTMirrorWarnEnum;

typedef enum _LUMINATESTATE{
    PT_LUMI_NORMAL = 0,
    PT_LUMI_DARK = 1,
    PT_LUMI_DAZZLING = 2,
    PT_LUMI_NUM = 3
} PTLumiStEnum;
    
    
typedef enum _HELMETSTATE{
    PT_HELMET_TIPS_NOTHING = 0,
    PT_HELMET_TIPS_ERROR = 1,
    PT_HELMET_TIPS_NUM = 2
} PTHelmetStEnum;

#define DEVICESTR_MAX_LEN 100
typedef struct _GameInitData {
    char*               pDevice;
    PTS32               nWidth;
    PTS32               nHeight;
    PTImageFormatEnum   eFormat;
    PTBOOL              needRotate;
    _GameInitData(){pDevice=nullptr; nWidth=0; nHeight=0; eFormat=PT_IMG_FORMAT_COUNT;needRotate=FALSE;}
} PTGameInitData;
    
typedef void* PTGameHandler;

typedef enum _HandStatus {
   HAND_OFF = 0,
   HAND_ON  = 1,
   HAND_STATUS_COUNT,
} PTHandStatus;

const char strHandGesture[HAND_STATUS_COUNT+1][MAX_STRING_LENGTH] = {
    "HAND_OFF", "HAND_ON", "NULL",
};
    
typedef enum _SystemEnum {
    PT_APPLE_IPAD2   = 0,
    PT_APPLE_IPAD3   = 1,
    PT_APPLE_IPAD4   = 2,

    PT_APPLE_MINI1   = 3,
    PT_APPLE_MINI2   = 4,
    PT_APPLE_MINI3   = 5,
    PT_APPLE_MINI4   = 6,

    PT_APPLE_AIR     = 7,
    PT_APPLE_AIR2    = 8,

    PT_APPLE_IPADPRO = 9,

    PT_SYS_NUM,
} PTSysEnum;

const char strBoard[PT_SYS_NUM+1][MAX_STRING_LENGTH] = {
   "PT_APPLE_IPAD2", "PT_APPLE_IPAD3", "PT_APPLE_IPAD4",
   "PT_APPLE_MINI1", "PT_APPLE_MINI2", "PT_APPLE_MINI3", "PT_APPLE_MINI4",
   "PT_APPLE_AIR", "PT_APPLE_AIR2",
   "PT_APPLE_IPADPRO",
   "PT_SYS_NUM"
};

#define iOSMinSdk 7
#define iOSMaxSdk 9

typedef enum _SDKEnum {
    PT_APPLE_IOS7_8_9   = 0,

    PT_SDK_NUM      = 1,
} PTSDKEnum;

typedef int PTMouleID;

typedef struct _IMAGE {
    PTU8   *pData;
    PTS32   width;
    PTS32   height;
    PTImageFormatEnum eFormat;
    _IMAGE() {pData = nullptr; width = 0; height = 0; eFormat = PT_IMG_FORMAT_COUNT;}
    _IMAGE(PTU8 *_pData, PTS32 _width, PTS32 _height, PTImageFormatEnum _eFormat) {pData = _pData; width = _width; height = _height; eFormat = _eFormat;}
} PTImage;

#define PT_RET_DEVICE_NOTSUPPORTED    -8
#define PT_RET_EXCEPTION    -7
#define PT_RET_BADRESOURCE  -6
#define PT_RET_TIMEOUT      -5
#define PT_RET_INVALIDPARAM -4
#define PT_RET_NOTSUPPORTED -3
#define PT_RET_NOMEM        -2
#define PT_RET_ERR          -1
#define PT_RET_OK            0
    
#ifdef _ANDROID_
    //#include <utils/Log.h>
#include <jni.h>
#include <android/log.h>
#undef  LOG_TAG
#define LOG_TAG "PuTao"
#endif  /* _ANDROID_ */
    
#ifdef _DEBUG_
#ifdef _ANDROID_
    //#define PTDEBUG        LOGW
#define PTDEBUG(...)   __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else /* !_DEBUG_ */
#include <stdio.h>
#define PTDEBUG        printf
#endif
#else /* !_DEBUG_ */
#define PTDEBUG(...)
#endif
    
#ifdef __cplusplus
}
#endif

#endif /* __BASEPT_H__ */

/* EOF */
