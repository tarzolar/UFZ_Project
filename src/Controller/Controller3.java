package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Model;

public class Controller3 extends MouseAdapter{
	private Model model;
	
	public Controller3(Model model) {
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(e.getButton()){
		case MouseEvent.BUTTON1:
			this.model.setup();	
			break;
		}
	}
}