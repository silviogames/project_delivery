package me.schmausio.deli;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntIntMap;

public enum Tile
{
   AIR(Color.CLEAR, false),
   DIRT(RenderUtil.TILE_COLOR_DIRT, true),
   DIRT_WITH_GRAS(RenderUtil.TILE_COLOR_DIRT_WITH_GRASS, true),
   GRASS_FLOWER(RenderUtil.TILE_COLOR_FLOWERS, false),
   GRASS(RenderUtil.TILE_COLOR_GRASS_ONLY, false),
   GRASS_FLOWER_TWO(RenderUtil.TILE_COLOR_GRASS_FLOWERS, false),

   ;

   public Color tex_color;
   public final boolean collision;

   Tile(Color c, boolean collision)
   {
      this.tex_color = c;
      this.collision = collision;
   }

   public static void init_colors()
   {
      for (int i = 0; i < values().length; i++)
      {
         // color rgba points to ordinal
         color_to_tile.put(values()[i].tex_color.toIntBits(), i);
      }
   }

   public static Tile safe_ord(int ordinal)
   {
      if (ordinal < 0 || ordinal >= values().length)
      {
         return AIR;
      } else
      {
         return values()[ordinal];
      }
   }

   static IntIntMap color_to_tile = new IntIntMap();

   public static Tile from_color(Color c)
   {
      int ordinal = color_to_tile.get(c.toIntBits(), AIR.ordinal());
      if (ordinal < 0 || ordinal >= values().length)
      {
         return AIR;
      } else
      {
         return values()[ordinal];
      }
   }
}
