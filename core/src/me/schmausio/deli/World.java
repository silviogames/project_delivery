package me.schmausio.deli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;

public class World
{
   public static Array<Chunk> list_chunks = new Array<>();

   // indices of chunks ist list_chunks that should be rendered right now
   static IntArray visible_chunks = new IntArray();
   static IntSet visible_chunks_set = new IntSet();
   // index that increments every frame and checks all chunks for visibility
   static int chunk_check_index = 0;

   public static float global_offset_x, global_offset_y;

   public static WorldStatus status;

   static Timer timer_debug_move_chunks = new Timer(0.1f);

   static Entity player = new Entity(0, 0, Entity.EntityType.PLAYER);

   public static Array<Entity> list_entities = new Array<>();

   static Array<FileHandle> list_chunk_files = new Array<>();

   static boolean debug_render = true;

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

   public static void update(float delta)
   {
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
               if (chunk_file.extension().equals("png") && chunk_file.nameWithoutExtension().length() == 7)
               {
                  Chunk loaded_chunk = Chunk.load_from_png(chunk_file);
                  if (loaded_chunk != null)
                  {
                     list_chunks.add(loaded_chunk);
                  }
               }
            }
         }
         break;
         case PLAY:
         {
            for (int i = 0; i < list_entities.size; i++)
            {
               Entity ent = list_entities.get(i);
               ent.update(delta);
            }

            // rendering the chunk ins visible list
            Chunk check_chunk = list_chunks.get(chunk_check_index);
            if (check_chunk.should_be_rendered(player.posx, player.posy))
            {
               visible_chunks_set.add(chunk_check_index);
            } else
            {
               visible_chunks_set.remove(chunk_check_index);
            }
            chunk_check_index++;
            if (chunk_check_index >= list_chunks.size) chunk_check_index = 0;

            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug_render = !debug_render;
         }
         break;
      }
   }

   public static void render()
   {
      if (debug_render)
      {
         int run_y = Main.SCREEN_HEIGHT;
         int off_y = 8;
         Text.draw("go " + MathUtils.round(global_offset_x) + "|" + MathUtils.round(global_offset_y), 2, run_y -= off_y);
         Text.draw("vis chunks " + visible_chunks_set.size, 2, run_y-=off_y);
      }

      global_offset_x = -player.posx + Main.SCREEN_WIDTH / 2f;
      global_offset_y = -player.posy + Main.SCREEN_HEIGHT / 2f;

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