precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_Texture;

void main() {
    vec4 textureColor = texture2D(u_TextureUnit, v_Texture);
    if (textureColor.a < 0.1) {
        discard;
    }
    gl_FragColor = textureColor;
}
