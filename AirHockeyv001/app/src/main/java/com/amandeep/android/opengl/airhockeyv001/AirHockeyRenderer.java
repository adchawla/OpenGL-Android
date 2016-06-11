package com.amandeep.android.opengl.airhockeyv001;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.amandeep.android.util.LoggerConfig;
import com.amandeep.android.util.ShaderHelper;
import com.amandeep.android.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by amandeep on 10/06/16.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private final FloatBuffer vertexData;
    private final Context context;
    private  int program;

    // variables to store and access the location of uniform.
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    // variables to store and access the location of attributes.
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;

        float[] tableVerticesWithTriangles = {
                // Triangle Fan
                0,     0,
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f,
                -0.5f, -0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };

        // move the data from the java side to the native side
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource
                = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragShaderSouorce
                = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShaderID = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShaderID = ShaderHelper.compileFragmentShader(fragShaderSouorce);
        program = ShaderHelper.linkProgram(vertexShaderID, fragmentShaderID);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        // get the location of u_Color uniform
        uColorLocation = glGetUniformLocation(program, U_COLOR);


        // get the location of a_Position attribute
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // associating the vertex data with the position attribute.
        vertexData.position(0);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, 0, vertexData);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        glViewport(0, 0, i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        // Draw the Table
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f );
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // Draw the line in the middle
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);

    }
}
