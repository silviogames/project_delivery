package me.schmausio.deli;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

public class Entity
{
   // very generic entity class, no inheritance!

   float posx, posy;
   public EntityType type;

   public Entity(float posx, float posy, EntityType type)
   {
      this.posx = posx;
      this.posy = posy;
      this.type = type;

      switch (type) {
         case PLAYER:
            this.posx = 10 * 16;
            this.posy = 10 * 16;
            break;
      }
   }

   public void update(float delta)
   {
      switch (type)
      {
         case PLAYER:
         {
            int move_x = 0;
            int move_y = 0;
            int speed = Config.CONF.WALK_SPEED.value;
            if (Gdx.input.isKeyPressed(Input.Keys.A))
            {
               move_x = -speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D))
            {
               move_x = speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W))
            {
               move_y = speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S))
            {
               move_y = -speed;
            }
            posx += move_x * speed * delta;
            posy += move_y * speed * delta;
         }
         break;
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
