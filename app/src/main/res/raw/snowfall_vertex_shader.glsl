uniform mat4 u_Matrix;

attribute vec4 a_Position;

uniform float u_PointSize;

void main() {
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = u_PointSize;
}
