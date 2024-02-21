package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Model;

public class Controller2 extends MouseAdapter{
	private Model model;
	
	public Controller2(Model model) {
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			this.model.setRunning(false);	
			break;
		}
	}
}