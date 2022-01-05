#version 460

in vec2 v_tex_coords; // screen quad coords

layout(location = 0) uniform sampler2D g_position;
layout(location = 1) uniform sampler2D g_normal;
layout(location = 3) uniform mat4 u_camera;

uniform vec3 u_samples[64];

uint  k_kernel_size = 64;
float k_sample_radius = 0.5;
float k_bias = 0.025;

layout(location = 5) out float f_ssao;

void main()
{
    vec3 frag_pos    = texture(g_position, v_tex_coords).rgb;
    vec3 frag_norm   = texture(g_normal,   v_tex_coords).rgb;

    float occlusion = 0;

    for (int i = 0; i < k_kernel_size; i++)
    {
        vec3 sample_pos = frag_pos + u_samples[i] * k_sample_radius; // move the frag pos by the sample vec calculated

        vec4 offset = vec4(sample_pos, 1.0);
        offset = u_camera * offset;
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5; // to [0.0, 1.0] range

        float sample_depth = texture(g_position, offset.xy).z;

        if (sample_depth >= sample_pos.z + k_bias)
        {
            occlusion += 1.0;
        }
    }

    occlusion = occlusion / k_kernel_size;

    f_ssao = occlusion;
}
