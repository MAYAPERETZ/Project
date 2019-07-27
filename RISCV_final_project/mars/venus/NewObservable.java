package mars.venus;

import java.util.Observable;

public class NewObservable extends Observable {
	
	void notifyObserversOfSelectedTab(Object object) {
		this.setChanged();
		this.notifyObservers(object);
	}
	
}
