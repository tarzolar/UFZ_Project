package model;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;


import Main.Cell;
/**
 * The Model class represents the data model of the application.
 * It contains methods to manage the simulation and handle data storage.
 * This class was created by Tomas Arzola Roeber
 */

public class Model extends Observable implements Runnable{
	private Cell[][] patches;
	private int scale;
	private int upscale;
	private int run;
	private int time;
	private boolean running;
	private double average_grid;
	private String time_series;
	private boolean rescale;
	private double expected;
	private double[][] intensities;
	private double[][] rescaled_intensities;
	private boolean rescaled;
	private String grid;
	private double[][] rescaled_growth;
	private boolean new_intensities;
	
	   /**
     * Constructs a new Model with the given parameters.
     * 
     * @param scale           The scale of the model
     * @param upscale         The upscale factor
     * @param runs            The number of runs
     * @param new_mortalities Whether new mortalities are considered
     */
	public Model(int scale, int upscale, int runs, boolean new_mortalities) {
		this.new_intensities = new_mortalities;
		this.rescaled = false;
		this.rescale = false;
		this.grid = "";
		this.time = 1;
		this.run = 1;
		this.scale = scale;
		this.upscale = upscale;
		this.patches = new Cell[5000/scale][5000/scale];
		this.intensities = new double[5000/scale][5000/scale];
		this.rescaled_intensities = new double[5000/this.upscale][5000/this.upscale];
		this.rescaled_growth = new double[5000/this.upscale][5000/this.upscale];
		this.average_grid = 0;
		this.running = false;
		
		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				this.patches[i][j] = new Cell(this);
				this.intensities[i][j] = this.patches[i][j].getIntensity();
			}
		}
		
		if(this.new_intensities) {
			try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("intensities_ob.txt"))){
				oos.writeObject(this.intensities);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("intensities_ob.txt"))){
				try {
					this.intensities = (double[][])ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < intensities[0].length; i++) {
				for(int j = 0; j < intensities[1].length; j++) {
					patches[i][j].setIntensity(intensities[i][j]);
				}
			}
			
		}

		int r = upscale/scale;

		for(int i = 0; i<intensities[0].length; i++) {
			for(int j = 0; j <intensities[1].length; j++) {				
				rescaled_intensities[((i-(i%r))/r)][((j-(j%r))/r)] += 1/intensities[i][j];	
				rescaled_growth[((i-(i%r))/r)][((j-(j%r))/r)] += patches[i][j].getG();
			}
		}
		
		
		for(int i = 0; i<rescaled_intensities[0].length; i++) {
			for(int j = 0; j<rescaled_intensities[1].length; j++) {
				rescaled_intensities[i][j] = (r*r)/rescaled_intensities[i][j];
			
			}
		}



		double sum_ = 0;
		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				sum_ += this.patches[i][j].getExcepted();
			}
		}

		this.expected = sum_;

		this.time_series = "0 0 "+this.getExpected()+ System.lineSeparator();

	}
	/**
     * Checks if the model is currently running.
     * 
     * @return true if the model is running, false otherwise
     */
	public boolean isRunning() {
		return this.running;
	}
	
	 /**
     * Checks if new intensities are being used.
     * 
     * @return true if new intensities are being used, false otherwise
     */
	public boolean newIntensities() {
		return this.new_intensities;
	}
	
    /**
     * Rescales the model.
     */
	public void rescale() {
		this.rescale = true;
		this.run = 1;
		this.time_series = "0 0 "+this.getExpected()+ System.lineSeparator();
		this.scale = this.upscale;
		this.patches = new Cell[5000/scale][5000/scale];
		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				this.patches[i][j] = new Cell(this);
				this.patches[i][j].setIntensity(rescaled_intensities[i][j]);
				this.patches[i][j].setG(rescaled_growth[i][j]);
			}
		}


		setRunning(false);
		this.setChanged();
		this.notifyObservers();
		this.rescale = false;
		this.rescaled = true;
		

	}

    /**
     * Checks if the model needs to be rescaled.
     * 
     * @return true if the model needs rescaling, false otherwise
     */
	public boolean mustRescale() {
		return this.rescale;
	}
	
    /**
     * Sets the running state of the model.
     * 
     * @param running true if the model is running, false otherwise
     */
	public void setRunning(boolean running) {
		this.running = running;
		this.setChanged();
		this.notifyObservers();
	}

    /**
     * Gets the scale of the model.
     *
     * @return The scale of the model
     */
	public int getScale() {
		return this.scale;
	}
    /**
     * Gets the upscale factor of the model.
     *
     * @return The upscale factor of the model
     */
	public int getUpscale() {
		return this.upscale;
	}
    /**
     * Gets the expected value of the model.
     *
     * @return The expected value of the model
     */
	public double getExpected() {
		return this.expected;
	}


    /**
     * Gets the cell at the specified coordinates.
     *
     * @param x The x-coordinate of the cell
     * @param y The y-coordinate of the cell
     * @return The cell at the specified coordinates
     */
	public Cell getCell(int x, int y) {
		return this.patches[x][y];
	}

    /**
     * Gets the time series of the model.
     *
     * @return The time series of the model
     */
	public double getTimeSeries() {
		double sum = 0;
		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				sum += this.patches[i][j].getAGB();
			}
		}
		
		return sum;
	}


    /**
     * Gets the average of the grid.
     *
     * @return The average of the grid
     */
	public double getGridAverage() {
		return this.average_grid;
	}


    /**
     * Updates the model.
     */
	public void update() {

		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				this.patches[i][j].update();
			}
		}


	}	

	@Override
	public void run() {
		setRunning(true);
		double sum_grid = 0;
		while(isRunning() & this.run <= 10) {
			while(isRunning() & (this.time <= 300)) {
				this.update();
				sum_grid += getTimeSeries();
				this.average_grid = sum_grid/time;
				this.time_series += time+" "+getTimeSeries()+" "+this.getExpected()+System.lineSeparator();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.time++;
				this.setChanged();
				this.notifyObservers();
			}
			
			for(int i = 0; i < patches[0].length; i++) {
				grid += (this.run-1)+" ";
				for(int j = 0; j < patches[1].length; j++) {
					double average = (patches[i][j].getAGB());
					grid += (average)+ " ";	
				}	
				grid += System.lineSeparator();
			}
			
			if(this.time == 301) {
				this.time = 1;
				
			}	
			for(int i = 0; i < patches[0].length; i++) {
				for(int j = 0; j < patches[1].length; j++) {
					this.patches[i][j].resetAGB();
					
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.run++;
			
		}

		if(!rescaled) {
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("time_series_or_one_cell0.txt"))){
				writer.write(this.time_series);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("grid_average_hist_100_runs.txt"))){
				writer.write(this.grid);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("intensities_100_original.txt"))){
				String s = "";
				for(int i = 0; i < patches[0].length;i++) {
					for(int j = 0; j < patches[1].length; j++) {
						s += (patches[i][j].getIntensity())+ " ";
					}
					s += System.lineSeparator();
				}
				
				writer.write(s);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("time_series_hist_2500_runs.txt"))){
				writer.write(this.time_series);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("grid_average_hist_arit_2500_runs.txt"))){
				writer.write(this.grid);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter("rescaled_intensities_hist_2500_arit_runs.txt"))){
				String s = "";
				for(int i = 0; i < patches[0].length;i++) {
					for(int j = 0; j < patches[1].length; j++) {
						s += (patches[i][j].getIntensity())+ " ";
					}
					s += System.lineSeparator();
				}
				writer.write(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}

		this.grid = "";
		setRunning(false);
		this.setChanged();
		this.notifyObservers();
	}

	public int getRunNumber() {
		return this.run;
	}
    /**
     * Sets up the model.
     */
	public void setup() {
		this.time_series = "0 0"+ System.lineSeparator();
		this.time = 1;
		running = false;
		this.patches = new Cell[5000/scale][5000/scale];
		this.average_grid = 0;

		for(int i = 0; i < patches[0].length; i++) {
			for(int j = 0; j < patches[1].length; j++) {
				this.patches[i][j] = new Cell(this);
			}
		}

		this.setChanged();
		this.notifyObservers();
	}

    /**
     * Gets the current time.
     *
     * @return The current time
     */

	public int getTime() {
		return this.time;
	}

    /**
     * Checks if the model has been rescaled.
     *
     * @return true if the model has been rescaled, false otherwise
     */
	public boolean isRescaled() {
		return this.rescaled;
	}
	


}
