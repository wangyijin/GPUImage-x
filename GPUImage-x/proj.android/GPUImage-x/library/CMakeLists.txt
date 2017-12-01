cmake_minimum_required(VERSION 3.4.1)

add_library( GPUImage-x
             SHARED
             src/main/cpp/Ref.cpp
             src/main/cpp/util.cpp
             src/main/cpp/FramebufferCache.cpp
             src/main/cpp/Framebuffer.cpp
             src/main/cpp/GLProgram.cpp
             src/main/cpp/Context.cpp
             src/main/cpp/math.cpp
             src/main/cpp/GPUImagexJNI.cpp
             src/main/cpp/source/Source.cpp
             src/main/cpp/source/SourceImage.cpp
             src/main/cpp/source/SourceCamera.cpp
             src/main/cpp/target/Target.cpp
             src/main/cpp/target/TargetView.cpp
             src/main/cpp/target/TargetRawDataOutput.cpp
             src/main/cpp/filter/Filter.cpp
             src/main/cpp/filter/FilterGroup.cpp
             src/main/cpp/filter/BrightnessFilter.cpp
             src/main/cpp/filter/ColorInvertFilter.cpp
             src/main/cpp/filter/GrayscaleFilter.cpp
             src/main/cpp/filter/GaussianBlurFilter.cpp
             src/main/cpp/filter/GaussianBlurMonoFilter.cpp
             src/main/cpp/filter/NearbySampling3x3Filter.cpp
             src/main/cpp/filter/DirectionalSobelEdgeDetectionFilter.cpp
             src/main/cpp/filter/DirectionalNonMaximumSuppressionFilter.cpp
             src/main/cpp/filter/WeakPixelInclusionFilter.cpp
             src/main/cpp/filter/CannyEdgeDetectionFilter.cpp
             src/main/cpp/filter/BilateralFilter.cpp
             src/main/cpp/filter/ColorMatrixFilter.cpp
             src/main/cpp/filter/HSBFilter.cpp
             src/main/cpp/filter/BeautifyFilter.cpp
             src/main/cpp/filter/SobelEdgeDetectionFilter.cpp
             src/main/cpp/filter/SketchFilter.cpp
             src/main/cpp/filter/ToonFilter.cpp
             src/main/cpp/filter/PixellationFilter.cpp
             src/main/cpp/filter/SaturationFilter.cpp
             src/main/cpp/filter/ContrastFilter.cpp
             src/main/cpp/filter/ExposureFilter.cpp
             src/main/cpp/filter/RGBFilter.cpp
             src/main/cpp/filter/HueFilter.cpp
             src/main/cpp/filter/WhiteBalanceFilter.cpp
             src/main/cpp/filter/SmoothToonFilter.cpp
             src/main/cpp/filter/PosterizeFilter.cpp
             src/main/cpp/filter/LuminanceRangeFilter.cpp
             src/main/cpp/filter/IOSBlurFilter.cpp
             src/main/cpp/filter/NonMaximumSuppressionFilter.cpp
             src/main/cpp/filter/SingleComponentGaussianBlurMonoFilter.cpp
             src/main/cpp/filter/SingleComponentGaussianBlurFilter.cpp
             src/main/cpp/filter/Convolution3x3Filter.cpp
             src/main/cpp/filter/EmbossFilter.cpp
             src/main/cpp/filter/HalftoneFilter.cpp
             src/main/cpp/filter/CrosshatchFilter.cpp
             src/main/cpp/filter/SphereRefractionFilter.cpp
             src/main/cpp/filter/GlassSphereFilter.cpp
             src/main/cpp/filter/CropFilter.cpp
             )

find_library( log-lib
              log )

target_link_libraries( GPUImage-x
                       ${log-lib}
                       GLESv2
                       jnigraphics )

include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
CHECK_CXX_COMPILER_FLAG("-std=c++0x" COMPILER_SUPPORTS_CXX0X)
if(COMPILER_SUPPORTS_CXX11)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
elseif(COMPILER_SUPPORTS_CXX0X)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++0x")
else()
     message(STATUS "The compiler ${CMAKE_CXX_COMPILER} has no C++11 support. Please use a different C++ compiler.")
endif()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti")