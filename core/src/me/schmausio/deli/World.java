package me.schmausio.deli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntSet;

public class World
{
   public static Array<Chunk> list_chunks = new Array<>();

   // combinded cx,cy map to index of chunks in list_chunk
   public static IntIntMap chunk_map = new IntIntMap();

   static IntSet visible_chunks_set = new IntSet();
   // index that increments every frame and checks all chunks for visibility
   static int chunk_check_index = 0;

   public static float global_offset_x, global_offset_y;
   public static float camera_offset_x, camera_offset_y = -50;

   public static WorldStatus status;

   static Timer timer_debug_move_chunks = new Timer(0.1f);

   static Entity player = new Entity(0, 0, Entity.EntityType.PLAYER);

   public static Array<Entity> list_entities = new Array<>();
   // used for copy
   public static IntArray list_entity_index_remove = new IntArray();

   public static Array<Entity> list_spawn = new Array<>();

   static Array<FileHandle> list_chunk_files = new Array<>();

   static boolean debug_render = true;
   static boolean debug_slow_motion = false;

   static Osc osc_box_hover = new Osc(3, 5, 2);

   static
   {
      list_entities.add(player);
   }

   public static void init_status(WorldStatus new_status)
   {
      switch (new_status)
      {
         case LOAD_CHUNKS:
         {
            // populate the list of files that should be loaded
            FileHandle chunk_dir = Gdx.files.local("chunks/");
            list_chunk_files.addAll(chunk_dir.list());
         }
         break;
      }
      status = new_status;
   }

   public static boolean collision(float entity_x, float entity_y)
   {
      return get_tile(entity_x, entity_y).collision;
   }

   public static Tile get_tile(float entity_x, float entity_y)
   {
      int tx = (int) (entity_x / Chunk.TILE_SIZE);
      int ty = (int) (entity_y / Chunk.TILE_SIZE);
      tx = tx % Chunk.CHUNK_SIZE;
      ty = ty % Chunk.CHUNK_SIZE;
      Chunk c = get_chunk(entity_x, entity_y);
      if (c == null)
      {
         return Tile.AIR;
      } else
      {
         return Tile.safe_ord(c.content.get(tx, ty));
      }
   }

   public static int entity_pos_to_chunk_x(float entity_x)
   {
      return (int) (entity_x / (Chunk.TILE_SIZE * Chunk.CHUNK_SIZE));
   }

   public static int entity_pos_to_chunk_y(float entity_y)
   {
      return (int) (entity_y / (Chunk.TILE_SIZE * Chunk.CHUNK_SIZE));
   }

   public static Chunk get_chunk(float entity_x, float entity_y)
   {
      return get_chunk(entity_pos_to_chunk_x(entity_x), entity_pos_to_chunk_y(entity_y));
   }

   public static Chunk get_chunk(int cx, int cy)
   {
      int combinedValue = (cx << 16) | cy;
      int chunk_index = chunk_map.get(combinedValue, -1);
      if (chunk_index == -1) return null;
      return list_chunks.get(chunk_index);
   }

   public static void update(float delta)
   {
      osc_box_hover.update(delta);

      Entity.AI_check = false;
      if (Entity.timer_AI.update(delta))
      {
         Entity.AI_check = true;
      }

      switch (status)
      {
         case LOAD_CHUNKS:
         {
            // LOAD
            if (list_chunk_files.isEmpty())
            {
               init_status(WorldStatus.PLAY);
            } else
            {
               FileHandle chunk_file = list_chunk_files.pop();
               if (chunk_file.extension().equals("png") && chunk_file.nameWithoutExtension().length() == 9)
               {
                  Chunk loaded_chunk = Chunk.load_from_png(chunk_file);
                  if (loaded_chunk != null)
                  {
                     // Combine the two values into a single int
                     int combinedValue = (loaded_chunk.cx << 16) | loaded_chunk.cy;
                     chunk_map.put(combinedValue, list_chunks.size);
                     list_chunks.add(loaded_chunk);

                     //int cx = combinedValue >> 16;
                     //int cy = combinedValue & 0xFFFF;
                  }
               }
            }
         }
         break;
         case PLAY:
         {
            if (debug_slow_motion) delta /= 10f;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            {
               delta *= 5;
            }

            for (int i = 0; i < list_entities.size; i++)
            {
               Entity ent = list_entities.get(i);
               ent.update(delta, i);
            }

            for (int i = list_entity_index_remove.size - 1; i >= 0; i--)
            {
               list_entities.removeIndex(list_entity_index_remove.get(i));
            }
            list_entity_index_remove.clear();

            if (list_spawn.size > 0)
            {
               list_entities.addAll(list_spawn);
               list_spawn.clear();
            }

            if (list_chunks.size > 0)
            {
               // rendering the chunk ins visible list
               Chunk check_chunk = list_chunks.get(chunk_check_index);
               if (check_chunk.should_be_rendered(player.posx, player.posy))
               {
                  if (!visible_chunks_set.contains(chunk_check_index)) check_chunk.init();
                  visible_chunks_set.add(chunk_check_index);
               } else
               {
                  if (visible_chunks_set.contains(chunk_check_index)) check_chunk.unload_entities();
                  visible_chunks_set.remove(chunk_check_index);
               }
               chunk_check_index++;
               if (chunk_check_index >= list_chunks.size) chunk_check_index = 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug_render = !debug_render;

            if (Gdx.input.isKeyJustPressed(Input.Keys.F4))
            {
               Config.load_config(true);
            }

            // TODO: 01.05.23 DEBUG REMOVE BEFORE RELEASE
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            {
               camera_offset_x += delta * 500;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            {
               camera_offset_x -= delta * 500;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP))
            {
               camera_offset_y -= delta * 500;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            {
               camera_offset_y += delta * 500;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F10))
            {
               camera_offset_x = 0;
               camera_offset_y = -50;
            }
         }
         break;
      }
   }

   public static void render()
   {
      switch (status)
      {
         case PLAY:
         {

            if (debug_render)
            {
               int run_y = Main.SCREEN_HEIGHT;
               int off_y = 8;
               Text.draw("go " + MathUtils.round(global_offset_x) + "|" + MathUtils.round(global_offset_y), 2, run_y -= off_y);
               Text.draw("vis chunks " + visible_chunks_set.size, 2, run_y -= off_y);
               Text.draw("current chunk [" + entity_pos_to_chunk_x(player.posx) + "|" + entity_pos_to_chunk_y(player.posy) + "]", 2, run_y -= off_y);
               Tile test_tile = get_tile(player.posx, player.posy);
               Text.draw("test_tile " + test_tile.toString(), 2, run_y -= off_y);
               Text.draw("player pos " + player.posx + " " + player.posy, 2, run_y -= off_y);
               Text.draw("falling " + player.falling, 2, run_y -= off_y);
               Text.draw("player v " + player.vx + " " + player.vy, 2, run_y -= off_y);
               Text.draw("jump hold " + Entity.jump_hold, 2, run_y -= off_y);
               Text.draw("jump hold time " + Entity.jump_hold_time, 2, run_y -= off_y);
               Text.draw("num entities " + World.list_entities.size, 2, run_y -= off_y);
               Text.draw("wutz life " + Entity.wutz_life, 2, run_y -= off_y);
            }

            global_offset_x = -player.posx + Main.SCREEN_WIDTH / 2f + camera_offset_x;
            global_offset_y = -player.posy + Main.SCREEN_HEIGHT / 2f + camera_offset_y;

            IntSet.IntSetIterator iterator = visible_chunks_set.iterator();
            while (iterator.hasNext)
            {
               int value = iterator.next();
               Chunk c = list_chunks.get(value);
               c.render();
            }

            for (int i = 0; i < list_entities.size; i++)
            {
               Entity ent = list_entities.get(i);
               ent.render();
            }

            for (int i = 1; i <= 3; i++)
            {
               Color color_heart = Color.WHITE;
               if(i<= Entity.wutz_life){
                  color_heart = Color.SALMON;
               }else{
                  color_heart = Color.FIREBRICK;
               }

               // TODO: 01.05.23 render heart
               RenderUtil.render_box( i * 20 - 16, Main.SCREEN_HEIGHT - 20, 16,16,color_heart);
            }
         }
         break;
         case LOAD_CHUNKS:
         {
            Text.cdraw("loading chunks " + list_chunk_files.size, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Color.WHITE, 2f);
         }
         break;
      }
   }

   public enum WorldStatus
   {
      LOAD_CHUNKS,
      PLAY,
      PAUSE,
      TRANSITION,

      ;
   }
}