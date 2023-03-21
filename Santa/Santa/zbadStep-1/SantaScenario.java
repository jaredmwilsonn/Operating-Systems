import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;



public class SantaScenario {

	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	public boolean isDecember;
	
	public Semaphore trouble;
	public Semaphore deer;
	public List<Elf> atDoor;
	public Queue<Elf> wait;
	public Object lock = new Object();
	public Object lockDeer = new Object();

	//main
	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		scenario.isDecember = false;
		// create the participants
		// Santa
		scenario.santa = new Santa(scenario);
		Thread th = new Thread(scenario.santa);
		th.start();
		// The elves: in this case: 10
		scenario.elves = new ArrayList<>();
		for(int i = 0; i != 10; i++) {
			Elf elf = new Elf(i+1, scenario);
			scenario.elves.add(elf);
			th = new Thread(elf);
			th.start();
		}
		// The reindeer: in this case: 9
		scenario.reindeers = new ArrayList<>();
		for(int i=0; i != 9; i++) {
			Reindeer reindeer = new Reindeer(i+1, scenario);
			scenario.reindeers.add(reindeer);
			th = new Thread(reindeer);
			th.start();
		}

		scenario.trouble = new Semaphore(3, true);		// elves semaphore - 3 elves in trouble needed to wake santa
		scenario.deer = new Semaphore(8, true);			// deer semaphore - all but 1 reindeer must be in warming hut for last to wake santa
		scenario.wait = new ArrayBlockingQueue<Elf>(3);	//list for elves at santas door
		scenario.atDoor = new ArrayList<>();			//list 

		// now, start the passing of time
		for(int day = 1; day < 500; day++) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}
			// terminate threads at day 370 using deferred termination
			// Note: main thread with the counting of days will continue
			if(day == 370){
				scenario.santa.kill();
				for(Elf elf: scenario.elves){
					elf.kill();
				}
				for(Reindeer reindeer: scenario.reindeers){
					reindeer.kill();
				}
				break;
			}

			if(scenario.wait.size() == 3){
				scenario.notifyElves();
			}

			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			//System.out.println("isDecember =  " + scenario.isDecember);
			scenario.santa.report();
			for(Elf elf: scenario.elves) {
				elf.report();
			}
			for(Reindeer reindeer: scenario.reindeers) {
				reindeer.report();
			}
		}
		//System.out.println("-----End-----");
	}
	
	public void notifyElves(){
		synchronized(lock){
			this.atDoor.addAll(this.wait);	//appends waiting elves list to door list
			this.wait.clear();				//clear waitlist for santas door
			this.lock.notifyAll();			//signal
		}
	}
	
	public void notifyReindeer(){
		synchronized(lockDeer){
			this.lockDeer.notifyAll();
		}
	}
	
}
