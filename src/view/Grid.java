package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;

import Controller.Controller1;
import Controller.Controller2;
import Controller.Controller3;
import Controller.Controller4;
import model.Model;


public class Grid extends JPanel implements Observer{
	private Model model;
	private JPanel[][] cells;
	private JPanel grid;
	private JButton start;
	private JButton stop;
	private JButton setup;
	private JButton rescale;
	private JLabel runs;
	private JPanel buttons;
	private JPanel panel;
	private ChartPanel plot;
	private JLabel average_system;
	private JLabel expected;
	private JPanel info;
	private JPanel left;
	private JPanel right;
	private TimeSeriesCollection collection;
	private TimeSeries expected_serie;
	private TimeSeries average_agb;
	
	/**
	 * The Grid class represents the graphical user interface for displaying the simulation grid and control buttons.
	 * It implements the Observer interface to receive updates from the Model.
	 * This class was created by Tomas Arzola Roeber
	 */
	public Grid(Model model) {
		this.model = model;
		this.grid = new JPanel();
		this.cells = new JPanel[5000/this.model.getScale()][5000/this.model.getScale()];
		
		this.model.addObserver(this);
		grid.setLayout(new GridLayout(5000/this.model.getScale(), 5000/this.model.getScale()));
		panel = new JPanel();
		buttons = new JPanel();
		for(int i = 0; i < cells[0].length; i++) {
			for(int j = 0; j < cells[1].length; j++) {
				this.cells[i][j] = new JPanel();
				this.cells[i][j].setPreferredSize(new Dimension((int) (this.model.getScale()*0.1), (int) (this.model.getScale()*0.1)));
				this.cells[i][j].setBackground(this.model.getCell(i, j).getColor());
				this.cells[i][j].setEnabled(false);
				grid.add(this.cells[i][j]);
			}
		}
		

		
		start = new JButton("Start");
		start.setEnabled(true);
		this.start.addMouseListener(new Controller1(this.model));
		stop = new JButton("Stop");
		this.stop.addMouseListener(new Controller2(this.model));
		setup = new JButton("Setup");
		stop.setEnabled(false);
		this.setup.addMouseListener(new Controller3(this.model));
		runs = new JLabel("Run: 0");
		rescale = new JButton("Upscale");
		rescale.addMouseListener(new Controller4(this.model));
		buttons.setLayout(new FlowLayout());
		buttons.add(start);
		buttons.add(stop);
		buttons.add(setup);
		buttons.add(runs);
		buttons.add(rescale);
		this.left = new JPanel();
		
		
		left.setLayout(new BorderLayout());
		
		left.add(grid, BorderLayout.NORTH);
		left.add(buttons, BorderLayout.SOUTH);
		
		
		this.expected_serie = new TimeSeries("Expected AGB");
		this.collection = new TimeSeriesCollection(expected_serie);
		this.average_agb = new TimeSeries("Average AGB of grid");
		this.collection.addSeries(average_agb);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series", "Time", "Average AGB", this.collection, true, true, true);
		this.plot = new ChartPanel(chart);

		this.info = new JPanel();
		
		this.expected = new JLabel("Expected Average AGB: "+(this.model.getExpected()));
		this.average_system = new JLabel("System's Simulated Average AGB: 0");
		this.info.setLayout(new BorderLayout());
		this.info.add(expected, BorderLayout.SOUTH);
		this.info.add(average_system, BorderLayout.NORTH);
		this.right = new JPanel();
		this.right.setLayout(new BorderLayout());
		this.right.add(plot, BorderLayout.NORTH);
		this.right.add(info, BorderLayout.SOUTH);
		this.setLayout(new FlowLayout());
		
		this.panel.add(left);
		this.panel.add(right);
		this.add(panel);
		
	
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		runs.setText("Run: "+(model.getRunNumber()));
		average_system.setText("System's Simulated Average AGB: "+this.model.getTimeSeries());
		Year year = new Year(this.model.getTime());
		expected_serie.addOrUpdate(year, this.model.getExpected());
		average_agb.addOrUpdate(year, this.model.getTimeSeries());
		
		if(model.mustRescale()) {
			grid.removeAll();
			this.grid = new JPanel();
			this.cells = new JPanel[5000/model.getScale()][5000/model.getScale()];
			grid.setLayout(new GridLayout(5000/model.getScale(), 5000/model.getScale()));
			for(int i = 0; i < cells[0].length; i++) {
				for(int j = 0; j < cells[1].length; j++) {
					this.cells[i][j] = new JPanel();
					this.cells[i][j].setPreferredSize(new Dimension((int) (this.model.getScale()*0.1), (int) (this.model.getScale()*0.1)));
					this.cells[i][j].setBackground(this.model.getCell(i, j).getColor());
					this.cells[i][j].setEnabled(false);
					grid.add(this.cells[i][j]);
				}
			}
			
			this.left = new JPanel();
			
			
			left.setLayout(new BorderLayout());
			
			left.add(grid, BorderLayout.NORTH);
			left.add(buttons, BorderLayout.SOUTH);
			this.setLayout(new FlowLayout());	
			this.panel.add(left);
			this.panel.add(right);
			this.add(panel);



			
		}
		
		if(model.isRunning()) {
			start.setEnabled(false);
			stop.setEnabled(true);
			setup.setEnabled(false);
		} else {
			start.setEnabled(true);
			stop.setEnabled(false);
			setup.setEnabled(true);
		}
		
		for(int i = 0; i < cells[0].length; i++) {
			for(int j = 0; j < cells[1].length; j++) {
				Color color = this.model.getCell(i, j).getColor();
				cells[i][j].setBackground(color);
			}
		}
		
		

		
	}

}
