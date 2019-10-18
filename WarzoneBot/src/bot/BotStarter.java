// class of all of the AIs decision making

package bot;
import java.io.*;
import java.io.FileNotFoundException;

/**
 * This is a simple bot that does random (but correct) moves.
 * This class implements the Bot interface and overrides its Move methods.
 * You can implements these methods yourself very easily now,
 * since you can retrieve all information about the match from variable 
 * When the bot decided on the move to make, it returns an ArrayList of Moves. 
 * The bot is started by creating a Parser to which you add
 * a new instance of your bot, and then the parser is started.
 */

import java.util.ArrayList;

import main.Region;
import main.SuperRegion;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

public class BotStarter {
	ArrayList<Integer> regionsFromCompletion = new ArrayList<Integer>();
	ArrayList<Boolean> hasPresense = new ArrayList<Boolean>();
	ArrayList<SuperRegion> superRegions = new ArrayList<SuperRegion>();
	ArrayList<Region> bestPath = new ArrayList<Region>();
	ArrayList<Region> bordersEnemyRegions = new ArrayList<Region>();
	ArrayList<Region> bordersNeutralsRegions = new ArrayList<Region>();
	
	public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut){
		ArrayList<Region> preferredStartingRegions = new ArrayList<Region>();
		ArrayList<Integer> continentPreference = new ArrayList<Integer>();
		
		for (int i = 0; i < 12; i++){
			if (state.getPickableStartingRegions().get(i).getId() < 10)
				continentPreference.add(5);
			else if (state.getPickableStartingRegions().get(i).getId() >= 10 && state.getPickableStartingRegions().get(i).getId() < 14)
				continentPreference.add(2);
			else if (state.getPickableStartingRegions().get(i).getId() >= 14 && state.getPickableStartingRegions().get(i).getId() < 21)
				continentPreference.add(4);
			else if (state.getPickableStartingRegions().get(i).getId() >= 21 && state.getPickableStartingRegions().get(i).getId() < 27)
				continentPreference.add(3);
			else if (state.getPickableStartingRegions().get(i).getId() >= 27 && state.getPickableStartingRegions().get(i).getId() < 39)
				continentPreference.add(6);
			else
				continentPreference.add(1); }
		
		for (int i = 1; i < 7; i++){
			for (int j = 0; j < 12; j++){
				if (preferredStartingRegions.size() >= 6)
					continue;
				if (continentPreference.get(j) == i){
					preferredStartingRegions.add(state.getPickableStartingRegions().get(j)); }}}
		return preferredStartingRegions; }
	
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {
		ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		ArrayList<Region> myRegions = state.getMyRegions();
		String myName = state.getMyPlayerName();
		regionsFromCompletion = state.getRegionsFromCompletion();
		hasPresense = state.getPresenseInSuperRegion();
		for (int i = 0; i < myRegions.size() && placeArmiesMoves.size() == 0; i++){								//looks for region next to enemy
			int myRegionID = myRegions.get(i).getId();
			Region myRegion = state.getVisibleMap().getRegion(myRegionID);
			ArrayList<Region> neighbors = myRegion.getNeighbors();
			for (int j = 0; j < neighbors.size() && placeArmiesMoves.size() == 0; j++){
				if (neighbors.get(j).ownedByPlayer(state.getOpponentPlayerName())){
					placeArmiesMoves.add(new PlaceArmiesMove(myName, myRegion, state.getStartingArmies()));
					myRegion.setArmies(myRegion.getArmies() + state.getStartingArmies()); }}}
		if (placeArmiesMoves.size() == 1)
			return placeArmiesMoves;	
		else {																									//if none, finds best region for bonus completion
			Region bestRegion = findPlacementRegion(state);
			placeArmiesMoves.add(new PlaceArmiesMove(myName, bestRegion, state.getStartingArmies()));
			state.getVisibleMap().getRegion(bestRegion.getId()).setArmies(bestRegion.getArmies() + state.getStartingArmies()); }
		return placeArmiesMoves; }
	
	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
//		long startTime = 0;
//		long endTime = 0;
//		if (state.getRoundNumber() == 13){
//			startTime = System.currentTimeMillis();
//		}
		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
		ArrayList<Region> myRegions = state.getMyRegions();
		bordersEnemyRegions = new ArrayList<Region>();
		bordersNeutralsRegions = state.getBordersNeutralsRegions();
		String myName = state.getMyPlayerName();
		
		for (Region fromRegion : myRegions){							//attacks on enemy
			int numberOfEnemyNeighbors = 0;
			for (Region neighborRegion : fromRegion.getNeighbors()){
				if (neighborRegion.ownedByPlayer(state.getOpponentPlayerName()))
					numberOfEnemyNeighbors++; }
			if (numberOfEnemyNeighbors > 0){
				bordersEnemyRegions.add(fromRegion);
				for (Region toRegion : fromRegion.getNeighbors()){
					if (toRegion.ownedByPlayer(state.getOpponentPlayerName())){
						int defenders = toRegion.getArmies();
						int minimumAttackers = minimumAttackersNeeded(fromRegion.getArmies() - 1, defenders);
						if (minimumAttackers > 0){
							if (numberOfEnemyNeighbors == 1){
								attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, fromRegion.getArmies() - 1));
								state.getVisibleMap().getRegion(fromRegion.getId()).setArmies(1); }
							else{
								attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, minimumAttackers));
								state.getVisibleMap().getRegion(fromRegion.getId()).setArmies(fromRegion.getArmies() - minimumAttackers); }}}}}}
		for (Region fromRegion: myRegions){								//attacks on neutrals
			boolean isEnemyBorderRegion = false;
			for (Region enemyBorderRegion : bordersEnemyRegions){
				if (fromRegion.getId() == enemyBorderRegion.getId())
					isEnemyBorderRegion = true; }
			if (!isEnemyBorderRegion){				
				ArrayList<Region> neutralNeighbors = new ArrayList<Region>();
				for (Region toRegion : fromRegion.getNeighbors()){
					if (toRegion.getPlayerName().equals("neutral")){
						bordersNeutralsRegions.add(fromRegion);
						neutralNeighbors.add(toRegion); }}
				while (fromRegion.getArmies() > 3 && neutralNeighbors.size() > 0){
					if (neutralNeighbors.size() == 1){
						attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, neutralNeighbors.get(0), fromRegion.getArmies() - 1));
						state.getVisibleMap().getRegion(fromRegion.getId()).setArmies(1); 
						continue; }
					ArrayList<Integer> neutralsSuperRegionCompletion = new ArrayList<Integer>();
					for (int i = 0; i < neutralNeighbors.size(); i++)
						neutralsSuperRegionCompletion.add(regionsFromCompletion.get(neutralNeighbors.get(i).getSuperRegion().getId() - 1));
					int mostComplete = 9999;
					Region bestNeutral = null;
					int bestNeutralIndex = 0;
					for (int i = 0; i < neutralsSuperRegionCompletion.size(); i++){
						if (neutralsSuperRegionCompletion.get(i) < mostComplete){
							mostComplete = neutralsSuperRegionCompletion.get(i);
							bestNeutral = neutralNeighbors.get(i);
							bestNeutralIndex = i; }}
					neutralNeighbors.remove(bestNeutralIndex);
					neutralsSuperRegionCompletion.remove(bestNeutralIndex);
					attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, bestNeutral, 4));
					state.getVisibleMap().getRegion(fromRegion.getId()).setArmies(fromRegion.getArmies() - 4); 		
					state.getVisibleMap().getRegion(bestNeutral.getId()).setPlayerName(myName); }
				if (fromRegion.getArmies() == 3 && neutralNeighbors.size() == 1 && neutralNeighbors.get(0).getArmies() == 1){
					attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, neutralNeighbors.get(0), 2)); }}}
		for (Region fromRegion : myRegions){							//transfers
			boolean isEnemyBorderRegion = false;
			boolean isNeutralBorderRegion = false;
			for (Region enemyBorderRegion : bordersEnemyRegions){
				if (fromRegion.getId() == enemyBorderRegion.getId())
					isEnemyBorderRegion = true; }
			for (Region neutralBorderRegion : bordersNeutralsRegions){
				if (fromRegion.getId() == neutralBorderRegion.getId())
					isNeutralBorderRegion = true; }
			if (!isEnemyBorderRegion && !isNeutralBorderRegion && fromRegion.getArmies() > 1){
				ArrayList<Region> path = new ArrayList<Region>();
				for (int i = 0; i < 1000; i++){
					bestPath.add(fromRegion); }
				findPathsToEnemy(fromRegion, fromRegion, path, state);
				if (bestPath.size() < 1000){
					attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, bestPath.get(1), fromRegion.getArmies() - 1)); }
				else {
					findPathsToNeutrals(fromRegion, fromRegion, path, state);
					attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, bestPath.get(1), fromRegion.getArmies() - 1)); }}}
		regionsFromCompletion.clear();
//		if (state.getRoundNumber() == 13){
//			endTime = System.currentTimeMillis();
//			System.out.println("time: " + (endTime - startTime));
//		}
		return attackTransferMoves; 
	}
	
	public int minimumAttackersNeeded(int availableAttackers, int defenders){
		boolean hasEnough = false;
		if (availableAttackers >= defenders * 2 - 1){
			if (defenders == 1 && (availableAttackers >= 2) )
				return 2;
			if (defenders == 2 && (availableAttackers >= 4) )
				return 4;
			hasEnough = true; }
		if (hasEnough)
			return defenders * 2;
		return 0; }
	
	public Region findPlacementRegion(BotState state){
		superRegions = state.getVisibleMap().superRegions;
		int fewestRegionsLeft = 9999999;
		int mostCompleteSuperRegionID = 0;
		for (int i = 0; i < superRegions.size(); i++){
			if (hasPresense.get(i) && regionsFromCompletion.get(i) != 0 && regionsFromCompletion.get(i) < fewestRegionsLeft){
				fewestRegionsLeft = regionsFromCompletion.get(i);
				mostCompleteSuperRegionID = i + 1; }}
		if (mostCompleteSuperRegionID != 0){
			ArrayList<Region> regions = superRegions.get(mostCompleteSuperRegionID - 1).getSubRegions();
			ArrayList<Integer> numbersOfNeutralNeighbors = new ArrayList<Integer>();
			for (int i = 0; i < regions.size(); i++){
				int neutralNeighbors = 0;
				for (int j = 0; j < regions.get(i).getNeighbors().size(); j++){
					Region neutralNeighbor = regions.get(i).getNeighbors().get(j);
					if (neutralNeighbor.ownedByPlayer("neutral") && neutralNeighbor.getSuperRegion().getId() == mostCompleteSuperRegionID)
						neutralNeighbors++; }
				numbersOfNeutralNeighbors.add(neutralNeighbors); }
			int mostNeutralNeighbors = 0; 
			int mostNeutralNeighborsID = 0;
			for (int i = 0; i < regions.size(); i++){
				if (regions.get(i).ownedByPlayer(state.getMyPlayerName()) && numbersOfNeutralNeighbors.get(i) > mostNeutralNeighbors){
					mostNeutralNeighbors = numbersOfNeutralNeighbors.get(i);
					mostNeutralNeighborsID = superRegions.get(mostCompleteSuperRegionID - 1).getSubRegions().get(i).getId(); }}
			return state.getVisibleMap().getRegion(mostNeutralNeighborsID); }
		else {
			SuperRegion fewestNeutralsSuperRegion = new SuperRegion(7, 7);
			for (int i = 0; i  < 15; i++){
				Region fakeRegion = new Region(i, fewestNeutralsSuperRegion);
				fewestNeutralsSuperRegion.addSubRegion(fakeRegion); }
			Region nextToBestSuperRegion = null;
			for (Region fromRegion : state.getBordersNeutralsRegions()){
				for (Region neighbor : fromRegion.getNeighbors()){
					if (neighbor.getPlayerName().equals("neutral")){
						if (neighbor.getSuperRegion().getSubRegions().size() < fewestNeutralsSuperRegion.getSubRegions().size()){
							fewestNeutralsSuperRegion = neighbor.getSuperRegion();
							nextToBestSuperRegion = fromRegion; }}}}
			return nextToBestSuperRegion; }}
	
	public void findPathsToEnemy(Region startingRegion, Region currentRegion, ArrayList<Region> path, BotState state){
		path.add(currentRegion);
		ArrayList<Region> neighbors = currentRegion.getNeighbors();
		for (Region neighbor : neighbors){
			if (path.indexOf(currentRegion) != path.size() - 1){
				int numberToDelete = path.size() - 1 - path.indexOf(currentRegion);
				for (int i = 0; i < numberToDelete; i++){
					path.remove(path.size() - 1); }}
			if (neighbor.getPlayerName().equals(state.getOpponentPlayerName())){
				if (path.size() < bestPath.size()){
					bestPath = (ArrayList<Region>) path.clone(); }}
			if (neighbor.getPlayerName().equals(state.getMyPlayerName())){
				boolean isOnPath = false;
				for (Region pathRegion : path){
					if (neighbor.getId() == pathRegion.getId()){
						isOnPath = true; }}
				if (!isOnPath){
					findPathsToEnemy(startingRegion, neighbor, path, state); }}}}
	
	public void findPathsToNeutrals(Region startingRegion, Region currentRegion, ArrayList<Region> path, BotState state){
		path.add(currentRegion);
		ArrayList<Region> neighbors = currentRegion.getNeighbors();
		for (Region neighbor : neighbors){
			if (path.indexOf(currentRegion) != path.size() - 1){
				int numberToDelete = path.size() - 1 - path.indexOf(currentRegion);
				for (int i = 0; i < numberToDelete; i++){
					path.remove(path.size() - 1); }}
			if (neighbor.getPlayerName().equals("neutral")){
				if (path.size() < bestPath.size()){
					bestPath = (ArrayList<Region>) path.clone(); }}
			if (neighbor.getPlayerName().equals(state.getMyPlayerName())){
				boolean isOnPath = false;
				for (Region pathRegion : path){
					if (neighbor.getId() == pathRegion.getId()){
						isOnPath = true; }}
				if (!isOnPath){
					//System.out.println(currentRegion.getId() + " " + neighbor.getId() + " " + bestPath.size());
					findPathsToNeutrals(startingRegion, neighbor, path, state); }}}}
	
	public static void main(String[] args) throws FileNotFoundException {
		BotParser parser = new BotParser(new BotStarter());
		parser.run(); }}