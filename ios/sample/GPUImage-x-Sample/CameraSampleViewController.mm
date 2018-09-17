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

#import "CameraSampleViewController.h"
#include "GPUImage-x.h"

@interface CameraSampleViewController()
{
    GPUImage::SourceCamera* camera;
    GPUImage::Filter* filter;
    GPUImageView* filteredView;
    FilterType curFilterType;
    UITextField* filterHint;
    unsigned char* processedFrameDataForSave;
}

@end

@implementation CameraSampleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    CGRect mainScreenFrame = [[UIScreen mainScreen] bounds];
    
    filteredView = [[GPUImageView alloc] initWithFrame:mainScreenFrame];
    GPUImageView* pureView = [[GPUImageView alloc] initWithFrame:CGRectMake(mainScreenFrame.size.width - 110, 80.0, 100, 100)];
    [pureView setFillMode:GPUImage::TargetView::FillMode::PreserveAspectRatioAndFill];
    
    //[self.view addSubview:filteredView];
    self.view = filteredView;
    [self.view addSubview:pureView];
    
    // slider
    UISlider* slider = [[UISlider alloc] initWithFrame:CGRectMake(25.0, mainScreenFrame.size.height - 50.0, mainScreenFrame.size.width - 50.0, 40.0)];
    [slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    slider.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    slider.minimumValue = 0.0;
    slider.maximumValue = 1.0;
    slider.value = 0.5;
    [self.view addSubview:slider];
    
    // flip camera btn
    UIButton* flipCameraBtn = [[UIButton alloc] initWithFrame:CGRectMake(10, 100, 150, 40)];
    [flipCameraBtn setTitle:@"Flip Camera" forState:UIControlStateNormal];
    [flipCameraBtn setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    [flipCameraBtn setBackgroundColor:[UIColor colorWithWhite:0.7 alpha:0.5]];
    [flipCameraBtn addTarget:self action:@selector(flipCameraBtnClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:flipCameraBtn];
    
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
    
    curFilterType = FilterType::FILTER_BRIGHTNESS;
    GPUImage::Context::getInstance()->runSync([&]{
        camera = GPUImage::SourceCamera::create();
        camera->setOutputImageOrientation(UIInterfaceOrientationPortrait);
        camera->setHorizontallyMirrorFrontFacingCamera(true);
        camera->addTarget(pureView);
        
        filter = GPUImage::BrightnessFilter::create();
        filter->setProperty("brightness", 0.0f);
        camera->addTarget(filter)->addTarget(filteredView);
        
        camera->start();
    });
    
}

- (void)sliderValueChanged:(id)sender
{
    CGFloat value = [(UISlider *)sender value];
    GPUImage::Context::getInstance()->runSync([&]{
        [FilterHelper applyFilter:filter type:curFilterType withSliderValue:value];
    });
}

- (void)flipCameraBtnClicked
{
    GPUImage::Context::getInstance()->runSync([&]{
        camera->flip();
    });
}

- (void)chooseFilterBtnClicked
{
    FilterListViewController* filterListViewController = [[FilterListViewController alloc] initWithDelegate:self];
    [[self navigationController] pushViewController:filterListViewController animated:true];
}

- (void)saveImageBtnClicked
{
    GPUImage::Context::getInstance()->runSync([&]{
        int width = camera->getRotatedFramebufferWidth();
        int height = camera->getRotatedFramebufferHeight();
        if (processedFrameDataForSave != 0) {
            delete[] processedFrameDataForSave;
            processedFrameDataForSave = 0;
        }
        processedFrameDataForSave = camera->captureAProcessedFrameData(filter, width, height);
        
        CGDataProviderRef dataProvider = CGDataProviderCreateWithData(NULL, processedFrameDataForSave, width * height * 4 * sizeof(unsigned char), nil);
        __block CGImageRef cgImageFromBytes = CGImageCreate(width, height, 8, 32, 4 * width, CGColorSpaceCreateDeviceRGB(), kCGBitmapByteOrderDefault | kCGImageAlphaLast, dataProvider, NULL, NO, kCGRenderingIntentDefault);
        
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

- (void)dealloc
{
    GPUImage::Context::getInstance()->runSync([&]{
        if (camera) {
            camera->release();
            camera = 0;
        }
        if (filter) {
            filter->release();
            filter = 0;
        }
    });
    GPUImage::Context::getInstance()->purge();
}

#pragma mark - FilterListView delegate
- (void)filterSelected:(FilterType)filterIdx
{
    curFilterType = filterIdx;
    NSString* hint = [[NSString alloc] initWithFormat:@"Filter: %@", [FilterHelper getFilterName:filterIdx]];
    [filterHint setText:hint];
    GPUImage::Context::getInstance()->runSync([&]{
        camera->removeTarget(filter);
        filter->release(); filter = 0;
        filter = [FilterHelper createFilter:(FilterType)curFilterType];
        camera->addTarget(filter)->addTarget(filteredView);
    });
}

@end
