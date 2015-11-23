package bots;

import java.awt.Graphics;
import java.awt.Image;
//import java.util.Random;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class Archon extends Bot {

	String name;
	Image up, down, right, left, current;
	boolean stuck = false;
	//largest priority
	private double largest;
	//the move that currently holds the most priority
	private int maxindex;
	//learning variables;
	private int aggression = 1;//a modifier for how aggressive archon shoudl be
	private double searchcone = Math.PI/2;//an angle that deterimes how big the cone of search is for search and destroy
	private double firecone = 0.463647609;//an angle that determines how big cone is for firing
	private double dodgemodifier = 5;
	//
	/**
	 * bulletdown is cooldown for shooting down
	 * bulletup is cooldown for shooting up
	 * bulletleft is cooldown for shooting left
	 * bulletright is cooldown for shooting right
	 */
	private int bulletdown, bulletup, bulletleft, bulletright;
	private double[] ai = {0,0,0,0};
	//private Random rand;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
	private int shootdir;
	//private int dodgethis;
	private Bullet dodgethis;
	/**
	 * My last location - used for detecting when I am stuck
	 */
	private double x, y;
	/**
	 * dodgemode value decides whcih direction the bot is dodging if it is dodging
	 * 1 is x dodge
	 * 2 is y dodge
	 */
	private int dodgemode;
	public Archon() {
		// TODO Auto-generated constructor stub
		bulletdown = bulletup = bulletleft = bulletright = 0;
		//rand = new Random();
		move = (int)(Math.random()*(BattleBotArena.RIGHT+1));
		dodgemode = 0;
	}

	@Override
	public void newRound() {
		bulletdown = bulletup = bulletleft = bulletright = 0;
		//rand = new Random();
		move = (int)(Math.random()*(BattleBotArena.RIGHT+1));
		dodgemode = 0;
		shootdir = move;
	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		//bullet dodging
		/*
		try{
			dodgescript(bullets, me);
		}
		catch (Exception e){
			System.out.println("error is in dodge");
		}
		 */
		//we are going to have another part that overrides the dodge mechanics if bot is stuck.
		/*
		try {
			stuckscript(me);
			x = me.getX();
			y = me.getY();
		}

		catch (Exception e){
			System.out.println("error is in stuck");
		}
		 */
		ai[0]=0;
		ai[1]=0;
		ai[2]=0;
		ai[3]=0;
		//here we will try ai

		try{
			directionalbulletdodge(bullets, me);
		}
		catch (Exception e){
			System.out.println("error is in driectional dodge");
		}
		
		
		try {
			searchanddestroy(liveBots, me, deadBots);
		}
		catch(Exception e){
			System.out.println("error is in search and destroy method");
		}
		
		try{
			avoidDeadBot(me,deadBots);
		}
		catch(Exception e){
			System.out.println("error is in avoidDeadBot");
		}
		try{
			avoidBots(me, liveBots);
		}
		catch(Exception e){
			System.out.println("error in avoid live bots");
		}
		
		
		try {
			if (shotOK){//only if not dodging
				shootdir = -1;
				shootscript(liveBots, me, shotOK);
				if (shootdir != -1){
					return shootdir;
				}
			}
		}
		catch (Exception e){
			System.out.println ("error is in whole shooting");
		}
		

		try {
			if (me.getY()<100){
				ai[0]-=5/Math.abs(me.getY()-RADIUS);
				ai[1]+=5/Math.abs((me.getY()-RADIUS)+40);
			}
			if(me.getY()>400){//me.getY-500. we get negative value. We must be within 100 to care. if <0 then that mean we are above. if >-100 then we are within 100 while approaching from above
				ai[1]-=5/Math.abs(500-me.getY()+RADIUS);
				ai[0]+=5/Math.abs((500-me.getY()+RADIUS)+40);
			}
			if(me.getX()<100){
				ai[2]-=5/Math.abs(me.getX()-RADIUS);
				ai[3]+=5/Math.abs((me.getX()-RADIUS)+40);
			}
			if(me.getX()>600){
				ai[3]-=5/Math.abs(700-me.getX()+RADIUS);
				ai[2]+=5/Math.abs((700-me.getX()+RADIUS)+40);
			}
		}
		catch(Exception e) {
			System.out.println("error is in edge priority");
		}
		

		largest = -Double.MAX_VALUE;
		maxindex = 0;
		for(int i =0;i<ai.length ;i++) {
			//System.out.println(ai[i]>largest);
			if(ai[i]>largest) {
				largest = ai[i];
				maxindex = i;
				//System.out.println(largest+" "+ maxindex);
			}
		}
		//System.out.println(ai[0] + " " + ai[1] + " " + ai[2] + " " + ai[3] + " " + maxindex);
		move = maxindex+1;

		return (maxindex+1);
	}
	
	/* set aggression
     * 	set dodgemodifier
     * 	run rounds and record kills
     * 	after x rounds
     * 	average kills per round and print it out as "aggression n: dodgemodifier m: kills: avg"
     * 	change dodgemodifier
     * 	repeat
     * change aggression
     * repeat
     * print greatest average kills "Greatest aggression n: dodgemodifier m: kills: avg"
	 */
	
	@Override
	public void draw(Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		g.drawImage(current, x, y, RADIUS*2, RADIUS*2, null);
	}

	@Override
	public String getName() {
		name = "Archon";
		return name;
	}

	@Override
	public String getTeamName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String outgoingMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] imageNames() {
		String images[] = {"starfish4.png"};
		return images;
	}

	@Override
	public void loadedImages(Image[] images) {
		// TODO Auto-generated method stub
		if (images != null)
		{
			current = up = images[0];
			down = images[0];
			left = images[0];
			right = images[0];
		}
	}
	
	private void shootscript(BotInfo[] liveBots, BotInfo me, boolean shotOK){
		double dx,adx,dy,ady,angle,d;
		if (bulletdown >0){
			bulletdown --;
		}
		if (bulletright >0){
			bulletright --;
		}
		if (bulletleft >0){
			bulletleft --;
		}
		if (bulletup >0){
			bulletup --;
		}
		for(int i = 0; i < liveBots.length; i ++){
			//this checks the angle between the bot
			dx = me.getX()-liveBots[i].getX();//+ is bot----me, - is me----bot
			adx = Math.abs(dx);
			dy = me.getY()-liveBots[i].getY();
			ady = Math.abs(dy);
			d = ady+adx;
			//System.out.println(ady + " " + adx + " " + dy + " "+dx);
			
			try {
				if(d<200){
					if(adx>ady){
						angle = Math.tan(ady/adx);
						if (angle<=firecone || liveBots[i].getY() + RADIUS > me.getY() && liveBots[i].getY() - RADIUS < me.getY()){//liveBots[i].getY() + RADIUS > me.getY() && liveBots[i].getY() - RADIUS < me.getY()){
							if (dx < 0 && bulletright == 0){
								bulletright = 15;
								shootdir = BattleBotArena.FIRERIGHT;
								//System.out.println("Shooting right at " +liveBots[i].getName()+ " who's distance is = " +dx);
								return;
							}
							else if (dx > 0 && bulletleft == 0) {
								bulletleft = 15;
								shootdir = BattleBotArena.FIRELEFT;
								//System.out.println("Shooting left at " +liveBots[i].getName()+ " who's distance is = " +dx);
								return;
							}
						}
					}
					else {
						angle = Math.tan(adx/ady);
						if (angle <= firecone || liveBots[i].getX() + RADIUS > me.getX() && liveBots[i].getX() - RADIUS < me.getX()){//liveBots[i].getX() + RADIUS > me.getX() && liveBots[i].getX() - RADIUS < me.getX()){
							if (dy < 0 && bulletdown == 0){
								bulletdown = 15;
								shootdir = BattleBotArena.FIREDOWN;
								//System.out.println("Shooting down at " +liveBots[i].getName() + " who's distance is = " +dy);
								return;
							}
							else if (dy > 0 && bulletup == 0) {
								bulletup = 15;
								shootdir = BattleBotArena.FIREUP;
								//System.out.println("Shooting up at " +liveBots[i].getName() + " who's distance is = " +dy);
								return;
							}
						}
					}
				}
			}
			catch(Exception e){
				System.out.print("error is in shootscript");
			}
			
		}
	}
	/**
	 * 
	 * @param liveBots
	 * @param me
	 * @param deadBots
	 */
	private void searchanddestroy(BotInfo[] liveBots, BotInfo me, BotInfo[]deadBots){
		double dx, adx, dy, ady, d, angle;
		double lowest = Double.MAX_VALUE;
		int target = -1;
		//check the manhattan distance if it's more than a certain value that move to line up a shot on that target
		for(int i = 0; i<liveBots.length; i++){
			dx = me.getX()-liveBots[i].getX();//+ is bot----me, - is me----bot
			adx = Math.abs(dx);
			dy = me.getY()-liveBots[i].getY();
			ady = Math.abs(dy);
			try{
				if (adx+ady > 30/aggression){
					if (ady==0 || adx == 0){// if it's already lined up and also to not divide by zero
						return;
					}
					//this is for if vertical distance is greater than horizontal
					else if (Math.tan(adx/ady)<searchcone){//unused right now but it can limit search to a cone
						if (Math.tan(adx/ady)*(adx+ady)<lowest){//find the product of manhattan distance and angle to pick closest target
							lowest = Math.tan(adx/ady)*(adx+ady);
							target = i;
						}
					}
					//this is for horizontal > vertcal
					else if (Math.tan(ady/adx)<searchcone) {
						if (Math.tan(ady/adx)*(adx+ady)<lowest){
							lowest = Math.tan(ady/adx)*(adx+ady);
							target = i;
						}
					}
				}
			}
			catch(Exception e){
				System.out.println("error is in finding tartget");
			}
		}
		if (target !=-1){//if it has a target
			//try{
			dx = me.getX()-liveBots[target].getX();//+ is bot----me, - is me----bot
			adx = Math.abs(dx);
			dy = me.getY()-liveBots[target].getY();
			ady = Math.abs(dy);
			//System.out.println("hunting " + liveBots[target].getName());
			//}
			/*catch (Exception e){
				System.out.println("error is in s&d variable setting part 2" + " || " + target);

				dx = me.getX()-liveBots[target].getX();//+ is bot----me, - is me----bot
				adx = Math.abs(dx);
				dy = me.getY()-liveBots[target].getY();
				ady = Math.abs(dy);
			}
			 */
			try {
				if(ady>adx){// y distance is greater so align by x movement
					angle = Math.tan(adx/ady);//currently not used
					//System.out.println(dx);
					//if snddeadcheck false
					if (snddeadcheck(me, 0, deadBots, liveBots[target])==false){
						if(dx>0){
							ai[2]+=aggression/21.0;
						}
						else if (dx<0) {
							ai[3]+=aggression/21.0;
						}
					}
					else {
						if (dy>0){
							ai[0]+=aggression/21.0;
						}
						else if (dy<0) {
							ai[1]+=aggression/21.0;
						}
					}
				}
				else{
					angle = Math.tan(ady/adx);//currently not used
					//System.out.println(ady + " " + adx + " " + dy + " "+dx);
					if (snddeadcheck(me, 1, deadBots, liveBots[target])){
						if (dy>0){
							ai[0]+=aggression/21.0;
						}
						else if (dy<0) {
							ai[1]+=aggression/21.0;
						}
					}
					else {
						if(dx>0){
							ai[2]+=aggression/21.0;
						}
						else if (dx<0) {
							ai[3]+=aggression/21.0;
						}
					}

				}
				//System.out.println(ai[0]+" "+ai[1]+" "+ai[2]+" "+ai[3]);
			}
			catch(Exception e){
				System.out.println("error is in moving to cone");
			}
		}
	}

	private boolean snddeadcheck(BotInfo me, int direction, BotInfo[]deadBots, BotInfo target){
		double dybot;
		double dy = me.getY()-target.getY();
		double dxbot;
		double dx = me.getX()-target.getX();
		if (direction == 1){//move up and down to align y
			/*
			 * for each dead bot
			 * if there is partial alignment between bot and target
			 * if the deadbot is between
			 */
			for (int i = 0; i<deadBots.length; i++){
				dybot = deadBots[i].getY()-target.getY();
				if (deadBots[i].getX()>target.getX()){
					dxbot = deadBots[i].getX()-RADIUS-target.getX();
				}
				else{
					dxbot = deadBots[i].getX()+RADIUS-target.getX();
				}
				if (deadBots[i].getY()+RADIUS*2 > target.getY() && deadBots[i].getY()-RADIUS*2 < target.getY()//aligned
						&& Math.abs(dx)>Math.abs(dxbot)//archon is further away from target
						&& dx*dxbot > 0){//they have the same direction of displacement (deadbot is between archon and target)
					//if conditions are passed then blocking is true
					return true;
				}
			}
		}
		else{//move left and right to align x
			for (int i = 0; i<deadBots.length; i ++){
				if (deadBots[i].getY()>target.getY()){
					dybot = deadBots[i].getY()-RADIUS-target.getY();
				}
				else{
					dybot = deadBots[i].getY()+RADIUS-target.getY();
				}
				dxbot = deadBots[i].getX()-target.getX();
				if (deadBots[i].getX()+RADIUS*2 > target.getX() && deadBots[i].getX()-RADIUS*2 < target.getX()//aligned
						&& Math.abs(dy)>Math.abs(dybot)//archon is further away from target
						&& dy*dybot > 0){//they have the same direction of displacement (deadbot is between archon and target)
					//if conditions are passed then blocking is true
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Loops through all the deadbots and compares position of the deadbots to the bot
	 * Moves away to a certain extend 
	 * Method only functions if within a manhattan distance of 50
	 */
	 private void avoidBots(BotInfo me, BotInfo [] liveBots){
		double dx,dy,adx,ady,d;
		
		for (int x =0; x<liveBots.length;x++) {
			dx = me.getX() - liveBots[x].getX();
			dy = me.getY() - liveBots[x].getY();
			adx = Math.abs(dx);
			ady = Math.abs(dy);
			d = adx + ady;
			if (d < 50 && liveBots[x].getBulletsLeft() > 0) { // When there is ammo on the enemy
				if (liveBots[x].getX() == me.getX()){//perfect vertical alignment check
					if (dy > 0){
						//ai[1]+=5/(ady+40);//this is not neccessary but may be included
						ai[3]+=dodgemodifier/(ady+30);
						ai[2]+=dodgemodifier/(ady+30);
						ai[0]-=dodgemodifier/(ady+30);
					}
					else {
						ai[3]+=dodgemodifier/(ady+30);
						ai[2]+=dodgemodifier/(ady+30);
						ai[1]-=dodgemodifier/(ady+30);
					}
				}
				else if (liveBots[x].getX()+RADIUS*2 > me.getX() && liveBots[x].getX()-RADIUS*2 < me.getX()){//partial alignment check
					if (dx > 0){
						if (dy>0){
							ai[3]+=dodgemodifier/(ady+30);
							ai[0]-=dodgemodifier/(ady+30);
							ai[2]+=(dodgemodifier/(ady+30))-(dodgemodifier/(Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2)+30));
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
						else{
							ai[3]+=dodgemodifier/(ady+30);
							ai[1]-=dodgemodifier/(ady+30);
							ai[2]+=(dodgemodifier/(ady+30))-(dodgemodifier/(Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2)+30));
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
					}
					else {
						if (dy>0){
							ai[2]+=dodgemodifier/(ady+30);
							ai[0]-=dodgemodifier/(ady+30);
							ai[3]+=(dodgemodifier/(ady+30))-(dodgemodifier/(Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2)+30));
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
						else {
							ai[2]+=dodgemodifier/(ady+30);
							ai[1]-=dodgemodifier/(ady+30);
							ai[3]+=(dodgemodifier/(ady+30))-(dodgemodifier/(Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2)+30));
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
					}
				}

				else if (liveBots[x].getY() == me.getY()){
					if (dx >0){
						ai[0]+=dodgemodifier/(adx+30);
						ai[1]+=dodgemodifier/(adx+30);
						ai[2]-=dodgemodifier/(adx+30);
					}
					else{
						ai[0]+=dodgemodifier/(adx+30);
						ai[1]+=dodgemodifier/(adx+30);
						ai[3]-=dodgemodifier/(adx+30);
					}
				}
				else if (liveBots[x].getY()+RADIUS*2 > me.getY() && liveBots[x].getY()-RADIUS*2 < me.getY()){
					if (dy > 0){//bullet is above me
						if (dx > 0){
							ai[1]+=dodgemodifier/(adx+30);
							ai[2]-=dodgemodifier/(adx+30);
							ai[0]+=(dodgemodifier/(adx+30))-(dodgemodifier/(Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2)+30));
							//ai[0]+=((dodgemodifier/2)/adx+40);//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
						}
						else{
							ai[1]+=dodgemodifier/(adx+30);
							ai[3]-=dodgemodifier/(adx+30);
							ai[0]+=(dodgemodifier/(adx+30))-(dodgemodifier/(Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2)+30));
							//ai[0]+=((dodgemodifier/2)/adx+40);
						}
					}
					else {//bullet is below me
						if (dx>0){
							ai[0]+=dodgemodifier/(adx+30);
							ai[2]-=dodgemodifier/(adx+30);
							ai[1]+=(dodgemodifier/(adx+30))-(dodgemodifier/(Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2)+30));
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
						else {
							ai[0]+=dodgemodifier/(adx+30);
							ai[3]-=dodgemodifier/(adx+30);
							ai[1]+=(dodgemodifier/(adx+30))-(dodgemodifier/(Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2)+30));
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
					}
				}
			}
		}
	}
	 
	private void avoidDeadBot(BotInfo me,BotInfo [] deadBots){
		double dx,dy,adx,ady,d;
		int dodgemodifier = 5;
		
		for (int x =0; x<deadBots.length;x++) {
			dx = me.getX() - deadBots[x].getX();
			dy = me.getY() - deadBots[x].getY();
			adx = Math.abs(dx);
			ady = Math.abs(dy);
			d = adx + ady;
			
			/*
			 * if within manhattan dsitance (adx+ady)
			 * then check horizonta alignment
			 * else check vertical alignment
			 * if aligned then check if above or below (if dy > 0 bot is below dead)
			 * then discourage moving closer
			 * 
			 */
			
			if (d < 50 && deadBots[x].getBulletsLeft() == 0) {
				/*
				 * check if its perfect vertical then partial vertical
				 * then check if its perfect horizontal then partial horizontal
				 */
				if (deadBots[x].getX() == me.getX()){//perfect vertical alignment check
					if (dy > 0){
						ai[1]+=dodgemodifier/(ady+40);//this is not neccessary but may be included
						//ai[3]+=dodgemodifier/(ady+40);
						//ai[2]+=dodgemodifier/(ady+40);
						ai[0]-=dodgemodifier/(ady+40);
					}
					else {
						//ai[3]+=dodgemodifier/(ady+40);
						//ai[2]+=dodgemodifier/(ady+40);
						ai[1]-=dodgemodifier/(ady+40);
						ai[0]+=dodgemodifier/(ady+40);
					}
				}
				else if (deadBots[x].getX()+RADIUS*2 > me.getX() && deadBots[x].getX()-RADIUS*2 < me.getX()){//partial alignment check
					if (dx > 0){//me is right of bullet
						if (dy>0){
							//ai[3]+=aggression/(ady+40);
							ai[0]-=dodgemodifier/(ady+40);
							ai[1]+=dodgemodifier/(ady+40);
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
						else{
							//ai[3]+=aggression/(ady+40);
							ai[1]-=dodgemodifier/(ady+40);
							ai[0]+=dodgemodifier/(ady+40);
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
					}
					else {
						if (dy>0){
							//ai[2]+=aggression/(ady+40);
							ai[0]-=dodgemodifier/(ady+40);
							ai[1]+=dodgemodifier/(ady+40);
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
						else {
							//ai[2]+=aggression/(ady+40);
							ai[1]-=dodgemodifier/(ady+40);
							ai[0]+=dodgemodifier/(ady+40);
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
					}
				}
				
				else if (deadBots[x].getY() == me.getY()){
					if (dx >0){
						//ai[0]+=dodgemodifier/(adx+40);
						//ai[1]+=dodgemodifier/(adx+40);
						ai[2]-=dodgemodifier/(adx+40);
						ai[3]+=dodgemodifier/(adx+40);
					}
					else{
						//ai[0]+=dodgemodifier/(adx+40);
						//ai[1]+=dodgemodifier/(adx+40);
						ai[3]-=dodgemodifier/(adx+40);
						ai[2]+=dodgemodifier/(adx+40);
					}
				}
				else if (deadBots[x].getY()+RADIUS*2 > me.getY() && deadBots[x].getY()-RADIUS*2 < me.getY()){
					if (dy > 0){//bullet is above me
						if (dx > 0){
							//ai[1]+=aggression/(adx+40);
							ai[2]-=dodgemodifier/(adx+40);
							ai[3]+=dodgemodifier/(adx+40);
							//ai[0]+=((dodgemodifier/2)/adx+40);//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
						}
						else{
							//ai[1]+=aggression/(adx+40);
							ai[3]-=dodgemodifier/(adx+40);
							ai[2]+=dodgemodifier/(adx+40);
							//ai[0]+=((dodgemodifier/2)/adx+40);
						}
					}
					else {//bullet is below me
						if (dx>0){
							//ai[0]+=aggression/(adx+40);
							ai[2]-=dodgemodifier/(adx+40);
							ai[3]+=dodgemodifier/(adx+40);
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
						else {
							//ai[0]+=aggression/(adx+40);
							ai[3]-=dodgemodifier/(adx+40);
							ai[2]+=dodgemodifier/(adx+40);
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
					}
				}
			}// ends if condiiton of less than 50
			else { // When there is ammo on the deadbots
				
				if (deadBots[x].getX() == me.getX()){//perfect vertical alignment check
					if (dy > 0){
						//ai[1]+=5/(ady+40);//this is not neccessary but may be included
						//ai[3]+=dodgemodifier/(ady+40);
						//ai[2]+=dodgemodifier/(ady+40);
						ai[0]+=aggression/(ady+40);
					}
					else {
						//ai[3]+=dodgemodifier/(ady+40);
						//ai[2]+=dodgemodifier/(ady+40);
						ai[1]+=aggression/(ady+40);
					}
				}
				else if (deadBots[x].getX()+RADIUS*2 > me.getX() && deadBots[x].getX()-RADIUS*2 < me.getX()){//partial alignment check
					if (dx > 0){
						if (dy>0){
							//ai[3]+=aggression/(ady+40);
							ai[0]+=aggression/(ady+40);
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
						else{
							//ai[3]+=aggression/(ady+40);
							ai[1]+=aggression/(ady+40);
							//ai[2]+=((dodgemodifier/2)/ady+40);//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
						}
					}
					else {
						if (dy>0){
							//ai[2]+=aggression/(ady+40);
							ai[0]+=aggression/(ady+40);
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
						else {
							//ai[2]+=aggression/(ady+40);
							ai[1]+=aggression/(ady+40);
							//ai[3]+=((dodgemodifier/2)/ady+40);//same but right side
						}
					}
				}

				else if (deadBots[x].getY() == me.getY()){
					if (dx >0){
						//ai[0]+=dodgemodifier/(adx+40);
						//ai[1]+=dodgemodifier/(adx+40);
						ai[2]+=aggression/(adx+40);
					}
					else{
						//ai[0]+=dodgemodifier/(adx+40);
						//ai[1]+=dodgemodifier/(adx+40);
						ai[3]+=aggression/(adx+40);
					}
				}
				else if (deadBots[x].getY()+RADIUS*2 > me.getY() && deadBots[x].getY()-RADIUS*2 < me.getY()){
					if (dy > 0){//bullet is above me
						if (dx > 0){
							//ai[1]+=aggression/(adx+40);
							ai[2]+=aggression/(adx+40);
							//ai[0]+=((dodgemodifier/2)/adx+40);//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
						}
						else{
							//ai[1]+=aggression/(adx+40);
							ai[3]+=aggression/(adx+40);
							//ai[0]+=((dodgemodifier/2)/adx+40);
						}
					}
					else {//bullet is below me
						if (dx>0){
							//ai[0]+=aggression/(adx+40);
							ai[2]+=aggression/(adx+40);
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
						else {
							//ai[0]+=aggression/(adx+40);
							ai[3]+=aggression/(adx+40);
							//ai[1]+=((dodgemodifier/2)/adx);//same but down side
						}
					}
				}
				
			}
		}


	}

	private void directionalbulletdodgeT(Bullet[] bullets, BotInfo me) {
		double dx, adx, dy, ady, d;
		for(int i = 0; i<bullets.length; i++){
			dx = me.getX()-bullets[i].getX();
			adx = Math.abs(dx);
			dy = me.getY()-bullets[i].getY();
			ady = Math.abs(dy);
			d = Math.abs(dx)+Math.abs(dy);
			if (d < 100){// only do if manhattan distance is 150 or less
				if(bullets[i].getYSpeed() * (me.getY()+RADIUS-bullets[i].getY()) >0){//if bullet is moving vertically to bot and not past the lowest point of the bot
					if (bullets[i].getYSpeed() > 0){
						if (bullets[i].getX() == me.getX()){//perfect alignment check
							ai[3]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS)/2);
							ai[2]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS)/2);
							ai[0]-=dodgemodifier/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){//partial alignment check
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS-adx)/2);
								ai[0]-=dodgemodifier/ady;
								ai[2]+=(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2))-(1/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS-adx)/2);
								ai[0]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2))-(1/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {//not aligned
							if (dx > 0){//me is right of bullet
								ai[0]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10)+dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[0]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
					else {
						if (bullets[i].getX() == me.getX()){
							ai[3]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS)/2);
							ai[2]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS)/2);
							ai[1]-=dodgemodifier/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS-adx)/2);
								ai[1]-=dodgemodifier/ady;
								ai[2]+=((dodgemodifier/2)/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2))-(1/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS-adx)/2);
								ai[1]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2))-(1/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {
							if (dx > 0){//me is right of bullet/bullet is left of me
								ai[1]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10);//discourage moving closer as y gets closer to a limit but don't move into the bullet
								if((ady-RADIUS)/4>(RADIUS*2)/2){//if the time for bullet to reach y position is greater than time to get out of the way, allow moving through the path of bullet
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{//otherwise don't allow it
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[1]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
				}
				else if (bullets[i].getXSpeed() * (me.getX()+RADIUS-bullets[i].getX()) >0){
					if (bullets[i].getXSpeed()>0){//bullet moving left to right
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS)/2);
							ai[1]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS)/2);
							ai[2]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS-ady)/2);
								ai[2]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2))-(1/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS-ady)/2);
								ai[2]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2))-(1/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[2]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[2]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}
							}
						}
					}
					else {
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS)/2);
							ai[1]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS)/2);
							ai[3]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS-ady)/2);
								ai[3]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2))-(1/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS-ady)/2);
								ai[3]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2))-(1/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[3]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[3]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}		
							}
						}
					}
				}
				//bullet not moving towards bot. since bot is slower, there's no need to worry about it.
			}
		}
	}
	
	private void directionalbulletdodgeunused(Bullet[] bullets, BotInfo me) {
		double dx, adx, dy, ady, d;
		for(int i = 0; i<bullets.length; i++){
			dx = me.getX()-bullets[i].getX();
			adx = Math.abs(dx);
			dy = me.getY()-bullets[i].getY();
			ady = Math.abs(dy);
			d = Math.abs(dx)+Math.abs(dy);
			if (d < 150){// only do if manhattan distance is 150 or less
				if(bullets[i].getYSpeed() * (me.getY()+RADIUS-bullets[i].getY()) >0){//if bullet is moving vertically to bot and not past the lowest point of the bot
					if (bullets[i].getYSpeed() > 0){
						if (bullets[i].getX() == me.getX()){//perfect alignment check
							ai[3]+=dodgemodifier/ady;
							ai[2]+=dodgemodifier/ady;
							ai[0]-=dodgemodifier/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){//partial alignment check
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/ady;
								ai[0]-=dodgemodifier/ady;
								ai[2]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit

							}
							else {
								ai[2]+=dodgemodifier/ady;
								ai[0]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {//not aligned
							if (dx > 0){//me is right of bullet
								ai[0]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10)+dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[0]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
					else {
						if (bullets[i].getX() == me.getX()){
							ai[3]+=dodgemodifier/ady;
							ai[2]+=dodgemodifier/ady;
							ai[1]-=dodgemodifier/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/ady;
								ai[1]-=dodgemodifier/ady;
								ai[2]+=((dodgemodifier/2)/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=dodgemodifier/ady;
								ai[1]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {
							if (dx > 0){//me is right of bullet/bullet is left of me
								ai[1]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10);//discourage moving closer as y gets closer to a limit but don't move into the bullet
								if((ady-RADIUS)/4>(RADIUS*2)/2){//if the time for bullet to reach y position is greater than time to get out of the way, allow moving through the path of bullet
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{//otherwise don't allow it
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[1]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS*2)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
				}
				else if (bullets[i].getXSpeed() * (me.getX()+RADIUS-bullets[i].getX()) >0){
					if (bullets[i].getXSpeed()>0){//bullet moving left to right
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/adx;
							ai[1]+=dodgemodifier/adx;
							ai[2]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/adx;
								ai[2]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/adx;
								ai[2]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[2]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[2]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}
							}
						}
					}
					else {
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/adx;
							ai[1]+=dodgemodifier/adx;
							ai[3]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/adx;
								ai[3]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/adx;
								ai[3]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[3]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[3]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS*2)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}
							}
						}

					}
				}
				//bullet not moving towards bot. since bot is slower, there's no need to worry about it.
			}
		}
	}
	private void directionalbulletdodgereliable(Bullet[] bullets, BotInfo me) {
		double dx, adx, dy, ady, d;
		for(int i = 0; i<bullets.length; i++){
			dx = me.getX()-bullets[i].getX();
			adx = Math.abs(dx);
			dy = me.getY()-bullets[i].getY();
			ady = Math.abs(dy);
			d = Math.abs(dx)+Math.abs(dy);
			if (d < 150){// only do if manhattan distance is 150 or less
				if(bullets[i].getYSpeed() * (me.getY()+RADIUS-bullets[i].getY()) >0){//if bullet is moving vertically to bot and not past the lowest point of the bot
					if (bullets[i].getYSpeed() > 0){
						if (bullets[i].getX() == me.getX()){//perfect alignment check
							ai[3]+=5/ady;
							ai[2]+=5/ady;
							ai[0]-=5/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){//partial alignment check
							if (dx > 0){//me is right of bullet
								ai[3]+=5/ady;
								ai[0]-=5/ady;
								ai[2]+=(5/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=5/ady;
								ai[0]-=5/ady;
								ai[3]+=(5/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//same but right side
							}
						}
						else {//not aligned
							if (dx > 0){//me is right of bullet
								ai[0]-=5/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=3/(adx+10)+3/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=3/(ady+10)+5/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
							}
							else {
								ai[0]-=5/(ady+10);
								ai[2]+=3/(adx+10)+3/(ady+10);
								ai[3]-=3/(ady+10)+5/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
							}
						}
					}
					else {
						if (bullets[i].getX() == me.getX()){
							ai[3]+=5/ady;
							ai[2]+=5/ady;
							ai[1]-=5/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){
							if (dx > 0){//me is right of bullet
								ai[3]+=5/ady;
								ai[1]-=5/ady;
								ai[2]+=(5/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=5/ady;
								ai[1]-=5/ady;
								ai[3]+=(5/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//same but right side
							}
						}
						else {
							if (dx > 0){//me is right of bullet/bullet is left of me
								ai[1]-=5/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=3/(adx+10)+3/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=3/(ady+10)+5/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
							}
							else {
								ai[1]-=5/(ady+10);
								ai[2]+=3/(adx+10)+3/(ady+10);
								ai[3]-=3/(ady+10)+5/(Math.abs(me.getX()-RADIUS+bullets[i].getX()));
							}
						}
					}
				}
				else if (bullets[i].getXSpeed() * (me.getX()+RADIUS-bullets[i].getX()) >0){
					if (bullets[i].getXSpeed()>0){//bullet moving left to right
						if (bullets[i].getY() == me.getY()){
							ai[0]+=5/adx;
							ai[1]+=5/adx;
							ai[2]-=5/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=5/adx;
								ai[2]-=5/adx;
								ai[0]+=(5/adx)-(5/Math.abs((adx-RADIUS)/4-(RADIUS*2-ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=5/adx;
								ai[2]-=5/adx;
								ai[1]+=(5/adx)-(5/Math.abs((adx-RADIUS)/4-(RADIUS*2-ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[2]-=5/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=3/(ady+10)+3/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=3/(adx+10)+5/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
							}
							else {
								ai[2]-=5/(adx+10);
								ai[0]+=3/(ady+10)+3/(adx+10);
								ai[1]-=3/(adx+10)+5/(Math.abs(me.getY()+RADIUS-bullets[i].getX()));
							}
						}
					}
					else {
						if (bullets[i].getY() == me.getY()){
							ai[0]+=5/adx;
							ai[1]+=5/adx;
							ai[3]-=5/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=5/adx;
								ai[3]-=5/adx;
								ai[0]+=(5/adx)-(5/Math.abs((adx-RADIUS)/4-(RADIUS*2-ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=5/adx;
								ai[3]-=5/adx;
								ai[1]+=(5/adx)-(5/Math.abs((adx-RADIUS)/4-(RADIUS*2-ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[3]-=5/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=3/(ady+10)+3/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=3/(adx+10)+5/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
							}
							else {
								ai[3]-=5/(adx+10);
								ai[0]+=3/(ady+10)+3/(adx+10);
								ai[1]-=3/(adx+10)+5/(Math.abs(me.getY()+RADIUS-bullets[i].getX()));
							}
						}

					}
				}
				//bullet not moving towards bot. since bot is slower, there's no need to worry about it.
			}
		}
	}

	private void directionalbulletdodge(Bullet[] bullets, BotInfo me) {
		double dx, adx, dy, ady, d;
		for(int i = 0; i<bullets.length; i++){
			dx = me.getX()-bullets[i].getX();
			adx = Math.abs(dx);
			dy = me.getY()-bullets[i].getY();
			ady = Math.abs(dy);
			d = Math.abs(dx)+Math.abs(dy);
			if (d < 150){// only do if manhattan distance is 150 or less
				if(bullets[i].getYSpeed() * (me.getY()+RADIUS-bullets[i].getY()) >0){//if bullet is moving vertically to bot and not past the lowest point of the bot
					if (bullets[i].getYSpeed() > 0){
						if (bullets[i].getX() == me.getX()){//perfect alignment check
							ai[3]+=dodgemodifier/ady;//dodge left or right
							ai[2]+=dodgemodifier/ady;
							ai[0]-=dodgemodifier/ady;// don't move up
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){//partial alignment check
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/ady;//dodge rihgt
								ai[0]-=dodgemodifier/ady;//dont move up
								ai[2]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
								
							}
							else {
								ai[2]+=dodgemodifier/ady;//dodge left
								ai[0]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {//not aligned
							if (dx > 0){//me is right of bullet
								ai[0]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10);//discourage moving left into the bullet as y alignment gets closer to a limit
								/* old stuff
								*+dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
								*/
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((ady-RADIUS)/4>(RADIUS+adx)/2){//if time to hit is greater than time to dodge
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[0]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS+adx)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
					else {
						if (bullets[i].getX() == me.getX()){
							ai[3]+=dodgemodifier/ady;
							ai[2]+=dodgemodifier/ady;
							ai[1]-=dodgemodifier/ady;
						}
						else if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX()){
							if (dx > 0){//me is right of bullet
								ai[3]+=dodgemodifier/ady;
								ai[1]-=dodgemodifier/ady;
								ai[2]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=dodgemodifier/ady;
								ai[1]-=dodgemodifier/ady;
								ai[3]+=(dodgemodifier/ady)-(dodgemodifier/Math.abs((ady-RADIUS)/4-(RADIUS+adx)/2));//same but right side
							}
						}
						else {
							if (dx > 0){//me is right of bullet/bullet is left of me
								ai[1]-=dodgemodifier/(ady+10);//discourage moving closer on y but only to limit
								ai[3]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=(dodgemodifier/2)/(ady+10);//discourage moving closer as y gets closer to a limit but don't move into the bullet
								if((ady-RADIUS)/4>(RADIUS+adx)/2){//if the time for bullet to reach y position is greater than time to get out of the way, allow moving through the path of bullet
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX())+5);
								}
								else{//otherwise don't allow it
									ai[2]-=dodgemodifier/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));
								}
							}
							else {
								ai[1]-=dodgemodifier/(ady+10);
								ai[2]+=(dodgemodifier/2)/(adx+10)+(dodgemodifier/2)/(ady+10);
								ai[3]-=(dodgemodifier/2)/(ady+10);
								if((ady-RADIUS)/4>(RADIUS+adx)/2){
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX())+5);
								}
								else{
									ai[3]-=dodgemodifier/(Math.abs(me.getX()+RADIUS-bullets[i].getX()));
								}
							}
						}
					}
				}
				else if (bullets[i].getXSpeed() * (me.getX()+RADIUS-bullets[i].getX()) >0){
					if (bullets[i].getXSpeed()>0){//bullet moving left to right
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/adx;
							ai[1]+=dodgemodifier/adx;
							ai[2]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/adx;
								ai[2]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/adx;
								ai[2]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[2]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS+ady)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[2]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS+ady)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}
							}
						}
					}
					else {
						if (bullets[i].getY() == me.getY()){
							ai[0]+=dodgemodifier/adx;
							ai[1]+=dodgemodifier/adx;
							ai[3]-=dodgemodifier/adx;
						}
						else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()){
							if (dy > 0){//bullet is above me
								ai[1]+=dodgemodifier/adx;
								ai[3]-=dodgemodifier/adx;
								ai[0]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//so if bullet is aligned and above encourage dodge up but discourage as difference in dodge time and hit time approaches 0
							}
							else {//bullet is below me
								ai[0]+=dodgemodifier/adx;
								ai[3]-=dodgemodifier/adx;
								ai[1]+=(dodgemodifier/adx)-(dodgemodifier/Math.abs((adx-RADIUS)/4-(RADIUS+ady)/2));//same but down side
							}
						}
						else {
							if (dy > 0){//bullet is above me
								ai[3]-=dodgemodifier/(adx+10);//discourage moving closer on x but only to limit
								ai[1]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[0]-=(dodgemodifier/2)/(adx+10);//discourage moving closer on y as x gets closer to a limit but don't move into the bullet
								//x-radius is the left edge of the bot. x+radius is right edge of bot. y-radius is top edge. y+radius is bottom edge
								if((adx-RADIUS)/4>(RADIUS+ady)/2){
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[0]-=dodgemodifier/(Math.abs(me.getY()-RADIUS-bullets[i].getY()));
								}
							}
							else {
								ai[3]-=dodgemodifier/(adx+10);
								ai[0]+=(dodgemodifier/2)/(ady+10)+(dodgemodifier/2)/(adx+10);
								ai[1]-=(dodgemodifier/2)/(adx+10);
								if((adx-RADIUS)/4>(RADIUS+ady)/2){
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY())+5);
								}
								else{
									ai[1]-=dodgemodifier/(Math.abs(me.getY()+RADIUS-bullets[i].getY()));
								}
							}
						}

					}
				}
				//bullet not moving towards bot. since bot is slower, there's no need to worry about it.
			}
		}
	}
}
