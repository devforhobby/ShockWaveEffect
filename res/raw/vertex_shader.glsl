uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Position;
varying vec4 v_Color;
varying vec2 v_TextureCoord;

void main() {
    v_Position = a_Position;
    v_Color = a_Color;
    v_TextureCoord.s = a_Position.x;
    v_TextureCoord.t = -a_Position.y;

    gl_Position = u_MVPMatrix * a_Position;
}