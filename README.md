# GPUImage-x

<div style="float: right"><img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/icon/icon_240.jpg" /></div>

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Idea from: [iOS GPUImage framework](https://github.com/BradLarson/GPUImage) and [Android GPUImage framework](https://github.com/CyberAgent/android-gpuimage)

The GPUImage-x framework is a **cross-platform (for both Android and iOS) library**, which aims to have something similar to GPUImage that let you apply GPU-accelerated filters to images, live camera video. Part of vertex and fragment shaders is taken from GPUImage. 

The greatest strength of GPUImage-x is that it enables you to **develop your Android and iOS project with one library. The core code of this framework is written in C++, and is exactly the same for both iOS and Android projects,** which locates in `GPUImage-x/proj.iOS/GPUImage-x/GPUImage-x/*.cpp` and `GPUImage-x/proj.android/GPUImage-x/library/src/main/cpp/*.cpp` respectively. Also, you can add new customized filters easily.

## Requirements
- Android 2.2 or higher 
- iPhone 4 or later
- OpenGL ES 2.0

## Usage - iOS

### Frameworks Dependency
Following frameworks are required to be add to your project.
- `AVFoundation.framework`
- `CoreMedia.framework`

### Sample Code

#### Filtering An Image

```c++
GPUImage::SourceImage* sourceImage;
GPUImage::Filter* filter;
GPUImageView* filterView = (GPUImageView*)self.view;
UIImage* inputImage = [UIImage imageNamed:@"test.jpg"];
GPUImage::Context::getInstance()->runSync([&]{
    sourceImage = GPUImage::SourceImage::create(inputImage); // 1. create image source
    filter = GPUImage::GaussianBlurFilter::create();         // 2. create a filter
    sourceImage->addTarget(filter)->addTarget(filterView);   // 3. build pipeline
    sourceImage->proceed();                                  // 4. proceed
});
```

This will filter an image with Gaussian Blur effect. GPUImage-x function calls must be embraced between `GPUImage::Context::getInstance()->runSync([&]{` and `});`, as GPUImage-x code should run in a seperate thread. As you can see, invocation chaining is preferred that will produce concise, elegant, and easy-to-read code. e.g. `sourceImage->addTarget(filter1)->addTarget(filter2)->...->addTarget(filterN)->addTarget(filterView);`

#### Filtering Camera Video

```c++
GPUImage::SourceCamera* camera;
GPUImage::Filter* filter;
GPUImageView* filterView = (GPUImageView*)self.view;
GPUImage::Context::getInstance()->runSync([&]{
    camera = GPUImage::SourceCamera::create();          // 1. create camera source
    filter = GPUImage::BeautifyFilter::create();        // 2. create a filter
    camera->addTarget(filter)->addTarget(filterView);   // 3. build pipeline
    camera->start();                                    // 4. start the camera and proceed
});
```

This will filter a camera video in real time with Beautify Effect.

## Usage - Android

### Gradle Dependency

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.jin.gpuimage-x:gpuimage-x:1.0.0'
}
```

### Sample Code

#### Filtering An Image

```java
// 1. create image source
Bitmap bmp = BitmapFactory.decodeStream(getAssets().open("test.jpg"));
GPUImageSourceImage sourceImage = new GPUImageSourceImage(bmp);   

// 2. create a filter
GPUImageFilter filter = GPUImageFilter.create("GrayscaleFilter");

// 3. build the pipeline
sourceImage.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));

// 4. let the GPUImage-x know which source to use
GPUImage.getInstance().setSource(sourceImage);

// 5. proceed
sourceImage.proceed();
```

This will filter an image with Graysacle effect. More filters can be applied in sequence if you want, e.g. `sourceImage.addTarget(filter1).addTarget(filter2). ... .addTarget(filterN).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));`

#### Filtering Camera Video

```java
// 1. create the camera source
GPUImageSourceCamera sourceCamera = new GPUImageSourceCamera(CameraSampleActivity.this);

// 2. create a filter
GPUImageFilter filter = GPUImageFilter.create("EmbossFilter");

// 3. build the pipeline
sourceCamera.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));

// 4. let the GPUImage-x know which source to use
GPUImage.getInstance().setSource(sourceCamera);
```

This will filter a camera video in real time with Emboss Effect.

## Sample Image Result

<div>
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_raw.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_beautify.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_emboss.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_gaussian_blur.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_pixellation.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_posterize.jpg" />
<img src="https://github.com/wangyijin/raw/blob/master/gpuimage-x/sample_image/sample_sketch.jpg" />
</div>

## License
    Copyright (C) 2017 Yijin Wang, Yiqian Wang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## TODO
- More filters and features will be added. 
- More platforms will be supported.

## Donate
Your donation will be greatly appreciated :)

![Alipay](https://github.com/wangyijin/raw/blob/master/gpuimage-x/alipay.jpg?raw=true "alipay")

## Contact Info
- **Email:** aptx4869wyj@126.com
- **QQ Group:** 21486619
