package com.brajesh.solarsystem.solarsystem;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ObjectHandler {
    Context context;

    Calculations calc;

    private final float orbitRadius;
    private static float[] pos = new float[2] ;
    private final float[] scale ;
    private float angle=0;
    private final float TimePeriod;

    private final int BytesInFloat = 4;
    private final int DataSizeofPosition = 3;
    private final int DataSizeofNormal = 3;
    private final int DataSizeofTextureCoordinates = 2;


    public static int mMVPMatrixHandle;
    public static int mMVMatrixHandle;
    public static int mLightPosHandle;
    public static int mTextureUniformHandle;
    public static int mPositionHandle;
    public static int mNormalHandle;
    public static int mTextureCoordinateHandle;

    private final FloatBuffer ObjectPositions;
    private final FloatBuffer ObjectNormals;
    private final FloatBuffer ObjectTextureCoordinates;

    public int TextureFromImg;

    private final int vertexCount;



    public ObjectHandler(Context context,int RawObjectId, float rad, float[] Pos, float[] Scale){

        this.context = context;
        pos = Pos;
        scale = Scale;
        orbitRadius = rad;

        TimePeriod = 2*(float)Math.PI*((float)Math.pow(((float)Math.pow(orbitRadius,3)/10.0f),0.5f));

        calc = new Calculations();

        ObjRead obj = new ObjRead(context, RawObjectId);
        final float[] ObjectPositionData = obj.positions;
        final float[]ObjectNormalData = obj.normals;
        final float[] cubeTextureCoordinateData = obj.textureCoordinates;

        vertexCount = ObjectPositionData.length/3;

        ObjectPositions = ByteBuffer.allocateDirect(ObjectPositionData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectPositions.put(ObjectPositionData).position(0);

        ObjectNormals = ByteBuffer.allocateDirect(ObjectNormalData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectNormals.put(ObjectNormalData).position(0);

        ObjectTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * BytesInFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        ObjectTextureCoordinates.put(cubeTextureCoordinateData).position(0);

    }

    public void LoadTexture(int RawTextureId){
        TextureFromImg = loadTexture.loadingTexture(this.context, RawTextureId);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }

    public static void LoadHandles(int ProgramHandle){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(ProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(ProgramHandle, "a_TexCoordinate");
    }

    public void drawObject()
    {
        pos = calc.calcPos(OpenGLRenderer.time,orbitRadius,TimePeriod);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.TextureFromImg);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        ObjectTextureCoordinates.position(0);

        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, DataSizeofTextureCoordinates, GLES20.GL_FLOAT, false,
                0, ObjectTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        ObjectPositions.position(0);

        GLES20.glVertexAttribPointer(mPositionHandle, DataSizeofPosition, GLES20.GL_FLOAT, false,
                0, ObjectPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);


        ObjectNormals.position(0);

        GLES20.glVertexAttribPointer(mNormalHandle, DataSizeofNormal, GLES20.GL_FLOAT, false,
                0, ObjectNormals);

        GLES20.glEnableVertexAttribArray(mNormalHandle);


        float[] MVPMatrix=new float[16];

        float[] ModelMatrix = new float[16];

        Matrix.setIdentityM(ModelMatrix,0);

        Matrix.scaleM(ModelMatrix,0,scale[0],scale[1],scale[2]);
        Matrix.translateM(ModelMatrix,0,pos[0],1.0f,pos[1]);
        Matrix.rotateM(ModelMatrix, 0,angle,0.0f,1.0f,0.0f);

        Matrix.multiplyMM(MVPMatrix, 0, OpenGLRenderer.ViewMatrix, 0, ModelMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, MVPMatrix, 0);

        Matrix.multiplyMM(MVPMatrix, 0, OpenGLRenderer.ProjectionMatrix, 0, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, OpenGLRenderer.LightPositionInEyeSpace[0], OpenGLRenderer.LightPositionInEyeSpace[1], OpenGLRenderer.LightPositionInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
