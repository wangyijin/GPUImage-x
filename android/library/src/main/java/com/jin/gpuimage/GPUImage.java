/*
 * GPUImage-x
 *
 * Copyright (C) 2017 Yijin Wang, Yiqian Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jin.gpuimage;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.graphics.PixelFormat;
import android.os.Build;

public class GPUImage {
    public static final int NoRotation = 0;
    public static final int RotateLeft = 1;
    public static final int RotateRight = 2;
    public static final int FlipVertical = 3;
    public static final int FlipHorizontal = 4;
    public static final int RotateRightFlipVertical = 5;
    public static final int RotateRightFlipHorizontal = 6;
    public static final int Rotate180 = 7;

    private GPUImageRenderer mRenderer = null;
    private GLSurfaceView mGLSurfaceView = null;
    private int mGLSurfaceViewRenderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;

    private static class SingletonHolder {
        private static final GPUImage INSTANCE = new GPUImage();
    }

    private GPUImage(){ init(); }
    public static final GPUImage getInstance() {
        GPUImage instance = SingletonHolder.INSTANCE;
        if (!instance.isInited()) {
            instance.init();
        }
        return instance;
    }

    public boolean isInited() {
        return mRenderer != null;
    }

    public void init() {
        mRenderer = new GPUImageRenderer();
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                nativeContextInit();
            }
        });
    }

    public void destroy() {
        if (!isInited()) return;
        purge();
        setGLSurfaceView(null);
        setSource(null);
        mRenderer.clear();
        mRenderer = null;
    }

    public void setSource(GPUImageSource source) {
        mRenderer.setSource(source);
    }

    public void setGLSurfaceView(final GLSurfaceView view) {
        mGLSurfaceView = view;
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            mGLSurfaceView.setRenderer(mRenderer);
            mGLSurfaceView.setRenderMode(mGLSurfaceViewRenderMode);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                mGLSurfaceView.setPreserveEGLContextOnPause(true);
            }
            mGLSurfaceView.requestRender();
        } else {
            mGLSurfaceViewRenderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;
        }
    }

    public void setGLSurfaceViewRenderMode(int glSurfaceViewRenderMode) {
        mGLSurfaceViewRenderMode = glSurfaceViewRenderMode;
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setRenderMode(mGLSurfaceViewRenderMode);
        }
    }

    public GLSurfaceView getGLSurfaceView() {
        return mGLSurfaceView;
    }

    public void requestRender() {
        if (mGLSurfaceView != null) {
            mGLSurfaceView.requestRender();
        }
    }

    public void purge() {
        if (mGLSurfaceView != null) {
            GPUImage.getInstance().runOnDraw(new Runnable() {
                @Override
                public void run() {
                    GPUImage.nativeContextPurge();
                }
            });
            mGLSurfaceView.requestRender();
        } else {
            GPUImage.nativeContextPurge();
        }
    }

    public GPUImageRenderer getRenderer() {
        return mRenderer;
    }

    public boolean isPreDrawQueueEmpty() { return mRenderer.isPreDrawQueueEmpty(); }

    public void runOnPreDraw(Runnable runnable) {
        mRenderer.runOnPreDraw(runnable);
    }

    public boolean isDrawQueueEmpty() { return mRenderer.isDrawQueueEmpty(); }

    public void runOnDraw(Runnable runnable) {
        mRenderer.runOnDraw(runnable);
    }

    public boolean isPostDrawQueueEmpty() { return mRenderer.isPostDrawQueueEmpty(); }

    public void runOnPostDraw(Runnable runnable) {
        mRenderer.runOnPostDraw(runnable);
    }



    static {
        System.loadLibrary("GPUImage-x");
    }

    // Filter
    public static native long nativeFilterCreate(final String filterClassName);
    public static native void nativeFilterDestroy(long classID);
    public static native void nativeFilterFinalize(long classID);
    public static native void nativeFilterSetPropertyFloat(long classID, String property, float value);
    public static native void nativeFilterSetPropertyInt(long classID, String property, int value);
    public static native void nativeFilterSetPropertyString(long classID, String prooerty, String value);

    // SourceImage
    public static native long nativeSourceImageNew();
    public static native void nativeSourceImageDestroy(final long classID);
    public static native void nativeSourceImageFinalize(final long classID);
    public static native void nativeSourceImageSetImage(final long classID, final Bitmap bitmap);

    // SourceCamera
    public static native long nativeSourceCameraNew();
    public static native void nativeSourceCameraDestroy(final long classID);
    public static native void nativeSourceCameraFinalize(final long classID);
    public static native void nativeSourceCameraSetFrame(final long classID, final int width, final int height, final int[] data, final int rotation);

    // Source
    public static native long nativeSourceAddTarget(final long classID, final long targetClassID, final int texID, final boolean isFilter);
    public static native boolean nativeSourceRemoveTarget(final long classID, final long targetClassID, final boolean isFilter);
    public static native boolean nativeSourceRemoveAllTargets(final long classID);
    public static native boolean nativeSourceProceed(final long classID, final boolean bUpdateTargets);
    public static native int nativeSourceGetRotatedFramebuferWidth(final long classID);
    public static native int nativeSourceGetRotatedFramebuferHeight(final long classID);
    public static native byte[] nativeSourceCaptureAProcessedFrameData(final long classId, final long upToFilterClassId, final int width, final int height);

    // view
    public static native long nativeTargetViewNew();
    public static native void nativeTargetViewFinalize(final long classID);
    public static native void nativeTargetViewOnSizeChanged(final long classID, final int width, final int height);
    public static native void nativeTargetViewSetFillMode(final long classID, final int fillMode);
    // context
    public static native void nativeContextInit();
    public static native void nativeContextDestroy();
    public static native void nativeContextPurge();

    // utils
    public static native void nativeYUVtoRBGA(byte[] yuv, int width, int height, int[] out);

}
