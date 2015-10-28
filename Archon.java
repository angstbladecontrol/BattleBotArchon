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
	
	/**
	 * bulletdown is cooldown for shooting down
	 * bulletup is cooldown for shooting up
	 * bulletleft is cooldown for shooting left
	 * bulletright is cooldown for shooting right
	 */
	private int bulletdown, bulletup, bulletleft, bulletright;
	//private Random rand;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
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
		//count down the directional shot cooldowns
		
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
		//bullet dodging
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
		//we are going to have another part that overrides the dodge mechanics if bot is stuck.
		if (shotOK&&dodgemode==0){//only if not dodging
			for(int i = 0; i < liveBots.length; i ++){
				//this checks if something has same value
				if (liveBots[i].getX() + 3 > me.getX() && liveBots[i].getX() - RADIUS < me.getX()){
					if (liveBots[i].getY()-me.getY() > 0 && bulletdown == 0){
						bulletdown = 10;
						return BattleBotArena.FIREDOWN;
					}
					else if (bulletup == 0) {
						bulletup = 10;
						return BattleBotArena.FIREUP;
					}
				}
				//
				else if (liveBots[i].getY() + 3 > me.getY() && liveBots[i].getY() - RADIUS < me.getY()){
					if (liveBots[i].getX()-me.getX() > 0 && bulletright == 0){
						bulletright = 10;
						return BattleBotArena.FIRERIGHT;
					}
					else if (bulletleft == 0) {
						bulletleft = 10;
						return BattleBotArena.FIRELEFT;
					}
				}
			}
		}
		
		return move;
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

}
