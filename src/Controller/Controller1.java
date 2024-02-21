package Controller;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Model;

public class Controller1 extends MouseAdapter{
	private Model model;
	
	public Controller1(Model model) {
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			Thread t = new Thread(this.model);
			t.start();
			break;
			
			
		}
	}
}
