package com.linxonline.mallet.renderer.desktop.GL ;

import com.linxonline.mallet.resources.AbstractManager ;
import com.linxonline.mallet.resources.Resource ;
import com.linxonline.mallet.util.settings.Settings ;

public class GLFontManager extends AbstractManager<GLFontMap>
{
	private final GLFontGenerator gen ;

	public GLFontManager( final GLTextureManager _manager )
	{
		gen = new GLFontGenerator( _manager ) ;
	}

	@Override
	public GLFontMap get( final String _key, final Settings _settings )
	{
		System.out.println( "GLFontManager: get( _key, _settings ). Not implemented yet." ) ;
		return null ;
	}

	@Override
	public GLFontMap get( final String _key, final String _file )
	{
		System.out.println( "GLFontManager: get( _key, _file ). Not implemented yet." ) ;
		return null ;
	}

	@Override
	public GLFontMap get( final String _file )
	{
		System.out.println( "GLFontManager: get( _file ). Not implemented yet." ) ;
		return null ;
	}
	
	public GLFontMap get( final String _name, final int _size )
	{
		final String id = _name + _size ;
		if( exists( id ) == true )
		{
			return resources.get( id ) ;
		}

		final GLFontMap resource = createResource( _name, _size ) ;
		if( resource != null )
		{
			add( id, resource ) ;
			resource.register() ;
		}

		return resource ;
	}

	protected GLFontMap createResource( final String _name, final int _size )
	{
		// Generate the Glyphs for the passed in characters
		return gen.generateFontMap( _name, _size, "\0 []{}:;'@~#<>,/?|`-=¬abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\"£$%^&*()_+.", 5 ) ;
	}
}