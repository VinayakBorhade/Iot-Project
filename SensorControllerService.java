import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;
import com.pi4j.io.gpio.*;

class SensorController implements Runnable{
	HashSet<String> sendEvents;

	BlockingQueue<String> sendQueue;

	SensorController(BlockingQueue<String> sendQueue,String fileName){
		this.sendQueue = sendQueue;

		sendEvents = new HashSet<String>();
		try{
			BufferedReader inFromFile = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = inFromFile.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				String type = st.nextToken();
				if(type.compareTo("SEND") == 0){
					sendEvents.add(st.nextToken());
				}
				
			}
			if(sendEvents.contains("MOTIONDETECTED")) {
				final GpioController gpio= GpioFactory.getInstance();
				final GpioPinDigitalInput input = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, PinPullResistance.PULL_DOWN);
	
		        // create and register gpio pin listener
		        input.addListener(new GpioPinListenerDigital() {
		                @Override
		                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		                    if(event.getState().isHigh()){
		                      sendQueue.put("MOTIONDETECTED");
		                    }
		                }
		            });
			}//end of if
		}catch(Exception e){}
	}

	//to be replaced with a listener method
	void senseMovement(){
		
	}

	public void run(){
		//Scanner s = new Scanner(System.in);
		while(true){
			/*try{
				//String event = s.nextLine();
				//if(senseMovement())
					sendQueue.put("MOTIONDETECTED");
			}catch(Exception e){}*/
		}
	}

}

class SensorControllerService{
	public static void main(String[] args) throws Exception {
		BlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(10);

		Thread controllerService = new Thread(new SensorController(sendQueue,args[0]));
		Thread eventSender = new Thread(new EventSender(sendQueue));
		controllerService.start();
		eventSender.start();
	}
}