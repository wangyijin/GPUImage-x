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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_image_sample).setOnClickListener(this);
        findViewById(R.id.btn_camera_sample).setOnClickListener(this);
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                     int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == R.id.btn_camera_sample) {
                startActivity(new Intent(this, CameraSampleActivity.class));
            }
        } else {
            Log.e("Jin", "onRequestPermissionsResult: camera permission denied");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image_sample:
                startActivity(new Intent(this, ImageSampleActivity.class));
                break;
            case R.id.btn_camera_sample:
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, v.getId());
                } else {
                    startActivity(new Intent(this, CameraSampleActivity.class));
                }
                break;
            default:
                break;
        }
    }
}
