package dev.jatzuk.snowwallpaper.opengl.util

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20.*
import dev.jatzuk.snowwallpaper.utilities.Logger
import java.io.IOException
import java.io.InputStreamReader

object ShaderHelper {

    private const val TAG = "ShaderHelper"

    fun readShaderCodeFromResource(context: Context, resourceId: Int) = buildString {
        try {
            InputStreamReader(context.resources.openRawResource(resourceId)).forEachLine { append("$it\n") }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId", e)
        } catch (e: Resources.NotFoundException) {
            throw RuntimeException("Resource $resourceId not found", e)
        }
    }

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        val program = linkProgram(vertexShader, fragmentShader)
        if (Logger.isLogging) validateProgram(program)
        return program
    }

    private fun compileVertexShader(shaderCode: String) =
        compileShader(GL_VERTEX_SHADER, shaderCode)

    private fun compileFragmentShader(shaderCode: String) =
        compileShader(GL_FRAGMENT_SHADER, shaderCode)

    private fun compileShader(shaderType: Int, shaderCode: String): Int {
        val shaderObjectId = glCreateShader(shaderType)

        if (shaderObjectId == 0) {
            Logger.e("Could not create a new shader", TAG)
            return 0
        }

        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)
        val compileStatus = intArrayOf(0)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
        Logger.d(
            "Results of compiling shader:\n$shaderCode${glGetShaderInfoLog(shaderObjectId)}",
            TAG, sendToFBA = false
        )

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)
            Logger.e("Compilation of shader failed", TAG)
            return 0
        }

        return shaderObjectId
    }

    private fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()

        if (programObjectId == 0) {
            Logger.e("Could not link program", TAG)
            return 0
        }

        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        glLinkProgram(programObjectId)
        val linkStatus = intArrayOf(0)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
        Logger.d(
            "Results of linking program:${linkStatus[0]}${glGetProgramInfoLog(programObjectId)}",
            TAG
        )

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)
            Logger.e("Linking of program failed", TAG)
            return 0
        }

        return programObjectId
    }

    private fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val validateStatus = intArrayOf(0)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        Logger.d(
            "Result of validating program:${validateStatus[0]}${
                glGetProgramInfoLog(
                    programObjectId
                )
            }", TAG
        )

        return validateStatus[0] != 0
    }
}
