package Main;
import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import model.Model;

/**
 * The Cell class represents a single cell in the simulation.
 * It contains methods to update the state of the cell and calculate its properties.
 * This class was created by Tomas Arzola Roeber
 */
public class Cell {
	private double AGB;
	private Color color;
	private double growth;
	private double mortality;
	private Model model;
	private double sum;
	private double p;
	
    /**
     * Constructs a new Cell with the given model.
     *
     * @param model The model associated with the cell
     */
	public Cell(Model model) {
		this.model = model;
		this.growth = 500;
		setIntensity(0);
		this.AGB = 0;
		this.sum = 0;
		this.p = 0.2; 
		float c = (float) (this.AGB/1000);
		if(c > 1) {
			c = 1;
		}
		this.color = new Color(0, c, 0);
	}
	
	   /**
     * Gets the above-ground biomass (AGB) of the cell.
     *
     * @return The AGB of the cell
     */
	public double getAGB() {
		return this.AGB;
	}
	
    /**
     * Gets the growth rate (G) of the cell.
     *
     * @return The growth rate of the cell
     */
	public double getG() {
		return this.growth;
	}
	
	
    /**
     * Sets the growth rate (G) of the cell.
     *
     * @param G The new growth rate of the cell
     */
	public void setG(double G) {
		this.growth = G;
	}
	
    /**
     * Gets the expected value of the cell.
     *
     * @return The expected value of the cell
     */
	public double getExcepted() {
		return (this.growth/this.mortality)*((1/this.p)-1);		
	}
	
    /**
     * Gets the intensity (M) of the cell.
     *
     * @return The intensity of the cell
     */
	public double getIntensity() {
		return this.mortality;
	}
	
    /**
     * Sets the intensity (M) of the cell.
     *
     * @param intensity The new intensity of the cell
     */
	public void setIntensity(double intensity) {
		if(!model.mustRescale() & model.newIntensities()) {
			Random random = new Random();
			this.mortality = 0.1 + (0.9 - 0.1) * random.nextDouble();
			if(this.mortality < 0 ) {
				this.mortality = 0.1;
			}
		} else if(model.mustRescale() || !model.newIntensities()) {
			this.mortality = intensity;
		
		}

		
	}
	


    /**
     * Gets the color of the cell.
     *
     * @return The color of the cell
     */
	public Color getColor() {
	return this.color;
	}
	
    /**
     * Resets the above-ground biomass (AGB) of the cell.
     */
	public void resetAGB() { 
		this.sum = 0;
		this.AGB = 0;
	}
	
    /**
     * Gets the sum of AGB of the cell.
     *
     * @return The sum of AGB of the cell
     */
	public double getSum() {
		return this.sum;
	}
	
    /**
     * Updates the state of the cell.
     */
	public void update() {
		
		Random rand = new Random();
		
		
		if(!model.isRescaled()) {
			if(rand.nextDouble()<this.p) {
				AGB = AGB - mortality * AGB;
				
			} else {
				AGB += growth;
			}

			
		} else {
	        
	       
	        double one_dimension = (model.getScale()/100);
			double N = one_dimension*one_dimension;
			
			RandomGenerator randomGenerator = RandomGeneratorFactory.createRandomGenerator(rand);

	        BinomialDistribution binomialDistribution = new BinomialDistribution(randomGenerator, (int) N, this.p);
	        
	        int k = binomialDistribution.sample();

	        AGB = (N-(double)k)*(((AGB+growth)/N)) + (double)k*((1.0-mortality)*(AGB/N));
	       
		}
		
		if(AGB < 0) {
			AGB = 0;
		} 


		
		float c = (float) (this.AGB/50000);
		if(c > 1 || c<0) {
			c = 1;
		}
		
		this.sum += this.AGB;
		this.color = new Color(0, c, 0);
	}

}