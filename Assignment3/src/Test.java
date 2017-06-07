
public class Test {
	  public static void main(String[] args) throws Exception{

		    TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile1.txt", "/Users/Tranheden/Downloads/test/feasibleschedule1", 4);
		   /** There is a feasible schedule on 4 cores */      
		   // TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile1.txt", "/Users/Tranheden/Downloads/test/feasibleschedule2", 3);
		   /** There is no feasible schedule on 3 cores */
		    TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile2.txt", "/Users/Tranheden/Downloads/test/feasibleschedule3", 5);
		   /** There is a feasible scheduler on 5 cores */ 
		   // TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile2.txt", "/Users/Tranheden/Downloads/test/feasibleschedule4", 4);
		   /** There is no feasible schedule on 4 cores */

		    //TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/Testattribute.txt", "/Users/Tranheden/Downloads/test/feasibleschedule5", 5);
		    
		    TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile1shuffled.txt", "/Users/Tranheden/Downloads/test/feasibleschedule6", 4);
		   /** There is a feasible schedule on 4 cores */      
		    //TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile1shuffled.txt", "/Users/Tranheden/Downloads/test/feasibleschedule7", 3);
		   /** There is no feasible schedule on 3 cores */
		   
		   TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile2shuffled.txt", "/Users/Tranheden/Downloads/test/feasibleschedule8", 5);
		   /** There is a feasible scheduler on 5 cores */ 
		   //TaskScheduler.scheduler("/Users/Tranheden/Downloads/test/samples/samplefile2shuffled.txt", "/Users/Tranheden/Downloads/test/feasibleschedule9", 4);
		   /** There is no feasible schedule on 4 cores */
		   
		   
		   /** The sample task sets are sorted. You can shuffle the tasks and test your program again */  

		  }
}
