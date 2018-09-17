//
//  Vector3.cpp
//  GPUImage-x iOS
//
//  Created by James Perlman on 9/17/18.
//  Copyright © 2018 Jin. All rights reserved.
//

#include "Vector3.hpp"

USING_NS_GI

Vector3::Vector3()
: x(0.0f), y(0.0f), z(0.0f)
{
}

Vector3::Vector3(float xx, float yy, float zz)
: x(xx), y(yy), z(zz)
{
}

Vector3::Vector3(const float* array)
{
    set(array);
}

Vector3::Vector3(const Vector3& p1, const Vector3& p2)
{
    set(p1, p2);
}

Vector3::Vector3(const Vector3& copy)
{
    set(copy);
}

Vector3::~Vector3()
{
}

bool Vector3::isZero() const
{
    return x == 0.0f && y == 0.0f && z == 0.0f;
}

bool Vector3::isOne() const
{
    return x == 1.0f && y == 1.0f && z == 1.0f;
}

void Vector3::add(const Vector3& v)
{
    x += v.x;
    y += v.y;
    z += v.z;
}

float Vector3::distanceSquared(const Vector3& v) const
{
    float dx = v.x - x;
    float dy = v.y - y;
    float dz = v.z - z;
    return (dx * dx + dy * dy + dz * dz);
}

float Vector3::dot(const Vector3& v) const
{
    return (x * v.x + y * v.y + z * v.z);
}

float Vector3::lengthSquared() const
{
    return (x * x + y * y + z * z);
}

void Vector3::negate()
{
    x = -x;
    y = -y;
    z = -z;
}

void Vector3::scale(float scalar)
{
    x *= scalar;
    y *= scalar;
    z *= scalar;
}

void Vector3::scale(const Vector3& scale)
{
    x *= scale.x;
    y *= scale.y;
    z *= scale.z;
}

void Vector3::set(float xx, float yy, float zz)
{
    this->x = xx;
    this->y = yy;
    this->z = zz;
}

void Vector3::set(const Vector3& v)
{
    this->x = v.x;
    this->y = v.y;
    this->z = v.z;
}

void Vector3::set(const Vector3& p1, const Vector3& p2)
{
    x = p2.x - p1.x;
    y = p2.y - p1.y;
}

void Vector3::setZero()
{
    x = y = z = 0.0f;
}

void Vector3::subtract(const Vector3& v)
{
    x -= v.x;
    y -= v.y;
    z -= v.z;
}

void Vector3::smooth(const Vector3& target, float elapsedTime, float responseTime)
{
    if (elapsedTime > 0)
    {
        *this += (target - *this) * (elapsedTime / (elapsedTime + responseTime));
    }
}

const Vector3 Vector3::operator+(const Vector3& v) const
{
    Vector3 result(*this);
    result.add(v);
    return result;
}

Vector3& Vector3::operator+=(const Vector3& v)
{
    add(v);
    return *this;
}

const Vector3 Vector3::operator-(const Vector3& v) const
{
    Vector3 result(*this);
    result.subtract(v);
    return result;
}

Vector3& Vector3::operator-=(const Vector3& v)
{
    subtract(v);
    return *this;
}

const Vector3 Vector3::operator-() const
{
    Vector3 result(*this);
    result.negate();
    return result;
}

const Vector3 Vector3::operator*(float s) const
{
    Vector3 result(*this);
    result.scale(s);
    return result;
}

Vector3& Vector3::operator*=(float s)
{
    scale(s);
    return *this;
}

const Vector3 Vector3::operator/(const float s) const
{
    return Vector3(this->x / s, this->y / s, this->z / s);
}

bool Vector3::operator<(const Vector3& v) const
{
    if (x == v.x) {
        if (y == v.y) {
            return z < v.z;
        }
        return y < v.y;
    }
    return x < v.x;
}

bool Vector3::operator>(const Vector3& v) const
{
    if (x == v.x) {
        if (y == v.y) {
            return z > v.z;
        }
        return y > v.y;
    }
    return x > v.x;
}

bool Vector3::operator==(const Vector3& v) const
{
    return x==v.x && y==v.y && z==v.z;
}

bool Vector3::operator!=(const Vector3& v) const
{
    return x!=v.x || y!=v.y || z!=v.z;
}

const Vector3 operator*(float x, const Vector3& v)
{
    Vector3 result(v);
    result.scale(x);
    return result;
}
