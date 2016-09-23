package lazers.api;

import java.util.LinkedList;

import bonzai.Entity;
import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Rotatable;
import bonzai.Team;

public class Emitter extends Entity<Emitter> implements Rotatable, Positionable, Identifiable {
	private float rotation;
	private Position position;
	private Team team;
	private int uid;
	
	/**
	 * @param team the team that controls the Emitter
	 * @param rotation the rotation of the Emitter, in degrees
	 * @param position the position of the Emitter
	 */
	public Emitter(Team team, float rotation, Position position, int id) {
		this.rotation = rotation;
		this.position = position;
		this.team = team;
		this.uid = id;
	}
	
	/**
	 * Copy constructor
	 * @param emitter the Emitter to copy
	 */
	public Emitter(Entity<Emitter> emitter, float rotation) {
		super(emitter);
		this.team = ((Emitter)emitter).getTeam();
		this.rotation = rotation;
		this.position = ((Emitter)emitter).getPosition();
		this.uid = ((Emitter)emitter).uid;
	}

	/**
	 * @return the team that controls the Emitter
	 */
	public Team getTeam() {
		return team;
	}

	public int getID() {
		return uid;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public LinkedList<Positionable> getNeighbors() {
		// TODO 
		return null;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(float degree) {
		rotation = degree % 360;
	}

	@Override
	public void rotate(float delta) {
		rotation = (rotation + delta) % 360;
	}
}
