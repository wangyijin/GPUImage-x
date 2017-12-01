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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


public class GPUImageSourceCamera extends GPUImageSource implements Camera.PreviewCallback {
    private Camera mCamera;
    private int mCurrentCameraId = 1;
    private IntBuffer mRGBABuffer;
    private int mRotation = GPUImage.NoRotation;
    private Context mContext;
    private SurfaceTexture mSurfaceTexture = null;
    int[] textures = new int[1];

    public GPUImageSourceCamera(Context context) {
        mContext = context;
        if (mNativeClassID != 0) return;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUImage.nativeSourceCameraNew();
            }
        });
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        final Camera.Size previewSize = camera.getParameters().getPreviewSize();
        if (mRGBABuffer == null) {
            mRGBABuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }
        final Camera cam = camera;
        GPUImage.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mNativeClassID != 0) {
                    GPUImage.nativeYUVtoRBGA(data, previewSize.width, previewSize.height, mRGBABuffer.array());
                    cam.addCallbackBuffer(data);
                    GPUImage.nativeSourceCameraSetFrame(mNativeClassID, previewSize.width, previewSize.height, mRGBABuffer.array(), mRotation);
                }
            }
        });
        proceed(true, true);
    }

    public void onResume() {
        setUpCamera(mCurrentCameraId);
    }

    public void onPause() {
        releaseCamera();
    }

    public void switchCamera() {
        releaseCamera();
        mCurrentCameraId = (mCurrentCameraId + 1) % Camera.getNumberOfCameras();
        setUpCamera(mCurrentCameraId);
    }

    private void setUpCamera(final int id) {

        mCamera = Camera.open(id);
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);//自动白平衡
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);//自动相机场景类型
        parameters.setRotation(90);
        mCamera.setParameters(parameters);

        int deviceRotation = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getRotation();
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);

        int rotation = 0;
        switch (deviceRotation) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }

        mRotation = GPUImage.NoRotation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation + rotation) % 360;
            switch (rotation) {
                case 0:
                    mRotation = GPUImage.FlipHorizontal;
                    break;
                case 90:
                    mRotation = GPUImage.RotateRightFlipVertical;
                    break;
                case 180:
                    mRotation = GPUImage.FlipVertical;
                    break;
                case 270:
                    mRotation = GPUImage.RotateRightFlipHorizontal;
                    break;
            }
        } else {
            rotation = (info.orientation - rotation + 360) % 360;
            switch (rotation) {
                case 90:
                    mRotation = GPUImage.RotateRight;
                    break;
                case 180:
                    mRotation = GPUImage.Rotate180;
                    break;
                case 270:
                    mRotation = GPUImage.RotateLeft;
                    break;
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
//            GPUImage.getInstance().runOnDraw(new Runnable() {
//                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//                @Override
//                public void run() {
//                    if (mNativeClassID != 0) {
//                        int[] textures = new int[1];
//                        GLES20.glGenTextures(1, textures, 0);
//                        mSurfaceTexture = new SurfaceTexture(textures[0]);
//                        try {
//                            mCamera.setPreviewTexture(mSurfaceTexture);
//                            mCamera.setPreviewCallback(GPUImageSourceCamera.this);
//                            mCamera.startPreview();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            GPUImage.getInstance().requestRender();
            GPUImage.getInstance().runOnDraw(new Runnable() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    if (mNativeClassID != 0) {

                        GLES20.glGenTextures(1, textures, 0);
                        mSurfaceTexture = new SurfaceTexture(textures[0]);
                        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
                        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

                        final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();


                        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                            @Override
                            public void onFrameAvailable(SurfaceTexture surfaceTexture) {

                                final SurfaceTexture surface = surfaceTexture;
                                GPUImage.getInstance().runOnDraw(new Runnable() {
                                    @Override
                                    public void run() {
                                        surface.updateTexImage();
                                        GPUImage.nativeSourceCameraSetFrameTexture(mNativeClassID, previewSize.width, previewSize.height, textures, mRotation);
                                    }
                                });
                                proceed(true, true);
                            }
                        });
                        try {
                            mCamera.setPreviewTexture(mSurfaceTexture);
                            mCamera.startPreview();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            GPUImage.getInstance().requestRender();
        } else {
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        }
    }

    private void releaseCamera() {
        mSurfaceTexture = null;
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
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
                            GPUImage.nativeSourceCameraDestroy(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    }
                });
            } else {
                GPUImage.nativeSourceCameraDestroy(mNativeClassID);
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
                            GPUImage.nativeSourceCameraFinalize(mNativeClassID);
                            mNativeClassID = 0;
                        }
                    });
                    GPUImage.getInstance().requestRender();
                } else {
                    GPUImage.nativeSourceCameraFinalize(mNativeClassID);
                    mNativeClassID = 0;
                }
            }
        } finally {
            super.finalize();
        }
    }
}
