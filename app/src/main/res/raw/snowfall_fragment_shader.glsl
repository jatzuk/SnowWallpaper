precision mediump float;

//uniform sampler2D u_TextureUnit;

uniform vec4 u_Color;

void main() {
//    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);
    gl_FragColor = u_Color;
}
