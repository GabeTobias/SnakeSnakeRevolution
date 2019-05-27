uniform float _time;
uniform mat4 transform;

attribute vec4 position;

void main() {
    gl_Position = position * transform;
}