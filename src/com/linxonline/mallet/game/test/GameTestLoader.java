package com.linxonline.mallet.game.test ;

import java.util.ArrayList ;

import com.linxonline.mallet.renderer.DrawFactory ;
import com.linxonline.mallet.renderer.TextFactory ;
import com.linxonline.mallet.renderer.GeometryFactory ;
import com.linxonline.mallet.renderer.CameraFactory ;
import com.linxonline.mallet.renderer.MalletFont ;
import com.linxonline.mallet.renderer.Line ;
import com.linxonline.mallet.renderer.Shape ;
import com.linxonline.mallet.animation.AnimationFactory ;
import com.linxonline.mallet.audio.AudioFactory ;

import com.linxonline.mallet.util.sort.* ;

import com.linxonline.mallet.maths.* ;
import com.linxonline.mallet.io.filesystem.* ;

import com.linxonline.mallet.game.* ;
import com.linxonline.mallet.entity.* ;
import com.linxonline.mallet.entity.components.* ;

import com.linxonline.mallet.util.SourceCallback ;
import com.linxonline.mallet.util.id.IDInterface ;

import com.linxonline.mallet.util.factory.creators.AnimMouseCreator ;
import com.linxonline.mallet.util.factory.creators.ImageCreator ;
import com.linxonline.mallet.util.settings.Settings ;

import com.linxonline.mallet.io.formats.ogg.OGG ;
import com.linxonline.mallet.io.formats.ogg.Vorbis ;

import com.linxonline.mallet.physics.hulls.Box2D ;
import com.linxonline.mallet.physics.primitives.AABB ;

import com.linxonline.mallet.event.* ;
import com.linxonline.mallet.input.* ;
//import com.linxonline.mallet.input.desktop.XInputLinux ;

/**
	Example on how to implement the Game Loader class.
	Initialise your Game States and add them to the 
	Game System passed in. Remember to tell the Game System
	what is the default-state!
*/
public final class GameTestLoader extends GameLoader
{
	public GameTestLoader() {}

	@Override
	public void loadGame( final GameSystem _system )
	{
		_system.addGameState( new GameState( "DEFAULT" )
		{
			//private final XInputLinux device = new XInputLinux( "/dev/input/js0" ) ;
		
			public void initGame()			// Called when state is started
			{
				/*device.setXInputListener( new XInputListener()
				{
					public void keyPressed( final XInputDevice.Event _event )
					{
						System.out.println( _event ) ;
					}

					public void keyReleased( final XInputDevice.Event _event )
					{
						System.out.println( _event ) ;
					}

					// D-Pad, Joysticks, L2, R2
					public void analogue( final XInputDevice.Event _event )
					{
						System.out.println( _event ) ;
					}

					public void start() {}
					public void end() {}
				} ) ;*/

				renderTextureExample() ;
				renderAnimationExample() ;
				renderTextExample() ;
				playAudioExample() ;
				createEntityExample() ;
				createMouseAnimExample() ;
			}

			/**
				Add a texture and render directly to the renderer
			**/
			public void renderTextureExample()
			{
				eventSystem.addEvent( DrawFactory.amendGUI( DrawFactory.createTexture( "base/textures/moomba.png", 		// Texture Location
																	new Vector3( 415.0f, 385.0f, 0.0f ),				// Position
																	new Vector2( -32, -32 ), 							// Offset
																	new Vector2( 64, 64 ),								// Dimension, how large - scaled
																	null,												// fill, texture repeat
																	new Vector2(),										// clip
																	new Vector2( 1, 1 ),								// clip offset
																	10,
																	null ), true ) ) ;									// layer

				final Shape lines = new Shape( 7, 6 ) ;
				lines.addPoint( new Vector2( 0, 10 ) ) ;
				lines.addPoint( new Vector2( 0, 0 ) ) ;
				lines.addPoint( new Vector2( 100, 0 ) ) ;
				lines.addPoint( new Vector2( 100, 5 ) ) ;
				lines.addPoint( new Vector2( 200, 0 ) ) ;
				lines.addPoint( new Vector2( 200, 10 ) ) ;

				lines.addIndex( 0 ) ;
				lines.addIndex( 1 ) ;
				lines.addIndex( 2 ) ;
				lines.addIndex( 3 ) ;
				lines.addIndex( 2 ) ;
				lines.addIndex( 4 ) ;
				lines.addIndex( 5 ) ;

				eventSystem.addEvent( GeometryFactory.createShape( "DRAWLINES",
																	lines,
																	new Vector3( -100.0f, 50.0f, 0.0f ),
																	null,
																	null,
																	null,
																	10,
																	null ) ) ;
			}

			/**
				Add an animation directly to the animation system
			**/
			public void renderAnimationExample()
			{
				final AnimComponent anim = new AnimComponent() ;
				anim.addAnimation( "DEFAULT", AnimationFactory.createAnimation( "base/anim/moomba.anim",
																		 new Vector3( 0.0f, 0.0f, 0.0f ),
																		 new Vector2( -32, -32 ),
																		 new Vector2( 64, 64 ),
																		 null,
																		 null,
																		 null,
																		 10,
																		 anim ) ) ;

				anim.setDefaultAnim( "DEFAULT" ) ;

				final Entity entity = new Entity( "Test Animation" ) ;
				entity.addComponent( anim ) ;
				addEntity( entity ) ;
			}

			/**
				Add text and render directly to the renderer
			**/
			public void renderTextExample()
			{
				eventSystem.addEvent( TextFactory.createText( "Hello World!", 						// Text
															  new Vector3( 0.0f, -80.0f, 0.0f ),	// Position
															  new Vector2( 0, 0 ), 					// Offset
															  new MalletFont( "Arial", 12 ),		// Mallet Font
															  null,									// Mallet Colour
															  null,									// clip
															  null,									// clip offset
															  200,									// layer
															  2,									// Text alignment, Centre
															  null ) ) ;
			}

			/**
				Play audio file directly to the audio system
			**/
			public void playAudioExample()
			{
				final SoundComponent sound = new SoundComponent() ;
				sound.addSound( "DEFAULT", AudioFactory.createAudio( "base/music/fairing-well.wav", sound ) ) ;

				sound.setDefaultSound( "DEFAULT" ) ;

				final Entity entity = new Entity( "Test Sound" ) ;
				entity.addComponent( sound ) ;
				addEntity( entity ) ;

				/*final OGG ogg = OGG.readOGG( "base/music/fairing-well.ogg" ) ;
				System.out.println( ogg ) ;
				final Vorbis vorbis = new Vorbis() ;
				try
				{
					vorbis.decode( ogg ) ;
					System.out.println( vorbis ) ;
				}
				catch( Exception ex )
				{
					ex.printStackTrace() ;
				}*/
			}

			/**
				Create an Entity using the ImageCreator and add 
				it to the Game State
			**/
			public void createEntityExample()
			{
				final Settings image = new Settings() ;
				image.addString( "IMAGE", "base/textures/moomba.png" ) ;
				image.addString( "POS", "100, 0" ) ;
				image.addString( "DIM", "64, 64" ) ;
				image.addString( "OFFSET", "-32, -32" ) ;

				final ImageCreator creator = new ImageCreator() ;
				final Entity entity = creator.create( image ) ;

				entity.addComponent( CollisionComponent.generateBox2D( new Vector2(),
																	new Vector2( 64, 64 ),
																	new Vector2( 100, 0 ),
																	new Vector2( -32, -32 ) ) ) ;

				addEntity( entity ) ;
			}

			/**
				Create an Entity that follows the mouse
			**/
			public void createMouseAnimExample()
			{
				final Settings mouse = new Settings() ;
				mouse.addString( "ANIM", "base/anim/moomba.anim" ) ;
				mouse.addObject( "DIM", new Vector2( 32, 32 ) ) ;
				mouse.addObject( "OFFSET", new Vector2( -16, -16 ) ) ;

				final AnimMouseCreator creator = new AnimMouseCreator() ;
				final Entity entity = creator.create( mouse ) ;

				entity.addComponent( CollisionComponent.generateBox2D( new Vector2(),
																	   new Vector2( 32, 32 ),
																	   new Vector2( 0, 0 ),
																	   new Vector2( -16, -16 ) ) ) ;

				addEntity( entity ) ;
			}
		} ) ;

		_system.setDefaultGameState( "DEFAULT" ) ;		// Define what Game State should be run first
	}
}