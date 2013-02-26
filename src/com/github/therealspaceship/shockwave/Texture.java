package com.github.therealspaceship.shockwave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

/**
 * Author: Igor Lysak. Class based on this article <a href="http://andmonahov.blogspot.com/2012/10/opengl-es-20_13.html">http://andmonahov.blogspot.com</a>
 * Date: 20.02.13
 * Time: 22:59
 */
public class Texture {

    /**
     * Load texture picture from resource
     *
     * @param context   Android context
     * @param pictureId Picture resource id
     * @return identifier of loaded texture
     */
    public static int loadTexture(Context context, int pictureId, FloatBuffer mTextureBuffer) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), pictureId);

        return loadTexture(bitmap, mTextureBuffer);
    }

    public static int loadTexture(Bitmap bitmap, FloatBuffer mTextureBuffer) {
        // создаем пустой массив из одного элемента
        // в этот массив OpenGL ES запишет свободный номер текстуры,
        // который называют именем текстуры
        int[] names = new int[1];
        // получаем свободное имя текстуры, которое будет записано в names[0]
        GLES20.glGenTextures(1, names, 0);
        // теперь мы можем обращаться к текстуре по ее имени name
        // устанавливаем режим выравнивания по байту
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        // делаем текстуру с именем name текущей
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, names[0]);
        // устанавливаем фильтры текстуры
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        // устанавливаем режим повтора изображения
        // если координаты текстуры вышли за пределы от 0 до 1
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // переписываем Bitmap в память видеокарты
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // удаляем Bitmap из памяти, т.к. картинка уже переписана в видеопамять
        bitmap.recycle();
        // Важный момент !
        // Создавать мипмапы нужно только
        // после загрузки текстуры в видеопамять
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        return names[0];
    }
}
