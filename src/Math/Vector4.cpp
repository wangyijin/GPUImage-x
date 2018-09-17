//
//  Vector4.cpp
//  Rectomatic
//
//  Created by James Perlman on 8/30/18.
//

#include "Vector4.hpp"

USING_NS_GI

Vector4::Vector4()
: x(0.0f), y(0.0f), z(0.0f), w(0.0f)
{
}

Vector4::Vector4(float xx, float yy, float zz, float ww)
: x(xx), y(yy), z(zz), w(ww)
{
}

Vector4::Vector4(const float* array)
{
    set(array);
}

Vector4::Vector4(const Vector4& p1, const Vector4& p2)
{
    set(p1, p2);
}

Vector4::Vector4(const Vector4& copy)
{
    set(copy);
}

Vector4::~Vector4()
{
}

bool Vector4::isZero() const
{
    return x == 0.0f && y == 0.0f && z == 0.0f && w == 0.0f;
}

bool Vector4::isOne() const
{
    return x == 1.0f && y == 1.0f && z == 0.0f && w == 0.0f;
}

void Vector4::add(const Vector4& v)
{
    x += v.x;
    y += v.y;
    z += v.z;
    w += v.w;
}

float Vector4::distanceSquared(const Vector4& v) const
{
    float dx = v.x - x;
    float dy = v.y - y;
    float dz = v.z - z;
    float dw = v.w - w;
    return (dx * dx + dy * dy + dz * dz + dw * dw);
}

float Vector4::dot(const Vector4& v) const
{
    return (x * v.x + y * v.y + z * v.z + w * v.w);
}

float Vector4::lengthSquared() const
{
    return (x * x + y * y + z * z + w * w);
}

void Vector4::negate()
{
    x = -x;
    y = -y;
    z = -z;
    w = -w;
}

void Vector4::scale(float scalar)
{
    x *= scalar;
    y *= scalar;
    z *= scalar;
    w *= scalar;
}

void Vector4::scale(const Vector4& scale)
{
    x *= scale.x;
    y *= scale.y;
    z *= scale.z;
    w *= scale.w;
}

void Vector4::set(float xx, float yy, float zz, float ww)
{
    this->x = xx;
    this->y = yy;
    this->z = zz;
    this->w = ww;
}

void Vector4::set(const Vector4& v)
{
    this->x = v.x;
    this->y = v.y;
    this->z = v.z;
    this->w = v.w;
}

void Vector4::set(const Vector4& p1, const Vector4& p2)
{
    x = p2.x - p1.x;
    y = p2.y - p1.y;
    z = p2.z - p1.z;
    w = p2.w - p1.w;
}

void Vector4::setZero()
{
    x = y = z = w = 0.0f;
}

void Vector4::subtract(const Vector4& v)
{
    x -= v.x;
    y -= v.y;
    z -= v.z;
    w -= v.w;
}

void Vector4::smooth(const Vector4& target, float elapsedTime, float responseTime)
{
    if (elapsedTime > 0)
    {
        *this += (target - *this) * (elapsedTime / (elapsedTime + responseTime));
    }
}

const Vector4 Vector4::operator+(const Vector4& v) const
{
    Vector4 result(*this);
    result.add(v);
    return result;
}

Vector4& Vector4::operator+=(const Vector4& v)
{
    add(v);
    return *this;
}

const Vector4 Vector4::operator-(const Vector4& v) const
{
    Vector4 result(*this);
    result.subtract(v);
    return result;
}

Vector4& Vector4::operator-=(const Vector4& v)
{
    subtract(v);
    return *this;
}

const Vector4 Vector4::operator-() const
{
    Vector4 result(*this);
    result.negate();
    return result;
}

const Vector4 Vector4::operator*(float s) const
{
    Vector4 result(*this);
    result.scale(s);
    return result;
}

Vector4& Vector4::operator*=(float s)
{
    scale(s);
    return *this;
}

const Vector4 Vector4::operator/(const float s) const
{
    return Vector4(this->x / s, this->y / s, this->z / s, this->w / s);
}

bool Vector4::operator<(const Vector4& v) const
{
    if (x == v.x) {
        if (y == v.y) {
            if (z == v.z) {
                return w < v.w;
            }
            return z < v.z;
        }
        return y < v.y;
    }
    return x < v.x;
}

bool Vector4::operator>(const Vector4& v) const
{
    if (x == v.x) {
        if (y == v.y) {
            if (z == v.z) {
                return w < v.w;
            }
            return z > v.z;
        }
        return y > v.y;
    }
    return x > v.x;
}

bool Vector4::operator==(const Vector4& v) const
{
    return x == v.x && y == v.y && z == v.z && w == v.w;
}

bool Vector4::operator!=(const Vector4& v) const
{
    return x != v.x || y != v.y || z != v.z || w != v.w;
}

const Vector4 operator*(float x, const Vector4& v)
{
    Vector4 result(v);
    result.scale(x);
    return result;
}
