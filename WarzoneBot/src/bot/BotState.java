// class with information about the state of the game

package bot;
import java.util.ArrayList;
import main.Map;
import main.Region;
import main.SuperRegion;
import move.AttackTransferMove;
import move.PlaceArmiesMove;
import move.Move;
public class BotState {	
	private String myName = "";
	private String opponentName = "";	
	private final Map fullMap = new Map(); 
	// map visible to the AI
	private Map visibleMap;
	// territories available to pick during distribution round
	private ArrayList<Region> pickableStartingRegions; 	
	private ArrayList<Move> opponentMoves; 
	// number of armies on each starting territory
	private int startingArmies; 
	private int roundNumber;	
	// if boolean has 1 or more territories in a bonus
	private ArrayList<Boolean> hasPresenseInSuperRegion = new ArrayList<Boolean>(); 
	
	public BotState() {
		pickableStartingRegions = new ArrayList<Region>();
		opponentMoves = new ArrayList<Move>();
		roundNumber = 0; }
	
	public void updateSettings(String key, String value) {
		if(key.equals("your_bot")) 
			myName = value;
		else if(key.equals("opponent_bot")) 
			opponentName = value;
		else if(key.equals("starting_armies")) {
			startingArmies = Integer.parseInt(value);
			roundNumber++; }} 
	
	public void setupMap(String[] mapInput) {
		int i, regionId, superRegionId, reward;
		if(mapInput[1].equals("super_regions")) {
			for(i=2; i<mapInput.length; i++) {
				try {
					superRegionId = Integer.parseInt(mapInput[i]);
					i++;
					reward = Integer.parseInt(mapInput[i]);
					fullMap.add(new SuperRegion(superRegionId, reward)); }
				catch(Exception e) {
					System.err.println("Unable to parse SuperRegions"); }}}
		else if(mapInput[1].equals("regions")){
			for(i=2; i<mapInput.length; i++) {
				try {
					regionId = Integer.parseInt(mapInput[i]);
					i++;
					superRegionId = Integer.parseInt(mapInput[i]);
					SuperRegion superRegion = fullMap.getSuperRegion(superRegionId);
					fullMap.add(new Region(regionId, superRegion)); }
				catch(Exception e) {
					System.err.println("Unable to parse Regions " + e.getMessage()); }}}
		else if(mapInput[1].equals("neighbors")) {
			for(i=2; i<mapInput.length; i++) {
				try {
					Region region = fullMap.getRegion(Integer.parseInt(mapInput[i]));
					i++;
					String[] neighborIds = mapInput[i].split(",");
					for(int j=0; j<neighborIds.length; j++) {
						Region neighbor = fullMap.getRegion(Integer.parseInt(neighborIds[j]));
						region.addNeighbor(neighbor); }}
				catch(Exception e) {
					System.err.println("Unable to parse Neighbors " + e.getMessage()); }}}}
	
	public void setPickableStartingRegions(String[] mapInput) {
		for(int i=2; i < mapInput.length; i++) {
			int regionId;
			try {
				regionId = Integer.parseInt(mapInput[i]);
				Region pickableRegion = fullMap.getRegion(regionId);
				pickableStartingRegions.add(pickableRegion); }
			catch(Exception e) {
				System.err.println("Unable to parse pickable regions " + e.getMessage()); }}}
	
	public void updateMap(String[] mapInput) {
		visibleMap = fullMap.getMapCopy();
		for(int i=1; i<mapInput.length; i++) {
			try {
				Region region = visibleMap.getRegion(Integer.parseInt(mapInput[i]));
				String playerName = mapInput[i+1];
				int armies = Integer.parseInt(mapInput[i+2]);
				region.setPlayerName(playerName);
				region.setArmies(armies);
				i += 2; }
			catch(Exception e) {
				System.err.println("Unable to parse Map Update " + e.getMessage()); }}
		ArrayList<Region> unknownRegions = new ArrayList<Region>();
		for(Region region : visibleMap.regions)
			if(region.getPlayerName().equals("unknown"))
				unknownRegions.add(region);
		for(Region unknownRegion : unknownRegions)
			visibleMap.getRegions().remove(unknownRegion); }
	
	public void readOpponentMoves(String[] moveInput) {
		opponentMoves.clear();
		for(int i=1; i<moveInput.length; i++) {
			try {
				Move move;
				if(moveInput[i+1].equals("place_armies")) {
					Region region = visibleMap.getRegion(Integer.parseInt(moveInput[i+2]));
					String playerName = moveInput[i];
					int armies = Integer.parseInt(moveInput[i+3]);
					move = new PlaceArmiesMove(playerName, region, armies);
					i += 3; }
				else if(moveInput[i+1].equals("attack/transfer")) {
					Region fromRegion = visibleMap.getRegion(Integer.parseInt(moveInput[i+2]));
					if(fromRegion == null) //might happen if the region isn't visible
						fromRegion = fullMap.getRegion(Integer.parseInt(moveInput[i+2]));
					Region toRegion = visibleMap.getRegion(Integer.parseInt(moveInput[i+3]));
					if(toRegion == null) //might happen if the region isn't visible
						toRegion = fullMap.getRegion(Integer.parseInt(moveInput[i+3]));
					String playerName = moveInput[i];
					int armies = Integer.parseInt(moveInput[i+4]);
					move = new AttackTransferMove(playerName, fromRegion, toRegion, armies);
					i += 4; }
				else { //never happens
					continue; }
				opponentMoves.add(move); }
			catch(Exception e) {
				System.err.println("Unable to parse Opponent moves " + e.getMessage()); }}}
	
	public String getMyPlayerName(){
		return myName; }
	
	public String getOpponentPlayerName(){
		return opponentName; }
	
	public int getStartingArmies(){
		return startingArmies; }
	
	public int getRoundNumber(){
		return roundNumber; }
	
	public Map getVisibleMap(){
		return visibleMap; }
	
	public Map getFullMap(){
		return fullMap; }
	
	public ArrayList<Move> getOpponentMoves(){
		return opponentMoves; }
	
	public ArrayList<Region> getPickableStartingRegions(){
		return pickableStartingRegions; }
	
	public ArrayList<Boolean> getPresenseInSuperRegion(){
		return hasPresenseInSuperRegion; }	
	
	public ArrayList<Region> getMyRegions(){
		ArrayList<Region> myRegions = new ArrayList<Region>();
		for (int i = 0; i < getVisibleMap().regions.size(); i++){
			if (getVisibleMap().regions.get(i).ownedByPlayer(myName))
				myRegions.add(getVisibleMap().regions.get(i)); }
		return myRegions; }	
	
	public ArrayList<Region> getBordersNeutralsRegions(){
		ArrayList<Region> bordersNeutralsRegions = new ArrayList<Region>();
		ArrayList<Region> myRegions = getMyRegions();
		for (Region fromRegion : myRegions){							
			int numberOfEnemyNeighbors = 0;
			for (Region neighborRegion : fromRegion.getNeighbors()){
				if (neighborRegion.getPlayerName().equals("neutral"))
					numberOfEnemyNeighbors++; }
			if (numberOfEnemyNeighbors > 0)
				bordersNeutralsRegions.add(fromRegion); }
		return bordersNeutralsRegions; }
	
	public ArrayList<Integer> getRegionsFromCompletion(){
		hasPresenseInSuperRegion.clear();
		ArrayList<Integer> regionsFromCompletion = new ArrayList<Integer>();
		for (int i = 0; i < getVisibleMap().superRegions.size(); i++){
			ArrayList<Region> regions = getVisibleMap().superRegions.get(i).getSubRegions();
			int numberOfUntakenRegions = regions.size();
			for (int j = 0; j < regions.size(); j++){
				if (regions.get(j).ownedByPlayer(this.getMyPlayerName())){
					numberOfUntakenRegions--; }}
			regionsFromCompletion.add(numberOfUntakenRegions);
			if (numberOfUntakenRegions == regions.size())
				hasPresenseInSuperRegion.add(false);
			else
				hasPresenseInSuperRegion.add(true); }
		return regionsFromCompletion; }}