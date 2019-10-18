// class for initializing the map the AI plays on

package main;

import java.util.ArrayList;

public class Map {
	// map is made up of lists of territories (regions) and bonuses (superRegions)
	public ArrayList<Region> regions;
	public ArrayList<SuperRegion> superRegions;
	
	public Map(){
		this.regions = new ArrayList<Region>();
		this.superRegions = new ArrayList<SuperRegion>(); }
	
	public Map(ArrayList<Region> regions, ArrayList<SuperRegion> superRegions){
		this.regions = regions;
		this.superRegions = superRegions; }	
	
	public void add(Region region){
		for(Region r : regions)
			if(r.getId() == region.getId()){
				System.err.println("Region cannot be added: id already exists.");
				return; }
		regions.add(region); }
	
	public void add(SuperRegion superRegion){
		for(SuperRegion s : superRegions)
			if(s.getId() == superRegion.getId()){
				System.err.println("SuperRegion cannot be added: id already exists.");
				return; }
		superRegions.add(superRegion); }
	
	public Map getMapCopy() {
		Map newMap = new Map();
		for(SuperRegion sr : superRegions){
			SuperRegion newSuperRegion = new SuperRegion(sr.getId(), sr.getArmiesReward());
			newMap.add(newSuperRegion); }
		for(Region r : regions){
			Region newRegion = new Region(r.getId(), newMap.getSuperRegion(r.getSuperRegion().getId()), r.getPlayerName(), r.getArmies());
			newMap.add(newRegion); }
		for(Region r : regions){
			Region newRegion = newMap.getRegion(r.getId());
			for(Region neighbor : r.getNeighbors())
				newRegion.addNeighbor(newMap.getRegion(neighbor.getId())); }
		return newMap; }
	
	public ArrayList<Region> getRegions() {
		return regions; }
	public ArrayList<SuperRegion> getSuperRegions() {
		return superRegions; }
	public Region getRegion(int id){
		for(Region region : regions)
			if(region.getId() == id)
				return region;
		return null; }
	
	public SuperRegion getSuperRegion(int id){
		for(SuperRegion superRegion : superRegions)
			if(superRegion.getId() == id)
				return superRegion;
		return null; }
	
	public String getMapString(){
		String mapString = "";
		for(Region region : regions){
			mapString = mapString.concat(region.getId() + ";" + region.getPlayerName() + ";" + region.getArmies() + " "); }
		return mapString; }}