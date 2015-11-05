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
	double largest;
	int maxindex;

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
		directionalbulletdodge(bullets, me);
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
			System.out.println ("error is in shooting");
		}
		try {
			if (me.getY()<100){
				ai[0]-=1/(me.getY()-RADIUS);
				ai[1]+=1/Math.abs((me.getY()-RADIUS)+2);
			}
			if(me.getY()>400){
				ai[1]-=1/(500-me.getY()+RADIUS);
				ai[0]+=1/Math.abs((500-me.getY()+RADIUS)+2);
			}
			if(me.getX()<100){
				ai[2]-=1/(me.getX()-RADIUS);
				ai[3]+=1/Math.abs((me.getX()-RADIUS)+2);
			}
			if(me.getX()>600){
				ai[3]-=1/(700-me.getX()+RADIUS);
				ai[2]+=1/Math.abs((700-me.getX()+RADIUS)+2);
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
	private void avoidai(){
		
	}
	private void dodgescript(Bullet[] bullets, BotInfo me){
		if (dodgemode == 1) {
			try {
				if (dodgethis == null){
					dodgemode = 0;
				}
				else if(!(dodgethis.getX()+RADIUS*2 > me.getX() && dodgethis.getX()-RADIUS*2 < me.getX() 
						&& dodgethis.getYSpeed()*(me.getY()-dodgethis.getY())>0)){
					//System.out.println("TE1 | " + bullets[dodgethis].getX() + " | " + me.getX() + " | " + bullets[dodgethis].getYSpeed() + " | " + (me.getY()-bullets[dodgethis].getY()));
					dodgemode = 0;
					//move = 0;
				}
			}
			catch(Exception e) {
				System.out.println("error is when bullet is doing dodge 1 (horizontal)");
				try {
					System.out.println(dodgethis);
				}
				catch (Exception ee){
					System.out.println("cannot refre to dodgethis");
				}
			}
		}
		else if(dodgemode == 2){
			try {
				if (dodgethis == null){
					dodgemode = 0;
				}
				else if(dodgethis.getY()+RADIUS*2 < me.getY() || dodgethis.getY()-RADIUS*2 < me.getY() 
						|| dodgethis.getXSpeed()*(me.getX()-dodgethis.getX())<=0){
					//System.out.println("TE2 | " + bullets[dodgethis].getY() + " | " + me.getY() + " | " + bullets[dodgethis].getXSpeed() + " | " + (me.getX()-bullets[dodgethis].getX()));
					dodgemode = 0;
					//move = 0;
				}
			}
			catch (Exception e) {
				System.out.println("error is when bullet is doing dodge 2 (vertical)");
				try {
					System.out.println(dodgethis);
				}
				catch (Exception ee){
					System.out.println("cannot refer to dodgethis");
				}
			}
		}
		else {
			try {
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
						//System.out.println("1 | "+bullets[i].getYSpeed() + " | " + (me.getY()-bullets[i].getY()) + " | " + move);
						dodgemode = 1;
						dodgethis = bullets[i];
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
						//System.out.println("2 | "+bullets[i].getXSpeed() + " | " + (me.getX()-bullets[i].getX()) + " | " + move);
						dodgemode = 2;
						dodgethis = bullets[i];
						//System.out.println("2 | " + bullets[dodgethis].getY() + " | " + me.getY());
						break;
					}
				}
			}
			catch (Exception e){
				System.out.println("error is in detect bullet threat");
			}
		}
	}
	private void stuckscript (BotInfo me){
		if (me.getX() == x && me.getY() == y)
		{
			stuck = true;
			if (move == BattleBotArena.UP)
				move = BattleBotArena.DOWN;
			else if (move == BattleBotArena.LEFT)
				move = BattleBotArena.RIGHT;
			else if (move == BattleBotArena.DOWN)
				move = BattleBotArena.LEFT;
			else if (move == BattleBotArena.RIGHT)
				move = BattleBotArena.UP;
		}
		else {
			stuck = false;
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
			if (liveBots[i].getX() + RADIUS > me.getX() && liveBots[i].getX() - RADIUS < me.getX()){
				if (liveBots[i].getY()-me.getY() > 0 && bulletdown == 0){
					bulletdown = 15;
					shootdir = BattleBotArena.FIREDOWN;
					return;
				}
				else if (bulletup == 0) {
					bulletup = 15;
					shootdir = BattleBotArena.FIREUP;
					return;
				}
			}
			//
			else if (liveBots[i].getY() + RADIUS > me.getY() && liveBots[i].getY() - RADIUS < me.getY()){
				if (liveBots[i].getX()-me.getX() > 0 && bulletright == 0){
					bulletright = 15;
					shootdir = BattleBotArena.FIRERIGHT;
					return;
				}
				else if (bulletleft == 0) {
					bulletleft = 15;
					shootdir = BattleBotArena.FIRELEFT;
					return;
				}
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
								ai[3]+=5/(adx+10)+5/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
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
								ai[2]+=(3/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//so if bullet is aligned and to the left, encourage a dodge to left but decrease as the time to dodge approaches time to hit
							}
							else {
								ai[2]+=5/ady;
								ai[1]-=5/ady;
								ai[3]+=(5/ady)-(5/Math.abs((ady-RADIUS)/4-(RADIUS*2-adx)/2));//same but right side
							}
						}
						else {
							if (dx > 0){//me is right of bullet/bullet is left of me
								ai[1]-=5/(ady+1);//discourage moving closer on y but only to limit
								ai[3]+=3/(adx+10)+3/(ady+10);//so encourage moving further as the bullet gets closer but only to a limit
								ai[2]-=3/(ady+1)+5/(Math.abs(me.getX()-RADIUS-bullets[i].getX()));//discourage moving closer as y gets closer to a limit but don't move into the bullet
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
}
