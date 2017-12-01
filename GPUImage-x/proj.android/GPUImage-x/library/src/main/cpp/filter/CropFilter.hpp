//
// Created by blutter on 2017/11/30.
//

#ifndef GPUIMAGE_X_CROPFILTER_HPP
#define GPUIMAGE_X_CROPFILTER_HPP

#include "../macros.h"
#include "Filter.hpp"

NS_GI_BEGIN

    typedef struct RectF {
        RectF(float left, float top, float right, float bottom):
                left(left),
                top(top),
                right(right),
                bottom(bottom)
        {}

        float left;
        float top;
        float right;
        float bottom;
    } RectF;

class CropFilter  : public Filter{
public:
    static CropFilter *create();
    static CropFilter *create(RectF cropRegion);

    void setCropRegion(RectF cropRegion);
    bool init(RectF cropRegion);

    virtual bool proceed(bool bUpdateTargets = true) override;
protected:
    CropFilter();
    void calculateCropTextureCoordinates();

private:
    RectF _cropRegion;
    GLfloat cropTextureCoordinates[8];
};

NS_GI_END
#endif //GPUIMAGE_X_CROPFILTER_HPP
