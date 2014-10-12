package com.yahoo.bananas.models;

/** User Stats for the current user. */
public class UserStats {
	// Member Variables
	private int created = -1;
	private int read    = 0;
	private int shared  = 0;
	private int touched = 0;
	private int votesUp = 0;
	private int votesDn = 0;
	// Getters
	/** Number of jokes created/authored/posted by the user (including any deleted since so it may not reflect the current list).
	 *  SPECIAL VALUE: -1 = Not Determined (when offline cannot tell who the current user is, so cannot calculate this if offline). */
	public int created(){ return created; }
	/** Number of jokes actually opened in detail view. */
	public int read   (){ return read   ; }
	/** Number of jokes read+touched by user -- all read, voted, shared, etc. */
	public int touched(){ return touched; }
	public int votesUp(){ return votesUp; }
	public int votesDn(){ return votesDn; }
	/** Number of jokes shared once or more*/
	public int shared (){ return shared ; }
	// Setters
	public void created(int val){ created = val; }
	public void read   (int val){ read    = val; }
	public void shared (int val){ shared  = val; }
	public void touched(int val){ touched = val; }
	public void votesUp(int val){ votesUp = val; }
	public void votesDn(int val){ votesDn = val; }
	// Easy increment methods to avoid setX(getX()+1);
	public void addCreated(){ if(created>=0) created++; } // Only increment if we have an accurate count to start with.
	public void addRead   (){ read++   ; }
	public void addShared (){ shared++ ; }
	public void addTouched(){ touched++; }
	public void addVotesUp(){ votesUp++; }
	public void addVotesDn(){ votesDn++; }
}
