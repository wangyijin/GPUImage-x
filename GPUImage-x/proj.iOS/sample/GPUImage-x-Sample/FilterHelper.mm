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

#import "FilterHelper.h"
#import "math.h"

@interface FilterHelper ()

@end

@implementation FilterHelper

+ (NSString*)getFilterName:(FilterType)filterIdx
{
    switch (filterIdx) {
        case FILTER_BRIGHTNESS: return @"Brightness";
        case FILTER_COLOR_INVERT: return @"Color Invert";
        case FILTER_GRAYSCALE: return @"Grayscale";
        case FILTER_GAUSSIAN_BLUR: return @"Gaussian Blur";
        case FILTER_BILATERAL: return @"Bilateral Blur";
        case FILTER_IOS_BLUR: return @"iOS 7 Blur";
        case FILTER_CANNY_EDGE_DETECTION: return @"Canny Edge Detection";
        case FILTER_WEAK_PIXEL_INCLUSION: return @"Weak Pixel Inclusion";
        case FILTER_NON_MAXIMUM_SUPPRESSION: return @"Non Maximum Suppression";
        case FILTER_BEAUTIFY: return @"Beautify";
        case FILTER_SOBEL_EDGE_DETECTION: return @"Sobel Edge Detection";
        case FILTER_SKETCH: return @"Sketch";
        case FILTER_TOON: return @"Toon";
        case FILTER_SMOOTH_TOON: return @"Smooth Toon";
        case FILTER_POSTERIZE: return @"Posterize";
        case FILTER_PIXELLATION: return @"Pixellation";
        case FILTER_SATURATION: return @"Saturation";
        case FILTER_CONTRAST: return @"Contrast";
        case FILTER_EXPOSURE: return @"Exposure";
        case FILTER_RGB: return @"RGB Adjustment";
        case FILTER_HUE: return @"Hue Adjustment";
        case FILTER_WHITE_BALANCE: return @"White Balance";
        case FILTER_LUMINANCE_RANGE_FILTER: return @"Luminance Range";
        default:break;
    }
    return @"";
}

+ (GPUImage::Filter*)createFilter:(FilterType)filterIdx
{
    GPUImage::Filter* filter = 0;
    switch (filterIdx) {
        case FILTER_BRIGHTNESS:
        {
            filter = GPUImage::BrightnessFilter::create();
        }; break;
        case FILTER_COLOR_INVERT:
        {
            filter = GPUImage::ColorInvertFilter::create();
        }; break;
        case FILTER_GRAYSCALE:
        {
            filter = GPUImage::GrayscaleFilter::create();
        }; break;
        case FILTER_GAUSSIAN_BLUR:
        {
            filter = GPUImage::GaussianBlurFilter::create();
        }; break;
        case FILTER_BILATERAL:
        {
            filter = GPUImage::BilateralFilter::create();
        }; break;
        case FILTER_IOS_BLUR:
        {
            filter = GPUImage::IOSBlurFilter::create();
        }; break;
        case FILTER_CANNY_EDGE_DETECTION:
        {
            filter = GPUImage::CannyEdgeDetectionFilter::create();
        }; break;
        case FILTER_WEAK_PIXEL_INCLUSION:
        {
            filter = GPUImage::WeakPixelInclusionFilter::create();
        }; break;
        case FILTER_NON_MAXIMUM_SUPPRESSION:
        {
            filter = GPUImage::NonMaximumSuppressionFilter::create();
        }; break;
        case FILTER_BEAUTIFY:
        {
            filter = GPUImage::BeautifyFilter::create();
        }; break;
        case FILTER_SOBEL_EDGE_DETECTION:
        {
            filter = GPUImage::SobelEdgeDetectionFilter::create();
        }; break;
        case FILTER_SKETCH:
        {
            filter = GPUImage::SketchFilter::create();
        }; break;
        case FILTER_TOON:
        {
            filter = GPUImage::ToonFilter::create();
        }; break;
        case FILTER_SMOOTH_TOON:
        {
            filter = GPUImage::SmoothToonFilter::create();
        }; break;
        case FILTER_POSTERIZE:
        {
            filter = GPUImage::PosterizeFilter::create();
        }; break;
        case FILTER_PIXELLATION:
        {
            filter = GPUImage::PixellationFilter::create();
        }; break;
        case FILTER_SATURATION:
        {
            filter = GPUImage::SaturationFilter::create();
        }; break;
        case FILTER_CONTRAST:
        {
            filter = GPUImage::ContrastFilter::create();
        }; break;
        case FILTER_EXPOSURE:
        {
            filter = GPUImage::ExposureFilter::create();
        }; break;
        case FILTER_RGB:
        {
            filter = GPUImage::RGBFilter::create();
        }; break;
        case FILTER_HUE:
        {
            filter = GPUImage::HueFilter::create();
        }; break;
        case FILTER_WHITE_BALANCE:
        {
            filter = GPUImage::WhiteBalanceFilter::create();
        }; break;
        case FILTER_LUMINANCE_RANGE_FILTER:
        {
            filter = GPUImage::LuminanceRangeFilter::create();
        }; break;
        default:
            break;
    }
    
    return filter;
}

+ (void)applyFilter:(GPUImage::Filter*)filter type:(FilterType)type withSliderValue:(float)value;
{
    if (filter == 0) return;
    switch (type) {
        case FILTER_BRIGHTNESS:
        {
            // let the value between -1 and 1
            value = (value - 0.5) * 2.0;
            filter->setProperty("brightness", value);
        }; break;
        case FILTER_COLOR_INVERT:
        {
        }; break;
        case FILTER_GRAYSCALE:
        {
        }; break;
        case FILTER_GAUSSIAN_BLUR:
        {
            // let the value between 0 - 24
            value = value * 24;
            //filter->setProperty("radius", (int)round(value));
            filter->setProperty("sigma", (float)value);
        }; break;
        case FILTER_IOS_BLUR:
        {
            value = value * 10.0 + 1.0;
            filter->setProperty("downSampling", (float)value);
        }; break;
        case FILTER_BILATERAL:
        {
            // let the value between 0 - 10
            value = value * 10.0;
            filter->setProperty("distanceNormalizationFactor", value);
        }; break;
        case FILTER_CANNY_EDGE_DETECTION:
        {
        }; break;
        case FILTER_WEAK_PIXEL_INCLUSION:
        {
        }; break;
        case FILTER_NON_MAXIMUM_SUPPRESSION:
        {
            value = value * 5.0;
            filter->setProperty("texelSizeMultiplier", value);
        }; break;
        case FILTER_BEAUTIFY:
        {
        }; break;
        case FILTER_SOBEL_EDGE_DETECTION:
        {
            filter->setProperty("edgeStrength", value);
        }; break;
        case FILTER_SKETCH:
        {
            filter->setProperty("edgeStrength", value);
        }; break;
        case FILTER_TOON:
        {
            value = value * 20.0;
            filter->setProperty("quantizationLevels", value);
        }; break;
        case FILTER_SMOOTH_TOON:
        {
            // let the value between 1 - 6
            value = value * 5.0 + 1.0;
            filter->setProperty("blurRadius", (int)value);
        }; break;
        case FILTER_POSTERIZE:
        {
            // let the value between 1 and 256
            value = value * 19.0 + 1.0;
            filter->setProperty("colorLevels", (int)value);
        }; break;
        case FILTER_PIXELLATION:
        {
            filter->setProperty("pixelSize", value);
        }; break;
        case FILTER_SATURATION:
        {
            // let the value between 0 - 2
            value = value * 2.0;
            filter->setProperty("saturation", value);
        }; break;
        case FILTER_CONTRAST:
        {
            value = value * 4.0;
            filter->setProperty("contrast", value);
        }; break;
        case FILTER_EXPOSURE:
        {
            // let the value between -10 and 10
            value = (value - 0.5) * 20.0;
            filter->setProperty("exposure", value);
        }; break;
        case FILTER_RGB:
        {
            value = value * 2.0;
            filter->setProperty("greenAdjustment", value);
        }; break;
        case FILTER_HUE:
        {
            value = value * 360;
            filter->setProperty("hueAdjustment", value);
        }; break;
        case FILTER_WHITE_BALANCE:
        {
            value = value * 5000.0 + 2500.0;
            filter->setProperty("temperature", value);
        }; break;
        case FILTER_LUMINANCE_RANGE_FILTER:
        {
            filter->setProperty("rangeReductionFactor", value);
        }; break;
        default:
            break;
    }

}


@end
