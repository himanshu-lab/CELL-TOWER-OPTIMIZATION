package K_Clusterer;

import java.io.*;
import java.nio.channels.ReadPendingException;
import java.util.*;

class ReadDataset {
	
	protected List<double[]> features=new ArrayList<>();
	protected static int numberOfFeatures;
	
	public List<double[]> getFeatures()
	
	{
		return features;
	}
	
	void read(String s) throws NumberFormatException, IOException 
	{
		File file=new File(s);
		
		try 
		{
			BufferedReader readFile=new BufferedReader(new FileReader(file));
			String line;
			while((line=readFile.readLine()) != null)
			{
				 String[] split = line.split(",");
	             double[] feature = new double[split.length ];
	             numberOfFeatures = split.length;
	             for (int i = 0; i < split.length; i++)
	               		feature[i] = Double.parseDouble(split[i]);
	            features.add(feature);
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void display()
	{
		Iterator<double[]> itr=features.iterator();
		while(itr.hasNext())
		{ 
			double db[]=itr.next();
			for(int i=0; i<4;i++)
			{
				System.out.print(db[i]+" ");
			}	
		}
	}
}
class Distance {

	public Distance() {
		// TODO Auto-generated constructor stub
	}
	
	public static double eucledianDistance(double[] centroid, double[] point) {
        double sum = 0.0;
        for(int i = 0; i < centroid.length; i++){
            sum += ((centroid[i] - point[i]) * (centroid[i] - point[i]));
        }
        return Math.sqrt(sum);
    }
    
    public static double manhattanDistance(double centroid[], double point[]){
    	double sum = 0.0;
        for(int i = 0; i < centroid.length; i++) {
            sum += (Math.abs(centroid[i] - point[i]));
        }
        return sum;
    }
    

}
public class K_Clusterer extends ReadDataset {

	public K_Clusterer() {
		// TODO Auto-generated constructor stub
	}
//main method
	public static void main(String args[]) throws IOException {
		ReadDataset r1 = new ReadDataset();
		r1.features.clear();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the filename with path");
		String file=sc.next();
		r1.read(file); //load data
		int ex=1;
		do{
		System.out.println("Enter the no. of clusters");
		int k = sc.nextInt();
		System.out.println("Enter maximum iterations");
		int max_iterations = sc.nextInt();
		System.out.println("Enter distance metric 1 or 2: \n1. Euclidean\n2. Manhattan");
		int distance = sc.nextInt();
		//Hashmap to store centroids with index
		Map<Integer, double[]> centroids = new HashMap<>();
		// calculating initial centroids
		double[] x1 = new double[numberOfFeatures];
		int r =0;
		for (int i = 0; i < k; i++) 
		{
			x1=r1.features.get(r++);
			centroids.put(i, x1);
		}
		//Hashmap for finding cluster indexes
		Map<double[], Integer> clusters = new HashMap<>();
		clusters = kmeans(r1.features, distance, centroids, k);
		// initial cluster print
		double db[] = new double[numberOfFeatures];
		//reassigning to new clusters
		for (int i = 0; i < max_iterations; i++)
		{
			for (int j = 0; j < k; j++) 
			{
				List<double[]> list = new ArrayList<>();
				for (double[] key : clusters.keySet()) 
				{
					if (clusters.get(key)==j) 
					{
						list.add(key);
					}
				}
				db = centroidCalculator(list);
				centroids.put(j, db);
			
			}
			clusters.clear();
			clusters = kmeans(r1.features,distance, centroids, k);
			
		}
		
		//final cluster print
		System.out.println("\nFinal Clustering of Data");
		System.out.println("X_coordinate\tY_coordinate\tCluster");
		for (double[] key : clusters.keySet()) 
		{
			for (int i = 0; i < key.length; i++) 
			{
				System.out.print(key[i] + "\t \t");
			}
			System.out.print(clusters.get(key) + "\n");
		}
		
		//Calculate WCSS
		double wcss=0;
		
		for(int i=0;i<k;i++)
		{
			double sse=0;
			for (double[] key : clusters.keySet()) 
			{
				if (clusters.get(key)==i) 
				{
					sse+=Math.pow(Distance.eucledianDistance(key, centroids.get(i)),2);
				}
			}
			wcss+=sse;
		}
		String dis="";
		if(distance ==1)
			 dis="Euclidean";
		else
			dis="Manhattan";
		System.out.println("\n*********Programmed by Himanshu & Chandrahas *********\n\n\t\t******Results********\n\nDistance Metric: "+dis);
		System.out.println("\nIterations: "+max_iterations);
		System.out.println("\nNumber of Clusters: "+k);
		System.out.println("\nWCSS: "+wcss);
		System.out.println("\nLocation Of All Tower");
		for(int i = 0 ; i < k; i++)
		{
			double location[] = centroids.get(i);
			System.out.print("Location of tower "+i);
			System.out.println(" is  "+location[0]+"  ,  "+location[1] );
		}
		System.out.println("Press 1 if you want to continue else press 0 to exit..");
		ex=sc.nextInt();
		}while(ex==1);
	}
	
	//method to calculate centroids
	public static double[] centroidCalculator(List<double[]> a) 
	{	
		int count = 0;
		double sum=0.0;
		double[] centroids = new double[ReadDataset.numberOfFeatures];
		for (int i = 0; i < ReadDataset.numberOfFeatures; i++) 
		{
			sum=0.0;
			count = 0;
			for(double[] x:a)
			{
				count++;
				sum = sum + x[i];
			}
			centroids[i] = sum / count;
		}
		return centroids;
	}

	//method for putting features to clusters and reassignment of clusters.
	public static Map<double[], Integer> kmeans(List<double[]> features,int distance, Map<Integer, double[]> centroids, int k) {
		Map<double[], Integer> clusters = new HashMap<>();
		int k1 = 0;
		double dist=0.0;
		for(double[] x:features) 
		{
			double minimum = 999999.0;
			for (int j = 0; j < k; j++) 
			{
				if(distance==1)
				{
				 dist = Distance.eucledianDistance(centroids.get(j), x);
				}
				else if(distance==2)
				{
					dist = Distance.manhattanDistance(centroids.get(j), x);
				}
				if (dist < minimum)
				{
					minimum = dist;
					k1 = j;
				}
			}
			clusters.put(x, k1);
		}
		
		return clusters;

	}
}