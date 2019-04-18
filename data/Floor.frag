uniform sampler2D texture;
uniform sampler2D _tilemap;

uniform vec3 _color;
uniform float _opacity;

uniform vec2 _size;

void main() {
    //Declare final color variable
    vec4 color = vec4(1,1,1,1);

    //Divide map by dimensions
    vec2 st = gl_FragCoord.xy/vec2(800,800);
    st.y = 1.-st.y;

    //Check if on a covered tile
    if(texture2D(_tilemap,st).r != 0){
        color = vec4(0.8667, 0.8667, 0.8667, 1.0);
    }

    gl_FragColor = color;
}
