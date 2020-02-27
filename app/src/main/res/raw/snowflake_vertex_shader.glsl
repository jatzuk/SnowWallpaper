uniform mat4 u_Matrix;
attribute vec4 a_Position;

varying vec2 v_Texture;
attribute vec2 a_Texture;

void main() {
    gl_Position = u_Matrix * a_Position;
    v_Texture = a_Texture;
}
