import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class contains fields/attributes associated with each bank account.
 * Also contains methods that give the bank account functionality.
 * All code that doesn't work or was used for debugging purposes has been commented out.
 * 
 * @author Faran Azadi
 */

public class Account {
	private String name; // Bank account name e.g. "Main"
	private int startingBalance; // Starting balance of the bank account
	private int balance; // Current balance of the bank account
	private static final int MIN_THRESHOLD = 10; // Minimum threshold for main
													// account
	private ArrayList<String> transactionList = new ArrayList<String>(); // The list of each withdrawal and deposit
	public int sumOfDeposits = 0; // The total sum of all deposits
	public int sumOfWithdrawals = 0; // The total sum of all withdrawals
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Check to make sure program has been called with correct number of
		// command line arguments
		if (args.length != 3) {
			System.err.println("Error: program should take exactly three command line arguments:");
			System.err.println("\t<No. of card holders> <main acct starting bal.> <backup acct. starting bal.>");
			System.exit(0);
		}
		// And then make sure that those args are all integers
		try {
			int numCards = Integer.parseInt(args[0]);
			Account account = new Account("Main", Integer.parseInt(args[1]));
			Account backup = new Account("Backup", Integer.parseInt(args[2]));

			// Create the array of threads, each of which has a CardHolder
			// associated with it
			Thread[] cards = new Thread[numCards];

			// Assign a CardHolder to each thread
			// Start each thread
			for (int i = 0; i < numCards; i++) {
				cards[i] = new Thread(new CardHolder(i, account));
				cards[i].start();
			}
			
			// Set up and start the monitor thread 
			Thread monitorThread;
			monitorThread = new Thread(new Monitor(100, account, backup)); // ID has to be a high number
			monitorThread.start();
			System.out.println("Monitor thread started...");
			
		} catch (NumberFormatException e) {
			System.err.println("All three arguments should be integers");
			System.err.println("\t<No. of card holders> <main acct starting bal.> <backup acct. starting bal.>");
		}
	}
	
	/**
	 * Create an account - initialisation goes in here.
	 * 
	 * @param name the name of the bank account
	 * @param startingBalance the starting balance of the account
	 */
	public Account(String name, int startingBalance) {
		this.name = name;
		this.startingBalance = startingBalance;
		balance = this.startingBalance;
	}
	
	/**
	 * Deposit specific amount into desired account.
	 * Synchronized so only one thread can execute at once.
	 * 
	 * @param id the bank account to deposit the money in
	 * @param amount the amount being deposited into the account
	 */
	public synchronized void deposit(int id, int amount) {
		balance = balance + amount;
		sumOfDeposits = sumOfDeposits + amount;
		// Thread ID ----- Amount Deposited ----- Balance
		String outputStatement = ("(" + id + ")" + "                            " + "Amount:" + amount + "    " + "Balance:" + balance); // Couldn't manage to format the proper way
		//System.out.println(outputStatement);
		//transactionList.add("deposit");
		transactionList.add(outputStatement); // Add this to the ArrayList to be printed out later
		notify(); // Notify any blocked threads that the balance has been
					// updated
	}
	
	/**
	 * Withdraw <amount> from the account.
	 * If there are insufficient funds, thread has to wait till funds are available, then complete transaction.
	 * Synchronized so only one thread can execute at once.
	 * 
	 * @param id the bank account to withdraw the money from
	 * @param amount the amount to withdraw from the account
	 */
	public synchronized void withdraw(int id, int amount) {
		while (balance < amount) // Makes sure we're not withdrawing
									// more than what's actually in the
									// account
		{
			try {
				wait(); // Thread will be blocked until notified otherwise
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		balance = balance - amount;
		sumOfWithdrawals = sumOfWithdrawals + amount;
		
		// Thread ID ----- Amount Withdrawn ----- Balance
		String outputStatement = ("(" + id + ")" + "            " + "Amount:" + amount + "    " + "                Balance:" + balance); // Couldn't manage to format the proper way
		//System.out.println(outputStatement);
		transactionList.add(outputStatement); // Add this to the ArrayList to be printed out later
		//System.out.println("New bank balance: " + balance);
	}
	
	/**
	 * Check the balance of the main account.
	 * If below the minimum threshold, transfer funds from backup account  (if there's enough).
	 * 
	 * @param backupId the thread id where the withdrawal and deposit is done
	 * @param backup the backup account to withdraw money from
	 */
	public synchronized void checkBalance(int backupId, Account backup) {
		final int UPPER_THRESHOLD = startingBalance; // Set the upper threshold
														// to the
														// startingBalance of
														// the account
		if (balance < MIN_THRESHOLD && backup.balance > MIN_THRESHOLD) // If main account balance is less than
										// threshold
										// Work out how much to transfer from
										// backup account
										// Also work out if this is actually
										// possible
		{
			int amountToWithdraw = UPPER_THRESHOLD - balance;
			backup.withdraw(backupId, amountToWithdraw); // Withdraw the amount required from
											// backup account to get back to
											// upper limit
			deposit(backupId, amountToWithdraw); // Deposit the amount that was taken from the backup account into the main account
		}
		
		if (balance > UPPER_THRESHOLD) // Transfer excess funds from the main account to the backup account
		{
			int amountToWithdraw = balance - UPPER_THRESHOLD;
			
			withdraw(backupId, amountToWithdraw);
			backup.deposit(backupId, amountToWithdraw);
		}
		
	}
	
	/**
	 * Calculate the cash in hand. Can be a negative number.
	 * 
	 * @return the total withdrawals made minus the total deposits made
	 */
	public int getCashInHand()
	{
		return sumOfWithdrawals - sumOfDeposits;
	}
	
	/**
	 * Print out the statement of transactions in a 'nice' format.
	 */
	public void printStatement() {
		int transactionNo = 1; // Can't have the number of transactions starting at 0
		System.out.println("Account \"" + name + "\": \n");
		System.out.println(String.format("%s\t%s\t%s\t\t%s","Transactions","Withdrawal","Deposit","Balance")); // Headings on the statement
		/*for (String item : transactionList)
		{
			//System.out.println("Transaction No: " + transactionNo + item);
			System.out.println(transactionNo + item);
			transactionNo++;
		}*/

		// The method for printing the statements above didn't work for some reason...
		for (Iterator<String> i = transactionList.iterator(); i.hasNext();) {
		    String item = i.next();
		    System.out.println(transactionNo + item);
		    transactionNo++;
		}
	}
}
