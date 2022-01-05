#version 460

in vec3  v_position;
in vec3  v_normal;
in vec3  v_tex_coords;
in float v_block_light;
in float v_block_skylight;

layout(location = 2) uniform sampler2DArray u_block_textures;
layout(location = 3) uniform float u_world_skylight;

layout(location = 0) out vec3 g_position;
layout(location = 1) out vec3 g_normal;
layout(location = 2) out vec4 g_albedo;
layout(location = 3) out float g_block_light;
layout(location = 4) out float g_block_skylight;

void main()
{
    g_position    = v_position;
    g_normal      = v_normal;
    g_albedo      = texture(u_block_textures, v_tex_coords);
    g_block_light    = v_block_light;
    g_block_skylight = v_block_skylight;
}
