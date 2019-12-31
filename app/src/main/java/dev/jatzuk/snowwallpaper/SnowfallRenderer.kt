package dev.jatzuk.snowwallpaper

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import dev.jatzuk.snowwallpaper.data.VertexArray
import dev.jatzuk.snowwallpaper.util.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SnowfallRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val triangleVertexes = floatArrayOf(
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        0.0f, 1.0f, 0.0f
    )
    private val triangle = VertexArray(triangleVertexes)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f,0f,0f,0f)

        val vertexShaderSource = ShaderHelper.readShaderCodeFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = ShaderHelper.readShaderCodeFromResource(context, R.raw.simple_fragment_shader)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)


    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)


    }

}
