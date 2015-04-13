/*
 *  Copyright (c) 2011 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

/******************************************************************

 iLBC Speech Coder ANSI-C Source Code

 WebRtcIlbcfix_Lsf2Lsp.h

******************************************************************/

#ifndef WEBRTC_MODULES_AUDIO_CODING_CODECS_ILBC_MAIN_SOURCE_LSF_TO_LSP_H_
#define WEBRTC_MODULES_AUDIO_CODING_CODECS_ILBC_MAIN_SOURCE_LSF_TO_LSP_H_

#include "defines.h"

/*----------------------------------------------------------------*
 *  conversion from lsf to lsp coefficients
 *---------------------------------------------------------------*/

void WebRtcIlbcfix_Lsf2Lsp(
    WebRtc_Word16 *lsf, /* (i) lsf in Q13 values between 0 and pi */
    WebRtc_Word16 *lsp, /* (o) lsp in Q15 values between -1 and 1 */
    WebRtc_Word16 m     /* (i) number of coefficients */
                           );

#endif
