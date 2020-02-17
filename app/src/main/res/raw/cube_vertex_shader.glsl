//uniform mat4 u_Matrix;
//
//attribute vec4 a_Position;
//attribute vec4 a_Color;
//
//varying vec4 v_Color;
//
//void main() {
//    gl_Position = u_Matrix * a_Position;
////    v_Color = a_Color;
//}

attribute vec4 a_Position;
uniform mat4 u_Matrix;

void main()
{
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 5.0;
}
