//
// Created by blutter on 2017/11/30.
//

#include "CropFilter.hpp"
#include "../Context.hpp"
#include <assert.h>

USING_NS_GI

#define min(a,b) ((a) < (b) ? (a) : (b))

//REGISTER_FILTER_CLASS(CropFilter)

const std::string kCropFragmentShaderString =  SHADER_STRING
(
        varying highp vec2 vTexCoord;

        uniform sampler2D colorMap;

        void main()
        {
            gl_FragColor = texture2D(colorMap, vTexCoord);
        }
);

CropFilter *CropFilter::create() {
    return create(RectF(0.0, 0.0, 1.0, 1.0));
}

CropFilter *CropFilter::create(RectF cropRegion) {
    CropFilter* ret = new (std::nothrow)CropFilter();
    if (!ret || !ret->init(cropRegion)) {
        delete ret;
        ret = 0;
    }
    return ret;
}

CropFilter::CropFilter() :
        _cropRegion(0,0,0,0)
{

}

void CropFilter::setCropRegion(RectF cropRegion) {
    assert(
            cropRegion.left >= 0 && cropRegion.left <= 1 &&
            cropRegion.right >= 0 && cropRegion.right <= 1 &&
            cropRegion.top >= 0 && cropRegion.top <= 1 &&
            cropRegion.bottom >= 0 && cropRegion.bottom <= 1
    );
    memcpy(&_cropRegion, &cropRegion, sizeof(RectF));
    this->calculateCropTextureCoordinates();
}

bool CropFilter::init(RectF cropRegion) {
    if (Filter::initWithFragmentShaderString(kCropFragmentShaderString)) {
        setCropRegion(cropRegion);
        return true;
    }
    return false;
}

void CropFilter::calculateCropTextureCoordinates() {

    if (_inputFramebuffers.find(0) == _inputFramebuffers.end() || _inputFramebuffers[0].frameBuffer == 0) return;

    RotationMode inputRotation = _inputFramebuffers[0].rotationMode;

    float minX = _cropRegion.left;
    float minY = _cropRegion.top;
    float maxX = min(_cropRegion.right, 1);
    float maxY = min(_cropRegion.bottom, 1);

    switch (inputRotation)
    {
        case NoRotation: // Works
        {
            cropTextureCoordinates[0] = minX; // 0,0
            cropTextureCoordinates[1] = minY;

            cropTextureCoordinates[2] = maxX; // 1,0
            cropTextureCoordinates[3] = minY;

            cropTextureCoordinates[4] = minX; // 0,1
            cropTextureCoordinates[5] = maxY;

            cropTextureCoordinates[6] = maxX; // 1,1
            cropTextureCoordinates[7] = maxY;
        }; break;
        case RotateLeft: // Fixed
        {
            cropTextureCoordinates[0] = maxY; // 1,0
            cropTextureCoordinates[1] = 1.0f - maxX;

            cropTextureCoordinates[2] = maxY; // 1,1
            cropTextureCoordinates[3] = 1.0f - minX;

            cropTextureCoordinates[4] = minY; // 0,0
            cropTextureCoordinates[5] = 1.0f - maxX;

            cropTextureCoordinates[6] = minY; // 0,1
            cropTextureCoordinates[7] = 1.0f - minX;
        }; break;
        case RotateRight: // Fixed
        {
            cropTextureCoordinates[0] = minY; // 0,1
            cropTextureCoordinates[1] = 1.0f - minX;

            cropTextureCoordinates[2] = minY; // 0,0
            cropTextureCoordinates[3] = 1.0f - maxX;

            cropTextureCoordinates[4] = maxY; // 1,1
            cropTextureCoordinates[5] = 1.0f - minX;

            cropTextureCoordinates[6] = maxY; // 1,0
            cropTextureCoordinates[7] = 1.0f - maxX;
        }; break;
        case FlipVertical: // Works for me
        {
            cropTextureCoordinates[0] = minX; // 0,1
            cropTextureCoordinates[1] = maxY;

            cropTextureCoordinates[2] = maxX; // 1,1
            cropTextureCoordinates[3] = maxY;

            cropTextureCoordinates[4] = minX; // 0,0
            cropTextureCoordinates[5] = minY;

            cropTextureCoordinates[6] = maxX; // 1,0
            cropTextureCoordinates[7] = minY;
        }; break;
        case FlipHorizontal: // Works for me
        {
            cropTextureCoordinates[0] = maxX; // 1,0
            cropTextureCoordinates[1] = minY;

            cropTextureCoordinates[2] = minX; // 0,0
            cropTextureCoordinates[3] = minY;

            cropTextureCoordinates[4] = maxX; // 1,1
            cropTextureCoordinates[5] = maxY;

            cropTextureCoordinates[6] = minX; // 0,1
            cropTextureCoordinates[7] = maxY;
        }; break;
        case Rotate180: // Fixed
        {
            cropTextureCoordinates[0] = maxX; // 1,1
            cropTextureCoordinates[1] = maxY;

            cropTextureCoordinates[2] = minX; // 0,1
            cropTextureCoordinates[3] = maxY;

            cropTextureCoordinates[4] = maxX; // 1,0
            cropTextureCoordinates[5] = minY;

            cropTextureCoordinates[6] = minX; // 0,0
            cropTextureCoordinates[7] = minY;
        }; break;
        case RotateRightFlipVertical: // Fixed
        {
            cropTextureCoordinates[0] = minY; // 0,0
            cropTextureCoordinates[1] = 1.0f - maxX;

            cropTextureCoordinates[2] = minY; // 0,1
            cropTextureCoordinates[3] = 1.0f - minX;

            cropTextureCoordinates[4] = maxY; // 1,0
            cropTextureCoordinates[5] = 1.0f - maxX;

            cropTextureCoordinates[6] = maxY; // 1,1
            cropTextureCoordinates[7] = 1.0f - minX;
        }; break;
        case RotateRightFlipHorizontal: // Fixed
        {
            cropTextureCoordinates[0] = maxY; // 1,1
            cropTextureCoordinates[1] = 1.0f - minX;

            cropTextureCoordinates[2] = maxY; // 1,0
            cropTextureCoordinates[3] = 1.0f - maxX;

            cropTextureCoordinates[4] = minY; // 0,1
            cropTextureCoordinates[5] = 1.0f - minX;

            cropTextureCoordinates[6] = minY; // 0,0
            cropTextureCoordinates[7] = 1.0f - maxX;
        }; break;
    }
//    __android_log_print (ANDROID_LOG_INFO, __FUNCTION__ , " %f %f \n %f %f \n %f %f \n %f %f",
//                         cropTextureCoordinates[0],cropTextureCoordinates[1],
//                         cropTextureCoordinates[2],cropTextureCoordinates[3],
//                         cropTextureCoordinates[4],cropTextureCoordinates[5],
//                         cropTextureCoordinates[6],cropTextureCoordinates[7]);
}

bool CropFilter::proceed(bool bUpdateTargets) {

    static const GLfloat imageVertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f,
    };
    calculateCropTextureCoordinates();

    Context::getInstance()->setActiveShaderProgram(_filterProgram);
    _framebuffer->active();
    CHECK_GL(glClearColor(_backgroundColor.r, _backgroundColor.g, _backgroundColor.b, _backgroundColor.a));
    CHECK_GL(glClear(GL_COLOR_BUFFER_BIT));
    for (std::map<int, InputFrameBufferInfo>::const_iterator it = _inputFramebuffers.begin(); it != _inputFramebuffers.end(); ++it) {
        int texIdx = it->first;
        Framebuffer* fb = it->second.frameBuffer;
        CHECK_GL(glActiveTexture(GL_TEXTURE0 + texIdx));
        CHECK_GL(glBindTexture(GL_TEXTURE_2D, fb->getTexture()));
        _filterProgram->setUniformValue(
                texIdx == 0 ? "colorMap" : str_format("colorMap%d", texIdx),
                texIdx);
        // texcoord attribute
        GLuint filterTexCoordAttribute = _filterProgram->getAttribLocation(texIdx == 0 ? "texCoord" : str_format("texCoord%d", texIdx));

        CHECK_GL(glEnableVertexAttribArray(filterTexCoordAttribute));
        CHECK_GL(glVertexAttribPointer(filterTexCoordAttribute, 2, GL_FLOAT, 0, 0, cropTextureCoordinates));
    }
    CHECK_GL(glVertexAttribPointer(_filterPositionAttribute, 2, GL_FLOAT, 0, 0, imageVertices));
    CHECK_GL(glDrawArrays(GL_TRIANGLE_STRIP, 0, 4));

    _framebuffer->inactive();

    return Source::proceed(bUpdateTargets);
}





















