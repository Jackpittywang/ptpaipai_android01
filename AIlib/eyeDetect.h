/******************************************************************************
 *     Copyright (c) 2015 PuTao Co., Ltd.
 *     All Rights Reserved.
 ******************************************************************************/

#ifndef __EYEDETECT_H__
#define __EYEDETECT_H__

#include "basept.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct _EyeDetectInitData {
  PTS32               nWidth;
  PTS32               nHeight;
  PTImageFormatEnum   eFormat;

  const char * pFaceCascade;//path for Face cascade file
  const char * pEyeCascade; //path for Eye cascade file
  PTS32  minFaceSize;
  PTS32  maxFaceSize;
} PTEyeDetectInitData;

typedef void* PTEyeDetectHandler;

/********************************************************************
 *  Eye Detection Engine initialization
 *  Params:
 *  [IN]:
 *       pInitData:     Pointer to PTEyeDetectInitData struct.
 *       ppHandler:     Pointer to eye detect handler
 *  [OUT]:
 *       ppHandler:     Pointer to eye detect handler, return correct handler on function
 *                      success.
 *  [RET]:
 *       PT_RET_OK:               success
 *       PT_RET_INVALIDPARAM:     invalid parameters in pInitData (if it's not NULL) or
 *                                ppHandler is NULL
 *       PT_RET_NOMEM:            out of memory
 *
 *********************************************************************/

PTS32 PTEyeDetectInit(PTEyeDetectInitData *pInitData, PTEyeDetectHandler *ppHandler);

/********************************************************************
 *  Eye Detection Engine de-intialization
 *  Params:
 *  [IN]:
 *       ppHandler:     Pointer to eye detect handler
 *  [OUT]:
 *       ppHandler:     NULL written into this address.
 *
 *  [RET]:
 *       PT_RET_OK:              success
 *       PT_RET_INVALIDPARAM:    if ppHandler is NULL
 *
 *********************************************************************/

PTS32 PTEyeDetectDeinit(PTEyeDetectHandler *ppHandler);

/********************************************************************
 *  Eye Detection Engine major function
 *  Params:
 *  [IN]:
 *       pHandler:      eye detect handler
 *       pPixels :      pointer of the image to be detected
 *
 *  [RET]:
 *       PT_RET_OK:              success
 *       PT_RET_INVALIDPARAM:    if pHandler or pPixels is NULL
 *
 ********************************************************************/

PTS32 PTEyeDetect(PTEyeDetectHandler pHandler, PTU8 *pPixels, PTPoint& ptLeftEye, PTPoint& ptRightEye);

/********************************************************************
 *  Get Eye Detection Engine version infomation
 *  Params:
 *  [IN]:
 *       pVendorInfo:  pointer to buffer to hold version information string
 *  [OUT]:
 *       pVendorInfo:  pointer to buffer to hold version information string
 *
 *  [RET]:
 *       PT_RET_OK:    success
 *
 ********************************************************************/

PTS32 PTEyeDetectGetVendorString(char *pVendorInfo);

#ifdef __cplusplus
}
#endif

#endif /* __EYEDETECT_H__ */

/* EOF */

