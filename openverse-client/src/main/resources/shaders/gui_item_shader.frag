#version 460

in vec3 TexCoords;

uniform sampler2DArray image;

out vec4 f_color;

void main()
{
    f_color = texture(image, vec3(TexCoords.x, 1 - TexCoords.y, TexCoords.z));
}
