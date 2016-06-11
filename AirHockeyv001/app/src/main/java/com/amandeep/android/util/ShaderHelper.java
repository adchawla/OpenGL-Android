package com.amandeep.android.util;

import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by amandeep on 11/06/16.
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int linkProgram(int vertexShaderID, int fragmentShaderID) {

        // Create a program object and get its ID as reference.
        int programID = glCreateProgram();
        if (programID == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create a new program.");
            }
            return 0;
        }

        // attach the shaders to the program.
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);

        // link the shader
        glLinkProgram(programID);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programID, GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
            // Print the program info log to the Android log output.
            Log.v(TAG, "Results of linking program:\n"
                    + glGetProgramInfoLog(programID));
        }

        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programID);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        return programID;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    private static int compileShader(int type, String shaderCode) {
        // Create a shader Object and get its ID as reference.
        final int shaderObjectID = glCreateShader(type);
        if (shaderObjectID == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create a new shader.");
            }
            return 0;
        }

        // Associate the source code of the shader with the shader Object ID.
        glShaderSource(shaderObjectID, shaderCode);

        // Try to compile the code and check the compilation status.
        glCompileShader(shaderObjectID);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectID, GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON) {
            // Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectID));
        }

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectID);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }

            return 0;
        }

        // Return the shader object ID.
        return shaderObjectID;
    }
}
