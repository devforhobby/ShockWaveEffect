package com.github.therealspaceship.shockwave;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    // number of coordinates per vertex in this array
    private static final int CORDS_PER_VERTEX = 3;
    // 4 bytes per vertex
    private static final int VERTEX_STRIDE = CORDS_PER_VERTEX * 4;
    // top left(3), bottom left(3), bottom right(3), top right(3)
//    private static final float SQUARE_CORDS[] = {-1.0f, 1.0f, 0.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
    private static final float SQUARE_CORDS[] = {-1.0f, 1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
    // order to draw vertices
    private static final short DRAW_ORDER[] = {0, 1, 2, 3, 2, 0};
    // Set COLOR with red, green, blue and alpha (opacity) values
    private static final float COLOR[] = {0.0f, 0.0f, 0.0f, 1.0f};
    //Size of the texture coordinate data in elements.
    private final Context mContext;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTextureHandle;

    public Square(final Context context) {
        mContext = context;
        // initialize vertex byte buffer for shape coordinates
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(SQUARE_CORDS.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(SQUARE_CORDS);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(DRAW_ORDER);
        drawListBuffer.position(0);

        int vertexShaderHandle = MyGLSurfaceView.loadShader(GLES20.GL_VERTEX_SHADER, context, R.raw.vertex_shader);
        int fragmentShaderHandle = MyGLSurfaceView.loadShader(GLES20.GL_FRAGMENT_SHADER, context, R.raw.fragment_shader);

        // create empty OpenGL ES Program
        mProgram = MyGLSurfaceView.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[]{"a_Position", "a_Color"});
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        SquareRenderer.checkGlError("glGetAttribLocation: a_Position");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, CORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer);

        // Get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");
        SquareRenderer.checkGlError("glGetUniformLocation: a_Color");
        // Set COLOR for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, COLOR, 0);

        // Apply the projection and view transformation
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        SquareRenderer.checkGlError("glGetUniformLocation: u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SquareRenderer.checkGlError("glUniformMatrix4fv");

        // Get handle of fragment shader's texture uniform
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        SquareRenderer.checkGlError("glGetUniformLocation: u_Texture");
        // Select texture block
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind selected texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Texture.loadTexture(mContext, R.drawable.ic_launcher));
        // Link texture handle with texture uniform
        GLES20.glUniform1i(mTextureHandle, 0);


        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
