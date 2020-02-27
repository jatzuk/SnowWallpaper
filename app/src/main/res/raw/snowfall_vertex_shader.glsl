uniform mat4 u_Matrix;
uniform float u_PointSize;
attribute vec4 a_Position;

void main() {
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = u_PointSize;
}
