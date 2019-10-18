// class that defines an army deployment move

package move;
import main.Region;

public class PlaceArmiesMove extends Move {
	private Region region;
	private int armies;
	
	public PlaceArmiesMove(String playerName, Region region, int armies){
		super.setPlayerName(playerName);
		this.region = region;
		this.armies = armies; }
	public void setArmies(int n){
		armies = n; }
	public Region getRegion(){
		return region; }
	public int getArmies(){
		return armies; }
	public String getString(){
		if(getIllegalMove().equals(""))
			return getPlayerName() + " place_armies " + region.getId() + " " + armies;
		else
			return getPlayerName() + " illegal_move " + getIllegalMove(); }}