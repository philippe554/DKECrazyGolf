#version 150

in vec4 Color;
in vec4 Normal;
out vec4 outColor;

float map(float x,float a1,float a2,float b1,float b2){
    return ((x - a1)/(a2 - a1)) * (b2 - b1) + b1;
}

void main()
{
    vec4 light=normalize(vec4(0.3,1,0.3,0));
    vec4 n = normalize(Normal);
    float angle = dot(n,light);
    angle = angle>0?angle:-angle;
    float brightness = map(asin(angle),0f,1f,0.5f,1f);
    outColor = Color*brightness;
}
