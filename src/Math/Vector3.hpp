//
//  Vector3.hpp
//  GPUImage-x iOS
//
//  Created by James Perlman on 9/17/18.
//  Copyright © 2018 Jin. All rights reserved.
//

#ifndef Vector3_hpp
#define Vector3_hpp

#include "../macros.h"

NS_GI_BEGIN

class Vector3 {
public:
    float x;
    float y;
    float z;
    
    Vector3();
    Vector3(float xx, float yy, float zz);
    Vector3(const float* array);
    Vector3(const Vector3& p1, const Vector3& p2);
    Vector3(const Vector3& copy);
    ~Vector3();
    
    bool isZero() const;
    bool isOne() const;
    static float angle(const Vector3& v1, const Vector3& v2);
    void add(const Vector3& v);
    static void add(const Vector3& v1, const Vector3& v2, Vector3* dst);
    void clamp(const Vector3& min, const Vector3& max);
    static void clamp(const Vector3& v, const Vector3& min, const Vector3& max, Vector3* dst);
    float distance(const Vector3& v) const;
    float distanceSquared(const Vector3& v) const;
    float dot(const Vector3& v) const;
    static float dot(const Vector3& v1, const Vector3& v2);
    float length() const;
    float lengthSquared() const;
    void negate();
    void normalize();
    Vector3 getNormalized() const;
    void scale(float scalar);
    void scale(const Vector3& scale);
    void rotate(const Vector3& point, float angle);
    void set(float xx, float yy, float zz);
    void set(const Vector3& v);
    void set(const Vector3& p1, const Vector3& p2);
    void setZero();
    void subtract(const Vector3& v);
    static void subtract(const Vector3& v1, const Vector3& v2, Vector3* dst);
    void smooth(const Vector3& target, float elapsedTime, float responseTime);
    const Vector3 operator+(const Vector3& v) const;
    Vector3& operator+=(const Vector3& v);
    const Vector3 operator-(const Vector3& v) const;
    Vector3& operator-=(const Vector3& v);
    const Vector3 operator-() const;
    const Vector3 operator*(float s) const;
    Vector3& operator*=(float s);
    const Vector3 operator/(float s) const;
    bool operator<(const Vector3& v) const;
    bool operator>(const Vector3& v) const;
    bool operator==(const Vector3& v) const;
    bool operator!=(const Vector3& v) const;
};

NS_GI_END

#endif /* Vector3_hpp */
