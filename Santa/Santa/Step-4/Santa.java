//import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;


public class Santa implements Runnable {

	enum SantaState {SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER};
	private SantaState state;
	private boolean engine = true;
	private SantaScenario scenario;

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
				// FIXME: help the elves who are at the door and go back to sleep
				try{
					this.scenario.door.acquire();		//wait
					int size = this.scenario.santasDoor.size(); //size of queue santasDoor
					for(int i = 0; i < size; i++){		//for size of queue santasDoor
						//remove head of queue 1 by 1
						this.scenario.santasDoor.remove().setState(Elf.ElfState.WORKING);
					}
					this.scenario.door.release();	//release
				}catch(InterruptedException e){
					this.scenario.door.release();
					e.printStackTrace();
				}
				//elves are no longer in trouble - santa returns to sleeping
				state = SantaState.SLEEPING;
				break;
			case WOKEN_UP_BY_REINDEER: 
				// FIXME: assemble the reindeer to the sleigh then change state to ready 
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

	public void wokeByElves(){
		this.state = SantaState.WOKEN_UP_BY_ELVES;
	}
	public void wokeByReindeer(){
		this.state = SantaState.WOKEN_UP_BY_REINDEER;
	}

	public boolean SantaAsleep(){
		if(this.state == SantaState.SLEEPING){
			return true;
		}
		else{
			return false;
		}
	}
}
