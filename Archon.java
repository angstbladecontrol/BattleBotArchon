package bots;

import java.awt.Graphics;
import java.awt.Image;
//import java.util.Random;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class Archon extends Bot {
	
	String name;
	private String[] killMessages = {"Get rekt m8", "Gr8 b8 m8!", "eeeeezzzz", "Psyche!", "Eureka!", "Are you trying?", ""};
	Image up, down, right, left, current;
	
	/**
	 * bulletdown is cooldown for shooting down
	 * bulletup is cooldown for shooting up
	 * bulletleft is cooldown for shooting left
	 * bulletright is cooldown for shooting right
	 */
	private String nextMessage = null;
	private int bulletdown, bulletup, bulletleft, bulletright;
	private int msgCounter = 0;
	//private Random rand;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
	private int shootdir;
	private int dodgethis;
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
		int dodgemode = 0;
	}

	@Override
	public void newRound() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		//bullet dodging
		//dodgescript(bullets, me);
		//we are going to have another part that overrides the dodge mechanics if bot is stuck.
		if (shotOK&&dodgemode==0){//only if not dodging
			shootdir = move;
			shootscript(liveBots, me, shotOK);
			return shootdir;
		}
		else {
			return move;
		}
	}

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
		return "Archon FTW!";
	}

	@Override
	public String outgoingMessage() {
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by "+getName()+".*"))
		{
			int msgNum = (int)(Math.random()*killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int)(Math.random()*30 + 30);
		}


	}

	@Override
	public String[] imageNames() {
		String images[] = {"Archon_UP.gif","Archon_DOWN.png","Archon_LEFT.png","Archon_RIGHT.png"};
		return images;
	}

	@Override
	public void loadedImages(Image[] images) {
		// TODO Auto-generated method stub
		if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}
	
	// not functioning
	private void dodgeMechanics (Bullet[] bullets,BotInfo me) {
		
		int [] move = {1,2,3,4};
		
		double maxCheck = RADIUS*4;
		
		// if danger booleans are true call a movement method to move out of the way of the bullets
				
	}	
	// not functioning
	private boolean DangerX(Bullet [] bullets,BotInfo me) {
		boolean DangerXCor = false;
		for (int x =0; x<bullets.length;x++) {
			
			// Check for dangerous bullets in the x direction by comparing it to the current position of the bot
			
		}
		return DangerXCor;		
	}
	//not functioning
	private boolean DangerY (Bullet [] bullets,BotInfo me) {
		boolean DangerYCor = false;
	for (int x =0; x<bullets.length;x++) {
			
		// Check for dangerous bullets in the y direction by comparing it to the current position of the bot			
			
		}
	return DangerYCor;
	}
	// not functioning
	private void stuckCheck (BotInfo me,BotInfo deadBots) {
		
		double x = me.getX();
		double y = me.getY();
		
		if (y >= BattleBotArena.BOTTOM_EDGE && move == 0) {
			move = BattleBotArena.UP;			
		}
		if (y<= BattleBotArena.TOP_EDGE && move == 0) {
			move = BattleBotArena.UP;
		}
		if (x >= BattleBotArena.RIGHT_EDGE && move == 0) {
			move = BattleBotArena.UP;
		}
		if (x <= BattleBotArena.LEFT_EDGE && move == 0) {
			move = BattleBotArena.UP;
		}
		
		if (x <= deadBots.getX() && move ==0) {
			move = BattleBotArena.DOWN;
		}
		if (y <= deadBots.getY() && move == 0) {
			move = BattleBotArena.RIGHT;
		}
		
	}
	private void dodgescript(Bullet[] bullets, BotInfo me){
		if (dodgemode == 1) {
			if(!(bullets[dodgethis].getX()+RADIUS*2 > me.getX() && bullets[dodgethis].getX()-RADIUS*2 < me.getX() 
					&& bullets[dodgethis].getYSpeed()*(me.getY()-bullets[dodgethis].getY())>0)){
				System.out.println("TE1 | " + bullets[dodgethis].getX() + " | " + me.getX() + " | " + bullets[dodgethis].getYSpeed() + " | " + (me.getY()-bullets[dodgethis].getY()));
				dodgemode = 0;
				//move = 0;
			}
		}
		else if(dodgemode == 2){
			if(bullets[dodgethis].getY()+RADIUS*2 < me.getY() || bullets[dodgethis].getY()-RADIUS*2 < me.getY() 
					|| bullets[dodgethis].getXSpeed()*(me.getX()-bullets[dodgethis].getX())<=0){
				System.out.println("TE2 | " + bullets[dodgethis].getY() + " | " + me.getY() + " | " + bullets[dodgethis].getXSpeed() + " | " + (me.getX()-bullets[dodgethis].getX()));
				dodgemode = 0;
				//move = 0;
			}
		}
		else {
			for(int i = 0; i<bullets.length; i++){
				//bullet incoming from y axis
				if (bullets[i].getX()+RADIUS*2 > me.getX() && bullets[i].getX()-RADIUS*2 < me.getX() 
						&& bullets[i].getYSpeed() * (me.getY()-bullets[i].getY()) >0){
					if(bullets[i].getX() > me.getX()){
						move = BattleBotArena.LEFT;
					}
					else {
						move = BattleBotArena.RIGHT;
					}
					//move = 3;
					System.out.println("1 | "+bullets[i].getYSpeed() + " | " + (me.getY()-bullets[i].getY()) + " | " + move);
					dodgemode = 1;
					dodgethis = i;
					//System.out.println("2 | " + bullets[dodgethis].getX() + " | " + me.getX());
					break;
				}
				//bullet incoming from x axis
				else if (bullets[i].getY()+RADIUS*2 > me.getY() && bullets[i].getY()-RADIUS*2 < me.getY()
						&& bullets[i].getXSpeed() * (me.getX()-bullets[i].getX()) >0){
					if(bullets[i].getY() > me.getY()){
						move = BattleBotArena.UP;
					}
					else {
						move = BattleBotArena.DOWN;
					}
					System.out.println("2 | "+bullets[i].getXSpeed() + " | " + (me.getX()-bullets[i].getX()) + " | " + move);
					dodgemode = 2;
					dodgethis = i;
					//System.out.println("2 | " + bullets[dodgethis].getY() + " | " + me.getY());
					break;
				}
			}
		}
	}
	
	private void shootscript(BotInfo[] liveBots, BotInfo me, boolean shotOK){
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
				//this checks if something has same value
				if (liveBots[i].getX() + 3 > me.getX() && liveBots[i].getX() - RADIUS < me.getX()){
					if (liveBots[i].getY()-me.getY() > 0 && bulletdown == 0){
						bulletdown = 10;
						shootdir = BattleBotArena.FIREDOWN;
						return;
					}
					else if (bulletup == 0) {
						bulletup = 10;
						shootdir = BattleBotArena.FIREUP;
						return;
					}
				}
				//
				else if (liveBots[i].getY() + 3 > me.getY() && liveBots[i].getY() - RADIUS < me.getY()){
					if (liveBots[i].getX()-me.getX() > 0 && bulletright == 0){
						bulletright = 10;
						shootdir = BattleBotArena.FIRERIGHT;
						return;
					}
					else if (bulletleft == 0) {
						bulletleft = 10;
						shootdir = BattleBotArena.FIRELEFT;
						return;
					}
				}
			}
			
	}
