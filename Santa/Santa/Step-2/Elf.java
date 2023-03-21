import java.util.Random;

public class Elf implements Runnable {

	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR
	};

	private ElfState state;
	/**
	 * The number associated with the Elf
	 */
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;
	private boolean engine = true;

	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
	}


	public ElfState getState() {
		return state;
	}

	/**
	 * Santa might call this function to fix the trouble
	 * @param state
	 */
	public void setState(ElfState state) {
		this.state = state;
	}


	@Override
	public void run() {
		while (engine) {
      // wait a day
  		try {
  			Thread.sleep(100);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
		  Elf currentElf = scenario.elves.get(this.number - 1);
			switch (state) {
			case WORKING: {
				// at each day, there is a 1% chance that an elf runs into
				// trouble.
				// try{
					if (rand.nextDouble() < 0.01) {
						state = ElfState.TROUBLE;
									
				// 	scenario.elfSemaphore.acquire();
				// 	scenario.numElves++;
					}
				// }catch(InterruptedException e){
				// 	this.scenario.elfSemaphore.release();
				// 	e.printStackTrace();
				// }
				break;
			}
			case TROUBLE:
				// FIXME: if possible, move to Santa's door
				// if(scenario.numElves < 3){
				// 	//wait
				// }
				// else{
					currentElf.setState(Elf.ElfState.AT_SANTAS_DOOR);
				// }
				break;
			case AT_SANTAS_DOOR:
				// FIXME: if feasible, wake up Santa
				if(scenario.santa.SantaAsleep() == true){
					scenario.santa.wokeByElves();
				}
				break;
			}
		}
	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}

	public void kill(){
		engine = false;
	}

	public void backToWork(){
		scenario.elfSemaphore.release();
	}

}
