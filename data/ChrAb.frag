#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertTexCoord;

void main(void) {
  if(texture2D( texture, vertTexCoord.st + vec2(1,0)*texOffset.st) == vec4(1)){
	gl_FragColor = vec4(0,1.0,0,1);
  }
}