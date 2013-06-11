package com.linxonline.mallet.entity ;

import java.util.Collection ;
import java.util.ArrayList ;
import java.util.HashMap ;

import com.linxonline.mallet.io.serialisation.* ;
import com.linxonline.mallet.maths.* ;
import com.linxonline.mallet.event.* ;

/**
	Entity is a container class for Componets.
	By default an Entity should have a unique name, and a global family name.
	For example an Entity could be named HENCHMAN3, and be in the ENEMY family.
	
	Entity also allows an internal component to send messages to other components,
	it operates in a similar basis to the EventSystem.
	
	Entity does not have the capabilities to recieve Events from a Game State, this 
	should be done through a component like EventComponent that can then route it 
	through the Component Event System.
**/
public final class Entity implements SerialisableForm
{
	private final ArrayList<Component> components = new ArrayList<Component>() ;
	private EventSystem eventSystem = new EventSystem() ;			// Component Event System

	public String name = null ;										// A Unique identifier of this Entity
	public String family = null ;									// What type/family is this Entity apart of
	public Vector3 position = new Vector3( 0.0f, 0.0f, 0.0f ) ;		// Position of Entity in world space
	public boolean destroy = false ;									// Is the Entity to be destroyed and subsequently removed?

	public Entity()
	{
		name = "NONE" ;
		family = "NONE" ;
	}

	public Entity( final String _name )
	{
		name = _name ;
		family = "NONE" ;
	}

	public Entity( final String _name, final String _family )
	{
		name = _name ;
		family = _family ;
	}

	/**
		Once a name has been set it should not be changed.
		The Entity System may change an Entitys name to ensure it is unique.
	**/
	public void setName( final String _name )
	{
		name = _name ;
	}

	public String getName()
	{
		return name ;
	}
	
	public final void setPosition( final float _x, final float _y, final float _z )
	{
		position.x = _x ;
		position.y = _y ;
		position.z = _z ;
	}

	public final void addToPosition( final float _x, final float _y, final float _z )
	{
		position.x += _x ;
		position.y += _y ;
		position.z += _z ;
	}

	/**
		returns true if _name equals Entity name
		false if not
	**/
	public final boolean isName( final String _name )
	{
		return name.equals( _name ) ;
	}

	/**
		returns true if _family equals Entity family
		false if not
	**/
	public final boolean isFamily( final String _family )
	{
		return family.equals( _family ) ;
	}

	/**
		Add a component to the Entity and set its parent to the Entity
		The Component should not be owned by another Component, it could get messy!
	**/
	public final void addComponent( final Component _component )
	{
		_component.setParent( this ) ;
		components.add( _component ) ;

		final EventController controller = _component.getComponentEventController() ;
		controller.setAddEventInterface( eventSystem ) ;
		eventSystem.addEventHandler( controller ) ;
	}

	public final void removeComponent( final Component _component )
	{
		_component.setParent( null ) ;					// Required by j2Objc conversion, causes memory-leak otherwise
		components.remove( _component ) ;
		
		final EventController controller = _component.getComponentEventController() ;
		controller.setAddEventInterface( null ) ;
		eventSystem.removeEventHandler( controller ) ;
	}

	public final void removeComponents( final ArrayList<Component> _components )
	{
		for( final Component component : _components )
		{
			removeComponent( component ) ;
		}
	}

	/**
		Update the message system of the Entity
		and update the Components
	**/
	public final void update( final float _dt )
	{
		eventSystem.update() ;
		Component component = null ;

		// Update Components
		final int size = components.size() ;
		for( int i = 0; i < size; ++i )
		{
			component = components.get( i ) ;
			component.update( _dt ) ;
		}
	}

	/**
		Get a Component with the designated name.
		If there is more than one component with that name,
		then it will return the first one it finds.
	**/
	public final Component getComponentByName( final String _name )
	{
		final int nameID = _name.hashCode() ;
		final int size = components.size() ;
		Component component = null ;

		for( int i = 0; i < size; ++i )
		{
			component = components.get( i ) ;
			if( component.isNameID( nameID ) == true )
			{
				return component ;
			}
		}

		return null ;
	}

	/**
		Return all the components with the designated name
	**/
	public final ArrayList<Component> getComponentsByName( final String _name )
	{
		final int nameID = _name.hashCode() ;
		final ArrayList<Component> group = new ArrayList<Component>() ;

		for( Component component : components )
		{
			if( component.isNameID( nameID ) == true )
			{
				group.add( component ) ;
			}
		}

		return group ;
	}

	/**
		Get the Components that have the same Group name and return them in 
		an ArrayList.
		NOTE: A new ArrayList is created each time this function is called.
	**/
	public final ArrayList<Component> getComponentByGroup( final String _group )
	{
		final int groupID = _group.hashCode() ;
		final ArrayList<Component> group = new ArrayList<Component>() ;

		for( final Component component : components )
		{
			if( component.isGroupID( groupID ) == true )
			{
				group.add( component ) ;
			}
		}

		return group ;
	}

	/**
		Returns the ArrayList that contains all the Entity's Components.
		NOTE: the ArrayList is NOT a copy.
	**/
	public final ArrayList<Component> getAllComponents()
	{
		return components ;
	}

	/** 
		Returns the Entiy's current position
	**/
	public final Vector3 getPosition()
	{
		return position ;
	}

	/** 
		Call when you wish the Entity to be decalred dead.
		An Entity decalred destroyed, will be removed by the
		Entity System when appropriate.
	*/
	public final void destroy()
	{
		destroy = true ;
	}

	/**
		Used to write out the byte stream of the Entity object
	**/
	public boolean writeObject( final SerialiseOutput _output )
	{
		_output.writeString( name ) ;
		_output.writeString( family ) ;
		_output.writeFloat( position.x ) ;
		_output.writeFloat( position.y ) ;
		_output.writeFloat( position.z ) ;
		_output.writeBoolean( destroy ) ;
		
		final int length = components.size() ;
		Component component = null ;
		
		for( int i = 0 ; i < length; ++i )
		{
			component = components.get( i ) ;
			component.writeObject( _output ) ;
		}

		return true ;
	}

	/**
		Used to read an Entity byte stream and reconstruct it.
		It does NOT build the Entity from scratch.
	**/
	public boolean readObject( final SerialiseInput _input )
	{
		name = _input.readString() ;
		family = _input.readString() ;
		position.x = _input.readFloat() ;
		position.y = _input.readFloat() ;
		position.z = _input.readFloat() ;
		destroy = _input.readBoolean() ;

		final int length = components.size() ;
		Component component = null ;

		for( int i = 0 ; i < length; ++i )
		{
			component = components.get( i ) ;
			component.readObject( _input ) ;
		}

		return true ;
	}

	/**
	 Once clear() is called, the Entity it is pretty much dead,
	 it's lost all of its components and the ComponentEventSystem 
	 is nulled out.
	**/
	public void clear()
	{
		final ArrayList<Component> comps = new ArrayList<Component>( components ) ;
		removeComponents( comps ) ;

		comps.clear() ;
		eventSystem.clearHandlers() ;
	}
}