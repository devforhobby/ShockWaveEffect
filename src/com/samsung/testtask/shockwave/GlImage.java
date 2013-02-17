package com.samsung.testtask.shockwave;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GlImage {

	private Bitmap bitmap;
	private int mProgram;

	final String vertexShader =
		    "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
		 
		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
		  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
		 
		  + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
		 
		  + "void main()                    \n"     // The entry point for our vertex shader.
		  + "{                              \n"
		  + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
		                                            // It will be interpolated across the triangle.
		  + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
		  + "}                              \n";    // normalized screen coordinates.
	
	final String fragmentShader =
		    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
		                                            // precision in the fragment shader.
		  + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
		                                            // triangle per fragment.
		  + "void main()                    \n"     // The entry point for our fragment shader.
		  + "{                              \n"
		  + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
		  + "}                              \n";
	
	public GlImage(Bitmap bitmap) {
		this.bitmap = bitmap;
		// Load in the vertex shader.
		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		 
		loadingShaders(vertexShaderHandle, vertexShader);
		loadingShaders(fragmentShaderHandle, fragmentShader);
		
		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		if (mProgram != 0)
		{
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(mProgram, vertexShaderHandle);
		 
		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(mProgram, fragmentShaderHandle);
		 
		    // Bind attributes
		    GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
		    GLES20.glBindAttribLocation(mProgram, 1, "a_Color");
		 
		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(mProgram);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(mProgram);
		        mProgram = 0;
		    }
		}
		 
		if (mProgram == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}
	}
	
	private void loadingShaders(int shaderHandle, String shaderSource) {
		if (shaderHandle != 0)
		{
		    // Pass in the shader source.
		    GLES20.glShaderSource(shaderHandle, shaderSource);
		 
		    // Compile the shader.
		    GLES20.glCompileShader(shaderHandle);
		 
		    // Get the compilation status.
		    final int[] compileStatus = new int[1];
		    GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		 
		    // If the compilation failed, delete the shader.
		    if (compileStatus[0] == 0)
		    {
		        GLES20.glDeleteShader(shaderHandle);
		        shaderHandle = 0;
		    }
		}
		if (shaderHandle == 0)
		{
		    throw new RuntimeException("Error creating vertex shader.");
		}
	}

	public void draw() {
		GLES20.glUseProgram(mProgram);
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
		imageBuffer.order(ByteOrder.nativeOrder());
		byte buffer[] = new byte[4];
		for (int i = 0; i < bitmap.getHeight(); i++) {
			for (int j = 0; j < bitmap.getWidth(); j++) {
				int color = bitmap.getPixel(j, i);
				buffer[0] = (byte) Color.red(color);
				buffer[1] = (byte) Color.green(color);
				buffer[2] = (byte) Color.blue(color);
				buffer[3] = (byte) Color.alpha(color);
				imageBuffer.put(buffer);
			}
		}
		imageBuffer.position(0);
		GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, imageBuffer);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	}

}
