package me.schmausio.deli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.FloatArray;

public class Entity
{
   // very generic entity class, no inheritance!

   float posx, posy;
   float vx = 0, vy = 0;

   boolean falling = true;
   boolean coyote = false;
   public EntityType type;

   float coyote_time = 0f;

   static BooleanArray collisions = new BooleanArray();
   static FloatArray collision_ypos = new FloatArray();

   public Entity(float posx, float posy, EntityType type)
   {
      this.posx = posx;
      this.posy = posy;
      this.type = type;

      switch (type)
      {
         case PLAYER:
            int spawn_chunk_x = 0;
            int spawn_chunk_y = 5;
            int tilex_offset = 5;
            int tiley_offset = 14;
            this.posx = spawn_chunk_x * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE + tilex_offset * Chunk.TILE_SIZE;
            this.posy = spawn_chunk_y * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE + tiley_offset * Chunk.TILE_SIZE;
            break;
      }
   }

   public void update(float delta)
   {
      switch (type)
      {
         case PLAYER:
         {
            vx = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.A))
            {
               vx = -1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D))
            {
               vx = 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !falling)
            {
               vy = Config.CONF.JUMP_STRENGTH.value;
               falling = true;
            }
            if (!falling)
            {
               if (!World.collision(posx, posy - 4))
               {
                  coyote = true;
               }
               if (coyote)
               {
                  coyote_time += delta;
                  if (coyote_time > Util.INT_TO_FLOAT(Config.CONF.COYOTE_TIME_MS.value))
                  {
                     falling = true;
                     coyote_time = 0;
                     coyote = false;
                  }
               }
            }
         }
         break;
      }

      //posx += vx * Config.CONF.WALK_SPEED.value * delta;
      if (falling)
      {
         vy -= (Config.CONF.GRAVITY.value / 10f) * delta;
         vy = MathUtils.clamp(vy, -Config.CONF.MAX_FALLING_SPEED.value, Config.CONF.MAX_FALLING_SPEED.value);
      } else
      {
      }

      float nx = posx + vx * Config.CONF.WALK_SPEED.value * delta;
      float ny = posy + vy;

      boolean collision_hori = World.collision(nx, posy);
      boolean collision_vert = World.collision(posx, ny);

      int num_pixels_per_move = MathUtils.floor(Math.abs(posy - ny) * 3);

      if (num_pixels_per_move > 1)
      {
         boolean collided = false;
         for (int i = 0; i < num_pixels_per_move; i++)
         {
            float interp_posy = MathUtils.lerp(posy, ny, i / ((float) (num_pixels_per_move - 1)));
            if (World.collision(nx, interp_posy))
            {
               falling = false;
               coyote_time = 0;
               coyote = false;
               if (vy > 0)
               {
                  posy = MathUtils.round(interp_posy / 16f) * 16f - 1;
               } else
               {
                  posy = MathUtils.round(interp_posy / 16f) * 16f;
               }

               vy = 0;
               collided = true;
               break;
            }
         }
         if (!collided) posy = ny;
      } else
      {
         if (collision_vert)
         {
            falling = false;
            coyote_time = 0;
            coyote = false;
            vy = 0;
         } else
         {
            posy = ny;
         }
      }


      if (collision_hori)
      {
         vx = 0;
      } else
      {
         posx = nx;
      }
   }

   public void render()
   {
      float px = World.global_offset_x + posx;
      float py = World.global_offset_y + posy;

      // TODO: 29.04.23 placeholder texture for now
      RenderUtil.render_box(px - 5, py, 10, 20, Color.NAVY);
   }

   public enum EntityType
   {
      PLAYER,
      FOO,
      ;
   }
}
