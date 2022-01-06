#version 460

in vec2 v_tex_coords; // screen quad coords

layout(location = 0) uniform sampler2D g_position;
layout(location = 1) uniform sampler2D g_normal;
layout(location = 2) uniform sampler2D g_albedo;
layout(location = 3) uniform sampler2D g_block_light;
layout(location = 4) uniform sampler2D g_block_skylight;
layout(location = 5) uniform sampler2D g_ssao;

layout(location = 0) out vec4 f_color;

float k_skylight_intensity = 0.2f;

void main()
{
    vec3 frag_pos    = texture(g_position, v_tex_coords).rgb;
    vec3 frag_norm   = texture(g_normal, v_tex_coords).rgb;
    vec4 frag_albedo = texture(g_albedo, v_tex_coords).rgba;
    float frag_block_light    = texture(g_block_light, v_tex_coords).r;
    float frag_block_skylight = texture(g_block_skylight, v_tex_coords).r;
    float frag_ssao  = texture(g_ssao, v_tex_coords).r;

    float amb_light = (frag_block_skylight * k_skylight_intensity) + (frag_block_light + 0.1) * (1.0 - frag_ssao);
    f_color = amb_light * frag_albedo;
    f_color = min(f_color, 1.0);
}