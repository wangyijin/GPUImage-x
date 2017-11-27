//
// Created by blutter on 2017/11/26.
//

#include "TargetRawDataOutput.h"
#include "../filter/Filter.hpp"
#include "../Context.hpp"


USING_NS_GI

const std::string kColorSwizzlingFragmentShaderString = SHADER_STRING
(
        varying highp vec2 vTexCoord;
        uniform sampler2D colorMap;

        void main()
        {
            gl_FragColor = texture2D(colorMap, vTexCoord).bgra;
        }
);

TargetRawDataOutput::TargetRawDataOutput(int width, int height, bool resultsInBGRAFormat) :
        _width(width),
        _height(height),
        _outputBGRA(resultsInBGRAFormat),
        _delegate(nullptr),
        _hasReadFromTheCurrentFrame(false),
        _rawBytesForImage(NULL)
{
    init();
}

TargetRawDataOutput::~TargetRawDataOutput() {
    _delegate = nullptr;
    if (_rawBytesForImage != NULL) {
        free(_rawBytesForImage);
        _rawBytesForImage = NULL;
    }

    if (_displayProgram) {
        delete _displayProgram;
        _displayProgram = 0;
    }
}

void TargetRawDataOutput::update(float frameTime) {
    _hasReadFromTheCurrentFrame = false;
    if (_delegate != nullptr) {
        _delegate->newFrameAvailableBlock(this);
    }
}

void TargetRawDataOutput::setTargetRawDataDelegate(std::shared_ptr<ITargetRawDataDelegate> delegate) {
    this->_delegate = delegate;
}

void TargetRawDataOutput::init() {

    if (_outputBGRA) {
        _displayProgram = GLProgram::createByShaderString(kDefaultVertexShader, kColorSwizzlingFragmentShaderString);
    } else {
        _displayProgram = GLProgram::createByShaderString(kDefaultVertexShader, kDefaultFragmentShader);

    }

    _dataPositionAttribute = _displayProgram->getAttribLocation("position");
    _dataTextureCoordinateAttribute = _displayProgram->getAttribLocation("texCoord");
    _dataInputTextureUniform = _displayProgram->getUniformLocation("colorMap");
    Context::getInstance()->setActiveShaderProgram(_displayProgram);
    CHECK_GL(glEnableVertexAttribArray(_dataPositionAttribute));
    CHECK_GL(glEnableVertexAttribArray(_dataTextureCoordinateAttribute));
}

void TargetRawDataOutput::renderAtInternalSize() {

    Context::getInstance()->setActiveShaderProgram(_displayProgram);
    _outputFramebuffer = Context::getInstance()->getFramebufferCache()->fetchFramebuffer(_width, _height, false);
    _outputFramebuffer->active();

    CHECK_GL(glClearColor(0.0f, 0.0f, 0.0f, 1.0f));
    CHECK_GL(glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));

    static const GLfloat squareVertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f,
    };

    static const GLfloat textureCoordinates[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    CHECK_GL(glActiveTexture(GL_TEXTURE4));
    CHECK_GL(glBindTexture(GL_TEXTURE_2D, _inputFramebuffers[0].frameBuffer->getTexture()));
    CHECK_GL(glUniform1i(_dataInputTextureUniform, 4));
    CHECK_GL(glVertexAttribPointer(_dataPositionAttribute, 2, GL_FLOAT, 0, 0, squareVertices));
    CHECK_GL(glVertexAttribPointer(_dataTextureCoordinateAttribute, 2, GL_FLOAT, 0, 0, textureCoordinates));
    CHECK_GL(glEnableVertexAttribArray(_dataPositionAttribute));
    CHECK_GL(glEnableVertexAttribArray(_dataTextureCoordinateAttribute));
    CHECK_GL(glDrawArrays(GL_TRIANGLE_STRIP, 0, 4));
}

GLubyte *TargetRawDataOutput::getRawBytesForImage(int& width, int& height) {
    if (_rawBytesForImage == NULL) {
        _rawBytesForImage = (GLubyte*)calloc(_width * _height* 4 , sizeof(GLubyte));
        _hasReadFromTheCurrentFrame = false;
    }
    width = _width;
    height = _height;
    if (_hasReadFromTheCurrentFrame) {
        return _rawBytesForImage;
    } else {
        renderAtInternalSize();

        glReadPixels(0, 0, _width, _height, GL_RGBA, GL_UNSIGNED_BYTE, _rawBytesForImage);
        _hasReadFromTheCurrentFrame = true;
        return _rawBytesForImage;
    }
}

void TargetRawDataOutput::setImageSize(int width, int height) {
    _width = width;
    _height = height;
    if (_rawBytesForImage != NULL) {
        free(_rawBytesForImage);
        _rawBytesForImage = NULL;
    }
}

GLint TargetRawDataOutput::getBytesPerRowInOutput() {
    return _width * 4;
}





















