package com.samsung.testtask.shockwave;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLRenderer";

	private Bitmap bitmap;
	private GlImage mGlImage;
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		if (bitmap != null) {
			mGlImage = new GlImage(bitmap);
		}
		Log.i(TAG, "Surface Created");
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		Log.i(TAG, "Draw frame");
		if (mGlImage != null) {
			mGlImage.draw();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);
		Log.i(TAG, "Surface changed");

	}

	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}