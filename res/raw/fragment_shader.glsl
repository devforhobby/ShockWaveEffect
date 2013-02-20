precision mediump float;        // Set the default precision to medium. We don't need as high of a
                                // precision in the fragment shader.
uniform sampler2D u_Texture;

varying vec4 v_Color;
varying vec2 v_TextureCoord;

void main() {
    gl_FragColor = texture2D(u_Texture, v_TextureCoord);
}