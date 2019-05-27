uniform sampler2D texture;

uniform vec3 _color;
uniform float _opacity;

uniform vec2 _position;
uniform vec2 _size;

uniform int _shape;

void main() {
    vec4 color = vec4(_color,_opacity);

    float relX = gl_FragCoord.x - _position.x;
    float relY = (800-gl_FragCoord.y) - _position.y;

    if(relY > 30) color -= vec4(0.2,0.2,0.2,0);

    gl_FragColor = color;
}
