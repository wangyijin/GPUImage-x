package com.jin.gpuimage;

/**
 * Created by blutter on 2017/11/26.
 */

public class GPUImageRawDataOutput implements GPUImageTarget {

    protected long mNativeClassID = 0;

    public static GPUImageRawDataOutput create(final int newImageWidth, final int newImageHeight, final Boolean resultsInBGRAFormat) {
        return new GPUImageRawDataOutput(newImageWidth, newImageHeight, resultsInBGRAFormat);
    }

    private GPUImageRawDataOutput(final int newImageWidth, final int newImageHeight, final Boolean resultsInBGRAFormat) {
        if (mNativeClassID != 0) return;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUImage.nativeTargetRawDataOutputNew(newImageWidth, newImageHeight, resultsInBGRAFormat);
            }
        });
    }

    @Override
    public long getNativeClassID() {
        return mNativeClassID;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mNativeClassID != 0) {
                if (GPUImage.getInstance().getGLSurfaceView() != null) {
                    GPUImage.getInstance().runOnDraw(new Runnable() {
                        @Override
                        public void run() {
                            GPUImage.nativeTargetRawDataOutputFinalize(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    });
                } else {
                    GPUImage.nativeTargetRawDataOutputFinalize(mNativeClassID);
                    mNativeClassID = 0;
                }
            }
        } finally {
            super.finalize();
        }
    }

    public void setNewFrameAvailableCallback(final NewFrameAvailableCallback callback) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0) {
                    GPUImage.nativeTargetRawDataOutputNewFrameAvailableCallback(mNativeClassID, callback);
                }
            }
        });
    }

    public interface NewFrameAvailableCallback {
        void onNewFrameAvailable(byte[] bytes, int bytesPerRowInOutput);
    }
}
