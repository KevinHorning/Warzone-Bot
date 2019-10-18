// class for a bonus on the map

package main;
import java.util.ArrayList;

public class SuperRegion {
	private int id;
	private int armiesReward;
	private ArrayList<Region> subRegions;
	
	public SuperRegion(int id, int armiesReward){
		this.id = id;
		this.armiesReward = armiesReward;
		subRegions = new ArrayList<Region>(); }
	public void addSubRegion(Region subRegion){
		if(!subRegions.contains(subRegion))
			subRegions.add(subRegion); }
	public String ownedByPlayer(){
		String playerName = subRegions.get(0).getPlayerName();
		for(Region region : subRegions){
			if (!playerName.equals(region.getPlayerName()))
				return null; }
		return playerName; }
	public int getId(){
		return id; }
	public int getArmiesReward(){
		return armiesReward; }
	public ArrayList<Region> getSubRegions() {
		return subRegions; }}