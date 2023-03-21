//import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;


public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER};
	private SantaState state;
	private boolean engine = true;
	SantaScenario scenario;
	
	public Santa(SantaScenario scenario) {
		this.state = SantaState.SLEEPING;
		this.scenario = scenario;
	}
	
	
	@Override
	public void run() {
		while(engine) {
			// wait a day...
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(state) {
			case SLEEPING: // if sleeping, continue to sleep
				break;
			case WOKEN_UP_BY_ELVES: 
				// help the elves who are at the door and go back to sleep
				for(Elf elf: scenario.elves){
					if(elf.getState() == Elf.ElfState.AT_SANTAS_DOOR){
						elf.setState(Elf.ElfState.WORKING);
						elf.backToWork();
					}
				}
				scenario.atDoor.clear();
				state = SantaState.SLEEPING;
				break;
			case WOKEN_UP_BY_REINDEER: 
				// assemble the reindeer to the sleigh then change state to ready
				for(Reindeer r: scenario.reindeers){
					r.goToSleigh();
					r.setState(Reindeer.ReindeerState.AT_THE_SLEIGH);
				}
				scenario.notifyReindeer();
				state = SantaState.READY_FOR_CHRISTMAS;
				break;
			case READY_FOR_CHRISTMAS: // nothing more to be done
				break;
			}
		}
	}

	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Santa : " + state);
	}
	
	public void kill(){
		engine = false;
	}

	public void wokenByElves(){
		this.state = SantaState.WOKEN_UP_BY_ELVES;
	}

	public void wokenByReindeer(){
		this.state = SantaState.WOKEN_UP_BY_REINDEER;
	}
	
}
