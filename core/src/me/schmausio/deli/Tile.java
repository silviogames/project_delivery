package me.schmausio.deli;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntIntMap;

public enum Tile
{
   AIR(0, false),
   DIRT(10, true),
   DIRT_WITH_GRAS(20, true),
   GRASS_FLOWER(30, false),
   GRASS(40, false),
   GRASS_FLOWER_TWO(50, false),

   ;

   // are saved in the chunk image in the R channel of the pixel!
   public final int R_value;

   public final boolean collision;

   Tile(int r, boolean collision)
   {
      this.R_value = r;
      this.collision = collision;
   }

   public static void init_colors()
   {
      for (int i = 0; i < values().length; i++)
      {
         // color rgba points to ordinal
         R_to_tile.put(values()[i].R_value, i);
      }
   }

   public static Tile from_R(int R)
   {
      return safe_ord(R / 10);
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

   static IntIntMap R_to_tile = new IntIntMap();
}
