#version 460

#define KERNEL_SIZE 64
#define NOISE_TEXTURE_DIM vec2(4.0, 4.0)

in vec2 v_tex_coords; // screen quad coords

layout(location = 0) uniform sampler2D g_position;
layout(location = 1) uniform sampler2D g_normal;

layout(location = 2) uniform mat4 u_view_mtx;
layout(location = 3) uniform mat4 u_proj_mtx;

layout(location = 4) uniform sampler2D u_noise_tex;
layout(location = 5) uniform vec2 u_screen_dim;

layout(binding = 0) uniform b_kernel_samples
{
    vec4 u_samples[KERNEL_SIZE];
};

float k_sample_radius = 0.5;
float k_bias = 0.025;

layout(location = 0) out float f_ssao;

void main()
{
    vec3 frag_pos = texture(g_position, v_tex_coords).rgb; // position to view-space
    frag_pos = vec3(u_view_mtx * vec4(frag_pos, 1));

    vec3 normal = texture(g_normal, v_tex_coords).rgb; // normal to view-space (?)
    normal = transpose(inverse(mat3(u_view_mtx))) * normal;

    vec3 random_vec = normalize(texture(u_noise_tex, v_tex_coords * (u_screen_dim / NOISE_TEXTURE_DIM)).xyz);

    vec3 tangent   = normalize(random_vec - normal * dot(normal, random_vec));
    vec3 bitangent = cross(tangent, normal);
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0;

    for (int i = 0; i < KERNEL_SIZE; i++)
    {
        vec3 sample_dir = TBN * u_samples[i].xyz;
        vec3 sample_pos = frag_pos + sample_dir * k_sample_radius; // move the frag pos by the sample vec calculated

        vec4 offset = u_proj_mtx * vec4(sample_pos, 1.0);
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;// to [0.0, 1.0] range

        float sample_depth = (u_view_mtx * vec4(texture(g_position, offset.xy).xyz, 1.0)).z;

        if (sample_depth >= sample_pos.z + k_bias)
        {
            occlusion += smoothstep(0.0, 1.0, k_sample_radius / abs(frag_pos.z - sample_depth));
        }
    }

    occlusion /= KERNEL_SIZE;

    f_ssao = occlusion;
}
