//
//  Vector4.hpp
//  Rectomatic
//
//  Created by James Perlman on 8/30/18.
//  Copyright © 2018 Lottery.com. All rights reserved.
//

#ifndef Vector4_hpp
#define Vector4_hpp

#include "../macros.h"

NS_GI_BEGIN

class Vector4
{
public:
    
    float x;
    float y;
    float z;
    float w;
    
    Vector4();
    Vector4(float xx, float yy, float zz, float ww);
    Vector4(const float* array);
    Vector4(const Vector4& p1, const Vector4& p2);
    Vector4(const Vector4& copy);
    ~Vector4();
    
    bool isZero() const;
    bool isOne() const;
    static float angle(const Vector4& v1, const Vector4& v2);
    void add(const Vector4& v);
    static void add(const Vector4& v1, const Vector4& v2, Vector4* dst);
    void clamp(const Vector4& min, const Vector4& max);
    static void clamp(const Vector4& v, const Vector4& min, const Vector4& max, Vector4* dst);
    float distance(const Vector4& v) const;
    float distanceSquared(const Vector4& v) const;
    float dot(const Vector4& v) const;
    static float dot(const Vector4& v1, const Vector4& v2);
    float length() const;
    float lengthSquared() const;
    void negate();
    void normalize();
    Vector4 getNormalized() const;
    void scale(float scalar);
    void scale(const Vector4& scale);
    void rotate(const Vector4& point, float angle);
    void set(float xx, float yy, float zz, float ww);
    void set(const Vector4& v);
    void set(const Vector4& p1, const Vector4& p2);
    void setZero();
    void subtract(const Vector4& v);
    static void subtract(const Vector4& v1, const Vector4& v2, Vector4* dst);
    void smooth(const Vector4& target, float elapsedTime, float responseTime);
    const Vector4 operator+(const Vector4& v) const;
    Vector4& operator+=(const Vector4& v);
    const Vector4 operator-(const Vector4& v) const;
    Vector4& operator-=(const Vector4& v);
    const Vector4 operator-() const;
    const Vector4 operator*(float s) const;
    Vector4& operator*=(float s);
    const Vector4 operator/(float s) const;
    bool operator<(const Vector4& v) const;
    bool operator>(const Vector4& v) const;
    bool operator==(const Vector4& v) const;
    bool operator!=(const Vector4& v) const;
};

NS_GI_END

#endif /* Vector4_hpp */
