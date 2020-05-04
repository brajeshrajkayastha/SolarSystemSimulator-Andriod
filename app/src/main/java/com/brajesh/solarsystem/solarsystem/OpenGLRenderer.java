package com.brajesh.solarsystem.solarsystem;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    public static float time;

    private extendsOpenGLRenderer extendsopenglrenderer;


    public static float[] ViewMatrix = new float[16];

    public static float[] ProjectionMatrix = new float[16];

    public static float[] MVPMatrix = new float[16];



    private float[] TempMatrix = new float[16];

    private float[] ModelLightMatrix = new float[16];




    private final float[] LightPositionInModelSpace = new float[] {100.0f, 100.0f, -1000.0f, 1.0f};


    private final float[] LightPositionInWorldSpace = new float[4];


    public static final float[] LightPositionInEyeSpace = new float[4];


    public static int ProgramHandle;


    public static int PointProgramHandle;

    public OpenGLRenderer(Context context){
        this.context = context;
        extendsopenglrenderer = new extendsOpenGLRenderer(context);
    }

    public final String pointfragmentShader =
            "precision mediump float;\n" +
                    "       \t\t\t\t\t          \n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "//set point color to white with alpha 1\n" +
                    "\t gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);             \n" +
                    "}   ";
    public final String pointvertexShader =
            "uniform mat4 u_MVPMatrix;      \t\t\n" +
                    "//position the point in world\n" +
                    "attribute vec4 a_Position;     \t\t\n" +
                    "uniform float point_size;\n"+
                    "\n" +
                    "void main()                    \n" +
                    "{                              \n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "//set the size of point\n" +
                    "    gl_PointSize = 1.0;         \n" +
                    "}   ";

    private final String vertexShader =

            "//u_MVPMatrix is a uniform matrix that equals to Model (matrix of 4*4)*View*Projection \n" +
                    " uniform mat4 u_MVPMatrix;\n" +
                    "//u_MVMatrix is a uniform matrix that equals to Model (matrix of 4*4)*View \n" +
                    "uniform mat4 u_MVMatrix;\n" +

                    "mat4 aMat4 = mat4(1.0, 0.0, 0.0, 0.0,  // 1. column\n" +
                    "                  0.0, 1.0, 0.0, 0.0,  // 2. column\n" +
                    "                  0.0, 0.0, 1.0, 0.0,  // 3. column\n" +
                    "                  0.0, 0.0, 0.0, 1.0); // 4. column\n" +


                    "attribute vec4 a_Position;//Pass in the position coordinate(x,y,z,w)\n" +
                    "attribute vec3 a_Normal;//passing normal data this is used in fragment shader\n" +
                    "attribute vec2 a_TexCoordinate;//pass in texture coordinates\n" +

                    "varying vec3 v_Position;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n" +
                    "//for shadow\n" +
                    "varying vec4 v_ShadowCoord;\n" +

                    "void main()\n" +
                    "{\n" +
                    "v_Position = vec3(u_MVMatrix * a_Position);\n" +
                    "v_TexCoordinate = a_TexCoordinate;\n" +
                    "v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +

                    "v_ShadowCoord = aMat4 * a_Position;\n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "} ";

    private final String fragmentShader =
            "precision mediump float;\n" +
                    "uniform vec3 u_LightPos;\n" +
                    "uniform sampler2D u_Texture;\n"+
                    "varying vec3 v_Position;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n/*" +

                    "varying vec4 v_ShadowCoord;\n" +
                    "float x_shadow_shift = 1.0;\n" +
                    "float y_shadow_shift = 1.0;*/\n" +

                    "void main()\n" +
                    "{\n" +
                    "    float distance = length(u_LightPos - v_Position);\n" +
                    "    vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
                    "    float diffuse = max(dot(v_Normal, lightVector), 0.0);\n" +
                    "    diffuse = diffuse * (1.0 / distance);\t//must be equal to Color * diffuse * diffuseFactor\n " +
                    "    //diffuse = diffuse + 0.77 ;  \n" +

                    "    vec3 Reflection = reflect(lightVector, v_Normal);\n" +
                    "    float SpecularFactor = max(dot(v_Position,Reflection),0.0);\n" +
                    "    float SpecularColor = 0.1 * SpecularFactor;\n"+
                    "\n" +
                    "//diffusion light+ ambient light + SpecularLight\n" +
                    "\n" +
                    "    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));\t\t\n" +
                    "  } ";

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setLookAtM(ViewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -5.0f, 0.0f, 3.0f, 0.0f);



        int vertexShaderHandle = Shaders.CompileHandles(GLES20.GL_VERTEX_SHADER,vertexShader);
        int fragmentShaderHandle = Shaders.CompileHandles(GLES20.GL_FRAGMENT_SHADER,fragmentShader);


        //int fragmentShaderHandle = s.CompileHandles(fragmentShader);
        ProgramHandle = Shaders.attachbindshaders(vertexShaderHandle,fragmentShaderHandle,new String[]{"a_Position","a_Normal","a_TexCoordinate"});
        PointProgramHandle = Shaders.attachbindshaders(Shaders.CompileHandles(GLES20.GL_VERTEX_SHADER,pointvertexShader),Shaders.CompileHandles(GLES20.GL_FRAGMENT_SHADER,pointfragmentShader),new String[]{"a_Position"});

        extendsopenglrenderer.on_create();


        //Matrix.setIdentityM(mAccumulatedRotation, 0);
        ObjectHandler.LoadHandles(ProgramHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;

        Matrix.frustumM(ProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public static float xViewangleInDegrees=0;
    public static float yViewangleInDegrees=0;

    public static float xgetViewAngle(){
        return xViewangleInDegrees;
    }

    public static void ysetViewAngle(float Angle){

        yViewangleInDegrees = Angle;///75.0f;
    }

    public static float ygetViewAngle(){
        return yViewangleInDegrees;
    }


    @Override
    public void onDrawFrame(GL10 gl) {

        time += 0.001;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(ProgramHandle);


        Matrix.setIdentityM(ViewMatrix,0);

        Matrix.setLookAtM(ViewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -15.0f, 0.0f, 3.0f, 0.0f);

        Matrix.rotateM(ViewMatrix,0,xgetViewAngle(),1.0f,0.0f, 0.0f);
        Matrix.rotateM(ViewMatrix, 0, ygetViewAngle(), 0.0f, 1.0f, 0.0f);

        Matrix.translateM(ViewMatrix,0,0f,0f,30f);


        Matrix.setIdentityM(ModelLightMatrix, 0);
        Matrix.translateM(ModelLightMatrix, 0, -3000.0f, 3000.0f, -10000.0f);

        Matrix.multiplyMV(LightPositionInWorldSpace, 0, ModelLightMatrix, 0, LightPositionInModelSpace, 0);
        Matrix.multiplyMV(LightPositionInEyeSpace, 0, ViewMatrix, 0, LightPositionInWorldSpace, 0);

        extendsopenglrenderer.on_draw();

        GLES20.glUseProgram(PointProgramHandle);
        drawLight();
    }

    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(PointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(PointProgramHandle, "a_Position");
        final int pointsize = GLES20.glGetUniformLocation(PointProgramHandle,"point_size");

        GLES20.glUniform1f(pointsize,0.5f);

        GLES20.glVertexAttrib3f(pointPositionHandle, LightPositionInModelSpace[0], LightPositionInModelSpace[1], LightPositionInModelSpace[2]);


        GLES20.glDisableVertexAttribArray(pointPositionHandle);


        Matrix.multiplyMM(MVPMatrix, 0, ViewMatrix, 0, ModelLightMatrix, 0);
        Matrix.multiplyMM(TempMatrix, 0, ProjectionMatrix, 0, MVPMatrix, 0);
        System.arraycopy(TempMatrix, 0, MVPMatrix, 0, 16);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, MVPMatrix, 0);


        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}

class extendsOpenGLRenderer{
    Context context;

    public static ObjectHandler sun;
    public static ObjectHandler mercury;
    public static ObjectHandler venus;
    public static ObjectHandler earth;
    public static ObjectHandler mars;
    public static ObjectHandler jupiter;
    public static ObjectHandler saturn;
    public static ObjectHandler uranus;
    public static ObjectHandler neptune;
    public static ObjectHandler pluto;



    public extendsOpenGLRenderer(Context context){

        this.context =context;
        sun = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        mercury = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        venus = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        earth = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        mars = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        jupiter = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        saturn = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        uranus = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        neptune = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
        pluto = new ObjectHandler(this.context,R.raw.sun1,0,new float[] {1.0f,2.0f}, new float[]{1.0f,2.0f,3.0f});
    }

    public void on_create(){
        sun.LoadTexture(R.drawable.sun);
        mercury.LoadTexture(R.drawable.mercury);
        venus.LoadTexture(R.drawable.venus_surface);
        earth.LoadTexture(R.drawable.earth);
        mars.LoadTexture(R.drawable.mars);
        jupiter.LoadTexture(R.drawable.jupiter);
        saturn.LoadTexture(R.drawable.saturn);
        uranus.LoadTexture(R.drawable.uranus);
        neptune.LoadTexture(R.drawable.neptune);
        pluto.LoadTexture(R.drawable.pluto);
    }

//    float[] ModelTemp =new float[16];

    public void on_draw(){

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,250.0f,250.0f,250.0f);
//        Matrix.translateM(ModelTemp,0,.0f,1.0f,-45f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        sun.setModelMatrix(ModelTemp);
        sun.drawObject();
//
//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,60.0f,60.0f,60.0f);
//        Matrix.translateM(ModelTemp,0,1.0f,1.0f,1.0f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        mercury.setModelMatrix(ModelTemp);
        mercury.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,10.0f,1.0f,0.0f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        venus.setModelMatrix(ModelTemp);
        venus.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,0.0f,00.0f,0.0f);
//        Matrix.translateM(ModelTemp,0,-25.0f,5.0f,25.0f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        earth.setModelMatrix(ModelTemp);
        earth.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,25.0f,1.0f,5.0f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        mars.setModelMatrix(ModelTemp);
        mars.drawObject();

        jupiter.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,30.0f,1.0f,10.0f);
//        Matrix.rotateM(ModelTemp, 0,22,0.0f,1.0f,0.0f);
//        saturn.setModelMatrix(ModelTemp);
        saturn.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,7.0f,1.0f,-3.0f);
//        Matrix.rotateM(ModelTemp, 0,21,0.0f,1.0f,0.0f);
//        uranus.setModelMatrix(ModelTemp);
        uranus.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,5.0f,1.0f,-5.0f);
//        Matrix.rotateM(ModelTemp, 0,17,0.0f,1.0f,0.0f);
//        neptune.setModelMatrix(ModelTemp);
        neptune.drawObject();

//        Matrix.setIdentityM(ModelTemp,0);
//        Matrix.scaleM(ModelTemp,0,10.0f,10.0f,10.0f);
//        Matrix.translateM(ModelTemp,0,2.0f,1.0f,-9.0f);
//        Matrix.rotateM(ModelTemp, 0,2,0.0f,1.0f,0.0f);
//        pluto.setModelMatrix(ModelTemp);
        pluto.drawObject();


    }
}
