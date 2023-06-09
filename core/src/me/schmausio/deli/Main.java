package me.schmausio.deli;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter
{
   public static SpriteBatch batch;
   OrthographicCamera camera;
   Viewport viewport;

   public static Smartrix smx_text_data = new Smartrix(100, -1, -1);

   public final static int SCREEN_WIDTH = 400, SCREEN_HEIGHT = 300;

   @Override
   public void create()
   {
      camera = new OrthographicCamera();
      viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);

      batch = new SpriteBatch();
      Res.load();

      Text.init();
      Tile.init_colors();

      Config.load_config(false);

      World.init_status(World.WorldStatus.LOAD_CHUNKS);
   }

   @Override
   public void render()
   {
      ScreenUtils.clear(145 / 255f,220 / 255f,233 / 255f, 1);

      float d = Gdx.graphics.getDeltaTime();

      update(Math.min(d, 0.1f));

      camera.update();
      batch.setProjectionMatrix(camera.combined);

      batch.begin();

      World.render();

      // for now no sorting of the text entries happens, since it is not needed in this project
      for (int i = 0; i < smx_text_data.num_lines(); i++)
      {
         Text.render_from_text_smx(smx_text_data, i);

         // free entries after rendering
         smx_text_data.clear_line(i);
      }

      batch.end();
   }

   @Override
   public void resize(int width, int height)
   {
      viewport.update(width, height, true);
   }

   private void update(float delta)
   {
      World.update(delta);
   }

   @Override
   public void dispose()
   {
      batch.dispose();
      Res.dispose();
      Text.dispose();
   }
}