package me.schmausio.deli;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;

public class Chunk // DIE HEISSEN FUER LORENA ABSCHNITTE!
{
   public final int cx, cy; // CHUNK POSITION IN WORLD
   final static int CHUNK_SIZE = 32; // in tiles
   final static int TILE_SIZE = 16; // pixels

   public Flatbyte content = new Flatbyte(CHUNK_SIZE, CHUNK_SIZE, (byte) 0, (byte) 0);

   public Chunk(int chunk_x, int chunk_y)
   {
      this.cx = chunk_x;
      this.cy = chunk_y;
      for (int ix = 0; ix < CHUNK_SIZE; ix++)
      {
         for (int iy = 0; iy < CHUNK_SIZE; iy++)
         {
            content.set(ix, iy, (byte) MathUtils.random(1, 5));
         }
      }
   }

   public void render()
   {
      for (int ix = 0; ix < CHUNK_SIZE; ix++)
      {
         for (int iy = 0; iy < CHUNK_SIZE; iy++)
         {
            // using global offset
            float px = World.global_offset_x + ix * TILE_SIZE + cx * CHUNK_SIZE * TILE_SIZE;
            float py = World.global_offset_y + iy * TILE_SIZE + cy * CHUNK_SIZE * TILE_SIZE;
            Tile tile = Tile.safe_ord(content.get(ix, iy));
            RenderUtil.render_box(px, py, TILE_SIZE, TILE_SIZE, tile.tex_color);
         }
      }
   }

   public boolean should_be_rendered(float player_x, float player_y)
   {
      int chunk_midx = cx * CHUNK_SIZE * TILE_SIZE + (CHUNK_SIZE * TILE_SIZE / 2);
      int chunk_midy = cy * CHUNK_SIZE * TILE_SIZE + (CHUNK_SIZE * TILE_SIZE / 2);
      return Util.euclid_norm((int) player_x, (int) player_y, chunk_midx, chunk_midy) < 16 * 32;
   }

   public static Chunk load_from_png(FileHandle chunk_file)
   {
      String[] coords = chunk_file.nameWithoutExtension().split("_");
      int cx = Integer.parseInt(coords[0]);
      int cy = Integer.parseInt(coords[1]);

      System.out.println("loading chunk [" + cx + "|" + cy + "]");
      Chunk chunk = new Chunk(cx, cy);

      Pixmap pm = new Pixmap(chunk_file);
      Color local = new Color();
      if (pm.getWidth() != Chunk.CHUNK_SIZE || pm.getHeight() != Chunk.CHUNK_SIZE)
      {
         System.out.println("could not use file " + chunk_file.name() + " due to wrong dimensions! [" + pm.getWidth() + "|" + pm.getHeight() + "]");
         chunk = null;
      } else
      {
         for (int ix = 0; ix < Chunk.CHUNK_SIZE; ix++)
         {
            for (int iy = 0; iy < Chunk.CHUNK_SIZE; iy++)
            {
               Color.rgba8888ToColor(local, pm.getPixel(ix, CHUNK_SIZE - 1 - iy));
               Tile local_tile = Tile.from_color(local);
               chunk.content.set(ix, iy, (byte) local_tile.ordinal());
            }
         }
      }
      pm.dispose();
      return chunk;
   }
}