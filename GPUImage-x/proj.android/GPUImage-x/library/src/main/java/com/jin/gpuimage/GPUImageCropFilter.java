package com.jin.gpuimage;

import android.graphics.RectF;

/**
 * Created by blutter on 2017/11/30.
 */

public class GPUImageCropFilter extends GPUImageSource implements GPUImageTarget {

     private RectF cropRegion;

    static public GPUImageCropFilter create(final RectF cropRegion) {
        return new GPUImageCropFilter(cropRegion);
    }

    private GPUImageCropFilter(final RectF cropRegion) {
        if (mNativeClassID != 0) return;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUImage.nativeCropFilterCreate(cropRegion.left, cropRegion.top, cropRegion.right, cropRegion.bottom);
            }
        });
        this.cropRegion = cropRegion;
    }

    public void setCropRegion(final RectF cropRegion) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                GPUImage.nativeCropFilterSetCropRegion(mNativeClassID, cropRegion.left, cropRegion.top, cropRegion.right, cropRegion.bottom);
            }
        });
        this.cropRegion = cropRegion;
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
                            GPUImage.nativeCropFilterDestroy(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    }
                });
            } else {
                GPUImage.nativeCropFilterDestroy(mNativeClassID);
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
                            GPUImage.nativeCropFilterFinalize(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    });
                    GPUImage.getInstance().requestRender();
                } else {
                    GPUImage.nativeCropFilterFinalize(mNativeClassID);
                    mNativeClassID = 0;
                }
            }
        } finally {
            super.finalize();
        }
    }

}
