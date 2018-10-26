package com.example.student.rpg.FieldMap;

import com.example.student.rpg.GraphicUtil;
import com.example.student.rpg.MyRenderer;
import com.example.student.rpg.Obj;
import com.example.student.rpg.Point;

import java.util.ArrayList;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public class ObjMap extends Obj
{
    final int final_map_height = 10;
    final int final_map_width = 10;
    final float final_object_size = 0.2f;
    final float final_object_size_half = final_object_size / 2.f;
    final int final_not_search = 99999;

    //Map<Integer, Map<Integer, Integer>> m_map = new LinkedHashMap<Integer, Map<Integer, Integer>>();
    int[][] m_map_data_array =
            {
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, },
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, },
            };

    public enum Map_Kind
    {
        None(0),
        Impassable(1),
        // マップ遷移
        ;

        private int id;

        Map_Kind(final int id)
        {
            this.id = id;
        }

        public int getInt()
        {
            return this.id;
        }
    }

    public ObjMap()
    {
        // マップ情報を初期化
//        for(int y = 0; y < final_map_height; y++)
//        {
//            Map<Integer, Integer> tempMap = new LinkedHashMap<Integer, Integer>();
//            for(int x = 0; x < final_map_width; x++)
//            {
//                tempMap.put(x,0);
//            }
//            m_map.put(y, tempMap);
//        }
    }

    @Override
    public void Draw(GL10 gl)
    {
        for(int y = 0; y < final_map_height; y++)
        {
            for(int x = 0; x < final_map_width; x++)
            {
                if(m_map_data_array[y][x] == Map_Kind.Impassable.getInt())
                {
                    GraphicUtil.drawTexture(gl, m_x + x * final_object_size, m_y - y * final_object_size,
                            final_object_size, final_object_size, MyRenderer.m_block_texture);
                }
            }
        }
    }

    // ファイルからマップデータ取得
    public void LoadMapData(char filename)
    {

    }

    // マップから最短ルートを取得
    // 引数は全てのマップの要素数にする
    public ArrayList<Point> Shortest_Route(Point start_point, Point end_point)
    {
        // 探索配列を作成
        int[][] search_map = new int[final_map_height][final_map_width];
        for(int y = 0; y < final_map_height; y++)
        {
            for(int x = 0; x < final_map_width; x++)
            {
                search_map[y][x] = final_not_search;
            }
        }

        Route_Search(search_map, start_point, end_point, 0);

        // 最短ルート取得
        return Route_Get(search_map, end_point);
    }

    void Route_Search(int[][] search_map, Point start_point,
                      Point end_point, int depth_num)
    {
        // 調べる方向                               上                        左                         下                        右
        final Point direction[] = { new Point(0, -1), new Point(-1, 0), new Point(0, 1), new Point(1, 0) };
        depth_num++;
        search_map[start_point.y][start_point.x] = depth_num;

        // 現在の位置が目的についていたら処理終了
        if(start_point.x == end_point.x &&
                start_point.y == end_point.y) return;

        for(int i = 0; i < 4; i++)
        {
            // 次に調べる位置
            Point next_point = new Point();
            next_point.x = start_point.x + direction[i].x;
            next_point.y = start_point.y + direction[i].y;

            // 通行可能かどうか
            if(m_map_data_array[next_point.y][next_point.x] == Map_Kind.Impassable.getInt()) continue;

            // その次の先を調べるかどうか
            if(search_map[next_point.y][next_point.x] > depth_num)
            {
                Route_Search(search_map, next_point, end_point, depth_num);
            }
        }
    }

    // 最短ルート取得(検索配列を使って、ゴールの位置から調べる)
    private ArrayList<Point> Route_Get(int search_map[][], Point end_point)
    {
        // 調べる方向            上       左      下      右
        final Point direction[] = { new Point(0, -1), new Point(-1, 0),
                new Point(0, 1), new Point(1, 0) };

        // 現在地
        Point now_point = new Point();

        // 最短ルートを入れる配列
        ArrayList<Point> shortest_route = new ArrayList<Point>();

        // ゴールからスタートに戻るまでの距離
        int distance;
        distance = search_map[end_point.y][end_point.x];

        shortest_route.add(new Point(end_point.y, end_point.x));
        now_point = end_point;

        for(int i = 0; i < 4; i++)
        {
            // 次の調べる位置
            Point next_point = new Point();
            next_point.x = now_point.x + direction[i].x;
            next_point.y = now_point.y + direction[i].y;

            // 次に進むべき道を探す
            if(search_map[next_point.y][next_point.x] == distance - 1)
            {
                i = -1; // ループを最初からにする
                distance--;
                // 最短ルートに登録
                shortest_route.add(new Point(next_point.y, next_point.x));
                now_point = next_point;

                if(distance == 2) break;
            }
        }

        return shortest_route;
    }

    // 位置をマップの配列の要素に変換
    public Point PosConvertMapPos(float x, float y)
    {
        Point out_point = new Point();
        float map_pos_x, map_pos_y;
        // 計算が見づらい・・・修正案件
        map_pos_x = (x - m_x + final_object_size_half) / final_object_size;
        map_pos_y = (y - m_y - final_object_size_half) / final_object_size;

        out_point.x = (int) map_pos_x;
        out_point.y = (int)-map_pos_y;

        return out_point;
    }
}