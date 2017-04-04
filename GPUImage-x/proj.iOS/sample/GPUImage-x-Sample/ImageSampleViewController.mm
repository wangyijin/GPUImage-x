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

#import "ImageSampleViewController.h"
#include "GPUImage-x.h"


@interface ImageSampleViewController()
{
    GPUImage::SourceImage* sourceImage;
    GPUImage::Filter* filter;
    FilterType curFilterType;
    GPUImageView* gpuImageView;
    UITextField* filterHint;
    unsigned char* processedFrameDataForSave;
}

@end

@implementation ImageSampleViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Image Sample";
    
    CGRect mainScreenFrame = [[UIScreen mainScreen] bounds];
    
    gpuImageView = [[GPUImageView alloc] initWithFrame:mainScreenFrame];
    //[self.view addSubview:gpuImageView];
    self.view = gpuImageView;
    
    // slider
    UISlider* slider = [[UISlider alloc] initWithFrame:CGRectMake(25.0, mainScreenFrame.size.height - 50.0, mainScreenFrame.size.width - 50.0, 40.0)];
    [slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    slider.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    slider.minimumValue = 0.0;
    slider.maximumValue = 1.0;
    slider.value = 0.5;
    [self.view addSubview:slider];
    
    // select image btn
    UIButton* selectImageBtn = [[UIButton alloc] initWithFrame:CGRectMake(10, 100, 150, 40)];
    [selectImageBtn setTitle:@"Select Image" forState:UIControlStateNormal];
    [selectImageBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [selectImageBtn setBackgroundColor:[UIColor colorWithWhite:0.7 alpha:0.5]];
    [selectImageBtn addTarget:self action:@selector(selectImageBtnBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:selectImageBtn];
    
    // choose filter btn
    UIButton* chooseFilterBtn = [[UIButton alloc] initWithFrame:CGRectMake(10, 150, 150, 40)];
    [chooseFilterBtn setTitle:@"Choose Filter" forState:UIControlStateNormal];
    [chooseFilterBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [chooseFilterBtn setBackgroundColor:[UIColor colorWithWhite:0.7 alpha:0.5]];
    [chooseFilterBtn addTarget:self action:@selector(chooseFilterBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:chooseFilterBtn];
    
    // save image btn
    UIButton* saveImageBtn = [[UIButton alloc] initWithFrame:CGRectMake(10, 200, 150, 40)];
    [saveImageBtn setTitle:@"Save Image" forState:UIControlStateNormal];
    [saveImageBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [saveImageBtn setBackgroundColor:[UIColor colorWithWhite:0.7 alpha:0.5]];
    [saveImageBtn addTarget:self action:@selector(saveImageBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:saveImageBtn];
    processedFrameDataForSave = 0;
    
    // filter text field
    filterHint = [[UITextField alloc]initWithFrame:CGRectMake(10, mainScreenFrame.size.height - 150.0, 400, 30)];
    [filterHint setText:@"Filter: Brightness"];
    filterHint.textColor = [UIColor yellowColor];
    [self.view addSubview:filterHint];
    
    // proceed
    UIImage *inputImage = [UIImage imageNamed:@"test.jpg"];
    GPUImage::Context::getInstance()->runSync([&]{
        sourceImage = GPUImage::SourceImage::create(inputImage);
        filter = GPUImage::BrightnessFilter::create();
        sourceImage->addTarget(filter)->addTarget(gpuImageView);
        sourceImage->proceed();
    });

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orientationDidChange:) name:UIDeviceOrientationDidChangeNotification object:nil];
}

- (void)orientationDidChange:(NSNotification *)notification
{
    GPUImage::Context::getInstance()->runSync([&]{
        sourceImage->proceed();
    });
}

- (void)dealloc
{
    GPUImage::Context::getInstance()->runSync([&]{
        if (sourceImage) {
            sourceImage->release();
            sourceImage = 0;
        }
        
        if (filter) {
            filter->release();
            filter = 0;
        }
    });
    GPUImage::Context::getInstance()->purge();
}

- (void)sliderValueChanged:(id)sender
{
    CGFloat value = [(UISlider *)sender value];
    GPUImage::Context::getInstance()->runSync([&]{
        [FilterHelper applyFilter:filter type:curFilterType withSliderValue:value];
        sourceImage->proceed();
    });
}

- (void)selectImageBtnBtnClicked
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary])
    {
        UIImagePickerController* imagePickerController=[[UIImagePickerController alloc]init];
        imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        imagePickerController.delegate = self;
        [self presentViewController:imagePickerController animated:YES completion:nil];
    }
}

- (void)chooseFilterBtnClicked
{    
    FilterListViewController* filterListViewController = [[FilterListViewController alloc] initWithDelegate:self];
    [[self navigationController] pushViewController:filterListViewController animated:true];
}

- (void)saveImageBtnClicked
{
    GPUImage::Context::getInstance()->runSync([&]{
        int width = sourceImage->getRotatedFramebufferWidth();
        int heigh = sourceImage->getRotatedFramebufferHeight();
        if (processedFrameDataForSave != 0) {
            delete[] processedFrameDataForSave;
            processedFrameDataForSave = 0;
        }
        processedFrameDataForSave = sourceImage->captureAProcessedFrameData(filter, width, heigh);

        CGDataProviderRef dataProvider = CGDataProviderCreateWithData(NULL, processedFrameDataForSave, width * heigh * 4 * sizeof(unsigned char), nil);
        __block CGImageRef cgImageFromBytes = CGImageCreate(width, heigh, 8, 32, 4 * width, CGColorSpaceCreateDeviceRGB(), kCGBitmapByteOrderDefault | kCGImageAlphaLast, dataProvider, NULL, NO, kCGRenderingIntentDefault);
        
        UIImage* finalImage = [UIImage imageWithCGImage:cgImageFromBytes scale:1.0 orientation:UIImageOrientationUp];
        UIImageWriteToSavedPhotosAlbum(finalImage, self, @selector(image:didFinishSavingWithError:contextInfo:), (__bridge void *)self);
        
        CGImageRelease(cgImageFromBytes);
        CGDataProviderRelease(dataProvider);
    });
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo
{
    NSLog(@"didFinishSaving image = %@, error = %@, contextInfo = %@", image, error, contextInfo);
    
    delete[] processedFrameDataForSave;
    processedFrameDataForSave = 0;
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{
    NSString* mediaType = [info objectForKey:UIImagePickerControllerMediaType];
    if ([mediaType isEqualToString:@"public.image"])
    {
        UIImage* image = [info objectForKey:UIImagePickerControllerOriginalImage];
        GPUImage::Context::getInstance()->purge(); // purge the cache if you want
        GPUImage::Context::getInstance()->runSync([&]{
            sourceImage->setImage(image)->proceed();
        });
    }
    [picker dismissViewControllerAnimated:false completion:nil];
}

#pragma mark - FilterListView delegate
- (void)filterSelected:(FilterType)filterIdx
{
    curFilterType = filterIdx;
    NSString* hint = [[NSString alloc] initWithFormat:@"Filter: %@", [FilterHelper getFilterName:filterIdx]];
    [filterHint setText:hint];
    GPUImage::Context::getInstance()->runSync([&]{
        sourceImage->removeTarget(filter);
        filter->release(); filter = 0;
        filter = [FilterHelper createFilter:(FilterType)curFilterType];
        sourceImage->addTarget(filter)->addTarget(gpuImageView);
        sourceImage->proceed();
    });
}

@end
