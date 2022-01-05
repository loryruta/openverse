#version 450

in vec3  a_position;
in vec3  a_normal;
in vec3  a_tex_coords;
in float a_block_light;
in float a_block_skylight;

layout(location = 0) uniform mat4 u_transform;
layout(location = 1) uniform mat4 u_camera;

out vec3  v_position;
out vec3  v_normal;
out vec3  v_tex_coords;
out float v_block_light;
out float v_block_skylight;

void main()
{
    gl_Position = u_camera * u_transform * vec4(a_position, 1);

    v_position       = vec3(u_transform * vec4(a_position, 1));
    v_normal         = normalize(a_normal);
    v_tex_coords     = a_tex_coords;
    v_block_light    = a_block_light;
    v_block_skylight = a_block_skylight;
}
