package me.schmausio.deli;

public enum Deco
{
   NONE,
   FLOWER_1,
   FLOWER_2,
   FLOWER_3,
   FLOWER_4,
   FLOWER_5,
   MUSHROOM_1,
   MUSHROOM_2,
   MUSHROOM_3,
   MUSHROOM_4,
   MUSHROOM_5,
   ;

   public static Deco from_B(int B)
   {
      return safe_ord(B / 10);
   }

   public static Deco safe_ord(int ordinal)
   {
      if (ordinal < 0 || ordinal >= values().length)
      {
         return NONE;
      } else
      {
         return values()[ordinal];
      }
   }
}
