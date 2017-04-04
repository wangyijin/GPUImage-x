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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import com.jin.gpuimage.GPUImage;
import com.jin.gpuimage.GPUImageFilter;
import com.jin.gpuimage.GPUImageSource;
import com.jin.gpuimage.GPUImageSourceImage;
import com.jin.gpuimage.GPUImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSampleActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, FilterHelper.OnFilterSelectedListener {

    private final int REQ_PICK_IMG = 1;
    private GPUImageSourceImage sourceImage;
    private GPUImageFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sample);

        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        SeekBar seekBar = ((SeekBar) findViewById(R.id.seek));
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(50);

        try {
            Bitmap bmp = BitmapFactory.decodeStream(getAssets().open("test.jpg"));
            sourceImage = new GPUImageSourceImage(bmp);
            filter = GPUImageFilter.create("BrightnessFilter");
            sourceImage.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
            GPUImage.getInstance().setSource(sourceImage);
            sourceImage.proceed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQ_PICK_IMG);
                break;
            case R.id.btn_filter:
                FilterHelper.showListDialog(this, this);
                break;
            case R.id.btn_save:
                sourceImage.captureAProcessedFrameData(filter, new GPUImageSource.ProcessedFrameDataCallback() {
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
                                            Toast.makeText(ImageSampleActivity.this, "Image Saved:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Tell the media scanner about the new file,
                                    // so that it is immediately present in your album.
                                    MediaScannerConnection.scanFile(ImageSampleActivity.this,
                                            new String[] {
                                                    file.toString()
                                            }, null, null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                });
                sourceImage.proceed();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnFilterSelected(GPUImageFilter newFilter) {
        sourceImage.removeTarget(filter);
        filter.destroy(); // destroy instance if you want
        sourceImage.addTarget(newFilter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
        filter = newFilter;
        sourceImage.proceed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (sourceImage != null && filter != null) {
            float value = progress / 100.0f; // let the value between 0 and 1
            FilterHelper.applyFilterWithSliderValue(filter, value);
            sourceImage.proceed();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_PICK_IMG) {
            try {
                if (sourceImage != null)
                    sourceImage.setImage(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        GPUImage.getInstance().destroy();
        super.onDestroy();
    }
}
