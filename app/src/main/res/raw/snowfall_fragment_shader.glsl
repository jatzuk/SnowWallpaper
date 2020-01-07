precision mediump float;

uniform sampler2D u_TextureUnit;
uniform vec4 u_Color;

void main() {
    gl_FragColor = texture2D(u_TextureUnit, gl_PointCoord) * u_Color;
}
