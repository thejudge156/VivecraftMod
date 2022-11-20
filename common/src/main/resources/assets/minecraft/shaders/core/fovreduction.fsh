#version 150 core

uniform sampler2D Sampler0;
uniform float circle_radius;
uniform float circle_offset;
uniform float border;

uniform float water;
uniform float portal;
uniform float pumpkin;

uniform float portaltime;
uniform float redalpha;
uniform float bluealpha;
uniform float blackalpha;

in vec2 texCoordinates;

out vec4 fragColor;

const vec4 black = vec4(0.0f, 0.0f, 0.0f, 1.0f);
const vec4 orange = vec4(.25f, .125f, 0.0f, 1.0f);
const float pi = 3.14159265f;

uniform float eye;

void main(){

	vec4 bkg_color = texture(Sampler0,texCoordinates.st); 
    
	if(portal > 0.0f){ //swirly whirly
		float ts = texCoordinates.s;
		vec2 mod_texcoord = texCoordinates.st + vec2(portal*.005f*cos(portaltime + 2.0f*ts*pi), portal*.005f*sin(portaltime + 30.0f*ts*pi));
		bkg_color = texture(Sampler0, mod_texcoord);
	}

	if(water > 0.0f){ //goobly woobly
		float ts = texCoordinates.s;
		vec2 mod_texcoord = texCoordinates.st + vec2(0, water*.0010f*sin(portaltime + 10.0f*ts*pi));
		bkg_color = texture(Sampler0, mod_texcoord);
		vec4 blue = vec4(0.0f, 0.0f, bkg_color.b, 1.0f);
		bkg_color  = mix(bkg_color, blue, 0.1f);

	}
	
	if(redalpha > 0.0f){ //ouchy wouchy
		vec4 red = vec4(bkg_color.r, 0.0f, 0.0f, 1.0f);
		bkg_color  = mix(bkg_color, red, redalpha);
	}
	
	if(bluealpha > 0.0f){ //chilly willy
		vec4 blue = vec4(0.0f, bkg_color.g/2.0f, bkg_color.b, 1.0f);
		bkg_color  = mix(bkg_color, blue, bluealpha);
	}

	if(blackalpha > 0.0f){ //spooky wooky
		bkg_color  = mix(bkg_color, black, blackalpha);
	}

	if(circle_radius < 0.8f){ //arfy barfy
		vec2 circle_center = vec2(0.5f + eye*circle_offset, 0.5f);
		vec2 uv = texCoordinates.xy; 
		uv -= circle_center; 
		float dist =  sqrt(dot(uv, uv)); 
		float t = 1.0f + smoothstep(circle_radius, circle_radius+10.0f, dist) - smoothstep(circle_radius-border, circle_radius, dist);
		if(pumpkin>0.0f){
			bkg_color  = mix(orange, bkg_color,t);
		} else{
			bkg_color  = mix(black, bkg_color,t);
		}
	}

	fragColor = bkg_color;
	
}