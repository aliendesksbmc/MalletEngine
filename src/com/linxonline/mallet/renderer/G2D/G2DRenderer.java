package com.linxonline.mallet.renderer.G2D ;

import java.util.ArrayList ;
import javax.swing.JFrame ;
import java.awt.RenderingHints ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.FontMetrics ;
import java.awt.Graphics2D ;
import java.awt.Canvas ;
import java.awt.Rectangle ;
import java.awt.TexturePaint ;
import java.awt.image.BufferStrategy ;
import java.awt.image.BufferedImage ;
import java.awt.geom.AffineTransform ;

import com.linxonline.mallet.util.capture.DesktopCaptureScreen ;
import com.linxonline.mallet.resources.TextureManager ;
import com.linxonline.mallet.util.settings.Settings ;
import com.linxonline.mallet.maths.* ;
import com.linxonline.mallet.resources.texture.* ;
import com.linxonline.mallet.renderer.* ;

/**
	G2DRenderer is an event-driven renderer.

	It uses the Event System to recieve information on how things should 
	be rendered.
**/
public class G2DRenderer extends Basic2DRender
{
	protected final static TextureManager textures = new TextureManager() ;

	protected final Vector2 pos = new Vector2() ;
	protected final AffineTransform transform = new AffineTransform() ;
	protected final Rectangle rect = new Rectangle() ;
	protected final Canvas canvas = new Canvas() ;

	private int numID = 0 ;
	private BufferStrategy buffer = null ;
	private JFrame frame = null ;

	// Pointers to variables located in RenderInfo
	protected Vector2 display = null ;
	protected Vector2 render = null ;
	protected Vector3 camera = null ;
	protected Vector2 scale = null ;

	protected Graphics2D graphics = null ;
	protected DrawInterface drawShape = null ;
	protected DrawInterface drawTexture = null ;
	protected DrawInterface drawText = null ;

	public G2DRenderer()
	{
		canvas.setIgnoreRepaint( true ) ;
		initDrawCalls() ;
	}

	private void initDrawCalls()
	{
		drawShape = new DrawInterface()
		{
			public void draw( final Settings _settings, final Vector2 _position ) 
			{
				Graphics2DDraw.setGraphicsColour( graphics, _settings ) ;
				Graphics2DDraw.setClip( graphics, _settings, _position ) ;

				Graphics2DDraw.drawLine( graphics, _settings, _position ) ;
				Graphics2DDraw.drawLines( graphics, _settings, _position ) ;
				Graphics2DDraw.drawPolygon( graphics, _settings, _position ) ;
				Graphics2DDraw.drawPoints( graphics, _settings, _position ) ;
			}
		} ;
		
		drawTexture = new DrawInterface()
		{
			public void draw( final Settings _settings, final Vector2 _position ) 
			{
				Texture temp = _settings.getObject( "TEXTURE", Texture.class, null ) ;
				if( temp == null )
				{
					temp = loadTexture( _settings.getString( "FILE", null ) ) ;
					if( temp == null )
					{
						return ;
					}
				}

				transform.setToIdentity() ;
				if( _settings.getBoolean( "HUD", false ) == true )
				{
					_position.x -= camera.x ;
					_position.y -= camera.y ;
				}

				final Vector2 offset = _settings.getObject( "OFFSET", Vector2.class, DEFAULT_OFFSET ) ;
				transform.translate( _position.x + offset.x, _position.y + offset.y ) ;

				final float rotation = _settings.getFloat( "ROTATE", 0 ) ;
				transform.rotate( rotation, -offset.x, -offset.y ) ;

				final JavaImage texture = ( JavaImage )temp.image ;
				final Vector2 fill = _settings.getObject( "FILL", Vector2.class, null ) ;
				if( fill != null )
				{
					TexturePaint texPaint = _settings.getObject( "TEXTUREPAINT", TexturePaint.class, null ) ;
					if( texPaint == null )
					{
						rect.setSize( texture.width, texture.height ) ;
						texPaint = new TexturePaint( texture.bufferedImage, rect ) ;
						_settings.addObject( "TEXTUREPAINT", texPaint ) ;
					}

					final AffineTransform matrix = graphics.getTransform() ;

					graphics.transform( transform ) ;
					graphics.setPaint( texPaint ) ;

					graphics.fillRect( 0, 0, ( int )fill.x, ( int )fill.y ) ;

					graphics.setTransform( matrix ) ;
					graphics.setPaint( null ) ;
					return ;
				}

				// Scale the texture to the request dimensions.
				final Vector2 dim = _settings.getObject( "DIM", Vector2.class, null ) ;
				if( dim != null )
				{
					final float texWidth = ( float )temp.getWidth() ;
					final float texHeight = ( float )temp.getHeight() ;
					transform.scale( dim.x / texWidth, dim.y / texHeight ) ;
				}

				Graphics2DDraw.setClip( graphics, _settings, _position ) ;
				graphics.drawImage( texture.bufferedImage, transform, null ) ;
				temp.unregister() ;
			}
		} ;

		drawText = new DrawInterface()
		{
			public void draw( final Settings _settings, final Vector2 _position ) 
			{
				final String text = _settings.getString( "TEXT", null ) ;
				if( text == null )
				{
					System.out.println( "NO TEXT TO DRAW" ) ;
					return ;
				}

				Graphics2DDraw.setClip( graphics, _settings, _position ) ;
				Graphics2DDraw.setGraphicsColour( graphics, _settings ) ;

				final MalletFont font = _settings.getObject( "FONT", MalletFont.class, null ) ;
				if( font != null )
				{
					if( font.font == null )
					{
						font.setFont( new Font( font.fontName, font.style, font.size ) ) ;
					}
				}

				final FontMetrics fontMetric = graphics.getFontMetrics( ( Font )font.font ) ;
				final int height = fontMetric.getHeight() ;
				final int textWidth = fontMetric.stringWidth( text ) ;

				_settings.addInteger( "TEXTWIDTH", textWidth ) ;

				final Vector2 offset = _settings.getObject( "OFFSET", Vector2.class, DEFAULT_OFFSET ) ;
				final Vector2 position = new Vector2( _position.x + offset.x, _position.y + offset.y ) ;
				final Vector2 currentPos = new Vector2( position.x, position.y ) ;

				final int lineHeight = fontMetric.getHeight();
				final int lineWidth = _settings.getInteger( "LINEWIDTH", ( int )render.x ) + ( int )position.x ;
				String[] words = _settings.getObject( "WORDS", String[].class, null ) ;
				if( words == null )
				{
					words = optimiseText( fontMetric, text, position, lineWidth ) ;
					_settings.addObject( "WORDS", words ) ;
					_settings.addInteger( "TEXTWIDTH", -1 ) ;
				}

				final int size = words.length ;
				String word = null ;

				final int alignment = _settings.getInteger( "ALIGNMENT", ALIGN_LEFT ) ;
				_settings.addInteger( "TEXTHEIGHT", size * lineHeight ) ;

				int textHeight = 0 ;
				graphics.setFont( ( Font )font.font ) ;
				for( int i = 0; i < size; ++i )
				{
					word = words[i] ;
					setTextAlignment( alignment, currentPos, fontMetric.stringWidth( word ) ) ;
					graphics.drawString( word, currentPos.x, currentPos.y ) ;
					currentPos.y += lineHeight ;
					currentPos.x = position.x ;
				}
			}

			private String[] optimiseText( final FontMetrics _font, final String _text, final Vector2 _position, final int _lineWidth )
			{
				int length = 0 ;
				float wordWidth = 0.0f ;
				final Vector2 currentPos = new Vector2( _position.x, _position.y ) ;
				String[] words = _text.split( "(?<= )" ) ;

				final ArrayList<String> txt = new ArrayList<String>() ;
				final StringBuilder buffer = new StringBuilder() ;

				String word = null ;
				for( int i = 0; i < words.length; ++i )
				{
					word = words[i] ;
					wordWidth = _font.stringWidth( word ) ;

					if( word.contains( "<br>" ) == true )
					{
						if( length > 0 )
						{
							txt.add( buffer.toString() ) ;
							buffer.delete( 0, length ) ;
						}
						else
						{
							txt.add( "" ) ;
						}

						currentPos.x = _position.x ;
						continue ;
					}
					else if( currentPos.x + wordWidth >= _lineWidth )
					{
						txt.add( buffer.toString() ) ;
						buffer.delete( 0, length ) ;
						currentPos.x = _position.x ;
					}

					currentPos.x += wordWidth ;
					buffer.append( word ) ;
					length = buffer.length() ;
				}

				if( length > 0 )
				{
					txt.add( buffer.toString() ) ;
					buffer.delete( 0, length ) ;
				}

				words = new String[txt.size()] ;
				words = txt.toArray( words ) ;
				return words ;
			}

			private void setTextAlignment( final int _alignment, final Vector2 _position, final int _wordWidth )
			{
				//System.out.println( "WORD WIDTH: " + _wordWidth ) ;
				switch( _alignment )
				{
					case ALIGN_RIGHT :
					{
						_position.x -= _wordWidth ;
						break ;
					}
					case ALIGN_CENTRE :
					{
						_position.x -= _wordWidth / 2 ;
						break ;
					}
					default:
					{
						return ;
					}
				}
			}
		} ;
	}

	/**Render Content**/

	@Override
	public void draw()
	{
		camera = renderInfo.getCameraPosition() ;
		if( camera == null )
		{
			System.out.println( "Camera Not Set." ) ;
			return ;
		}

		graphics = ( Graphics2D )buffer.getDrawGraphics() ;
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) ;
		graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR ) ;

		try
		{
			display = renderInfo.getDisplayDimensions() ;
			render = renderInfo.getRenderDimensions() ;

			graphics.setClip( 0, 0, ( int )( display.x ), ( int )( display.y ) ) ;
			graphics.setColor( Color.BLACK ) ;
			graphics.fillRect( 0, 0, ( int )display.x, ( int )display.y ) ;

			scale = renderInfo.getScaleRenderToDisplay() ;
			graphics.scale( scale.x, scale.y ) ;
			graphics.translate( camera.x, camera.y ) ;

			updateEvents() ;
			render() ;
		}
		finally
		{
			graphics.dispose() ;
			graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF ) ;
		}

		if( buffer.contentsLost() == false )
		{
			buffer.show() ;
		}
	}

	protected void render()
	{
		final int length = content.size() ;
		RenderData data = null ;

		for( int i = 0; i < length; ++i )
		{
			data = content.get( i ) ;
			pos.setXY( data.position.x, data.position.y ) ;
			data.drawCall.draw( data.drawData, pos ) ;
		}
	}

	/**Create Content**/

	protected void createTexture( final Settings _draw )
	{
		final Vector3 position = _draw.getObject( "POSITION", Vector3.class, null ) ;
		final int layer = _draw.getInteger( "LAYER", -1 ) ;

		if( position != null )
		{
			final RenderData data = new RenderData( numID++, DrawRequestType.TEXTURE, _draw, position, layer ) ;
			data.drawCall = drawTexture ;
			insert( data ) ;
		}
	}

	protected void createGeometry( final Settings _draw )
	{
		final Vector3 position = _draw.getObject( "POSITION", Vector3.class, null ) ;
		final int layer = _draw.getInteger( "LAYER", -1 ) ;

		if( position != null )
		{
			final RenderData data = new RenderData( numID++, DrawRequestType.GEOMETRY, _draw, position, layer ) ;
			data.drawCall = drawShape ;
			insert( data ) ;
		}
	}

	protected void createText( final Settings _draw )
	{
		final Vector3 position = _draw.getObject( "POSITION", Vector3.class, null ) ;
		final int layer = _draw.getInteger( "LAYER", -1 ) ;

		if( position != null )
		{
			final RenderData data = new RenderData( numID++, DrawRequestType.TEXT, _draw, position, layer ) ;
			data.drawCall = drawText ;
			insert( data ) ;
		}
	}

	/**
		Add the Canvas to the JFrame
	**/
	public void hookToFrame( final JFrame _frame )
	{
		frame = _frame ;
		frame.add( canvas ) ;
		frame.pack() ;

		initCanvasBuffer( 2 ) ;
	}

	public Canvas getCanvas()
	{
		return canvas ;
	}

	private void initCanvasBuffer( final int _size )
	{
		canvas.createBufferStrategy( _size ) ;
		buffer = canvas.getBufferStrategy() ;
	}
	
	private Texture loadTexture( final String _file )
	{
		return _file != null ? ( Texture )textures.get( _file ) : null ;
	}
}