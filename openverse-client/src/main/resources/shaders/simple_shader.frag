#version 460

in vec3 TexCoords;
in float BlockLight;
in float BlockSkylight;

uniform sampler2DArray image;
uniform float worldSkylight;

out vec4 f_color;

void main()
{
    f_color = min((BlockSkylight * worldSkylight) + (BlockLight + 0.1), 1) * texture(image, TexCoords);
}