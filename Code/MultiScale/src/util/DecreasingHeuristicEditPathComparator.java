package util;

import java.util.Comparator;

public class DecreasingHeuristicEditPathComparator  implements
Comparator < EditPath > {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
  /**
   * {@inheritDoc}
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(final EditPath o1, final EditPath o2) {
	  double g1=o1.getTotalCosts();
	  double h1=o1.getHeuristicCosts();
	  double f1 = g1+h1;
	  double g2=o2.getTotalCosts();
	  double h2=o2.getHeuristicCosts();
	  double f2 = g2+h2;
	  
    if (f1 > f2) {
      return -1;
    }
    if (f1 < f2) {
      return 1;
    } else {
      return 0;
    }
  }

}



