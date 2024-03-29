import java.awt.Point;
import java.util.ArrayList;

public class ReturnValues {
	    public final ArrayList<Point> singleBoxes;
	    public final ArrayList<Point> diadChains;
	    public final ArrayList<Point> shortest;
	    public final int[] Histogram;
	    
	    // this structure allows a method to return three ArrayLists and an Array at the same time.
	    public ReturnValues(ArrayList<Point> singleBoxes, ArrayList<Point> diadChains, ArrayList<Point> shortest, int[] Histogram) {
	    	this.singleBoxes = singleBoxes;
	    	this.diadChains = diadChains;
	    	this.shortest = shortest;
	    	this.Histogram = Histogram;
	    }
	}
