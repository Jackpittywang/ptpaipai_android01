package com.putao.camera.camera.gpuimage;

import android.content.Context;
import android.opengl.GLES20;

import com.putao.camera.R;
import com.putao.camera.camera.gpuimage.util.OpenGLUtils;


public class MagicSketchFilter extends GPUImageFilter{
	
	private int mSingleStepOffsetLocation;
	//0.0 - 1.0
	private int mStrength;
	private Context mContext;
	
	public MagicSketchFilter(Context context){
		super(NO_FILTER_VERTEX_SHADER, OpenGLUtils.readShaderFromRawResource(context, R.raw.sketch));
		mContext = context;
	}

    @Override
    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrength = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrength, 0.5f);
    }
    

    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
	}
	
	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
