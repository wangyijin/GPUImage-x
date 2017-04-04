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

#if PLATFORM == PLATFORM_IOS

#import "GPUImageView.h"
#include "Context.hpp"
#include "GLProgram.hpp"
#include "Filter.hpp"
#import <AVFoundation/AVFoundation.h>


@interface GPUImageView()
{
    GPUImage::Framebuffer* inputFramebuffer;
    GPUImage::RotationMode inputRotation;
    GLuint displayFramebuffer;
    GLuint displayRenderbuffer;
    GPUImage::GLProgram* displayProgram;
    GLuint positionAttribLocation;
    GLuint texCoordAttribLocation;
    GLuint colorMapUniformLocation;
    
    GLfloat displayVertices[8];
    GLint framebufferWidth, framebufferHeight;
    CGSize lastBoundsSize;
    
    GLfloat backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha;
    
}

@end


@implementation GPUImageView

+ (Class)layerClass
{
    return [CAEAGLLayer class];
}

- (id)initWithFrame:(CGRect)frame
{
    if (!(self = [super initWithFrame:frame]))
    {
        return nil;
    }
    
    [self commonInit];
    
    return self;
}

- (void)commonInit;
{
    inputRotation = GPUImage::NoRotation;
    self.opaque = YES;
    self.hidden = NO;
    CAEAGLLayer* eaglLayer = (CAEAGLLayer*)self.layer;
    eaglLayer.opaque = YES;
    eaglLayer.drawableProperties = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:NO], kEAGLDrawablePropertyRetainedBacking, kEAGLColorFormatRGBA8, kEAGLDrawablePropertyColorFormat, nil];
    
    GPUImage::Context::getInstance()->runSync([&]{
        displayProgram = GPUImage::GLProgram::createByShaderString(GPUImage::kDefaultVertexShader, GPUImage::kDefaultFragmentShader);

        positionAttribLocation = displayProgram->getAttribLocation("position");
        texCoordAttribLocation = displayProgram->getAttribLocation("texCoord");
        colorMapUniformLocation = displayProgram->getUniformLocation("colorMap");
        
        GPUImage::Context::getInstance()->setActiveShaderProgram(displayProgram);
        glEnableVertexAttribArray(positionAttribLocation);
        glEnableVertexAttribArray(texCoordAttribLocation);
        
        [self setBackgroundColorRed:0.0 green:0.0 blue:0.0 alpha:0.0];
        _fillMode = GPUImage::TargetView::FillMode::PreserveAspectRatio;
        [self createDisplayFramebuffer];
    });
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    if (!CGSizeEqualToSize(self.bounds.size, lastBoundsSize) &&
        !CGSizeEqualToSize(self.bounds.size, CGSizeZero)) {
        [self destroyDisplayFramebuffer];
        [self createDisplayFramebuffer];
    }
}

- (void)dealloc
{
    [self destroyDisplayFramebuffer];
}

- (void)createDisplayFramebuffer;
{
    GPUImage::Context::getInstance()->runSync([&]{
        
        glGenRenderbuffers(1, &displayRenderbuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, displayRenderbuffer);
        [GPUImage::Context::getInstance()->getEglContext() renderbufferStorage:GL_RENDERBUFFER fromDrawable:(CAEAGLLayer*)self.layer];
        
        glGenFramebuffers(1, &displayFramebuffer);
        glBindFramebuffer(GL_FRAMEBUFFER, displayFramebuffer);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                  GL_RENDERBUFFER, displayRenderbuffer);
        
        glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, &framebufferWidth);
        glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, &framebufferHeight);
        
        lastBoundsSize = self.bounds.size;
        [self updateDisplayVertices];
    });
}

- (void)destroyDisplayFramebuffer;
{
    GPUImage::Context::getInstance()->runSync([&]{
        
        if (displayFramebuffer)
        {
            glDeleteFramebuffers(1, &displayFramebuffer);
            displayFramebuffer = 0;
        }
        
        if (displayRenderbuffer)
        {
            glDeleteRenderbuffers(1, &displayRenderbuffer);
            displayRenderbuffer = 0;
        }
    });
}

- (void)setDisplayFramebuffer;
{
    if (!displayFramebuffer)
    {
        [self createDisplayFramebuffer];
    }
    
    GPUImage::Context::getInstance()->runSync([&]{
        glBindFramebuffer(GL_FRAMEBUFFER, displayFramebuffer);
        glViewport(0, 0, framebufferWidth, framebufferHeight);
    });
}

- (void)presentFramebuffer;
{
    GPUImage::Context::getInstance()->runAsync([&]{
        glBindRenderbuffer(GL_RENDERBUFFER, displayRenderbuffer);
        GPUImage::Context::getInstance()->presentBufferForDisplay();
    });
}

- (void)setBackgroundColorRed:(GLfloat)redComponent green:(GLfloat)greenComponent blue:(GLfloat)blueComponent alpha:(GLfloat)alphaComponent;
{
    backgroundColorRed = redComponent;
    backgroundColorGreen = greenComponent;
    backgroundColorBlue = blueComponent;
    backgroundColorAlpha = alphaComponent;
}

- (void)update:(float)frameTime {
    
    GPUImage::Context::getInstance()->runSync([&]{
        GPUImage::Context::getInstance()->setActiveShaderProgram(displayProgram);
        [self setDisplayFramebuffer];
        glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, inputFramebuffer->getTexture());
        glUniform1i(colorMapUniformLocation, 0);
        
        glVertexAttribPointer(positionAttribLocation, 2, GL_FLOAT, 0, 0, displayVertices);
        glVertexAttribPointer(texCoordAttribLocation, 2, GL_FLOAT, 0, 0, [self textureCoordinatesForRotation:inputRotation] );
        
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        
        [self presentFramebuffer];
    });
}

- (void)setInputFramebuffer:(GPUImage::Framebuffer*)newInputFramebuffer withRotation:(GPUImage::RotationMode)rotation atIndex:(NSInteger)texIdx {
    GPUImage::Framebuffer* lastFramebuffer = inputFramebuffer;
    GPUImage::RotationMode lastInputRotation = inputRotation;
    
    inputRotation = rotation;
    inputFramebuffer = newInputFramebuffer;
    
    if (lastFramebuffer != newInputFramebuffer && newInputFramebuffer &&
        ( !lastFramebuffer ||
          !(lastFramebuffer->getWidth() == newInputFramebuffer->getWidth() &&
            lastFramebuffer->getHeight() == newInputFramebuffer->getHeight() &&
            lastInputRotation == rotation)
        ))
    {
        [self updateDisplayVertices];
    }
}

- (void)setFillMode:(GPUImage::TargetView::FillMode)newValue;
{
    if (_fillMode != newValue) {
        _fillMode = newValue;
        [self updateDisplayVertices];
    }
}

- (void)updateDisplayVertices;
{
    if (inputFramebuffer == 0) return;
    
    CGFloat scaledWidth = 1.0;
    CGFloat scaledHeight = 1.0;

    int rotatedFramebufferWidth = inputFramebuffer->getWidth();
    int rotatedFramebufferHeight = inputFramebuffer->getHeight();
    if (rotationSwapsSize(inputRotation))
    {
        rotatedFramebufferWidth = inputFramebuffer->getHeight();
        rotatedFramebufferHeight = inputFramebuffer->getWidth();
    }
    
    CGRect insetRect = AVMakeRectWithAspectRatioInsideRect(CGSizeMake(rotatedFramebufferWidth, rotatedFramebufferHeight), self.bounds);
    
    if (_fillMode == GPUImage::TargetView::FillMode::PreserveAspectRatio) {
        scaledWidth = insetRect.size.width / self.bounds.size.width;
        scaledHeight = insetRect.size.height / self.bounds.size.height;
    } else if (_fillMode == GPUImage::TargetView::FillMode::PreserveAspectRatioAndFill) {
        scaledWidth = self.bounds.size.height / insetRect.size.height;
        scaledHeight = self.bounds.size.width / insetRect.size.width;
    }
    
    displayVertices[0] = -scaledWidth;
    displayVertices[1] = -scaledHeight;
    displayVertices[2] = scaledWidth;
    displayVertices[3] = -scaledHeight;
    displayVertices[4] = -scaledWidth;
    displayVertices[5] = scaledHeight;
    displayVertices[6] = scaledWidth;
    displayVertices[7] = scaledHeight;
}


- (const GLfloat *)textureCoordinatesForRotation:(GPUImage::RotationMode)rotationMode;
{
    static const GLfloat noRotationTextureCoordinates[] = {
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
    };
    
    static const GLfloat rotateRightTextureCoordinates[] = {
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
    };
    
    static const GLfloat rotateLeftTextureCoordinates[] = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
    };
    
    static const GLfloat verticalFlipTextureCoordinates[] = {
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
    };
    
    static const GLfloat horizontalFlipTextureCoordinates[] = {
        1.0f, 1.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f,
    };
    
    static const GLfloat rotateRightVerticalFlipTextureCoordinates[] = {
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
    };
    
    static const GLfloat rotateRightHorizontalFlipTextureCoordinates[] = {
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    };
    
    static const GLfloat rotate180TextureCoordinates[] = {
        1.0f, 0.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f,
    };
    
    switch(inputRotation)
    {
        case GPUImage::NoRotation: return noRotationTextureCoordinates;
        case GPUImage::RotateLeft: return rotateLeftTextureCoordinates;
        case GPUImage::RotateRight: return rotateRightTextureCoordinates;
        case GPUImage::FlipVertical: return verticalFlipTextureCoordinates;
        case GPUImage::FlipHorizontal: return horizontalFlipTextureCoordinates;
        case GPUImage::RotateRightFlipVertical: return rotateRightVerticalFlipTextureCoordinates;
        case GPUImage::RotateRightFlipHorizontal: return rotateRightHorizontalFlipTextureCoordinates;
        case GPUImage::Rotate180: return rotate180TextureCoordinates;
    }
}

@end

#endif
