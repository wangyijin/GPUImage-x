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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;


public class GPUImageView extends FrameLayout implements GPUImageTarget {
    static final int FillModeStretch = 0;                   // Stretch to fill the view, and may distort the image
    static final int FillModePreserveAspectRatio = 1;       // preserve the aspect ratio of the image
    static final int FillModePreserveAspectRatioAndFill =2; // preserve the aspect ratio, and zoom in to fill the view

    protected long mNativeClassID = 0;

    private GLSurfaceView mGLSurfaceView;

    public GPUImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GPUImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (mNativeClassID != 0) return;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUImage.nativeTargetViewNew();
            }
        });

        mGLSurfaceView = new GPUImageViewGLSurfaceView(context, attrs, this);
        GPUImage.getInstance().setGLSurfaceView(mGLSurfaceView);
        addView(mGLSurfaceView);
        if (mGLSurfaceView.getWidth() != 0 && mGLSurfaceView.getHeight() != 0) {
            onSurfaceSizeChanged(mGLSurfaceView.getWidth(), mGLSurfaceView.getHeight());
        }
    }

    public long getNativeClassID() { return mNativeClassID; }

    protected void onSurfaceSizeChanged(final int w, final int h) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeTargetViewOnSizeChanged(mNativeClassID, w, h);
            }
        });
    }

    public void setFillMode(final int fillMode) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeTargetViewSetFillMode(mNativeClassID, fillMode);
            }
        });
    }

    public int getSurfaceWidth() {
        return mGLSurfaceView.getWidth();
    }

    public int getSurfaceHeight() {
        return mGLSurfaceView.getHeight();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mNativeClassID != 0) {
                if (GPUImage.getInstance().getGLSurfaceView() != null) {
                    GPUImage.getInstance().runOnDraw(new Runnable() {
                        @Override
                        public void run() {
                            GPUImage.nativeTargetViewFinalize(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    });
                    GPUImage.getInstance().requestRender();
                } else {
                    GPUImage.nativeTargetViewFinalize(mNativeClassID);
                    mNativeClassID = 0;
                }
            }
        } finally {
            super.finalize();
        }
    }

    private class GPUImageViewGLSurfaceView extends GLSurfaceView {
        private GPUImageView host;

        public GPUImageViewGLSurfaceView(Context context, AttributeSet attrs, GPUImageView host) {
            super(context, attrs);
            this.host = host;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            super.surfaceCreated(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.surfaceChanged(holder, format, w, h);
            host.onSurfaceSizeChanged(w, h);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            super.surfaceDestroyed(holder);
        }
    }

}
