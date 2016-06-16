#version 150

uniform mat4 viewMatrix, projMatrix;

in vec4 position;
in vec4 color;
in vec4 normal;

out vec4 Color;
out vec4 Normal;

void main()
{
    Color = color;
    Normal = normal;
    gl_Position = projMatrix * viewMatrix * position ;
}
