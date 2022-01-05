#version 460

out vec2 v_position;
out vec2 v_tex_coords;

vec2 k_positions[] = {
    vec2(0, 0), vec2(0, 1), vec2(1, 0),
    vec2(0, 1), vec2(1, 1), vec2(1, 0),
};

void main()
{
    vec2 vtx_pos = k_positions[gl_VertexID];

    v_position   = vtx_pos * 2 - 1;
    v_tex_coords = vtx_pos;

    gl_Position = vec4(v_position, 1, 1);

}
