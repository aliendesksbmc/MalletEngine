package com.linxonline.mallet.input ;

import com.linxonline.mallet.util.caches.Cacheable ;

public final class InputEvent implements Cacheable
{
	public InputType inputType ;
	public KeyCode keycode ;

	public int mouseX = 0 ;
	public int mouseY = 0 ;
	public boolean isActionKey = false ;

	public InputEvent()
	{
		inputType = InputType.NONE ;
		keycode = KeyCode.NONE ;
	}

	public InputEvent( final InputType _type, final int _x, final int _y )
	{
		inputType = _type ;
		mouseX = _x ;
		mouseY = _y ;
	}

	public InputEvent( final InputType _type, final KeyCode _keycode )
	{
		inputType = _type ;
		keycode = _keycode ;
	}

	public void setInput( final InputType _type, final int _x, final int _y )
	{
		inputType = _type ;
		mouseX = _x ;
		mouseY = _y ;
	}
	
	public final InputType getInputType()
	{
		return inputType ;
	}

	public final KeyCode getKeyCode()
	{
		return keycode ;
	}

	public final char getKeyCharacter()
	{
		return keycode.character ;
	}
	
	public final boolean isActionKey()
	{
		return isActionKey ;
	}
	
	public final int getMouseX()
	{
		return mouseX ;
	}

	public final int getMouseY()
	{
		return mouseY ;
	}

	public final void clone( final InputEvent _input )
	{
		inputType = _input.inputType ;
		mouseX = _input.mouseX ;
		mouseY = _input.mouseY ;
		keycode = _input.keycode ;
		isActionKey = _input.isActionKey ;
	}

	public final void reset()
	{
		inputType = InputType.NONE ;
		keycode = KeyCode.NONE ;
		mouseX = 0 ;
		mouseY = 0 ;
	}

	public String toString()
	{
		final StringBuffer buffer = new StringBuffer() ;
		buffer.append( '[' ) ;
		buffer.append( inputType ) ;
		switch( inputType )
		{
			case KEYBOARD_RELEASED :
			case KEYBOARD_PRESSED  :
			{
				buffer.append( "KeyCode: " + keycode ) ;
				break ;
			}
			default :
			{
				buffer.append( " X: " + mouseX ) ;
				buffer.append( " Y: " + mouseY ) ;
				break ;
			}
		}
		buffer.append( ']' ) ;
		return buffer.toString() ;
	}
}