import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	public Semaphore elfSemaphore = new Semaphore(maxElfPermits, true);
	public Semaphore deerSemaphore = new Semaphore(maxReindeerPermits, true);

	public Semaphore trouble = new Semaphore(1, true);			//semaphore for elves in trouble
	public Semaphore waitTrouble = new Semaphore(0, true);		//semaphore for elves waiting for there to be 3 elves in trouble
	public Semaphore door = new Semaphore(1, true);				//semaphore for elves waiting at santas door to wake santa
	public Queue<Elf> inTrouble;								//queue paired with trouble semaphore
	public Queue<Elf> santasDoor;								//queue paired with door semaphore

	public int reindeerAtShed;		//counter for number of reindeer at the warming shed
	
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
		scenario.reindeerAtShed = 0;

		// now, start the passing of time
		for(int day = 1; day < 500; day++) {
			//terminate threads at day 370
			if(day == 370){
				scenario.santa.kill();
				for(Elf elf: scenario.elves){
					elf.kill();		//cancel run for elf threads
				}
				for(Reindeer reindeer: scenario.reindeers){
					reindeer.kill();	//cancel run for reindeer threads
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
			
			scenario.inTrouble = new LinkedList<>();
			scenario.santasDoor = new LinkedList<>();
			try{
				scenario.trouble.acquire();		//wait
				int size = scenario.inTrouble.size();
				scenario.trouble.release();		//release
				if((size >= 3) && (day < 370)){		//being when it is possible for 3 elves to be in trouble
					scenario.door.acquire();	//wait
					if(scenario.santasDoor.isEmpty()){
						scenario.trouble.acquire();		//wait
						for(int i = 0; i < size; i++){	//size of inTrouble queue
							Elf e = scenario.inTrouble.remove();	//removes elves from head of queue
							e.setState(Elf.ElfState.AT_SANTAS_DOOR);	//moves elves in queue to santas door
							scenario.santasDoor.add(e);				//insert elves into santasDoor queue
							scenario.waitTrouble.release();	//release
						}
						scenario.trouble.release();		//relase
					}
					scenario.door.release();	//release
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	
	
}
