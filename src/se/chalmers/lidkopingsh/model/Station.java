package se.chalmers.lidkopingsh.model;

/**
 * A Station is somewhere where tasks can be performed.
 * @author Robin Gronberg
 *
 */
public class Station {
	private final int id;
	private final String name;
	
	/**
	 * Create a new {@link Station}
	 * @param id The id of the {@link Station}
	 * @param name The name of the {@link Station}
	 */
	public Station(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		else if(o == null || o.getClass() != this.getClass()){
			return false;
		}else{
			Station s = (Station)o;
			return s.id == id && s.getName().equals(getName());
		}
	}
	
	@Override
	public int hashCode() {
		return 67 * id + 83 * name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}
}

