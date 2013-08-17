package com.linxonline.mallet.renderer.font ;

import com.linxonline.mallet.util.sort.QuickSort ;
import com.linxonline.mallet.resources.texture.Texture ;

public class FontMap
{
	public final static Glyph FAILED_GLYPH = new Glyph( '\0', 0, 0 ) ;

	public final Glyph[] glyphs ;			// Data needed to render the glyphs available
	public final Texture texture ;			// Rendered glyph
	public final int height ;

	public FontMap( final Glyph[] _glyphs, final Texture _texture, final int _height )
	{
		texture = _texture ;
		height = _height ;

		// Sort the glyphs
		final Glyph[] temp = QuickSort.quicksort( _glyphs ) ;
		final int size = temp.length ;
		final int largestCode = temp[size - 1].sortValue() ;

		// Create a map based on the glyphs char number.
		glyphs = new Glyph[largestCode + 1] ;
		for( int i = 0; i < size; ++i)
		{
			final int code = temp[i].sortValue() ;
			glyphs[code] = temp[i] ;
		}
	}

	public Glyph getGlyphWithChar( final char _character )
	{
		return getGlyphWithCode( ( int )_character ) ;
	}

	public Glyph getGlyphWithCode( final int _code )
	{
		if( _code < glyphs.length )
		{
			if( glyphs[_code] != null ) { return glyphs[_code] ; }
		}

		return FAILED_GLYPH ;
	}

	public int getHeight() { return height ; }
	
	public String toString()
	{
		final StringBuffer buffer = new StringBuffer() ;
		for( int i = 0; i < glyphs.length; i++ )
		{
			if( glyphs[i] != null )
			{
				buffer.append( glyphs[i].character ) ;
			}
		}

		return buffer.toString() ;
	}
}