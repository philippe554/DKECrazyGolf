#version 150

in vec4 Color;
in vec4 Normal;
out vec4 outColor;

void main()
{
    float angle = acos(Normal.z);
    outColor = Color//*angle;
}
