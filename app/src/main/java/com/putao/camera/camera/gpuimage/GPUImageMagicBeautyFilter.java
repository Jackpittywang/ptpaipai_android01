package com.putao.camera.camera.gpuimage;

import android.content.Context;
import android.opengl.GLES20;


//import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
// import com.seu.magicfilter.filter.helper.MagicFilterParam;


public class GPUImageMagicBeautyFilter extends GPUImageFilter {
	private int mSingleStepOffsetLocation;
	private int mParamsLocation;
	
	
	public static final String BEAUTY_FRAGMENT_SHADER = "" +
			
		"precision highp float;\n"+

		"uniform sampler2D inputImageTexture;\n"+
		"uniform vec2 singleStepOffset;\n"+ 
		"uniform highp vec4 params;\n"+ 
	
		"varying highp vec2 textureCoordinate;\n"+
	
		"const highp vec3 W = vec3(0.299,0.587,0.114);\n"+
		"const mat3 saturateMatrix = mat3(\n"+
		"		1.1102,-0.0598,-0.061,\n"+
		"		-0.0774,1.0826,-0.1186,\n"+
		"		-0.0228,-0.0228,1.1772);\n"+
				
		"float hardlight(float color)\n"+
		"{\n"+
		"	if(color <= 0.5)\n"+
		"	{\n"+
		"		color = color * color * 2.0;\n"+
		"	}\n"+
		"	else\n"+
		"	{\n"+
		"		color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n"+
		"	}\n"+
		"	return color;\n"+
		"}\n"+
	
		"void main(){\n"+
		"	vec2 blurCoordinates[12];\n"+
			
		"	blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);\n"+
		"	blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);\n"+
		"	blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);\n"+
		"	blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);\n"+
			
		"	blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);\n"+
		"	blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);\n"+
		"	blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);	\n"+
		"	blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);\n"+
			
		"	blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);\n"+
		"	blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);\n"+
		"	blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);\n"+
		"	blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);\n"+	
		"	float sampleColor = texture2D(inputImageTexture, textureCoordinate).g * 22.0;\n"+
	
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[0]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[1]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[2]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[3]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[4]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[5]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[6]).g;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[7]).g;\n"+
			
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[8]).g * 2.0;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[9]).g * 2.0;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[10]).g * 2.0;\n"+
		"	sampleColor += texture2D(inputImageTexture, blurCoordinates[11]).g * 2.0;	\n"+
			
		"	sampleColor = sampleColor / 38.0;\n"+
			
		"	vec3 centralColor = texture2D(inputImageTexture, textureCoordinate).rgb;\n"+
			
		"	float highpass = centralColor.g - sampleColor + 0.5;\n"+
			
		"	for(int i = 0; i < 5;i++)\n"+
		"	{\n"+
		"		highpass = hardlight(highpass);\n"+
		"	}\n"+
		"	float lumance = dot(centralColor, W);\n"+
			
		"	float alpha = pow(lumance, params.r);\n"+
	
		"	vec3 smoothColor = centralColor + (centralColor-vec3(highpass))*alpha*0.1;\n"+
			
		"	smoothColor.r = clamp(pow(smoothColor.r, params.g),0.0,1.0);\n"+
		"	smoothColor.g = clamp(pow(smoothColor.g, params.g),0.0,1.0);\n"+
		"	smoothColor.b = clamp(pow(smoothColor.b, params.g),0.0,1.0);\n"+
			
		"	vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n"+
		"	vec3 bianliang = max(smoothColor, centralColor);\n"+
		"	vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n"+
			
		"	gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n"+
		"	gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);\n"+
		"	gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);\n"+
			
		"	vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n"+
		"	gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);\n"+
		"}";
	
    public GPUImageMagicBeautyFilter() {
    	/*super(NO_FILTER_VERTEX_SHADER,
    			( MagicFilterParam.mGPUPower == 1 ? 
    					OpenGLUtils.readShaderFromRawResource(context, R.raw.beautify_fragment) :
    					OpenGLUtils.readShaderFromRawResource(context, R.raw.beautify_fragment_low)));*/
    	super(NO_FILTER_VERTEX_SHADER, BEAUTY_FRAGMENT_SHADER);
    }
    
    @Override
    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mParamsLocation = GLES20.glGetUniformLocation(getProgram(), "params");
        setBeautyLevel(4);
    }
    
    
    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {2.0f / w, 2.0f / h});
	}
	
	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }

	public void setBeautyLevel(int level){
		switch (level) {
		case 1:
			setFloatVec4(mParamsLocation, new float[] {1.0f, 1.0f, 0.15f, 0.15f});
			break;
		case 2:
			setFloatVec4(mParamsLocation, new float[] {0.8f, 0.9f, 0.2f, 0.2f});
			break;
		case 3:
			setFloatVec4(mParamsLocation, new float[] {0.6f, 0.8f, 0.25f, 0.25f});
			break;
		case 4:
			setFloatVec4(mParamsLocation, new float[] {0.4f, 0.7f, 0.38f, 0.3f});
			break;
		case 5:
			setFloatVec4(mParamsLocation, new float[] {0.33f, 0.63f, 0.4f, 0.35f});
			break;
		case 6:
			setFloatVec4(mParamsLocation, new float[] {0.20f, 0.50f, 0.45f, 0.40f});
			break;
		default:
			break;
		}
	}
}
