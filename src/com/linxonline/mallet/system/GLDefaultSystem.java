package com.linxonline.mallet.system ;

import javax.swing.JFrame ;
import java.awt.Graphics2D ;
import java.awt.image.BufferedImage ;
import java.awt.Point ;
import java.awt.Dimension ;

import com.linxonline.mallet.audio.alsa.* ;
import com.linxonline.mallet.audio.* ;
import com.linxonline.mallet.resources.* ;
import com.linxonline.mallet.resources.gl.* ;
import com.linxonline.mallet.renderer.* ;
import com.linxonline.mallet.input.* ;
import com.linxonline.mallet.event.* ;
import com.linxonline.mallet.maths.* ;

/*===========================================*/
// DefaultSystem
// Used to hook up Engine to OS using Java API
// Central Location for Events, Input, 
// and Rendering
/*===========================================*/

public class GLDefaultSystem implements SystemInterface
{
	protected JFrame frame = null ;
	protected String titleName = new String( "Mallet Engine" ) ;
	protected ALSASourceGenerator sourceGenerator = new ALSASourceGenerator() ;
	protected GLRenderer renderer = new GLRenderer() ;
	public EventSystem eventSystem = new EventSystem() ;
	public InputSystem inputSystem = new InputSystem() ;

	public GLDefaultSystem()
	{
		// Initiliase the Resource System with Java based Managers
		ResourceManager resource = ResourceManager.getResourceManager() ;
		resource.spriteManager = new SpriteManager() ;
	}

	public void initSystem()
	{
		inputSystem.inputAdapter = renderer.renderInfo ;

		frame = new JFrame( titleName ) ;
		frame.createBufferStrategy( 1 ) ;
		frame.setCursor( frame.getToolkit().createCustomCursor( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ), 
																new Point( 0, 0 ), "null" ) ) ;
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
		frame.setIgnoreRepaint( true ) ;

		renderer.hookToWindow( frame ) ;

		// Hook Input System with GUI Canvas
		renderer.getCanvas().addMouseListener( inputSystem ) ;
		renderer.getCanvas().addMouseMotionListener( inputSystem ) ;
		renderer.getCanvas().addMouseWheelListener( inputSystem ) ;
		renderer.getCanvas().addKeyListener( inputSystem ) ;

		frame.setSize( ( int )renderer.renderInfo.getDisplayDimensions().x, 
					   ( int )renderer.renderInfo.getDisplayDimensions().y ) ;
		frame.setMinimumSize( new Dimension( ( int )renderer.renderInfo.getDisplayDimensions().x, 
							  ( int )renderer.renderInfo.getDisplayDimensions().y ) ) ;
		frame.validate() ;
		frame.setVisible( true ) ;
		//frame.setResizable( false ) ;
		
		draw() ; // Ensure OpenGL Context gets initialised.
	}

	public void startSystem() {}

	public void stopSystem() {}

	public void shutdownSystem() {}

	/*INPUT HOOK*/
	public void addInputHandler( final InputHandler _handler )
	{
		inputSystem.addInputHandler( _handler ) ;
	}

	public void removeInputHandler( final InputHandler _handler )
	{
		inputSystem.removeInputHandler( _handler ) ;
	}

	/*EVENT HOOK*/
	public void addEvent( final Event _event )
	{
		eventSystem.addEvent( _event ) ;
	}

	public void addEventHandler( final EventHandler _handler )
	{
		eventSystem.addEventHandler( _handler ) ;
	}

	public void removeEventHandler( final EventHandler _handler )
	{
		eventSystem.removeEventHandler( _handler ) ;
	}

	/*RENDER*/
	public void addRenderContainer( final RenderContainer _container )
	{
		renderer.addRenderContainer( _container ) ;
	}

	public void removeRenderContainer( final RenderContainer _container )
	{
		renderer.removeRenderContainer( _container ) ;
	}

	public void setTitleName( final String _titleName )
	{
		titleName = _titleName ;
	}
	
	public void setDisplayDimensions( final Vector2 _display )
	{
		renderer.setDisplayDimensions( ( int )_display.x, ( int )_display.y ) ;
	}

	public void setRenderDimensions( final Vector2 _render )
	{
		renderer.setRenderDimensions( ( int )_render.x, ( int )_render.y ) ;
	}

	public RenderInterface getRenderInterface()
	{
		return renderer ;
	}

	public void setCameraPosition( final Vector3 _camera )
	{
		renderer.setCameraPosition( _camera ) ;
	}

	/*AUDIO SOURCE GENERATOR*/
	public SourceGenerator getSourceGenerator()
	{
		return sourceGenerator ;
	}
	
	public void clear()
	{
		renderer.clear() ;
	}

	public void clearInputs()
	{
		inputSystem.clearInputs() ;
	}

	public void clearEvents()
	{
		eventSystem.clearEvents() ;
	}

	public boolean update()
	{
		inputSystem.update() ;
		eventSystem.update() ;
		return true ; // Informs the Game System whether to continue updating or not.
	}

	public void draw()
	{
		renderer.draw() ;
	}
}
