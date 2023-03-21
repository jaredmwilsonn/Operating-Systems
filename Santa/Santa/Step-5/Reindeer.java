import java.util.Random;


public class Reindeer implements Runnable {

	public enum ReindeerState {AT_BEACH, AT_WARMING_SHED, AT_THE_SLEIGH};
	private ReindeerState state;
	private SantaScenario scenario;
	private Random rand = new Random();
	private boolean engine = true;

	/**
	 * The number associated with the reindeer
	 */
	private int number;
	
	public Reindeer(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ReindeerState.AT_BEACH;
	}

	public void setState(ReindeerState state) {
		this.state = state;
	}

	public ReindeerState getState() {
		return state;
	}

	@Override
	public void run() {
		while(engine) {
		// wait a day
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// see what we need to do:
		switch(state) {
		case AT_BEACH: { // if it is December, the reindeer might think about returning from the beach
			if (scenario.isDecember) {
				if (rand.nextDouble() < 0.1) {
					state = ReindeerState.AT_WARMING_SHED;
					scenario.reindeerAtShed++;
					System.out.println(scenario.reindeerAtShed);
				}
			}
			break;			
		}
		case AT_WARMING_SHED: 
			// if all the reindeer are home, wake up santa
			try{
				if(scenario.reindeerAtShed > 8){
					scenario.santa.wokeByReindeer();
				}
				scenario.deerSemaphore.acquire();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			break;
		case AT_THE_SLEIGH: 
			// keep pulling
			break;
		}
		}
	};
	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Reindeer " + number + " : " + state);
	}
	
	public void kill(){
		engine = false;
	}

	public void goToSleigh(){
		state = ReindeerState.AT_THE_SLEIGH;
	}
}
