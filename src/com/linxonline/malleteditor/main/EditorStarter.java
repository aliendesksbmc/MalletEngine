package com.linxonline.malleteditor.main ;

import com.linxonline.mallet.game.GameLoader ;
import com.linxonline.mallet.main.desktop.DesktopStarter ;
import com.linxonline.mallet.io.filesystem.desktop.DesktopFileSystem ;

import com.linxonline.malleteditor.system.GLEditorSystem ;

public class EditorStarter extends DesktopStarter
{
	/**
		Specify the backend systems to be used.
		Use OpenGL & OpenAL - GLDefaultSystem.
		Use the DesktopFileSystem for the file system.
	*/
	public EditorStarter()
	{
		super( new GLEditorSystem(), new DesktopFileSystem() ) ;
	}

	@Override
	protected GameLoader getGameLoader()
	{
		return new EditorLoader() ;
	}
}