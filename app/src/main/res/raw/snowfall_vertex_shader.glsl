uniform float u_ScreenWidth;
uniform float u_ScreenHeight;
uniform mat4 u_Matrix;
uniform float u_PointSize;
attribute vec4 a_Position;

void main() {
    vec4 normalizedPosition = vec4(
    a_Position.x * 2.0 / u_ScreenWidth - 1.0,
    a_Position.y * -2.0 / u_ScreenHeight + 1.0,
    a_Position.z,
    1.0);
    gl_Position = u_Matrix * normalizedPosition;
    gl_PointSize = u_PointSize;
}
