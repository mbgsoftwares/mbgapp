/* ------------------------------------------------------------------
 * Copyright (C) 1998-2009 PacketVideo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * -------------------------------------------------------------------
 */
/****************************************************************************************
Portions of this file are derived from the following 3GPP standard:

    3GPP TS 26.173
    ANSI-C code for the Adaptive Multi-Rate - Wideband (AMR-WB) speech codec
    Available from http://www.3gpp.org

(C) 2007, 3GPP Organizational Partners (ARIB, ATIS, CCSA, ETSI, TTA, TTC)
Permission to distribute, modify and use this file under the standard license
terms listed above has been obtained from the copyright holder.
****************************************************************************************/

#include <pjmedia-codec/types.h>
#include "opencore-amrwb/pvamrwbdecoder_api.h"
#include "opencore-amrnb/interf_enc.h"
#include "opencore-amrwb/pvamrwbdecoder_api.h"


#define BIT_0     (pj_int16_t)-127
#define BIT_1     (pj_int16_t)127
#define MAX_16 (pj_int16_t)+32767   /* 0x7fff */
#define MIN_16 (pj_int16_t)-32768   /* 0x8000 */
#define MODE_24k      8

#define DHF_PARMS_MAX 32 /* homing frame pattern             */
#define NUM_OF_SPMODES 9

#define PRML 15
#define PRMN_7k NBBITS_7k/PRML + 1
#define PRMN_9k NBBITS_9k/PRML + 1
#define PRMN_12k NBBITS_12k/PRML + 1
#define PRMN_14k NBBITS_14k/PRML + 1
#define PRMN_16k NBBITS_16k/PRML + 1
#define PRMN_18k NBBITS_18k/PRML + 1
#define PRMN_20k NBBITS_20k/PRML + 1
#define PRMN_23k NBBITS_23k/PRML + 1
#define PRMN_24k NBBITS_24k/PRML + 1

/*----------------------------------------------------------------------------
; LOCAL FUNCTION DEFINITIONS
; Function Prototype declaration
----------------------------------------------------------------------------*/
 pj_int16_t dhf_test(pj_int16_t input_frame[], pj_int32_t mode, pj_int16_t nparms);

/*----------------------------------------------------------------------------
; LOCAL STORE/BUFFER/POINTER DEFINITIONS
; Variable declaration - defined here and used outside this module
----------------------------------------------------------------------------*/

 const short AMR_WB_COMPRESSED[NUM_OF_MODES] =
 {
     NBBITS_7k,
     NBBITS_9k,
     NBBITS_12k,
     NBBITS_14k,
     NBBITS_16k,
     NBBITS_18k,
     NBBITS_20k,
     NBBITS_23k,
     NBBITS_24k,
     NBBITS_SID
 };

const pj_int16_t prmnofsf[NUM_OF_SPMODES] =
{
    63,  81, 100,
    108, 116, 128,
    136, 152, 156
};


const pj_int16_t dfh_M7k[PRMN_7k] =
{
    3168, 29954, 29213, 16121,
    64, 13440, 30624, 16430,
    19008
};

const pj_int16_t dfh_M9k[PRMN_9k] =
{
    3168, 31665,  9943, 9123,
    15599,  4358, 20248, 2048,
    17040, 27787, 16816, 13888
};

const pj_int16_t dfh_M12k[PRMN_12k] =
{
    3168, 31665,  9943,  9128,
    3647,  8129, 30930, 27926,
    18880, 12319,   496,  1042,
    4061, 20446, 25629, 28069,
    13948
};

const pj_int16_t dfh_M14k[PRMN_14k] =
{
    3168, 31665,  9943,  9131,
    24815,   655, 26616, 26764,
    7238, 19136,  6144,    88,
    4158, 25733, 30567, 30494,
    221, 20321, 17823
};

const pj_int16_t dfh_M16k[PRMN_16k] =
{
    3168, 31665,  9943,  9131,
    24815,   700,  3824,  7271,
    26400,  9528,  6594, 26112,
    108,  2068, 12867, 16317,
    23035, 24632,  7528,  1752,
    6759, 24576
};

const pj_int16_t dfh_M18k[PRMN_18k] =
{
    3168, 31665,  9943,  9135,
    14787, 14423, 30477, 24927,
    25345, 30154,   916,  5728,
    18978,  2048,   528, 16449,
    2436,  3581, 23527, 29479,
    8237, 16810, 27091, 19052,
    0
};

const pj_int16_t dfh_M20k[PRMN_20k] =
{
    3168, 31665,  9943,  9129,
    8637, 31807, 24646,   736,
    28643,  2977,  2566, 25564,
    12930, 13960,  2048,   834,
    3270,  4100, 26920, 16237,
    31227, 17667, 15059, 20589,
    30249, 29123, 0
};

const pj_int16_t dfh_M23k[PRMN_23k] =
{
    3168, 31665,  9943,  9132,
    16748,  3202, 28179, 16317,
    30590, 15857, 19960,  8818,
    21711, 21538,  4260, 16690,
    20224,  3666,  4194,  9497,
    16320, 15388,  5755, 31551,
    14080,  3574, 15932,    50,
    23392, 26053, 31216
};

const pj_int16_t dfh_M24k[PRMN_24k] =
{
    3168, 31665,  9943,  9134,
    24776,  5857, 18475, 28535,
    29662, 14321, 16725,  4396,
    29353, 10003, 17068, 20504,
    720,     0,  8465, 12581,
    28863, 24774,  9709, 26043,
    7941, 27649, 13965, 15236,
    18026, 22047, 16681,  3968
};

pj_int16_t Serial_parm(                        /* Return the parameter    */
        pj_int16_t no_of_bits,                    /* input : number of bits  */
        pj_int16_t ** prms
)
{
    pj_int16_t i;
    pj_int16_t value = 0;

    for ( i = no_of_bits >> 1; i != 0; i--)
    {
        value <<= 2;

        if (*((*prms)++) == BIT_1)
        {
            value |= 2;
        }

        if (*((*prms)++) == BIT_1)
        {
            value |= 1;
        }

    }

    if (no_of_bits&1)
    {
        value <<= 1;

        if (*((*prms)++) == BIT_1)
        {
            value |= 1;
        }

    }

    return (value);
}

__inline pj_int16_t shl_int16(pj_int16_t var1, pj_int16_t var2)
{
    pj_int16_t var_out;

    if (var2 < 0)
    {
        var2 = (-var2) & (0xf);
        var_out = var1 >> var2;
    }
    else
    {
        var2 &= 0xf;
        var_out = var1 << var2;
        if (var_out >> var2 != var1)
        {
            var_out = (var1 >> 15) ^ MAX_16;
        }
    }
    return (var_out);
}


/*----------------------------------------------------------------------------
; EXTERNAL FUNCTION REFERENCES
; Declare functions defined elsewhere and referenced in this module
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; EXTERNAL GLOBAL STORE/BUFFER/POINTER REFERENCES
; Declare variables used in this module but defined elsewhere
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; FUNCTION CODE
----------------------------------------------------------------------------*/

pj_int16_t dhf_test(pj_int16_t input_frame[], pj_int32_t mode, pj_int16_t nparms)
{
    pj_int16_t i, j, tmp, shift;
    pj_int16_t param[DHF_PARMS_MAX];
    pj_int16_t *prms;

    /* overall table with the parameters of the
    decoder homing frames for all modes */

    const pj_int16_t *dhf[] =
    {
        dfh_M7k,
        dfh_M9k,
        dfh_M12k,
        dfh_M14k,
        dfh_M16k,
        dfh_M18k,
        dfh_M20k,
        dfh_M23k,
        dfh_M24k,
        dfh_M24k
    };

    prms = input_frame;
    j = 0;
    i = 0;

    if (mode != MRDTX)
    {
        if (mode != MODE_24k)
        {
            /* convert the received serial bits */
            tmp = nparms - 15;
            while (tmp > j)
            {
                param[i] = Serial_parm(15, &prms);
                j += 15;
                i++;
            }
            tmp = nparms - j;
            param[i] = Serial_parm(tmp, &prms);
            shift = 15 - tmp;
            param[i] = shl_int16(param[i], shift);
        }
        else
        {
            /*If mode is 23.85Kbit/s, remove high band energy bits */
            for (i = 0; i < 10; i++)
            {
                param[i] = Serial_parm(15, &prms);
            }
            param[10] = Serial_parm(15, &prms) & 0x61FF;

            for (i = 11; i < 17; i++)
            {
                param[i] = Serial_parm(15, &prms);
            }
            param[17] = Serial_parm(15, &prms) & 0xE0FF;

            for (i = 18; i < 24; i++)
            {
                param[i] = Serial_parm(15, &prms);
            }
            param[24] = Serial_parm(15, &prms) & 0x7F0F;

            for (i = 25; i < 31; i++)
            {
                param[i] = Serial_parm(15, &prms);
            }

            tmp = Serial_parm(8, &prms);
            param[31] = shl_int16(tmp, 7);
            shift = 0;
        }

        /* check if the parameters matches the parameters of the corresponding decoder homing frame */
        tmp = i;
        j = 0;
        for (i = 0; i < tmp; i++)
        {
            j = (param[i] ^ dhf[mode][i]);
            if (j)
            {
                break;
            }
        }
        tmp = 0x7fff;
        tmp >>= shift;
        tmp = shl_int16(tmp, shift);
        tmp = (dhf[mode][i] & tmp);
        tmp = (param[i] ^ tmp);
        j = (pj_int16_t)(j | tmp);

    }
    else
    {
        j = 1;
    }

    return (!j);
}

/*----------------------------------------------------------------------------
; FUNCTION CODE
----------------------------------------------------------------------------*/


pj_int16_t pvDecoder_AmrWb_homing_frame_test(pj_int16_t input_frame[], pj_int16_t mode)
{
    /* perform test for COMPLETE parameter frame */
    return dhf_test(input_frame, mode, AMR_WB_COMPRESSED[mode]);
}

/*----------------------------------------------------------------------------
; FUNCTION CODE
----------------------------------------------------------------------------*/


pj_int16_t pvDecoder_AmrWb_homing_frame_test_first(pj_int16_t input_frame[], pj_int16_t mode)
{
    /* perform test for FIRST SUBFRAME of parameter frame ONLY */
    return dhf_test(input_frame, mode, prmnofsf[mode]);
}
