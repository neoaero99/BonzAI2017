package lazers.api;

import java.util.LinkedList;

import bonzai.Entity;
import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Rotatable;
import bonzai.Team;

public class Repeater extends Entity<Repeater> implements Rotatable, Positionable, Identifiable {
	//private Team owner  null = no owner
	private LinkedList<Team> controllers;
	private int cooldown, uid;
	private float rotation;
	private Position position;
	
	/**
	 * Constructor
	 * @param rotation the rotation of a repeater
	 * @param position the position of a repeater
	 * @param cooldown the number of turns left before a repeater can be moved again
	 */
	public Repeater(float rotation, Position position, int cooldown, int id) {
		this.rotation = rotation;
		this.position = position;
		this.controllers = new LinkedList<>();
		this.uid = id;
	
		if (cooldown >= 0)
			this.cooldown = cooldown;
		
		else
			this.cooldown = 0;
	}
	
	/**
	 * Copy constructor
	 * @param repeater repeater to copy
	 * @param cooldown 
	 */	
	public Repeater(Entity<Repeater> repeater, int cooldown) {
		super(repeater);
		this.rotation = ((Repeater)repeater).getRotation();
		this.position = ((Repeater)repeater).getPosition();
		this.cooldown = cooldown;
		this.uid = ((Repeater)repeater).uid;
	}
	
	/**
	 * Copy constructor
	 * @param repeater repeater to copy
	 */	
	public Repeater(Entity<Repeater> repeater, int cooldown, float rotation) {
		super(repeater);
		this.rotation = rotation;
		this.position = ((Repeater)repeater).getPosition();
		this.cooldown = cooldown;
		this.uid = ((Repeater)repeater).uid;
	}
		
	
	public int getID() {
		return uid;
	}
	
	/**
	 * @return the number of turns until this repeater may be turned again
	 */
	public int getCooldown() {
		return cooldown;
	}
	
	/**
	 * @param team check if this team can move the repeater
	 * @return true if the team can move the repeater
	 */
	public boolean canMove(Team team) {
		if (cooldown == 0) {
			return (controllers.isEmpty() || controllers.contains(team));
		}
		return false; 
	}
	
	//TODO: Change the name.
	/**
	 * @return a list of the teams whose lazers are touching this repeater
	 */
	public LinkedList<Team> getControllers() {
		return controllers;
	}
	
	/**
	 * @param team check if this team's lazer is touching this repeater
	 * @return true if the team's lazer is touching this repeater, false otherwise
	 */
	public boolean isControlling(Team team) {
		return controllers.contains(team);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public LinkedList<Positionable> getNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float degree) {
		rotation = degree;
	}

	@Override
	public void rotate(float delta) {
		rotation = (rotation + delta) % 360;
	}
	
}
