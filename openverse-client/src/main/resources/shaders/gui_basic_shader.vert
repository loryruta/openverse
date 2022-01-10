#version 460

in vec2 a_position;

layout(location = 0) uniform vec4 u_bounds;
layout(location = 1) uniform float u_depth = 0f;

out vec2 v_tex_coord;

void main()
{
    // (coords + size * position) * 2 - 1
    // in 0, 0 only coords (minX, minY), in 1, 1 coords + size (maxX, maxY)...
    gl_Position = vec4((u_bounds.xy + u_bounds.zw * a_position) * vec2(2) - vec2(1), u_depth, 1.0);

    v_tex_coord = a_position;
}
