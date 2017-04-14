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

#import <UIKit/UIKit.h>
#include "GPUImage-x.h"

typedef enum {
    FILTER_BRIGHTNESS,
    FILTER_COLOR_INVERT,
    FILTER_GRAYSCALE,
    FILTER_GAUSSIAN_BLUR, 
    FILTER_BILATERAL,
    FILTER_IOS_BLUR,
    FILTER_CANNY_EDGE_DETECTION, 
    FILTER_WEAK_PIXEL_INCLUSION, 
    FILTER_NON_MAXIMUM_SUPPRESSION, 
    FILTER_BEAUTIFY,
    FILTER_SOBEL_EDGE_DETECTION,
    FILTER_SKETCH,
    FILTER_TOON,
    FILTER_SMOOTH_TOON,
    FILTER_POSTERIZE,
    FILTER_PIXELLATION,
    FILTER_SATURATION,
    FILTER_CONTRAST,
    FILTER_EXPOSURE,
    FILTER_RGB,
    FILTER_HUE,
    FILTER_WHITE_BALANCE,
    FILTER_LUMINANCE_RANGE,
    FILTER_EMBOSS,
    FILTER_HALFTONE,
    FILTER_CROSSHATCH,
    NUM_FILTERS
} FilterType;

@interface FilterHelper : NSObject

+ (NSString*)getFilterName:(FilterType)filterIdx;
+ (GPUImage::Filter*)createFilter:(FilterType)filterIdx;
+ (void)applyFilter:(GPUImage::Filter*)filter type:(FilterType)type withSliderValue:(float)value;

@end
