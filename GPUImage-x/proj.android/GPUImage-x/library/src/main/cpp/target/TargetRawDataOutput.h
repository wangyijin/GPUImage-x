//
// Created by blutter on 2017/11/26.
//

#ifndef GPUIMAGE_X_TARGETRAWDATAOUTPUT_H
#define GPUIMAGE_X_TARGETRAWDATAOUTPUT_H

#include "Target.hpp"
#include "../GLProgram.hpp"
#include <memory>

NS_GI_BEGIN

    class TargetRawDataOutput;
    class ITargetRawDataDelegate {
    public:
        virtual void newFrameAvailableBlock(TargetRawDataOutput* output) = 0;
    };

    class TargetRawDataOutput : public Target {
    public:
        TargetRawDataOutput(int width, int height, bool resultsInBGRAFormat);
        ~TargetRawDataOutput();
        virtual void update(float frameTime) override;

        void init();
        void setTargetRawDataDelegate(std::shared_ptr<ITargetRawDataDelegate> delegate);
        void setImageSize(int width, int height);
        GLubyte *getRawBytesForImage(int& width, int& height);
        GLint getBytesPerRowInOutput();
    private:
        int _width;
        int _height;
        bool _outputBGRA;
        bool _hasReadFromTheCurrentFrame;

        GLProgram* _displayProgram;
        GLint _dataPositionAttribute;
        GLint _dataTextureCoordinateAttribute;
        GLint _dataInputTextureUniform;

        GLubyte *_rawBytesForImage;

        std::shared_ptr<ITargetRawDataDelegate> _delegate;

        Framebuffer *_outputFramebuffer;

        void renderAtInternalSize();
    };

NS_GI_END

#endif //GPUIMAGE_X_TARGETRAWDATAOUTPUT_HPP
