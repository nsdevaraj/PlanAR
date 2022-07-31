package com.adams.awcoe.spaceplanar.room.opengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class Group extends FloorMesh {
	private Vector<FloorMesh> children = new Vector<FloorMesh>();
	private Vector<WallMesh> wall_children=new Vector<WallMesh>();
	@Override
	public void draw(GL10 gl) {
		int size = children.size();
		int wall_size=wall_children.size();
		
		for( int i = 0; i < size; i++)
			children.get(i).draw(gl);
		for( int i = 0; i < wall_size; i++)
			wall_children.get(i).draw(gl);
	}

	
	/*  ----------------- FLOOR MESH ------------------- */
	/**
	 * @param location
	 * @param object
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	public void add(int location, FloorMesh object) {
		children.add(location, object);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(FloorMesh object) {
		return children.add(object);
	}

	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		children.clear();
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#get(int)
	 */
	public FloorMesh get(int location) {
		return children.get(location);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public FloorMesh remove(int location) {
		return children.remove(location);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return children.remove(object);
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
/*	public int size() {
		return children.size();
	}*/
	
	
	
	/*  ----------------- WALL MESH ------------------- */
	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add_wall(WallMesh object) {
		return wall_children.add(object);
	}

	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear_wall() {
		wall_children.clear();
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#get(int)
	 */
	public WallMesh get_wall(int location) {
		return wall_children.get(location);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public WallMesh remove_wall(int location) {
		return wall_children.remove(location);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove_wall(Object object) {
		return wall_children.remove(object);
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size_wall() {
		return wall_children.size();
	}
	
}
