/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */


package com.mediatek.wfc.plugin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.ims.ImsConfig;


/** Dialog to be shown on first time wifi enabling.
 */
public class WfcUtils {

    private static final String TAG = "WfcUtils";

    private static final String ACTION_WIFI_ONLY_MODE_CHANGED
            = "android.intent.action.ACTION_WIFI_ONLY_MODE";
    private static final String EXTRA_WIFI_ONLY_MODE_CHANGED = "state";

    /**
     * Get Wfc mode summary.
     * @param wfcMode wfcMode
     * @return int wfcMode
     */
    public static int getWfcModeSummary(int wfcMode) {
        int resId = com.android.internal.R.string.wifi_calling_off_summary;
        switch (wfcMode) {
            case ImsConfig.WfcModeFeatureValueConstants.WIFI_ONLY:
                resId = com.android.internal.R.string.wfc_mode_wifi_only_summary;
                break;
            case ImsConfig.WfcModeFeatureValueConstants.CELLULAR_PREFERRED:
                resId = com.android.internal.R.string.wfc_mode_cellular_preferred_summary;
                break;
            case ImsConfig.WfcModeFeatureValueConstants.WIFI_PREFERRED:
                resId = com.android.internal.R.string.wfc_mode_wifi_preferred_summary;
                break;
            default:
                Log.e("@M_" + TAG, "Unexpected WFC mode value: " + wfcMode);
        }
        return resId;
    }

     /**
     * Sends wifi-only mode intent.
     * @param context context
     * @param mode mode
     * @return
     */
    public static void sendWifiOnlyModeIntent(Context context, boolean mode) {
        Intent intent = new Intent(ACTION_WIFI_ONLY_MODE_CHANGED);
        intent.putExtra(EXTRA_WIFI_ONLY_MODE_CHANGED, mode);
        Log.d("@M_" + TAG, "Sending wifiOnlyMode intent, mode:" + mode);
        context.sendBroadcast(intent);
    }
}
