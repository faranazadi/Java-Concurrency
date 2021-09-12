/**
 * Class for the monitor thread.
 * Purpose of this thread is to check the balance and transfer funds if necessary.
 * 
 * This monitor thread attempts to fix the scenarios that can cause deadlocks in this application; the majority of which have been addressed.
 * However, in my opinion, there is still a case where a deadlock can arise - when multiple threads try to withdraw more than what's available in the backup account.
 * This will lead to all of the threads being indefinitely blocked because the thread requesting the money from the backup account will never be freed.
 * This is very similar to the dining philosipher's problem.
 * 
 * @author Faran Azadi
 *
 */
public class Monitor implements Runnable {
	
	private int id; // ID of the thread
	private Account main, backup; // Main and backup account objects
	
	/**
	 * Constructor - initialisation goes in here
	 * 
	 * @param id the id of the thread
	 * @param main the main bank account
	 * @param backup the backup account
	 */
	public Monitor(int id, Account main, Account backup)
	{
		this.id = id;
		this.main = main;
		this.backup = backup;
	}
	
	public void run() {
		while(Thread.activeCount() > 2) // Greater than 2 because of main and monitor running on their own threads
		{
			main.checkBalance(id, backup);
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// When all threads have completed, print statements
		System.out.println("Threads completed!");
		main.printStatement();
		backup.printStatement();
	}
}
