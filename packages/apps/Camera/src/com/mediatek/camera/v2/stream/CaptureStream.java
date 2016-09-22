/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2014. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */

package com.mediatek.camera.v2.stream;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

/**
 * A stream used for capture.
 */
public class CaptureStream implements ICaptureStream {
    private static final String                   TAG = CaptureStream.class.getSimpleName();
    private static int                            MAX_CAPTURE_IMAGES = 2;
    // Receives the normal captured images.
    private ImageReader                           mImageReader;
    // Handler thread for camera-related operations.
    private HandlerThread                         mCaptureHandlerThread;
    private Handler                               mCaptureHandler;
    private Surface                               mCaptureSurface;
    private int                                   mCaptureWidth;
    private int                                   mCaptureHeight;
    // Receives the normal captured images.
    private ImageReader.OnImageAvailableListener  mCaptureImageListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.i(TAG, "onImageAvailable mCallback = " + mCallback);
                    if (mCallback != null) {
                        mCallback.onCaptureCompleted(reader.acquireLatestImage());
                    }
                }
            };
    private CaptureStreamCallback                 mCallback;

    public CaptureStream() {

    }

    @Override
    public void setCaptureStreamCallback(CaptureStreamCallback callback) {
        mCallback = callback;
    }

    @Override
    public boolean updateCaptureSize(Size pictureSize, int pictureFormat) {
        Log.i(TAG, "[updateCaptureSize]+ size:" + pictureSize.getWidth() + "x"
                    + pictureSize.getHeight());
        Assert.assertNotNull(pictureSize);
        checkSupportedFormat(pictureFormat);
        // if image reader thread not started, start it
        if (mCaptureHandler == null) {
            mCaptureHandlerThread = new HandlerThread("ImageReaderStream.CaptureThread");
            mCaptureHandlerThread.start();
            mCaptureHandler = new Handler(mCaptureHandlerThread.getLooper());
        }
        // check size, if same size skip
        int width = pictureSize.getWidth();
        int height = pictureSize.getHeight();
        if (mImageReader != null && mImageReader.getWidth() == width
                                 && mImageReader.getHeight() == height
                                 && mImageReader.getImageFormat() == pictureFormat) {
            Log.i(TAG, "[updateCaptureSize]- configure the same size, skip : " + "" +
                    " width  = " + width +
                    " height = " + height +
                    " format = " + pictureFormat);
            return false;
        }
        mCaptureWidth = width;
        mCaptureHeight = height;
        // if previous ImageReader exists, close it for create a new one.
        // FIXME consider capture state, may can not close directly
//        if (mImageReader != null) {
//            mImageReader.close();
//            mImageReader = null;
//        }
        mImageReader = ImageReader.newInstance(mCaptureWidth, mCaptureHeight, pictureFormat,
                MAX_CAPTURE_IMAGES);
        mImageReader.setOnImageAvailableListener(mCaptureImageListener, mCaptureHandler);
        mCaptureSurface = mImageReader.getSurface();
        Log.i(TAG, "[updateCaptureSize]- mCaptureSurface:" + mCaptureSurface);
        return true;
    }

    @Override
    public Map<String, Surface> getCaptureInputSurface() {
        Log.i(TAG, "[getCaptureInputSurface]+");
        Map<String, Surface> surfaceMap = new HashMap<String, Surface>();
        if (mCaptureSurface == null) {
            throw new IllegalStateException("You should call " +
                    "CaptureStream.updateCaptureSize firstly, " +
                    "when get input capture surface");
        }
        surfaceMap.put(CAPUTRE_SURFACE_KEY, mCaptureSurface);
        Log.i(TAG, "[getCaptureInputSurface]- mCaptureSurface:" + mCaptureSurface);
        return surfaceMap;
    }

    private void checkSupportedFormat(int format) {
        boolean supported = false;
        switch (format) {
        case PixelFormat.RGBA_8888:
        case ImageFormat.JPEG:
            supported = true;
            break;
        default:
            break;
        }
        if (!supported) {
            throw new IllegalArgumentException("ImageReaderStream unsupported format : " + format);
        }
    }

    @Override
    public void releaseCaptureStream() {
        Log.i(TAG, "releaseCaptureStream");
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }

        if (mCaptureHandlerThread != null) {
            mCaptureHandlerThread.quitSafely();
            mCaptureHandler = null;
        }
    }
}