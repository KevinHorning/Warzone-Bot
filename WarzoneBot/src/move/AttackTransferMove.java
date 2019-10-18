// class that defines an attack/transfer order

package move;
import main.Region;

public class AttackTransferMove extends Move {
	
	private Region fromRegion;
	private Region toRegion;
	private int armies;
	
	public AttackTransferMove(String playerName, Region fromRegion, Region toRegion, int armies){
		super.setPlayerName(playerName);
		this.fromRegion = fromRegion;
		this.toRegion = toRegion;
		this.armies = armies; }
	public void setArmies(int n){
		armies = n; }
	public Region getFromRegion(){
		return fromRegion; }
	public Region getToRegion(){
		return toRegion; }
	public int getArmies(){
		return armies; }
	public String getString(){
		if(getIllegalMove().equals("")){
			return getPlayerName() + " attack/transfer " + fromRegion.getId() + " " + toRegion.getId() + " " + armies; }
		else
			return getPlayerName() + " illegal_move " + getIllegalMove(); }}