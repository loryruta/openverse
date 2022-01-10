#version 460

in vec2 v_tex_coord;

layout(location = 4) uniform sampler2D u_tex;
layout(location = 5) uniform vec4 u_color;

out vec4 f_color;

void main()
{
    f_color = u_color * texture(u_tex, v_tex_coord);
}
