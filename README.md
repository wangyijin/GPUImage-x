# GPUImage-x
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Idea from: [iOS GPUImage framework](https://github.com/BradLarson/GPUImage) and [Android GPUImage framework](https://github.com/CyberAgent/android-gpuimage)

The GPUImage-x framework is a **cross-platform (for both Android and iOS) library**, which aims to have something similar to GPUImage that let you apply GPU-accelerated filters to images, live camera video. Part of vertex and fragment shaders is taken from GPUImage. 

The greatest strength of GPUImage-x is that it enables you to **develop your Android and iOS project with one library. The core code of this framework is written in C++, and is exactly the same for both iOS and Android projects,** which locates in `GPUImage-x/proj.iOS/GPUImage-x/GPUImage-x/*.cpp` and `GPUImage-x/proj.android/GPUImage-x/library/src/main/cpp/*.cpp` respectively.

## Requirements
- Android 2.2 or higher 
- iPhone 4 or later
- OpenGL ES 2.0

## Usage - iOS

### Frameworks Dependency
Following frameworks are required to be add to your project.
- `AVFoundation.framework`
- `CoreMedia.framework`

### Usage Description in Info.plist
- `NSPhotoLibraryUsageDescription` must be filled if your app will access photo library.
- `NSCameraUsageDescription` must be filled if camera is used.

### Sample Code

#### Filtering An Image

```c++
GPUImage::SourceImage* sourceImage;
GPUImage::Filter* filter;
GPUImageView* filterView = (GPUImageView*)self.view;
UIImage* inputImage = [UIImage imageNamed:@"test.jpg"];
GPUImage::Context::getInstance()->runSync([&]{
    sourceImage = GPUImage::SourceImage::create(inputImage);
    filter = GPUImage::GaussianBlurFilter::create();
    sourceImage->addTarget(filter)->addTarget(filterView);
    sourceImage->proceed();
});
```

This will filter an image with Gaussian Blur effect. GPUImage-x function calls must be embraced between `GPUImage::Context::getInstance()->runSync([&]{` and `});`, as GPUImage-x code should run in a seperate thread. As you can see, invocation chaining is preferred that will produce concise, elegant, and easy-to-read code. e.g. `sourceImage->addTarget(filter1)->addTarget(filter2)->...->addTarget(filterN)->addTarget(filterView);`

#### Filtering Camera Video

```c++
GPUImage::SourceCamera* camera;
GPUImage::Filter* filter;
GPUImageView* filterView = (GPUImageView*)self.view;
GPUImage::Context::getInstance()->runSync([&]{
    camera = GPUImage::SourceCamera::create();
    filter = GPUImage::BeautifyFilter::create();
    camera->addTarget(filter)->addTarget(filterView);
    camera->start();
});
```

This will filter a camera video in real time with Beautify Effect.

## Usage - Android

### Gradle dependency

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
Bitmap bmp = BitmapFactory.decodeStream(getAssets().open("test.jpg"));
GPUImageSourceImage sourceImage = new GPUImageSourceImage(bmp);
GPUImageFilter filter = GPUImageFilter.create("GrayscaleFilter");
sourceImage.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
GPUImage.getInstance().setSource(sourceImage);
sourceImage.proceed();
```

#### Filtering Camera Video

```java
GPUImageSourceCamera sourceCamera = new GPUImageSourceCamera(CameraSampleActivity.this);
GPUImageFilter filter = GPUImageFilter.create("EmbossFilter");
sourceCamera.addTarget(filter).addTarget((GPUImageView) findViewById(R.id.gpuimagexview));
GPUImage.getInstance().setSource(sourceCamera);
```

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

![Alipay](https://github.com/wangyijin/raw/blob/master/alipay.jpg?raw=true "alipay")
