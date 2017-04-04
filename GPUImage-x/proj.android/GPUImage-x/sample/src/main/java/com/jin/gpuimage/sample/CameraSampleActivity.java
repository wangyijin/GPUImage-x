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

package com.jin.gpuimage.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import com.jin.gpuimage.GPUImage;
import com.jin.gpuimage.GPUImageFilter;
import com.jin.gpuimage.GPUImageSource;
import com.jin.gpuimage.GPUImageSourceCamera;
import com.jin.gpuimage.GPUImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CameraSampleActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, FilterHelper.OnFilterSelectedListener {

    private GPUImageSourceCamera sourceCamera;
    private GPUImageFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_sample);

        findViewById(R.id.btn_flip_cam).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_capture).setOnClickListener(this);

        SeekBar seekBar = ((SeekBar) findViewById(R.id.seek));
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(50);

        sourceCamera = new GPUImageSourceCamera(CameraSampleActivity.this);
        filter = GPUImageFilter.create("BrightnessFilter");
        sourceCamera.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
        GPUImage.getInstance().setSource(sourceCamera);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flip_cam:
                if (sourceCamera != null) {
                    sourceCamera.switchCamera();
                }
                break;
            case R.id.btn_filter:
                FilterHelper.showListDialog(this, this);
                break;
            case R.id.btn_capture:
                sourceCamera.captureAProcessedFrameData(filter, new GPUImageSource.ProcessedFrameDataCallback() {
                    @Override
                    public void onResult(Bitmap result) {
                        if (result != null) {
                            File path = Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            final File file = new File(path, "gpuimage-x" + "/" + System.currentTimeMillis() + ".jpg");
                            try {
                                file.getParentFile().mkdirs();
                                result.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));

                                // make a toast
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CameraSampleActivity.this, "Image Saved:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Tell the media scanner about the new file,
                                // so that it is immediately present in your album.
                                MediaScannerConnection.scanFile(CameraSampleActivity.this,
                                        new String[] {
                                                file.toString()
                                        }, null, null);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void OnFilterSelected(GPUImageFilter newFilter) {
        sourceCamera.removeTarget(filter);
        filter.destroy(); // destroy instance if you want
        sourceCamera.addTarget(newFilter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
        filter = newFilter;
        //sourceCamera.proceed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (sourceCamera != null && filter != null) {
            float value = progress / 100.0f; // let the value between 0 and 1
            FilterHelper.applyFilterWithSliderValue(filter, value);
            //sourceCamera.proceed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sourceCamera != null) {
            sourceCamera.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (sourceCamera != null) {
            sourceCamera.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        GPUImage.getInstance().destroy();
        super.onDestroy();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
