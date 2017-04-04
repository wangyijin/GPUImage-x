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

#ifndef math_hpp
#define math_hpp

#include "macros.h"

NS_GI_BEGIN

class Vector3 {
    
};

class Matrix4 {
public:
    float m[16];
    Matrix4();
    Matrix4(const float* mat);
    Matrix4(float m11, float m12, float m13, float m14, float m21, float m22, float m23,float m24, float m31, float m32, float m33, float m34, float m41, float m42, float m43, float m44);
    Matrix4(const Matrix4& copy);
    ~Matrix4();
    
    void set(float m11, float m12, float m13, float m14, float m21, float m22, float m23, float m24, float m31, float m32, float m33, float m34, float m41, float m42, float m43, float m44);
    void set(const float* mat);
    void set(const Matrix4& mat);
    void setIdentity();
    
    void negate();
    Matrix4 getNegated() const;
    
    void transpose();
    Matrix4 getTransposed() const;
    
    void add(float scalar);
    void add(float scalar, Matrix4* dst) const;
    void add(const Matrix4& mat);
    static void add(const Matrix4& m1, const Matrix4& m2, Matrix4* dst);
    
    void subtract(const Matrix4& mat);
    static void subtract(const Matrix4& m1, const Matrix4& m2, Matrix4* dst);
    
    void multiply(float scalar);
    void multiply(float scalar, Matrix4* dst) const;
    static void multiply(const Matrix4& mat, float scalar, Matrix4* dst);
    void multiply(const Matrix4& mat);
    static void multiply(const Matrix4& m1, const Matrix4& m2, Matrix4* dst);
    
    const Matrix4 operator+(const Matrix4& mat) const;
    Matrix4& operator+=(const Matrix4& mat);
    const Matrix4 operator-(const Matrix4& mat) const;
    Matrix4& operator-=(const Matrix4& mat);
    const Matrix4 operator-() const;
    const Matrix4 operator*(const Matrix4& mat) const;
    Matrix4& operator*=(const Matrix4& mat);
    
    const Matrix4 operator+(float scalar) const;
    Matrix4& operator+=(float scalar);
    const Matrix4 operator-(float scalar) const;
    Matrix4& operator-=(float scalar);
    const Matrix4 operator*(float scalar) const;
    Matrix4& operator*=(float scalar);
    
    static const Matrix4 IDENTITY;

    
};

NS_GI_END

#endif /* math_hpp */
