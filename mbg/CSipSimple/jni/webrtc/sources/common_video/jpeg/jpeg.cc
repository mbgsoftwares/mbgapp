/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

#if defined(WIN32)
 #include <basetsd.h>
#endif
#include <setjmp.h>
#include <stdio.h>
#include <string.h>

#include "common_video/jpeg/include/jpeg.h"
#include "common_video/jpeg/data_manager.h"
#include "common_video/libyuv/include/webrtc_libyuv.h"
#include "libyuv.h"
#include "libyuv/mjpeg_decoder.h"

extern "C" {
#if defined(USE_SYSTEM_LIBJPEG)
#include <jpeglib.h>
#else
#include "jpeglib.h"
#endif
}


namespace webrtc
{

// Error handler
struct myErrorMgr {

    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};
typedef struct myErrorMgr * myErrorPtr;

METHODDEF(void)
MyErrorExit (j_common_ptr cinfo)
{
    myErrorPtr myerr = (myErrorPtr) cinfo->err;

    // Return control to the setjmp point
    longjmp(myerr->setjmp_buffer, 1);
}

JpegEncoder::JpegEncoder()
{
   _cinfo = new jpeg_compress_struct;
    strcpy(_fileName, "Snapshot.jpg");
}

JpegEncoder::~JpegEncoder()
{
    if (_cinfo != NULL)
    {
        delete _cinfo;
        _cinfo = NULL;
    }
}


WebRtc_Word32
JpegEncoder::SetFileName(const char* fileName)
{
    if (!fileName)
    {
        return -1;
    }

    if (fileName)
    {
        strncpy(_fileName, fileName, 256);
        _fileName[256] = 0;
    }
    return 0;
}


WebRtc_Word32
JpegEncoder::Encode(const VideoFrame& inputImage)
{
    if (inputImage.Buffer() == NULL || inputImage.Size() == 0)
    {
        return -1;
    }
    if (inputImage.Width() < 1 || inputImage.Height() < 1)
    {
        return -1;
    }

    FILE* outFile = NULL;

    const WebRtc_UWord32 width = inputImage.Width();
    const WebRtc_UWord32 height = inputImage.Height();

    // Set error handler
    myErrorMgr      jerr;
    _cinfo->err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = MyErrorExit;
    // Establish the setjmp return context
    if (setjmp(jerr.setjmp_buffer))
    {
        // If we get here, the JPEG code has signaled an error.
        jpeg_destroy_compress(_cinfo);
        if (outFile != NULL)
        {
            fclose(outFile);
        }
        return -1;
    }

    if ((outFile = fopen(_fileName, "wb")) == NULL)
    {
        return -2;
    }
    // Create a compression object
    jpeg_create_compress(_cinfo);

    // Setting destination file
    jpeg_stdio_dest(_cinfo, outFile);

    // Set parameters for compression
    _cinfo->in_color_space = JCS_YCbCr;
    jpeg_set_defaults(_cinfo);

    _cinfo->image_width = width;
    _cinfo->image_height = height;
    _cinfo->input_components = 3;

    _cinfo->comp_info[0].h_samp_factor = 2;   // Y
    _cinfo->comp_info[0].v_samp_factor = 2;
    _cinfo->comp_info[1].h_samp_factor = 1;   // U
    _cinfo->comp_info[1].v_samp_factor = 1;
    _cinfo->comp_info[2].h_samp_factor = 1;   // V
    _cinfo->comp_info[2].v_samp_factor = 1;
    _cinfo->raw_data_in = TRUE;

    WebRtc_UWord32 height16 = (height + 15) & ~15;
    WebRtc_UWord8* imgPtr = inputImage.Buffer();
    WebRtc_UWord8* origImagePtr = NULL;
    if (height16 != height)
    {
        // Copy image to an adequate size buffer
        WebRtc_UWord32 requiredSize = CalcBufferSize(kI420, width, height16);
        origImagePtr = new WebRtc_UWord8[requiredSize];
        memset(origImagePtr, 0, requiredSize);
        memcpy(origImagePtr, inputImage.Buffer(), inputImage.Length());
        imgPtr = origImagePtr;
    }

    jpeg_start_compress(_cinfo, TRUE);

    JSAMPROW y[16],u[8],v[8];
    JSAMPARRAY data[3];

    data[0] = y;
    data[1] = u;
    data[2] = v;

    WebRtc_UWord32 i, j;

    for (j = 0; j < height; j += 16)
    {
        for (i = 0; i < 16; i++)
        {
            y[i] = (JSAMPLE*)imgPtr + width * (i + j);

            if (i % 2 == 0)
            {
                u[i / 2] = (JSAMPLE*) imgPtr + width * height +
                            width / 2 * ((i + j) / 2);
                v[i / 2] = (JSAMPLE*) imgPtr + width * height +
                            width * height / 4 + width / 2 * ((i + j) / 2);
            }
        }
        jpeg_write_raw_data(_cinfo, data, 16);
    }

    jpeg_finish_compress(_cinfo);
    jpeg_destroy_compress(_cinfo);

    fclose(outFile);

    if (origImagePtr != NULL)
    {
        delete [] origImagePtr;
    }

    return 0;
}

int ConvertJpegToI420(const EncodedImage& input_image,
                      VideoFrame* output_image) {

  if (output_image == NULL)
    return -1;
  // TODO(mikhal): Update to use latest API from LibYuv when that becomes
  // available.
  libyuv::MJpegDecoder jpeg_decoder;
  bool ret = jpeg_decoder.LoadFrame(input_image._buffer, input_image._size);
  if (ret == false)
    return -1;
  if (jpeg_decoder.GetNumComponents() == 4)
    return -2;  // not supported.
  int width = jpeg_decoder.GetWidth();
  int height = jpeg_decoder.GetHeight();
  int req_size = CalcBufferSize(kI420, width, height);
  output_image->VerifyAndAllocate(req_size);
  output_image->SetWidth(width);
  output_image->SetHeight(height);
  output_image->SetLength(req_size);
  return ConvertToI420(kMJPG,
                       input_image._buffer,
                       0, 0,  // no cropping
                       width, height,
                       input_image._size,
                       kRotateNone,
                       output_image);
}


}
