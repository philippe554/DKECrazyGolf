#version 150

in vec4 Color;
in vec4 Normal;
out vec4 outColor;

void main()
{
    vec4 light=vec4(0,1,0,0);
    vec4 n = normalize(Normal);
    float brightness = sin(dot(n,light)+0.65);
    brightness = min(brightness,1);
    outColor = Color*brightness;
}
