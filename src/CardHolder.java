/**
 * Class for the CardHolder threads.
 * This thread handles the random deposits and withdrawals.
 * 
 * @author Faran Azadi
 *
 */
public class CardHolder implements Runnable {
	private int id; // ID of the thread
	private Account account; // Account object
	final static int numIterations = 20; // The number of times the loop will execute in the run method
	
	
	/**
	 * Constructor - initialisation goes here
	 * 
	 * @param id the id of the thread
	 * @param account the bank account
	 */
	public CardHolder(int id, Account account) {
		this.id = id;
		this.account = account;
	}
	
	/*
	 * run method is what is executed when you start a Thread that
	 * is initialised with an instance of this class.
	 * You will need to add code to keep track of local balance (cash
	 * in hand) and report this when the thread completes.
	 */
	public void run() {
		for (int i = 0; i < numIterations; i++) {
			// Generate a random amount from 1-10
			int amount = (int)(Math.random()*10)+1;
			// Then with 50/50 chance, either deposit or withdraw it
			if (Math.random() > 0.5) {
				account.withdraw(id, amount); 
			} else {
				account.deposit(id, amount); 
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		System.out.println("THREAD "+ id + " finished");
		
		// Work out and print cash in hand after each thread finishes - not working properly, only works for the first thread.
		System.out.println("Cash in hand: " + account.getCashInHand());
		// Need to be reset after each thread prints out results
		account.sumOfDeposits = 0;
		account.sumOfWithdrawals = 0;
	}
}
