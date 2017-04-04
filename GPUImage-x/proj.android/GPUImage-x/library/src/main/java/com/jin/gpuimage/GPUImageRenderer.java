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


import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Queue;
import java.util.LinkedList;

public class GPUImageRenderer implements Renderer{
    private final Queue<Runnable> mPreDrawQueue;
    private final Queue<Runnable> mDrawQueue;
    private final Queue<Runnable> mPostDrawQueue;

    private GPUImageSource mSource = null;

    public GPUImageRenderer() {
        mPreDrawQueue = new LinkedList<Runnable>();
        mDrawQueue = new LinkedList<Runnable>();
        mPostDrawQueue = new LinkedList<Runnable>();
    }

    public void setSource(GPUImageSource source) {
        mSource = source;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mSource != null) {
            mSource.proceed(true, false);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        runAll(mPreDrawQueue);
        runAll(mDrawQueue);
        runAll(mPostDrawQueue);
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    protected boolean isPreDrawQueueEmpty() {
        synchronized (mPreDrawQueue) {
            return mPreDrawQueue.isEmpty();
        }
    }

    protected void runOnPreDraw(final Runnable runnable) {
        synchronized (mPreDrawQueue) {
            mPreDrawQueue.add(runnable);
        }
    }

    protected boolean isDrawQueueEmpty() {
        synchronized (mDrawQueue) {
            return mDrawQueue.isEmpty();
        }
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mDrawQueue) {
            mDrawQueue.add(runnable);
        }
    }

    protected boolean isPostDrawQueueEmpty() {
        synchronized (mPostDrawQueue) {
            return mPostDrawQueue.isEmpty();
        }
    }

    protected void runOnPostDraw(final Runnable runnable) {
        synchronized (mPostDrawQueue) {
            mPostDrawQueue.add(runnable);
        }
    }

    public void clear() {
        mPreDrawQueue.clear();
        mDrawQueue.clear();
        mPostDrawQueue.clear();
    }
}
