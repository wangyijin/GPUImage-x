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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.jin.gpuimage.GPUImageFilter;
import java.util.LinkedList;
import java.util.List;

public class FilterHelper {
    static final int FILTER_BRIGHTNESS = 0;
    static final int FILTER_COLOR_INVERT = 1;
    static final int FILTER_GRAYSCALE = 2;
    static final int FILTER_GAUSSIAN_BLUR = 3;
    static final int FILTER_BILATERAL = 4;
    static final int FILTER_IOS_BLUR = 5;
    static final int FILTER_CANNY_EDGE_DETECTION = 6;
    static final int FILTER_WEAK_PIXEL_INCLUSION = 7;
    static final int FILTER_NON_MAXIMUM_SUPPRESSION = 8;
    static final int FILTER_BEAUTIFY = 9;
    static final int FILTER_SOBEL_EDGE_DETECTION = 10;
    static final int FILTER_SKETCH = 11;
    static final int FILTER_TOON = 12;
    static final int FILTER_SMOOTH_TOON = 13;
    static final int FILTER_POSTERIZE = 14;
    static final int FILTER_PIXELLATION = 15;
    static final int FILTER_SATURATION = 16;
    static final int FILTER_CONTRAST = 17;
    static final int FILTER_EXPOSURE = 18;
    static final int FILTER_RGB = 19;
    static final int FILTER_HUE = 20;
    static final int FILTER_WHITE_BALANCE = 21;
    static final int FILTER_LUMINANCE_RANGE = 22;
    static final int FILTER_EMBOSS = 23;

    private static FilterList filterList = null;

    private static FilterList getFilterList() {
        if (filterList == null) {
            filterList = new FilterList();
            filterList.addFilter(FILTER_BRIGHTNESS, "Brightness", "BrightnessFilter");
            filterList.addFilter(FILTER_COLOR_INVERT, "Color Invert", "ColorInvertFilter");
            filterList.addFilter(FILTER_GRAYSCALE, "Grayscale", "GrayscaleFilter");
            filterList.addFilter(FILTER_GAUSSIAN_BLUR, "Gaussian Blur", "GaussianBlurFilter");
            filterList.addFilter(FILTER_BILATERAL, "Bilateral", "BilateralFilter");
            filterList.addFilter(FILTER_IOS_BLUR, "iOS 7 Blur", "IOSBlurFilter");
            filterList.addFilter(FILTER_CANNY_EDGE_DETECTION, "Canny Edge Detection", "CannyEdgeDetectionFilter");
            filterList.addFilter(FILTER_WEAK_PIXEL_INCLUSION, "Weak Pixel Inclusion", "WeakPixelInclusionFilter");
            filterList.addFilter(FILTER_NON_MAXIMUM_SUPPRESSION, "Non Maximum Suppression", "NonMaximumSuppressionFilter");
            filterList.addFilter(FILTER_BEAUTIFY, "Beautify", "BeautifyFilter");
            filterList.addFilter(FILTER_SOBEL_EDGE_DETECTION, "Sobel Edge Detection", "SobelEdgeDetectionFilter");
            filterList.addFilter(FILTER_SKETCH, "Sketch", "SketchFilter");
            filterList.addFilter(FILTER_TOON, "Toon", "ToonFilter");
            filterList.addFilter(FILTER_SMOOTH_TOON, "Smooth Toon", "SmoothToonFilter");
            filterList.addFilter(FILTER_POSTERIZE, "Posterize", "PosterizeFilter");
            filterList.addFilter(FILTER_PIXELLATION, "Pixellation", "PixellationFilter");
            filterList.addFilter(FILTER_SATURATION, "Saturation", "SaturationFilter");
            filterList.addFilter(FILTER_CONTRAST, "Contrast", "ContrastFilter");
            filterList.addFilter(FILTER_EXPOSURE, "Exposure", "ExposureFilter");
            filterList.addFilter(FILTER_RGB, "RGB Adjustment", "RGBFilter");
            filterList.addFilter(FILTER_HUE, "Hue Adjustment", "HueFilter");
            filterList.addFilter(FILTER_WHITE_BALANCE, "White Balance", "WhiteBalanceFilter");
            filterList.addFilter(FILTER_LUMINANCE_RANGE, "Luminance Range", "LuminanceRangeFilter");
            filterList.addFilter(FILTER_EMBOSS, "Emboss Filter", "EmbossFilter");
        }
        return filterList;
    }

    public static void showListDialog(final Context context,
                                  final OnFilterSelectedListener listener) {

        final FilterList filters = getFilterList();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter");
        builder.setItems(filters.displayNames.toArray(new String[filters.displayNames.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        Toast.makeText(context, filters.displayNames.get(item),
                                Toast.LENGTH_SHORT).show();
                        listener.OnFilterSelected(createFilter(filters.filters.get(item)));
                    }
                });
        builder.create().show();
    }

    public static void applyFilterWithSliderValue(GPUImageFilter filter, float value) {
        final FilterList filters = getFilterList();
        String filterClassName = filter.getFilterClassName();
        int filterType = filters.classNames.indexOf(filterClassName);
        switch (filterType) {
            case FILTER_BRIGHTNESS:
            {
                // let the value between -1 and 1
                value = (value - 0.5f) * 2.0f;
                filter.setProperty("brightness", value);
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
                value = value * 24.0f;
                //filter->setProperty("radius", (int)value);
                filter.setProperty("sigma", value);
            }; break;
            case FILTER_IOS_BLUR:
            {
                value = value * 10.0f + 1.0f;
                filter.setProperty("downSampling", value);
            }; break;
            case FILTER_BILATERAL:
            {
                // let the value between 0 - 10
                value = value * 10.0f;
                filter.setProperty("distanceNormalizationFactor", value);
            }; break;
            case FILTER_CANNY_EDGE_DETECTION:
            {
                //todo
            }; break;
            case FILTER_WEAK_PIXEL_INCLUSION:
            {
            }; break;
            case FILTER_NON_MAXIMUM_SUPPRESSION:
            {
                value = value * 5.0f;
                filter.setProperty("texelSizeMultiplier", value);
            }; break;
            case FILTER_BEAUTIFY:
            {
            }; break;
            case FILTER_SOBEL_EDGE_DETECTION:
            {
                filter.setProperty("edgeStrength", value);
            }; break;
            case FILTER_SKETCH:
            {
                filter.setProperty("edgeStrength", value);
            }; break;
            case FILTER_TOON:
            {
                value = value * 20.0f;
                filter.setProperty("quantizationLevels", value);
            }; break;
            case FILTER_SMOOTH_TOON:
            {
                // let the value between 1 - 6
                value = value * 5.0f + 1.0f;
                filter.setProperty("blurRadius", (int)value);
            }; break;
            case FILTER_POSTERIZE:
            {
                // let the value between 1 and 256
                value = value * 19.0f + 1.0f;
                filter.setProperty("colorLevels", (int)value);
            }; break;
            case FILTER_PIXELLATION:
            {
                filter.setProperty("pixelSize", value);
            }; break;
            case FILTER_SATURATION:
            {
                // let the value between 0 - 2
                value = value * 2.0f;
                filter.setProperty("saturation", value);
            }; break;
            case FILTER_CONTRAST:
            {
                value = value * 4.0f;
                filter.setProperty("contrast", value);
            }; break;
            case FILTER_EXPOSURE:
            {
                // let the value between -10 and 10
                value = (value - 0.5f) * 20.0f;
                filter.setProperty("exposure", value);
            }; break;
            case FILTER_RGB:
            {
                value = value * 2.0f;
                filter.setProperty("greenAdjustment", value);
            }; break;
            case FILTER_HUE:
            {
                value = value * 360f;
                filter.setProperty("hueAdjustment", value);
            }; break;
            case FILTER_WHITE_BALANCE:
            {
                value = value * 5000.0f + 2500.0f;
                filter.setProperty("temperature", value);
            }; break;
            case FILTER_LUMINANCE_RANGE:
            {
                filter.setProperty("rangeReductionFactor", value);
            }; break;
            case FILTER_EMBOSS:
            {
                value = value * 4.0f;
                filter.setProperty("intensity", value);
            }; break;
            default:
                break;
        }
    }

    private static GPUImageFilter createFilter(final int type) {
        final FilterList filters = getFilterList();
        String filterClassName = filters.classNames.get(type);
        return GPUImageFilter.create(filterClassName);
    }

    private static class FilterList {
        public List<Integer> filters = new LinkedList<Integer>();
        public List<String> displayNames = new LinkedList<String>();
        public List<String> classNames = new LinkedList<String>();

        public void addFilter(final int filter, final String displayName, final String className) {
            filters.add(filter);
            displayNames.add(displayName);
            classNames.add(className);
        }
    }

    public interface OnFilterSelectedListener {
        void OnFilterSelected(GPUImageFilter newFilter);
    }
}
