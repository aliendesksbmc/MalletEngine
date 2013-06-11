package com.linxonline.mallet.audio ;

import com.linxonline.mallet.event.* ;
import com.linxonline.mallet.util.id.IDInterface ;
import com.linxonline.mallet.util.settings.Settings ;

public class AudioFactory
{
	private final static String REQUEST_TYPE = "REQUEST_TYPE" ;
	private final static String ID_REQUEST = "ID_REQUEST" ;
	private final static String AUDIO_FILE = "AUDIO_FILE" ;
	private final static String AUDIO = "AUDIO" ;

	public AudioFactory() {}
	
	public static Event createAudio( final String _file, final IDInterface _inter )
	{
		final Settings audio = new Settings() ;
		audio.addInteger( REQUEST_TYPE, RequestType.CREATE_AUDIO ) ;
		if( _file != null ) { audio.addString( AUDIO_FILE, _file ) ; }
		if( _inter != null ) { audio.addObject( ID_REQUEST, _inter ) ; }
		return new Event( AUDIO, audio ) ;
	}
}