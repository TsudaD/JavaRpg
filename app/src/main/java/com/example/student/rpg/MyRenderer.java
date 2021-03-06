package com.example.student.rpg;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.student.rpg.Battle.ObjBattleManager;
import com.example.student.rpg.Battle.ObjBattlePlayer;
import com.example.student.rpg.Battle.ObjBattleEnemy;
import com.example.student.rpg.Battle.ObjPlayerCommand;
import com.example.student.rpg.Scene.SceneManager;

import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer
{
    private int mWidth; // 画面の横幅
    private int mHieght; // 画面の縦幅

    public static int m_player_texture;
    public static int m_title_back_texture;
    public static int m_attack_select_texture;
    public static int m_messege_window_texture;
    public static int m_block_texture;
    public static int m_char_texture;
    public static int m_battle_background;
    public static int m_bat_enemy_texture;

    public MyRenderer(Context context)
    {
        Global.context = context;
    }

    private void renderMain(GL10 gl)
    {
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        ObjectManager.Update();
        ObjectManager.Draw(gl);

        int fps = FpsManager.FpsMeasurement(); // fpsを計測
        if(fps >= 30)// 30fps以上なら緑
        {
            FontTexture.DrawString(gl, -1.f, -0.8f, 0.1f, 0.1f, "fps : " + String.valueOf(fps),
                    0.f, 1.f, 0.f, 1.f);
        }
        else // それ以下なら赤
        {
            FontTexture.DrawString(gl, -1.f, -0.8f, 0.1f, 0.1f, "fps : " + String.valueOf(fps),
                    1.f, 0.f, 0.f, 1.f);
        }

        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        // 描画処理
        // OpenGl描画設定
        //gl.glViewport(0, 0, mWidth, mHieght);
        gl.glMatrixMode(GL10.GL_PROJECTION);;

        gl.glLoadIdentity();
        gl.glOrthof(-1.5f, 1.5f, -1.f, 1.f, 0.5f, -0.5f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // 画面クリア
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        renderMain(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // 初期化処理
        mWidth = width;
        mHieght = height;

        Global.gl = gl;

        // 常に3 : 2で描画
        int w = 0;
        int h = 0;
        while(w < width && h < height)
        {
            w += 3;
            h += 2;
        }
        int w_offset = (width - w) / 2;
        int h_offset = (height - h) / 2;
        gl.glViewport(w_offset, h_offset, w, h);

        m_player_texture         = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.hero);
        m_title_back_texture     = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.title_back_ground);
        m_attack_select_texture  = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.attack_select);
        m_messege_window_texture = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.box_red);
        m_block_texture          = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.normalice_block);
        m_char_texture           = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.freechara1);
        m_battle_background      = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.battle_background);
        m_bat_enemy_texture      = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.bat_enemy);

        //m_left_botton_texture  = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.left_button);
        //m_right_botton_texture = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.right_button);
        //m_down_botton_texture  = GraphicUtil.loadTexture(Global.gl, Global.context.getResources(), R.drawable.buttom_button);

        // 文字マップ情報をクリア
        FontTexture.MapClear();

        if(Global.is_object_create == true)
        {
            // 最初のシーンをタイトルに設定
            SceneManager.ChangeScene(SceneManager.Scene_Kind.Title);

//            m_battle_manager = new ObjBattleManager();
//            ObjectManager.Insert(m_battle_manager);

            Global.is_object_create = false;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
    {
        // 初期化処理
    }

    // テクスチャを読み込んで、IDを返す関数(エラーメッセージ付き)
    private int loadTextureGetID(GL10 gl, Resources resources, int resId)
    {
        int out_resId = GraphicUtil.loadTexture(
                gl, resources, resId);

        if(out_resId == 0)
        {
            Log.e(getClass().toString(),
                    "texture load ERROR!");
            return 0;
        }

        return out_resId;
    }
}