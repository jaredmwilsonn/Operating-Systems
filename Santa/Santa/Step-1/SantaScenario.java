import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;



public class SantaScenario {

	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	public boolean isDecember;

	public int maxElfPermits = 3;
	public int numElves = 0;

	public int maxReindeerPermits = 8;
	public int numReindeer = 0;
	Semaphore elfSemaphore = new Semaphore(maxElfPermits, true);
	Semaphore deerSemaphore = new Semaphore(maxReindeerPermits, true);
	
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
		// now, start the passing of time
		for(int day = 1; day < 500; day++) {
			//terminate threads at day 370
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
			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			scenario.santa.report();
			for(Elf elf: scenario.elves) {
				elf.report();
			}
			for(Reindeer reindeer: scenario.reindeers) {
				reindeer.report();
			}
		}
	}
	
	
	
}
