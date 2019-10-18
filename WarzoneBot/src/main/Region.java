// class for a territory on the map

package main;
import java.util.ArrayList;

public class Region {
	private int id;
	private ArrayList<Region> neighbors;			
	private SuperRegion superRegion;
	private int armies;
	private String playerName;
	
	public Region(int id, SuperRegion superRegion){
		this.id = id;
		this.superRegion = superRegion;
		this.neighbors = new ArrayList<Region>();
		this.playerName = "unknown";
		this.armies = 0;
		superRegion.addSubRegion(this); }
	public Region(int id, SuperRegion superRegion, String playerName, int armies){
		this.id = id;
		this.superRegion = superRegion;
		this.neighbors = new ArrayList<Region>();
		this.playerName = playerName;
		this.armies = armies;
		superRegion.addSubRegion(this); }
	public void addNeighbor(Region neighbor){
		if(!neighbors.contains(neighbor)){
			neighbors.add(neighbor);
			neighbor.addNeighbor(this); }}
	public boolean isNeighbor(Region region){
		if(neighbors.contains(region))
			return true;
		return false; }
	public boolean ownedByPlayer(String playerName){
		if(playerName.equals(this.playerName))
			return true;
		return false; }
	public void setArmies(int armies){
		this.armies = armies; }
	public void setPlayerName(String playerName){
		this.playerName = playerName; }
	public int getId(){
		return id; }
	public ArrayList<Region> getNeighbors(){
		return neighbors; }
	public SuperRegion getSuperRegion(){
		return superRegion; }
	public int getArmies(){
		return armies; }
	public String getPlayerName(){
			return playerName; }
	public boolean equals(Region r){
		if (this.getId() == r.getId())
			return true;
		return false; }}