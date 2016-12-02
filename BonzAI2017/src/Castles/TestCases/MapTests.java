package Castles.TestCases;

import static org.junit.Assert.*;

import org.junit.Test;
import Castles.*;
import Castles.api.*;
import Castles.util.*;
import Castles.util.linkedlist.*;
import Castles.Objects.*;

public class MapTests {
	
	CastlesMap m;
	
	
	private void setUp(){
		m = new CastlesMap();
		m.setField("Max Num Players", "2");
		m.setField("name", "firstblood");
		m.setField("theme", "desert");
		m.setField("size", "25, 25");
	}

	@Test
	public void fieldTest() {
		setUp();
		assertEquals("firstblood", m.getField("name"));
		assertEquals("desert", m.getField("theme"));
		String s = m.getField("size");
		int height = Integer.parseInt(s.split(" ")[0]);
		int width = Integer.parseInt(s.split(" ")[1]);
		assertEquals(25, height);
		assertEquals(25, width);
		assertEquals("2", m.getField("Max Num Players"));
		
	}
	
	@Test
	public void addPlayersTest(){
		setUp();
		//adding players to the map
		m.addPlayer(0, 24, "P0");
		m.addPlayer(24, 0, "P1");
		DualLinkList<Building> ll = m.getCastles();
		
		//verify players are inserted
		boolean p0 = false;
		boolean p1 = false;
		for(Building b: ll){
			if(b.getName().equals("P0")){
				p0 = true;
			}
			if(b.getName().equals("P1")){
				p1 = true;
			}
		}
		assertEquals(true, p0);
		assertEquals(true, p1);
		
		//check that players have the correct colors
		assertEquals(Castles.api.Color.values()[0], ((Castle)m.getEntity("P0")).getColor());
		assertEquals(Castles.api.Color.values()[1], ((Castle)m.getEntity("P1")).getColor());
		
		
	}
	
	@Test
	public void addBuildingsTest(){
		setUp();
		
		//test adding buildings
		m.addPlayer(0, 24, "P0");
		m.addPlayer(24, 0, "P1");
		m.addCastle(12, 11, "C0");
		m.addVillage(0, 0, "V0");
		m.addVillage(0, 0, "V1");
		
		//makes sure buildings were actually inserted
		RallyPoint[] r = new RallyPoint[5];
		r[0] = m.getEntity("P0");
		r[1] = m.getEntity("P1");
		r[2] = m.getEntity("C0");
		r[3] = m.getEntity("V0");
		r[4] = m.getEntity("V1");
		for(int i = 0; i< r.length; i++){
			assertNotEquals("Building " + i + " didnt' initialize correctly",null, r[i]);
		}
	}
	
	@Test
	public void pathTest(){
		setUp();
		
		/*
		 * <0, 11, R0>
    	 * <12, 0, R1>
    	 * <12, 24, R2>
    	 * <24, 11, R3>
		 */
		
		//populate map with buildings
		m.addPlayer(0, 24, "P0");
		m.addPlayer(24, 0, "P1");
		m.addCastle(12, 11, "C0");
		m.addVillage(0, 0, "V0");			
		m.addVillage(0, 0, "V1");
		m.addRally(0, 11, "R0");
		m.addRally(12, 0, "R1");
		m.addRally(12, 24, "R2");
		m.addRally(24, 11, "R3");
		
		//add some paths
		m.connect("V0", "R0", 10);
		m.connect("R0", "P0", 12);
		assertEquals(true, m.isAdjecent("P0", "R0"));
		assertEquals(true, m.isAdjecent("R0", "V0"));
		assertEquals(false, m.isAdjecent("R1", "P0"));
		assertEquals(false, m.isAdjecent("D69", "V1"));
		
	}
	/**
	 * Used to find errors in cloning the map
	 */
	@Test
	public void mapCloneTest(){
		setUp();
		
		m.addPlayer(0, 24, "P0");
		m.addPlayer(24, 0, "P1");
		m.addCastle(12, 11, "C0");
		m.addVillage(0, 0, "V0");			
		m.addVillage(0, 0, "V1");
		m.addRally(0, 11, "R0");
		m.addRally(12, 0, "R1");
		m.addRally(12, 24, "R2");
		m.addRally(24, 11, "R3");
		// add some paths
		m.connect("V0", "R0", 10);
		m.connect("R0", "P0", 12);
		
		CastlesMap clone = new CastlesMap(m);
		
		
		//verify fields are copied
		assertEquals("firstblood", clone.getField("name"));
		assertEquals("desert", clone.getField("theme"));
		String s = clone.getField("size");
		int height = Integer.parseInt(s.split(" ")[0]);
		int width = Integer.parseInt(s.split(" ")[1]);
		assertEquals(25, height);
		assertEquals(25, width);
		assertEquals("2", clone.getField("Max Num Players"));
		
		
		DualLinkList<Building> ll = clone.getCastles();

		// verify players are inserted
		boolean p0 = false;
		boolean p1 = false;
		for (Building b : ll) {
			if (b.getName().equals("P0")) {
				p0 = true;
			}
			if (b.getName().equals("P1")) {
				p1 = true;
			}
		}
		assertEquals(true, p0);
		assertEquals(true, p1);

		// check that players have the correct colors
		assertEquals(Castles.api.Color.values()[0], ((Castle) clone.getEntity("P0")).getColor());
		assertEquals(Castles.api.Color.values()[1], ((Castle) clone.getEntity("P1")).getColor());
		
		// makes sure buildings were actually inserted
		RallyPoint[] r = new RallyPoint[5];
		r[0] = clone.getEntity("P0");
		r[1] = clone.getEntity("P1");
		r[2] = clone.getEntity("C0");
		r[3] = clone.getEntity("V0");
		r[4] = clone.getEntity("V1");
		for (int i = 0; i < r.length; i++) {
			assertNotEquals("Building " + i + " didnt' initialize correctly", null, r[i]);
		}
		//check adjecentcies
		assertEquals(true, clone.isAdjecent("P0", "R0"));
		assertEquals(true, clone.isAdjecent("R0", "V0"));
		assertEquals(false, clone.isAdjecent("R1", "P0"));
		assertEquals(false, clone.isAdjecent("D69", "V1"));
		
		
	}

}
