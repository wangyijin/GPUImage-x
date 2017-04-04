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

public class GPUImageSourceImage extends GPUImageSource {

    protected  Bitmap bitmap;
    public GPUImageSourceImage(Bitmap bitmap) {
        if (mNativeClassID != 0) return;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUImage.nativeSourceImageNew();
            }
        });
        setImage(bitmap);
    }

    public void setImage(final Bitmap bitmap) {
        this.bitmap = bitmap;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeSourceImageSetImage(mNativeClassID, bitmap);
            }
        });
    }

    public void destroy() {
        destroy(true);
    }

    public void destroy(boolean onGLThread) {
        if (mNativeClassID != 0) {
            if (onGLThread) {
                GPUImage.getInstance().runOnDraw(new Runnable() {
                    @Override
                    public void run() {
                        if (mNativeClassID != 0) {
                            GPUImage.nativeSourceImageDestroy(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    }
                });
            } else {
                GPUImage.nativeSourceImageDestroy(mNativeClassID);
                mNativeClassID = 0;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mNativeClassID != 0) {
                if (GPUImage.getInstance().getGLSurfaceView() != null) {
                    GPUImage.getInstance().runOnDraw(new Runnable() {
                        @Override
                        public void run() {
                            GPUImage.nativeSourceImageFinalize(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    });
                    GPUImage.getInstance().requestRender();
                } else {
                    GPUImage.nativeSourceImageFinalize(mNativeClassID);
                    mNativeClassID = 0;
                }
            }
        } finally {
            super.finalize();
        }
    }
}
