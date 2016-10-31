package Castles.api;
/*
	Um, Dane I dunno what you did, but this class does not recognize JUnit
	imports or the CastlesMap class.

import static org.junit.Assert.*;

import org.junit.Test;
*/

public class CastlesMapTest {
	/*
	CastlesMap m;
	
	/**
	 * 
	 * Test data
	 * 
# BONZAI CONFIDENTIAL
# This map was auto generated

name: firstblood
size: 25, 25
theme: DEFAULT
playercount: 2

players:
    <0, 24, P0>
    <24, 0, P1>

castles:
    <12, 11, C0>

villages:
    <0, 0, V0>
    <24, 24, V1>

rally:
    <0, 11, R0>
    <12, 0, R1>
    <12, 24, R2>
    <24, 11, R3>

paths:
    <V0, R0, 10>
    <R0, P0, 12>
    <V0, R1, 11>
    <R0, C0, 11>
    <P0, R2, 11>
    <R1, C0, 10>
    <C0, R2, 12>
    <R1, P1, 11>
    <C0, R3, 11>
    <R2, V1, 11>
    <P1, R3, 10>
    <R3, V1, 12>
	 *
	
	
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
		/*setUp();
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
		assertEquals(Castles.api.Color.values()[1], ((Castle)m.getEntity("P1")).getColor());*
		
		
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
		 *
		
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
	*/
}
