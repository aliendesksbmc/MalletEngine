package com.linxonline.mallet.event ;

import java.util.ArrayList ;

public class EventQueue
{
	private final EventType name ;
	private final ArrayList<EventHandler> handlers = new ArrayList<EventHandler>() ;
	private final ArrayList<EventFilter> filters = new ArrayList<EventFilter>() ;
	private final ArrayList<Event<?>> optimisedEvents = new ArrayList<Event<?>>() ;
	private final EventMessenger messenger = new EventMessenger() ;

	public EventQueue( final String _name )
	{
		name = EventType.get( _name ) ;
	}
	
	public void addEventHandler( final EventHandler _handler )
	{
		handlers.add( _handler ) ;
	}

	public void removeEventHandler( final EventHandler _handler )
	{
		handlers.remove( _handler ) ;
	}

	public void addEventFilter( final EventFilter _filter )
	{
		filters.add( _filter ) ;
	}

	public void removeEventFilter( final EventFilter _filter )
	{
		filters.remove( _filter ) ;
	}

	public void addEvent( final Event<?> _event )
	{
		messenger.addEvent( _event ) ;
	}

	public void update()
	{
		messenger.refreshEvents() ;
		final int filterSize = filters.size() ;
		if( filterSize > 0 )
		{
			for( int i = 0; i < filterSize; ++i )
			{
				filters.get( i ).filter( messenger, optimisedEvents ) ;
			}
		}
		else
		{
			final ArrayList<Event<?>> events = messenger.getEvents() ;
			final int size = events.size() ;
			if( size > 0 )
			{
				optimisedEvents.addAll( events ) ;
			}
		}

		final int handlerSize = handlers.size() ;
		while( optimisedEvents.size() > 0 )				// Reduce chance of sending the same event twice.
		{
			final Event<?> event = optimisedEvents.get( 0 ) ;
			optimisedEvents.remove( 0 ) ;
			for( int j = 0; j < handlerSize; ++j )
			{
				handlers.get( j ).processEvent( event ) ;
			}
		}
	}

	public int size()
	{
		return messenger.size() ;
	}
	
	public void clearHandlers()
	{
		handlers.clear() ;
	}
	
	public void clearEvents()
	{
		messenger.clearEvents() ;
		optimisedEvents.clear() ;
	}
	
	public boolean hasEvents()
	{
		return messenger.hasEvents() ;
	}

	public String toString()
	{
		final StringBuffer buffer = new StringBuffer() ;
		buffer.append( "[ Event Queue: " + name + ", " ) ;
		for( final EventHandler handler : handlers )
		{
			buffer.append( handler.getName() + ", " ) ;
		}
		buffer.append( "]" ) ;
		return buffer.toString() ;
	}
}