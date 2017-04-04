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
import java.nio.ByteBuffer;

public abstract class GPUImageSource {
    protected long mNativeClassID = 0;

    public long getNativeClassID() { return mNativeClassID; }

    public GPUImageSource addTarget(GPUImageTarget target) {
        return addTarget(target, -1);
    }

    public final GPUImageSource addTarget(final GPUImageTarget target, final int texID) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeSourceAddTarget(mNativeClassID, target.getNativeClassID(), texID, target instanceof GPUImageFilter);
            }
        });
        if (target instanceof GPUImageSource)
            return (GPUImageSource)target;
        else
            return null;
    }

    public final void removeTarget(final GPUImageTarget target) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0 && target.getNativeClassID() != 0)
                    GPUImage.nativeSourceRemoveTarget(mNativeClassID, target.getNativeClassID(), target instanceof GPUImageFilter);
            }
        });
    }

    public final void removeAllTargets() {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeSourceRemoveAllTargets(mNativeClassID);
            }
        });
    }

    public void proceed() {
        proceed(true, true);
    }

    public void proceed(final boolean bUpdateTargets, final boolean bRequestRender) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0)
                    GPUImage.nativeSourceProceed(mNativeClassID, bUpdateTargets);
            }
        });
        if (bRequestRender) {
            GPUImage.getInstance().requestRender();
        }
    }

    public int getRotatedFramebufferWidth() {
        return GPUImage.nativeSourceGetRotatedFramebuferWidth(mNativeClassID);
    }

    public int getRotatedFramebufferHeight() {
        return GPUImage.nativeSourceGetRotatedFramebuferHeight(mNativeClassID);
    }

    public void captureAProcessedFrameData(final GPUImageFilter upToFilter, final ProcessedFrameDataCallback proceedResult) {
        captureAProcessedFrameData(upToFilter, getRotatedFramebufferWidth(), getRotatedFramebufferHeight(), proceedResult);
    }

    public void captureAProcessedFrameData(final GPUImageFilter upToFilter, final int width, final int height, final ProcessedFrameDataCallback proceedResult) {
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0) {
                    byte[] resultData = GPUImage.nativeSourceCaptureAProcessedFrameData(mNativeClassID, upToFilter.getNativeClassID(), width, height);
                    if(resultData != null){
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(resultData));
                        proceedResult.onResult(bmp);
                    }
                }
            }
        });
        GPUImage.getInstance().requestRender();
    }

    public interface ProcessedFrameDataCallback{
        void onResult(Bitmap result);
    }
}
